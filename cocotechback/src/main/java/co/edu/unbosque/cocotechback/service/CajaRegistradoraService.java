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

import co.edu.unbosque.cocotechback.dto.CajaRegistradoraDTO;
import co.edu.unbosque.cocotechback.model.CajaRegistradora;
import co.edu.unbosque.cocotechback.model.Empleado;
import co.edu.unbosque.cocotechback.model.Sucursal;
import co.edu.unbosque.cocotechback.repository.CajaRegistradoraRepository;
import co.edu.unbosque.cocotechback.repository.EmpleadoRepository;
import co.edu.unbosque.cocotechback.repository.SucursalRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link CajaRegistradora}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de cajas registradoras.
 * <p>
 * Valida las reglas de negocio del supuesto: cada caja registradora tiene
 * asignado un solo empleado (1:1), y una caja pertenece a una sucursal
 * específica. Se verifica que el empleado asignado no tenga ya otra caja
 * registradora antes de crear una nueva.
 */
@Service
public class CajaRegistradoraService implements CRUDOperation<CajaRegistradoraDTO, CajaRegistradora> {

	/**
	 * Repositorio para la gestión de la entidad {@link CajaRegistradora}.
	 */
	@Autowired
	private CajaRegistradoraRepository cajaRepo;

	/**
	 * Repositorio para resolver la relación con {@link Empleado}.
	 */
	@Autowired
	private EmpleadoRepository empleadoRepo;

	/**
	 * Repositorio para resolver la relación con {@link Sucursal}.
	 */
	@Autowired
	private SucursalRepository sucursalRepo;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Constructor por defecto de {@code CajaRegistradoraService}.
	 */
	public CajaRegistradoraService() {
	}

	/**
	 * Crea una nueva caja registradora en la base de datos a partir de un
	 * {@link CajaRegistradoraDTO}.
	 * <p>
	 * Valida que el empleado y la sucursal existan, y que el empleado no tenga
	 * ya una caja registradora asignada (restricción 1:1 del supuesto).
	 *
	 * @param data El {@link CajaRegistradoraDTO} con la información de la nueva
	 *             caja.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si el empleado ya tiene una caja asignada,
	 *         {@code 2} si el empleado o la sucursal no existen,
	 *         {@code 4} si algún campo requerido está ausente.
	 */
	@Override
	public int create(CajaRegistradoraDTO data, String rol) {
		if (data.getIdEmpleado() == null || data.getIdSucursal() == null
				|| data.getNumeroCaja() == null) {
			return 4;
		}
		if (cajaRepo.existsByEmpleado_Id(data.getIdEmpleado())) {
			return 1;
		}
		Optional<Empleado> empleadoFound = empleadoRepo.findById(data.getIdEmpleado());
		Optional<Sucursal> sucursalFound = sucursalRepo.findById(data.getIdSucursal());
		if (!empleadoFound.isPresent() || !sucursalFound.isPresent()) {
			return 2;
		}
		CajaRegistradora entity = new CajaRegistradora();
		entity.setNumeroCaja(data.getNumeroCaja());
		entity.setEstado(data.getEstado() != null ? data.getEstado()
				: CajaRegistradora.Estado.ACTIVA);
		entity.setEmpleado(empleadoFound.get());
		entity.setSucursal(sucursalFound.get());
		cajaRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todas las cajas registradoras registradas en la base de datos.
	 *
	 * @return Una lista de {@link CajaRegistradoraDTO}. Retorna una lista vacía
	 *         si no hay cajas registradas.
	 */
	@Override
	public List<CajaRegistradoraDTO> getAll() {
		List<CajaRegistradora> entityList = cajaRepo.findAll();
		List<CajaRegistradoraDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> {
			CajaRegistradoraDTO dto = new CajaRegistradoraDTO();
			dto.setIdCaja(entity.getIdCaja());
			dto.setNumeroCaja(entity.getNumeroCaja());
			dto.setEstado(entity.getEstado());
			if (entity.getEmpleado() != null) {
				dto.setIdEmpleado(entity.getEmpleado().getId());
			}
			if (entity.getSucursal() != null) {
				dto.setIdSucursal(entity.getSucursal().getIdSucursal());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Obtiene una caja registradora por su ID.
	 *
	 * @param id El ID de la caja a buscar.
	 * @return Un {@link CajaRegistradoraDTO} con la información de la caja, o
	 *         {@code null} si no existe.
	 */
	public CajaRegistradoraDTO getById(Long id) {
		Optional<CajaRegistradora> found = cajaRepo.findById(id);
		if (found.isPresent()) {
			CajaRegistradora entity = found.get();
			CajaRegistradoraDTO dto = new CajaRegistradoraDTO();
			dto.setIdCaja(entity.getIdCaja());
			dto.setNumeroCaja(entity.getNumeroCaja());
			dto.setEstado(entity.getEstado());
			if (entity.getEmpleado() != null) {
				dto.setIdEmpleado(entity.getEmpleado().getId());
			}
			if (entity.getSucursal() != null) {
				dto.setIdSucursal(entity.getSucursal().getIdSucursal());
			}
			return dto;
		}
		return null;
	}

	/**
	 * Elimina una caja registradora de la base de datos por su ID.
	 *
	 * @param id El ID de la caja a eliminar.
	 * @return {@code 0} si la eliminación fue exitosa,
	 *         {@code 2} si no existe ninguna caja con ese ID.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<CajaRegistradora> found = cajaRepo.findById(id);
		if (found.isPresent()) {
			cajaRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos de una caja registradora existente por su ID.
	 * <p>
	 * Permite actualizar el número de caja, el estado, el empleado asignado
	 * y la sucursal. Valida que el nuevo empleado no tenga ya otra caja asignada.
	 *
	 * @param id      El ID de la caja a actualizar.
	 * @param newData El {@link CajaRegistradoraDTO} con los nuevos datos.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 1} si el nuevo empleado ya tiene otra caja asignada,
	 *         {@code 2} si no existe la caja, el empleado o la sucursal.
	 */
	@Override
	public int updateById(Long id, CajaRegistradoraDTO newData) {
		Optional<CajaRegistradora> found = cajaRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		CajaRegistradora temp = found.get();
		if (newData.getNumeroCaja() != null) {
			temp.setNumeroCaja(newData.getNumeroCaja());
		}
		if (newData.getEstado() != null) {
			temp.setEstado(newData.getEstado());
		}
		if (newData.getIdEmpleado() != null
				&& !newData.getIdEmpleado().equals(temp.getEmpleado().getId())) {
			if (cajaRepo.existsByEmpleado_Id(newData.getIdEmpleado())) {
				return 1;
			}
			Optional<Empleado> empleadoFound = empleadoRepo.findById(newData.getIdEmpleado());
			if (!empleadoFound.isPresent()) {
				return 2;
			}
			temp.setEmpleado(empleadoFound.get());
		}
		if (newData.getIdSucursal() != null) {
			Optional<Sucursal> sucursalFound = sucursalRepo.findById(newData.getIdSucursal());
			if (!sucursalFound.isPresent()) {
				return 2;
			}
			temp.setSucursal(sucursalFound.get());
		}
		cajaRepo.save(temp);
		return 0;
	}

	/**
	 * Cuenta el número total de cajas registradoras.
	 *
	 * @return El número total de cajas registradoras.
	 */
	@Override
	public long count() {
		return cajaRepo.count();
	}

	/**
	 * Verifica si existe una caja registradora con el ID especificado.
	 *
	 * @param id El ID de la caja a verificar.
	 * @return {@code true} si existe, {@code false} en caso contrario.
	 */
	@Override
	public boolean exist(Long id) {
		return cajaRepo.existsById(id);
	}

	/** No aplica para CajaRegistradora. Retorna entidad mapeada. */
	@Override
	public CajaRegistradora encrypt(CajaRegistradoraDTO data) {
		return modelMapper.map(data, CajaRegistradora.class);
	}

	/** No aplica para CajaRegistradora. Retorna {@code null}. */
	@Override
	public String decrypt(CajaRegistradoraDTO data) {
		return null;
	}

	/** No aplica para CajaRegistradora. Retorna {@code -1}. */
	@Override
	public int updatePassword(Long id, CajaRegistradoraDTO newData) {
		return -1;
	}

	/** No aplica para CajaRegistradora. Retorna {@code -1}. */
	@Override
	public int updateCorreo(Long id, CajaRegistradoraDTO newData) {
		return -1;
	}

	/** No aplica para CajaRegistradora. Retorna {@code -1}. */
	@Override
	public int updateRol(Long id, CajaRegistradoraDTO newData) {
		return -1;
	}

	/** No aplica para CajaRegistradora. Retorna {@code -1}. */
	@Override
	public int updateCode(Long id, CajaRegistradoraDTO newData) {
		return -1;
	}
}
