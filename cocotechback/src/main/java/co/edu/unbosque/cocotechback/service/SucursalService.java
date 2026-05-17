/**
 * Paquete que contiene las clases de Servicio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.cocotechback.dto.SucursalDTO;
import co.edu.unbosque.cocotechback.model.Sucursal;
import co.edu.unbosque.cocotechback.model.Sucursal.NombreSucursal;
import co.edu.unbosque.cocotechback.repository.jpa.SucursalRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Sucursal}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de sucursales del
 * supermercado.
 * <p>
 * El nombre se persiste como enum ({@link NombreSucursal}) en la entidad, pero
 * en el DTO viaja como String para simplificar la serialización JSON. La
 * conversión segura entre ambos formatos se hace en este servicio.
 */
@Service
public class SucursalService implements CRUDOperation<SucursalDTO, Sucursal> {

	/**
	 * Repositorio para la gestión de la entidad {@link Sucursal} en la base de
	 * datos.
	 */
	@Autowired
	private SucursalRepository sucursalRepo;

	/**
	 * Constructor por defecto de {@code SucursalService}.
	 */
	public SucursalService() {
	}

	/**
	 * Convierte de forma segura un String al enum {@link NombreSucursal}.
	 *
	 * @param nombre Cadena con el nombre.
	 * @return El valor del enum, o {@code null} si la cadena no corresponde a
	 *         ninguno de los valores permitidos.
	 */
	private NombreSucursal parseNombre(String nombre) {
		if (nombre == null || nombre.isEmpty()) {
			return null;
		}
		try {
			return NombreSucursal.valueOf(nombre);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Convierte una entidad {@link Sucursal} a su DTO equivalente.
	 *
	 * @param entity Entidad a convertir.
	 * @return DTO con los datos de la entidad.
	 */
	private SucursalDTO toDTO(Sucursal entity) {
		SucursalDTO dto = new SucursalDTO();
		dto.setIdSucursal(entity.getIdSucursal());
		dto.setNombre(entity.getNombre() != null ? entity.getNombre().name() : null);
		dto.setTelefonoContacto(entity.getTelefonoContacto());
		dto.setCiudad(entity.getCiudad());
		dto.setBarrio(entity.getBarrio());
		dto.setDireccion(entity.getDireccion());
		return dto;
	}

	/**
	 * Crea una nueva sucursal en la base de datos a partir de un
	 * {@link SucursalDTO}.
	 * <p>
	 * Valida que el nombre sea un valor válido del enum {@link NombreSucursal},
	 * que la ciudad esté presente y que no exista ya una sucursal con el mismo
	 * nombre.
	 *
	 * @param data El {@link SucursalDTO} con la información de la nueva sucursal.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si ya existe una sucursal con ese nombre,
	 *         {@code 4} si el nombre es inválido o la ciudad está ausente.
	 */
	@Override
	public int create(SucursalDTO data, String rol) {
		NombreSucursal nombreEnum = parseNombre(data.getNombre());
		if (nombreEnum == null || data.getCiudad() == null || data.getCiudad().isEmpty()) {
			return 4;
		}
		if (sucursalRepo.existsByNombre(nombreEnum)) {
			return 1;
		}
		Sucursal entity = new Sucursal();
		entity.setNombre(nombreEnum);
		entity.setTelefonoContacto(data.getTelefonoContacto());
		entity.setCiudad(data.getCiudad());
		entity.setBarrio(data.getBarrio());
		entity.setDireccion(data.getDireccion());
		sucursalRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todas las sucursales registradas en la base de datos.
	 *
	 * @return Una lista de {@link SucursalDTO}. Retorna una lista vacía si no hay
	 *         sucursales registradas.
	 */
	@Override
	public List<SucursalDTO> getAll() {
		List<Sucursal> entityList = sucursalRepo.findAll();
		List<SucursalDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(toDTO(entity)));
		return dtoList;
	}

	/**
	 * Obtiene una sucursal por su ID.
	 *
	 * @param id El ID de la sucursal a buscar.
	 * @return Un {@link SucursalDTO} con la información de la sucursal, o
	 *         {@code null} si no existe.
	 */
	public SucursalDTO getById(Long id) {
		Optional<Sucursal> found = sucursalRepo.findById(id);
		return found.isPresent() ? toDTO(found.get()) : null;
	}

	/**
	 * Elimina una sucursal de la base de datos por su ID.
	 *
	 * @param id El ID de la sucursal a eliminar.
	 * @return {@code 0} si la eliminación fue exitosa,
	 *         {@code 2} si no existe ninguna sucursal con ese ID.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Sucursal> found = sucursalRepo.findById(id);
		if (found.isPresent()) {
			sucursalRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos de una sucursal existente por su ID.
	 * <p>
	 * Solo actualiza los campos no nulos del DTO recibido.
	 *
	 * @param id      El ID de la sucursal a actualizar.
	 * @param newData El {@link SucursalDTO} con los nuevos datos.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 1} si el nuevo nombre ya está en uso,
	 *         {@code 2} si no existe ninguna sucursal con ese ID,
	 *         {@code 4} si el nuevo nombre no es un valor válido del enum.
	 */
	@Override
	public int updateById(Long id, SucursalDTO newData) {
		Optional<Sucursal> found = sucursalRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Sucursal temp = found.get();
		if (newData.getNombre() != null) {
			NombreSucursal nuevoNombre = parseNombre(newData.getNombre());
			if (nuevoNombre == null) {
				return 4;
			}
			if (!nuevoNombre.equals(temp.getNombre())) {
				if (sucursalRepo.existsByNombre(nuevoNombre)) {
					return 1;
				}
				temp.setNombre(nuevoNombre);
			}
		}
		if (newData.getTelefonoContacto() != null) {
			temp.setTelefonoContacto(newData.getTelefonoContacto());
		}
		if (newData.getCiudad() != null) {
			temp.setCiudad(newData.getCiudad());
		}
		if (newData.getBarrio() != null) {
			temp.setBarrio(newData.getBarrio());
		}
		if (newData.getDireccion() != null) {
			temp.setDireccion(newData.getDireccion());
		}
		sucursalRepo.save(temp);
		return 0;
	}

	/**
	 * Cuenta el número total de sucursales registradas.
	 *
	 * @return El número total de sucursales.
	 */
	@Override
	public long count() {
		return sucursalRepo.count();
	}

	/**
	 * Verifica si existe una sucursal con el ID especificado.
	 *
	 * @param id El ID de la sucursal a verificar.
	 * @return {@code true} si existe, {@code false} en caso contrario.
	 */
	@Override
	public boolean exist(Long id) {
		return sucursalRepo.existsById(id);
	}

	/** No aplica para Sucursal. */
	@Override
	public Sucursal encrypt(SucursalDTO data) {
		Sucursal entity = new Sucursal();
		entity.setNombre(parseNombre(data.getNombre()));
		entity.setTelefonoContacto(data.getTelefonoContacto());
		entity.setCiudad(data.getCiudad());
		entity.setBarrio(data.getBarrio());
		entity.setDireccion(data.getDireccion());
		return entity;
	}

	/** No aplica para Sucursal. */
	@Override
	public String decrypt(SucursalDTO data) {
		return null;
	}

	/** No aplica para Sucursal. Retorna {@code -1}. */
	@Override
	public int updatePassword(Long id, SucursalDTO newData) {
		return -1;
	}

	/** No aplica para Sucursal. Retorna {@code -1}. */
	@Override
	public int updateCorreo(Long id, SucursalDTO newData) {
		return -1;
	}

	/** No aplica para Sucursal. Retorna {@code -1}. */
	@Override
	public int updateRol(Long id, SucursalDTO newData) {
		return -1;
	}

	/** No aplica para Sucursal. Retorna {@code -1}. */
	@Override
	public int updateCode(Long id, SucursalDTO newData) {
		return -1;
	}
}
