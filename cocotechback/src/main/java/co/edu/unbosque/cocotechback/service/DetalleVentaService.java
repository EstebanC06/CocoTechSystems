/**
 * Paquete que contiene las clases de Servicio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.cocotechback.dto.DetalleVentaDTO;
import co.edu.unbosque.cocotechback.model.DetalleVenta;
import co.edu.unbosque.cocotechback.model.Producto;
import co.edu.unbosque.cocotechback.model.Venta;
import co.edu.unbosque.cocotechback.repository.jpa.DetalleVentaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ProductoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.VentaRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link DetalleVenta}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de detalles de venta.
 * También expone métodos para análisis de ventas por método de pago y detalles
 * con promociones activas.
 * <p>
 * Al crear un detalle de venta, se descuenta automáticamente la cantidad
 * vendida del stock del producto correspondiente.
 */
@Service
public class DetalleVentaService implements CRUDOperation<DetalleVentaDTO, DetalleVenta> {

	/**
	 * Repositorio para la gestión de la entidad {@link DetalleVenta}.
	 */
	@Autowired
	private DetalleVentaRepository detalleVentaRepo;

	/**
	 * Repositorio para resolver la relación con {@link Venta}.
	 */
	@Autowired
	private VentaRepository ventaRepo;

	/**
	 * Repositorio para resolver la relación con {@link Producto} y actualizar
	 * el stock.
	 */
	@Autowired
	private ProductoRepository productoRepo;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Constructor por defecto de {@code DetalleVentaService}.
	 */
	public DetalleVentaService() {
	}

	/**
	 * Crea un nuevo detalle de venta en la base de datos a partir de un
	 * {@link DetalleVentaDTO}.
	 * <p>
	 * Valida que la venta y el producto existan y que haya stock suficiente.
	 * Descuenta automáticamente la cantidad vendida del stock del producto.
	 *
	 * @param data El {@link DetalleVentaDTO} con la información del detalle.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si no hay stock suficiente,
	 *         {@code 2} si la venta o el producto no existen,
	 *         {@code 4} si algún campo requerido está ausente.
	 */
	@Override
	public int create(DetalleVentaDTO data, String rol) {
		if (data.getIdVenta() == null || data.getIdProducto() == null
				|| data.getCantidadProductos() == null) {
			return 4;
		}
		Optional<Venta> ventaFound = ventaRepo.findById(data.getIdVenta());
		Optional<Producto> productoFound = productoRepo.findById(data.getIdProducto());
		if (!ventaFound.isPresent() || !productoFound.isPresent()) {
			return 2;
		}
		Producto producto = productoFound.get();
		if (producto.getStock() < data.getCantidadProductos()) {
			return 1;
		}
		DetalleVenta entity = new DetalleVenta();
		entity.setCantidadProductos(data.getCantidadProductos());
		entity.setPrecioUnitario(
				data.getPrecioUnitario() != null ? data.getPrecioUnitario() : producto.getPrecio());
		entity.setSubtotal(data.getSubtotal() != null ? data.getSubtotal()
				: entity.getPrecioUnitario() * data.getCantidadProductos());
		entity.setMetodoPago(data.getMetodoPago());
		entity.setPromocion(data.getPromocion() != null ? data.getPromocion() : false);
		entity.setPorcentajeDescuento(data.getPorcentajeDescuento());
		entity.setPrecioOriginal(data.getPrecioOriginal());
		entity.setPrecioNuevo(data.getPrecioNuevo());
		entity.setVenta(ventaFound.get());
		entity.setProducto(producto);
		// Descontar stock
		producto.setStock(producto.getStock() - data.getCantidadProductos());
		productoRepo.save(producto);
		detalleVentaRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todos los detalles de venta registrados en la base de datos.
	 *
	 * @return Una lista de {@link DetalleVentaDTO}. Retorna una lista vacía si
	 *         no hay detalles.
	 */
	@Override
	public List<DetalleVentaDTO> getAll() {
		List<DetalleVenta> entityList = detalleVentaRepo.findAll();
		List<DetalleVentaDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> {
			DetalleVentaDTO dto = modelMapper.map(entity, DetalleVentaDTO.class);
			if (entity.getVenta() != null) {
				dto.setIdVenta(entity.getVenta().getIdVenta());
			}
			if (entity.getProducto() != null) {
				dto.setIdProducto(entity.getProducto().getIdProducto());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Obtiene un detalle de venta por su ID.
	 *
	 * @param id El ID del detalle a buscar.
	 * @return Un {@link DetalleVentaDTO} o {@code null} si no existe.
	 */
	public DetalleVentaDTO getById(Long id) {
		Optional<DetalleVenta> found = detalleVentaRepo.findById(id);
		if (found.isPresent()) {
			DetalleVenta entity = found.get();
			DetalleVentaDTO dto = modelMapper.map(entity, DetalleVentaDTO.class);
			if (entity.getVenta() != null) {
				dto.setIdVenta(entity.getVenta().getIdVenta());
			}
			if (entity.getProducto() != null) {
				dto.setIdProducto(entity.getProducto().getIdProducto());
			}
			return dto;
		}
		return null;
	}

	/**
	 * Retorna el resumen de ventas agrupado por método de pago.
	 *
	 * @return Lista de {@code Object[]} con: [0] método de pago, [1] total de
	 *         transacciones, [2] ingreso total.
	 */
	public List<Object[]> getResumenPorMetodoPago() {
		return detalleVentaRepo.findResumenPorMetodoPago();
	}

	/**
	 * Retorna todos los detalles de venta con promoción activa.
	 *
	 * @return Lista de {@code Object[]} con: [0] nombre del producto,
	 *         [1] precio original, [2] precio nuevo, [3] porcentaje de descuento.
	 */
	public List<Object[]> getDetallesConPromocion() {
		return detalleVentaRepo.findDetallesConPromocion();
	}

	/**
	 * Elimina un detalle de venta por su ID.
	 *
	 * @param id El ID del detalle a eliminar.
	 * @return {@code 0} si fue exitosa, {@code 2} si no existe.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<DetalleVenta> found = detalleVentaRepo.findById(id);
		if (found.isPresent()) {
			detalleVentaRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos de un detalle de venta existente por su ID.
	 *
	 * @param id      El ID del detalle a actualizar.
	 * @param newData El {@link DetalleVentaDTO} con los nuevos datos.
	 * @return {@code 0} si fue exitosa, {@code 2} si no existe el detalle.
	 */
	@Override
	public int updateById(Long id, DetalleVentaDTO newData) {
		Optional<DetalleVenta> found = detalleVentaRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		DetalleVenta temp = found.get();
		if (newData.getCantidadProductos() != null) {
			temp.setCantidadProductos(newData.getCantidadProductos());
		}
		if (newData.getPrecioUnitario() != null) {
			temp.setPrecioUnitario(newData.getPrecioUnitario());
		}
		if (newData.getSubtotal() != null) {
			temp.setSubtotal(newData.getSubtotal());
		}
		if (newData.getMetodoPago() != null) {
			temp.setMetodoPago(newData.getMetodoPago());
		}
		if (newData.getPromocion() != null) {
			temp.setPromocion(newData.getPromocion());
		}
		if (newData.getPorcentajeDescuento() != null) {
			temp.setPorcentajeDescuento(newData.getPorcentajeDescuento());
		}
		if (newData.getPrecioOriginal() != null) {
			temp.setPrecioOriginal(newData.getPrecioOriginal());
		}
		if (newData.getPrecioNuevo() != null) {
			temp.setPrecioNuevo(newData.getPrecioNuevo());
		}
		detalleVentaRepo.save(temp);
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public long count() {
		return detalleVentaRepo.count();
	}

	/** {@inheritDoc} */
	@Override
	public boolean exist(Long id) {
		return detalleVentaRepo.existsById(id);
	}

	/** No aplica para DetalleVenta. */
	@Override
	public DetalleVenta encrypt(DetalleVentaDTO data) {
		return modelMapper.map(data, DetalleVenta.class);
	}

	/** No aplica para DetalleVenta. */
	@Override
	public String decrypt(DetalleVentaDTO data) {
		return null;
	}

	/** No aplica para DetalleVenta. Retorna {@code -1}. */
	@Override
	public int updatePassword(Long id, DetalleVentaDTO newData) {
		return -1;
	}

	/** No aplica para DetalleVenta. Retorna {@code -1}. */
	@Override
	public int updateCorreo(Long id, DetalleVentaDTO newData) {
		return -1;
	}

	/** No aplica para DetalleVenta. Retorna {@code -1}. */
	@Override
	public int updateRol(Long id, DetalleVentaDTO newData) {
		return -1;
	}

	/** No aplica para DetalleVenta. Retorna {@code -1}. */
	@Override
	public int updateCode(Long id, DetalleVentaDTO newData) {
		return -1;
	}
}
