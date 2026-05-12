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

import co.edu.unbosque.cocotechback.dto.ProveedorDTO;
import co.edu.unbosque.cocotechback.model.Proveedor;
import co.edu.unbosque.cocotechback.repository.jpa.ProveedorRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Proveedor}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de proveedores del
 * supermercado.
 */
@Service
public class ProveedorService implements CRUDOperation<ProveedorDTO, Proveedor> {

	/**
	 * Repositorio para la gestión de la entidad {@link Proveedor}.
	 */
	@Autowired
	private ProveedorRepository proveedorRepo;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Constructor por defecto de {@code ProveedorService}.
	 */
	public ProveedorService() {
	}

	/**
	 * Crea un nuevo proveedor en la base de datos.
	 * <p>
	 * Valida que el nombre esté presente y que no exista ya un proveedor con
	 * ese nombre.
	 *
	 * @param data El {@link ProveedorDTO} con la información del nuevo proveedor.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si ya existe un proveedor con ese nombre,
	 *         {@code 4} si el nombre está ausente.
	 */
	@Override
	public int create(ProveedorDTO data, String rol) {
		if (data.getNombre() == null || data.getNombre().isEmpty()) {
			return 4;
		}
		if (proveedorRepo.existsByNombre(data.getNombre())) {
			return 1;
		}
		Proveedor entity = modelMapper.map(data, Proveedor.class);
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
		entityList.forEach(entity -> dtoList.add(modelMapper.map(entity, ProveedorDTO.class)));
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
		return found.isPresent() ? modelMapper.map(found.get(), ProveedorDTO.class) : null;
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
	 *         {@code 2} si no existe el proveedor.
	 */
	@Override
	public int updateById(Long id, ProveedorDTO newData) {
		Optional<Proveedor> found = proveedorRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Proveedor temp = found.get();
		if (newData.getNombre() != null && !newData.getNombre().equals(temp.getNombre())) {
			if (proveedorRepo.existsByNombre(newData.getNombre())) {
				return 1;
			}
			temp.setNombre(newData.getNombre());
		}
		if (newData.getTelefono() != null) {
			temp.setTelefono(newData.getTelefono());
		}
		if (newData.getCalle() != null) {
			temp.setCalle(newData.getCalle());
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
		return modelMapper.map(data, Proveedor.class);
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
