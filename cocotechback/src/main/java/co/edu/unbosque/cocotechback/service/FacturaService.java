/**
 * Paquete que contiene las clases de Servicio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.cocotechback.dto.FacturaDTO;
import co.edu.unbosque.cocotechback.model.Factura;
import co.edu.unbosque.cocotechback.model.Venta;
import co.edu.unbosque.cocotechback.repository.jpa.FacturaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.VentaRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Factura}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de facturas. Garantiza la
 * regla de negocio del supuesto: cada venta genera una única factura (1:1).
 * <p>
 * Además, cada operación que modifica el estado en MySQL sincroniza el
 * estado correspondiente en MongoDB (Patrón de Referencia Extendida) a
 * través de {@link FacturaMongoService}, manteniendo MySQL como fuente de
 * verdad transaccional y MongoDB como vista de lectura intensiva.
 */
@Service
public class FacturaService implements CRUDOperation<FacturaDTO, Factura> {

	/**
	 * Repositorio para la gestión de la entidad {@link Factura} en MySQL.
	 */
	@Autowired
	private FacturaRepository facturaRepo;

	/**
	 * Repositorio para resolver la relación con {@link Venta}.
	 */
	@Autowired
	private VentaRepository ventaRepo;

	/**
	 * Servicio que mantiene la proyección Mongo en sincronía con MySQL.
	 */
	@Autowired
	private FacturaMongoService facturaMongoServ;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Constructor por defecto de {@code FacturaService}.
	 */
	public FacturaService() {
	}

	/**
	 * Crea una nueva factura en MySQL y la proyecta a MongoDB.
	 * <p>
	 * Valida que la venta exista y que aún no tenga una factura generada
	 * (restricción 1:1 del supuesto). La fecha se asigna al momento actual
	 * si no se proporciona. Tras persistir en MySQL, dispara la proyección
	 * a MongoDB para mantener consistencia en la vista de lectura.
	 *
	 * @param data el {@link FacturaDTO} con la información de la nueva factura.
	 * @param rol  no utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si la venta ya tiene una factura asociada,
	 *         {@code 2} si la venta no existe,
	 *         {@code 4} si el ID de venta está ausente.
	 */
	@Override
	public int create(FacturaDTO data, String rol) {
		if (data.getIdVenta() == null) {
			return 4;
		}
		Optional<Venta> ventaFound = ventaRepo.findById(data.getIdVenta());
		if (!ventaFound.isPresent()) {
			return 2;
		}
		if (facturaRepo.existsByVenta_IdVenta(data.getIdVenta())) {
			return 1;
		}
		Factura entity = new Factura();
		entity.setFecha(data.getFecha() != null ? data.getFecha() : LocalDateTime.now());
		entity.setPrecioTotal(data.getPrecioTotal() != null ? data.getPrecioTotal() : 0.0);
		entity.setPrecioImpuestos(
				data.getPrecioImpuestos() != null ? data.getPrecioImpuestos() : 0.0);
		entity.setVenta(ventaFound.get());

		Factura guardada = facturaRepo.save(entity);

		// Proyectar a MongoDB tras la persistencia en MySQL.
		facturaMongoServ.proyectar(guardada);

		return 0;
	}

	/**
	 * Obtiene todas las facturas registradas en la base de datos.
	 *
	 * @return una lista de {@link FacturaDTO}. Retorna una lista vacía si no
	 *         hay facturas.
	 */
	@Override
	public List<FacturaDTO> getAll() {
		List<Factura> entityList = facturaRepo.findAll();
		List<FacturaDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> {
			FacturaDTO dto = modelMapper.map(entity, FacturaDTO.class);
			if (entity.getVenta() != null) {
				dto.setIdVenta(entity.getVenta().getIdVenta());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Obtiene una factura por su ID.
	 *
	 * @param id el ID de la factura a buscar.
	 * @return un {@link FacturaDTO} o {@code null} si no existe.
	 */
	public FacturaDTO getById(Long id) {
		Optional<Factura> found = facturaRepo.findById(id);
		if (found.isPresent()) {
			Factura entity = found.get();
			FacturaDTO dto = modelMapper.map(entity, FacturaDTO.class);
			if (entity.getVenta() != null) {
				dto.setIdVenta(entity.getVenta().getIdVenta());
			}
			return dto;
		}
		return null;
	}

	/**
	 * Obtiene la factura asociada a una venta específica.
	 *
	 * @param idVenta el ID de la venta.
	 * @return un {@link FacturaDTO} o {@code null} si la venta no tiene factura.
	 */
	public FacturaDTO getByIdVenta(Long idVenta) {
		Optional<Factura> found = facturaRepo.findByVenta_IdVenta(idVenta);
		if (found.isPresent()) {
			FacturaDTO dto = modelMapper.map(found.get(), FacturaDTO.class);
			dto.setIdVenta(idVenta);
			return dto;
		}
		return null;
	}

	/**
	 * Elimina una factura por su ID y su proyección Mongo asociada.
	 *
	 * @param id el ID de la factura a eliminar.
	 * @return {@code 0} si fue exitosa, {@code 2} si no existe.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Factura> found = facturaRepo.findById(id);
		if (found.isPresent()) {
			facturaRepo.delete(found.get());
			facturaMongoServ.eliminarPorIdFacturaMySQL(id);
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos de una factura existente y re-proyecta a Mongo.
	 *
	 * @param id      el ID de la factura a actualizar.
	 * @param newData el {@link FacturaDTO} con los nuevos datos.
	 * @return {@code 0} si fue exitosa, {@code 2} si no existe la factura.
	 */
	@Override
	public int updateById(Long id, FacturaDTO newData) {
		Optional<Factura> found = facturaRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Factura temp = found.get();
		if (newData.getFecha() != null) {
			temp.setFecha(newData.getFecha());
		}
		if (newData.getPrecioTotal() != null) {
			temp.setPrecioTotal(newData.getPrecioTotal());
		}
		if (newData.getPrecioImpuestos() != null) {
			temp.setPrecioImpuestos(newData.getPrecioImpuestos());
		}
		Factura actualizada = facturaRepo.save(temp);

		// Re-proyectar a MongoDB para mantener la vista de lectura al día.
		facturaMongoServ.proyectar(actualizada);

		return 0;
	}

	/**
	 * Calcula el ingreso bruto total del supermercado en un periodo dado.
	 *
	 * @param inicio fecha y hora de inicio del periodo.
	 * @param fin    fecha y hora de fin del periodo.
	 * @return el ingreso bruto total, o {@code 0.0} si no hay facturas en el
	 *         rango.
	 */
	public Double getIngresoBrutoPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
		Double result = facturaRepo.findIngresoBrutoPorPeriodo(inicio, fin);
		return result != null ? result : 0.0;
	}

	/**
	 * Calcula el total de impuestos recaudados en un periodo dado.
	 *
	 * @param inicio fecha y hora de inicio del periodo.
	 * @param fin    fecha y hora de fin del periodo.
	 * @return el total de impuestos recaudados, o {@code 0.0} si no hay
	 *         facturas.
	 */
	public Double getTotalImpuestosRecaudados(LocalDateTime inicio, LocalDateTime fin) {
		Double result = facturaRepo.findTotalImpuestosRecaudados(inicio, fin);
		return result != null ? result : 0.0;
	}

	/** {@inheritDoc} */
	@Override
	public long count() {
		return facturaRepo.count();
	}

	/** {@inheritDoc} */
	@Override
	public boolean exist(Long id) {
		return facturaRepo.existsById(id);
	}

	/** No aplica para Factura. */
	@Override
	public Factura encrypt(FacturaDTO data) {
		return modelMapper.map(data, Factura.class);
	}

	/** No aplica para Factura. */
	@Override
	public String decrypt(FacturaDTO data) {
		return null;
	}

	/** No aplica para Factura. Retorna {@code -1}. */
	@Override
	public int updatePassword(Long id, FacturaDTO newData) {
		return -1;
	}

	/** No aplica para Factura. Retorna {@code -1}. */
	@Override
	public int updateCorreo(Long id, FacturaDTO newData) {
		return -1;
	}

	/** No aplica para Factura. Retorna {@code -1}. */
	@Override
	public int updateRol(Long id, FacturaDTO newData) {
		return -1;
	}

	/** No aplica para Factura. Retorna {@code -1}. */
	@Override
	public int updateCode(Long id, FacturaDTO newData) {
		return -1;
	}
}
