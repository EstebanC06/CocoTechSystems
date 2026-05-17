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

import co.edu.unbosque.cocotechback.dto.ProveedorDTO;
import co.edu.unbosque.cocotechback.model.Proveedor;
import co.edu.unbosque.cocotechback.model.Proveedor.NombreProveedor;
import co.edu.unbosque.cocotechback.repository.jpa.ProveedorRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Proveedor}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de proveedores del
 * supermercado.
 * <p>
 * El nombre se persiste como enum ({@link NombreProveedor}) en la entidad, pero
 * en el DTO viaja como String para simplificar la serialización JSON. La
 * conversión segura entre ambos formatos se hace en este servicio.
 */
@Service
public class ProveedorService implements CRUDOperation<ProveedorDTO, Proveedor> {

	/**
	 * Repositorio para la gestión de la entidad {@link Proveedor}.
	 */
	@Autowired
	private ProveedorRepository proveedorRepo;

	/**
	 * Constructor por defecto de {@code ProveedorService}.
	 */
	public ProveedorService() {
	}

	/**
	 * Convierte de forma segura un String al enum {@link NombreProveedor}.
	 *
	 * @param nombre Cadena con el nombre.
	 * @return El valor del enum, o {@code null} si la cadena no corresponde a
	 *         ninguno de los valores permitidos.
	 */
	private NombreProveedor parseNombre(String nombre) {
		if (nombre == null || nombre.isEmpty()) {
			return null;
		}
		try {
			return NombreProveedor.valueOf(nombre);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Convierte una entidad {@link Proveedor} a su DTO equivalente.
	 *
	 * @param entity Entidad a convertir.
	 * @return DTO con los datos de la entidad.
	 */
	private ProveedorDTO toDTO(Proveedor entity) {
		ProveedorDTO dto = new ProveedorDTO();
		dto.setIdProveedor(entity.getIdProveedor());
		dto.setNombre(entity.getNombre() != null ? entity.getNombre().name() : null);
		dto.setTelefono(entity.getTelefono());
		dto.setDireccion(entity.getDireccion());
		dto.setBarrio(entity.getBarrio());
		dto.setCiudad(entity.getCiudad());
		return dto;
	}

	/**
	 * Crea un nuevo proveedor en la base de datos.
	 * <p>
	 * Valida que el nombre sea un valor válido del enum {@link NombreProveedor}
	 * y que no exista ya un proveedor con ese nombre.
	 *
	 * @param data El {@link ProveedorDTO} con la información del nuevo proveedor.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si ya existe un proveedor con ese nombre,
	 *         {@code 4} si el nombre es inválido o no permitido.
	 */
	@Override
	public int create(ProveedorDTO data, String rol) {
		NombreProveedor nombreEnum = parseNombre(data.getNombre());
		if (nombreEnum == null) {
			return 4;
		}
		if (proveedorRepo.existsByNombre(nombreEnum)) {
			return 1;
		}
		Proveedor entity = new Proveedor();
		entity.setNombre(nombreEnum);
		entity.setTelefono(data.getTelefono());
		entity.setDireccion(data.getDireccion());
		entity.setBarrio(data.getBarrio());
		entity.setCiudad(data.getCiudad());
		proveedorRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todos los proveedores registrados en la base de datos.
	 *
	 * @return Una lista de {@link ProveedorDTO}. Retorna una lista vacía si no
	 *         hay proveedores.
	 */
	@Override
	public List<ProveedorDTO> getAll() {
		List<Proveedor> entityList = proveedorRepo.findAll();
		List<ProveedorDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(toDTO(entity)));
		return dtoList;
	}

	/**
	 * Obtiene un proveedor por su ID.
	 *
	 * @param id El ID del proveedor a buscar.
	 * @return Un {@link ProveedorDTO} o {@code null} si no existe.
	 */
	public ProveedorDTO getById(Long id) {
		Optional<Proveedor> found = proveedorRepo.findById(id);
		return found.isPresent() ? toDTO(found.get()) : null;
	}

	/**
	 * Elimina un proveedor por su ID.
	 *
	 * @param id El ID del proveedor a eliminar.
	 * @return {@code 0} si fue exitosa, {@code 2} si no existe.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Proveedor> found = proveedorRepo.findById(id);
		if (found.isPresent()) {
			proveedorRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos de un proveedor existente por su ID.
	 *
	 * @param id      El ID del proveedor a actualizar.
	 * @param newData El {@link ProveedorDTO} con los nuevos datos.
	 * @return {@code 0} si fue exitosa,
	 *         {@code 1} si el nuevo nombre ya está en uso,
	 *         {@code 2} si no existe el proveedor,
	 *         {@code 4} si el nuevo nombre no es un valor válido del enum.
	 */
	@Override
	public int updateById(Long id, ProveedorDTO newData) {
		Optional<Proveedor> found = proveedorRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Proveedor temp = found.get();
		if (newData.getNombre() != null) {
			NombreProveedor nuevoNombre = parseNombre(newData.getNombre());
			if (nuevoNombre == null) {
				return 4;
			}
			if (!nuevoNombre.equals(temp.getNombre())) {
				if (proveedorRepo.existsByNombre(nuevoNombre)) {
					return 1;
				}
				temp.setNombre(nuevoNombre);
			}
		}
		if (newData.getTelefono() != null) {
			temp.setTelefono(newData.getTelefono());
		}
		if (newData.getDireccion() != null) {
			temp.setDireccion(newData.getDireccion());
		}
		if (newData.getBarrio() != null) {
			temp.setBarrio(newData.getBarrio());
		}
		if (newData.getCiudad() != null) {
			temp.setCiudad(newData.getCiudad());
		}
		proveedorRepo.save(temp);
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public long count() {
		return proveedorRepo.count();
	}

	/** {@inheritDoc} */
	@Override
	public boolean exist(Long id) {
		return proveedorRepo.existsById(id);
	}

	/** No aplica para Proveedor. */
	@Override
	public Proveedor encrypt(ProveedorDTO data) {
		Proveedor entity = new Proveedor();
		entity.setNombre(parseNombre(data.getNombre()));
		entity.setTelefono(data.getTelefono());
		entity.setDireccion(data.getDireccion());
		entity.setBarrio(data.getBarrio());
		entity.setCiudad(data.getCiudad());
		return entity;
	}

	/** No aplica para Proveedor. */
	@Override
	public String decrypt(ProveedorDTO data) {
		return null;
	}

	/** No aplica para Proveedor. Retorna {@code -1}. */
	@Override
	public int updatePassword(Long id, ProveedorDTO newData) {
		return -1;
	}

	/** No aplica para Proveedor. Retorna {@code -1}. */
	@Override
	public int updateCorreo(Long id, ProveedorDTO newData) {
		return -1;
	}

	/** No aplica para Proveedor. Retorna {@code -1}. */
	@Override
	public int updateRol(Long id, ProveedorDTO newData) {
		return -1;
	}

	/** No aplica para Proveedor. Retorna {@code -1}. */
	@Override
	public int updateCode(Long id, ProveedorDTO newData) {
		return -1;
	}
}
