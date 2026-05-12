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

import co.edu.unbosque.cocotechback.dto.VentaDTO;
import co.edu.unbosque.cocotechback.model.Cliente;
import co.edu.unbosque.cocotechback.model.Empleado;
import co.edu.unbosque.cocotechback.model.Venta;
import co.edu.unbosque.cocotechback.repository.ClienteRepository;
import co.edu.unbosque.cocotechback.repository.EmpleadoRepository;
import co.edu.unbosque.cocotechback.repository.VentaRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Venta}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de ventas. También expone
 * métodos para los escenarios analíticos del proyecto: empleado del mes y
 * cliente con más compras.
 */
@Service
public class VentaService implements CRUDOperation<VentaDTO, Venta> {

	/**
	 * Repositorio para la gestión de la entidad {@link Venta}.
	 */
	@Autowired
	private VentaRepository ventaRepo;

	/**
	 * Repositorio para resolver la relación con {@link Empleado}.
	 */
	@Autowired
	private EmpleadoRepository empleadoRepo;

	/**
	 * Repositorio para resolver la relación con {@link Cliente}.
	 */
	@Autowired
	private ClienteRepository clienteRepo;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Constructor por defecto de {@code VentaService}.
	 */
	public VentaService() {
	}

	/**
	 * Registra una nueva venta en la base de datos a partir de un
	 * {@link VentaDTO}.
	 * <p>
	 * Valida que el empleado y el cliente existan. La fecha se asigna al
	 * momento actual si no se proporciona.
	 *
	 * @param data El {@link VentaDTO} con la información de la nueva venta.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 2} si el empleado o el cliente no existen,
	 *         {@code 4} si algún campo requerido está ausente.
	 */
	@Override
	public int create(VentaDTO data, String rol) {
		if (data.getIdEmpleado() == null || data.getIdCliente() == null) {
			return 4;
		}
		Optional<Empleado> empleadoFound = empleadoRepo.findById(data.getIdEmpleado());
		Optional<Cliente> clienteFound = clienteRepo.findById(data.getIdCliente());
		if (!empleadoFound.isPresent() || !clienteFound.isPresent()) {
			return 2;
		}
		Venta entity = new Venta();
		entity.setFecha(data.getFecha() != null ? data.getFecha() : LocalDateTime.now());
		entity.setTotal(data.getTotal() != null ? data.getTotal() : 0.0);
		entity.setEmpleado(empleadoFound.get());
		entity.setCliente(clienteFound.get());
		ventaRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todas las ventas registradas en la base de datos.
	 *
	 * @return Una lista de {@link VentaDTO}. Retorna una lista vacía si no hay
	 *         ventas.
	 */
	@Override
	public List<VentaDTO> getAll() {
		List<Venta> entityList = ventaRepo.findAll();
		List<VentaDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> {
			VentaDTO dto = new VentaDTO();
			dto.setIdVenta(entity.getIdVenta());
			dto.setFecha(entity.getFecha());
			dto.setTotal(entity.getTotal());
			if (entity.getEmpleado() != null) {
				dto.setIdEmpleado(entity.getEmpleado().getId());
			}
			if (entity.getCliente() != null) {
				dto.setIdCliente(entity.getCliente().getId());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Obtiene una venta por su ID.
	 *
	 * @param id El ID de la venta a buscar.
	 * @return Un {@link VentaDTO} o {@code null} si no existe.
	 */
	public VentaDTO getById(Long id) {
		Optional<Venta> found = ventaRepo.findById(id);
		if (found.isPresent()) {
			Venta entity = found.get();
			VentaDTO dto = new VentaDTO();
			dto.setIdVenta(entity.getIdVenta());
			dto.setFecha(entity.getFecha());
			dto.setTotal(entity.getTotal());
			if (entity.getEmpleado() != null) {
				dto.setIdEmpleado(entity.getEmpleado().getId());
			}
			if (entity.getCliente() != null) {
				dto.setIdCliente(entity.getCliente().getId());
			}
			return dto;
		}
		return null;
	}

	/**
	 * Elimina una venta por su ID.
	 *
	 * @param id El ID de la venta a eliminar.
	 * @return {@code 0} si fue exitosa, {@code 2} si no existe.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Venta> found = ventaRepo.findById(id);
		if (found.isPresent()) {
			ventaRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza el total de una venta existente por su ID.
	 *
	 * @param id      El ID de la venta a actualizar.
	 * @param newData El {@link VentaDTO} con el nuevo total.
	 * @return {@code 0} si fue exitosa, {@code 2} si no existe la venta.
	 */
	@Override
	public int updateById(Long id, VentaDTO newData) {
		Optional<Venta> found = ventaRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Venta temp = found.get();
		if (newData.getTotal() != null) {
			temp.setTotal(newData.getTotal());
		}
		if (newData.getFecha() != null) {
			temp.setFecha(newData.getFecha());
		}
		ventaRepo.save(temp);
		return 0;
	}

	/**
	 * Retorna el empleado con más ventas registradas en un periodo dado
	 * ("Empleado del mes").
	 * <p>
	 * Satisface el objetivo analítico del proyecto.
	 *
	 * @param inicio Fecha y hora de inicio del periodo.
	 * @param fin    Fecha y hora de fin del periodo.
	 * @return Una lista de arreglos {@code Object[]} con:
	 *         [0] nombres del empleado, [1] apellidos, [2] total de ventas.
	 */
	public List<Object[]> getEmpleadoDelMes(LocalDateTime inicio, LocalDateTime fin) {
		return ventaRepo.findEmpleadoDelMes(inicio, fin);
	}

	/**
	 * Retorna el cliente con más compras realizadas en el sistema.
	 * <p>
	 * Satisface el objetivo analítico del proyecto.
	 *
	 * @return Una lista de arreglos {@code Object[]} con:
	 *         [0] nombres del cliente, [1] apellidos, [2] total de compras.
	 */
	public List<Object[]> getClienteConMasCompras() {
		return ventaRepo.findClienteConMasCompras();
	}

	/**
	 * Retorna el total de ventas acumulado por empleado, ordenado de mayor a
	 * menor monto.
	 *
	 * @return Una lista de arreglos {@code Object[]} con:
	 *         [0] nombres, [1] apellidos, [2] monto total en ventas.
	 */
	public List<Object[]> getTotalVentasPorEmpleado() {
		return ventaRepo.findTotalVentasPorEmpleado();
	}

	/** {@inheritDoc} */
	@Override
	public long count() {
		return ventaRepo.count();
	}

	/** {@inheritDoc} */
	@Override
	public boolean exist(Long id) {
		return ventaRepo.existsById(id);
	}

	/** No aplica para Venta. */
	@Override
	public Venta encrypt(VentaDTO data) {
		return modelMapper.map(data, Venta.class);
	}

	/** No aplica para Venta. */
	@Override
	public String decrypt(VentaDTO data) {
		return null;
	}

	/** No aplica para Venta. Retorna {@code -1}. */
	@Override
	public int updatePassword(Long id, VentaDTO newData) {
		return -1;
	}

	/** No aplica para Venta. Retorna {@code -1}. */
	@Override
	public int updateCorreo(Long id, VentaDTO newData) {
		return -1;
	}

	/** No aplica para Venta. Retorna {@code -1}. */
	@Override
	public int updateRol(Long id, VentaDTO newData) {
		return -1;
	}

	/** No aplica para Venta. Retorna {@code -1}. */
	@Override
	public int updateCode(Long id, VentaDTO newData) {
		return -1;
	}
}
