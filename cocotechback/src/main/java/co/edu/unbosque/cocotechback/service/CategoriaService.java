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

import co.edu.unbosque.cocotechback.dto.CategoriaDTO;
import co.edu.unbosque.cocotechback.model.Categoria;
import co.edu.unbosque.cocotechback.model.Categoria.NombreCategoria;
import co.edu.unbosque.cocotechback.repository.jpa.CategoriaRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Categoria}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de categorías de productos.
 * <p>
 * El nombre de categoría se persiste como enum ({@link NombreCategoria}) en
 * la entidad, pero en el DTO viaja como String para simplificar la
 * serialización JSON. La conversión segura entre ambos se hace en este
 * servicio.
 */
@Service
public class CategoriaService implements CRUDOperation<CategoriaDTO, Categoria> {

	/**
	 * Repositorio para la gestión de la entidad {@link Categoria}.
	 */
	@Autowired
	private CategoriaRepository categoriaRepo;

	/**
	 * Constructor por defecto de {@code CategoriaService}.
	 */
	public CategoriaService() {
	}

	/**
	 * Convierte de forma segura un String al enum {@link NombreCategoria}.
	 *
	 * @param nombre Cadena con el nombre.
	 * @return El valor del enum, o {@code null} si la cadena no corresponde a
	 *         ninguno de los valores permitidos.
	 */
	private NombreCategoria parseNombre(String nombre) {
		if (nombre == null || nombre.isEmpty()) {
			return null;
		}
		try {
			return NombreCategoria.valueOf(nombre);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Convierte una entidad {@link Categoria} a su DTO equivalente.
	 *
	 * @param entity Entidad a convertir.
	 * @return DTO con los datos de la entidad.
	 */
	private CategoriaDTO toDTO(Categoria entity) {
		CategoriaDTO dto = new CategoriaDTO();
		dto.setIdCategoria(entity.getIdCategoria());
		dto.setNombre(entity.getNombre() != null ? entity.getNombre().name() : null);
		dto.setDescripcion(entity.getDescripcion());
		dto.setImagenUrl(entity.getImagenUrl());
		dto.setIcono(entity.getIcono());
		return dto;
	}

	/**
	 * Crea una nueva categoría en la base de datos.
	 * <p>
	 * Valida que el nombre esté presente y que sea un valor válido del enum
	 * {@link NombreCategoria}, y que no exista ya una categoría con ese nombre.
	 *
	 * @param data El {@link CategoriaDTO} con la información de la nueva categoría.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si ya existe una categoría con ese nombre,
	 *         {@code 4} si el nombre está ausente o no es un valor válido.
	 */
	@Override
	public int create(CategoriaDTO data, String rol) {
		NombreCategoria nombreEnum = parseNombre(data.getNombre());
		if (nombreEnum == null) {
			return 4;
		}
		if (categoriaRepo.existsByNombre(nombreEnum)) {
			return 1;
		}
		Categoria entity = new Categoria();
		entity.setNombre(nombreEnum);
		entity.setDescripcion(data.getDescripcion());
		entity.setImagenUrl(data.getImagenUrl());
		entity.setIcono(data.getIcono());
		categoriaRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todas las categorías registradas en la base de datos.
	 *
	 * @return Una lista de {@link CategoriaDTO}. Retorna una lista vacía si no
	 *         hay categorías.
	 */
	@Override
	public List<CategoriaDTO> getAll() {
		List<Categoria> entityList = categoriaRepo.findAll();
		List<CategoriaDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(toDTO(entity)));
		return dtoList;
	}

	/**
	 * Obtiene una categoría por su ID.
	 *
	 * @param id El ID de la categoría a buscar.
	 * @return Un {@link CategoriaDTO} o {@code null} si no existe.
	 */
	public CategoriaDTO getById(Long id) {
		Optional<Categoria> found = categoriaRepo.findById(id);
		return found.isPresent() ? toDTO(found.get()) : null;
	}

	/**
	 * Elimina una categoría por su ID.
	 *
	 * @param id El ID de la categoría a eliminar.
	 * @return {@code 0} si fue exitosa, {@code 2} si no existe.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Categoria> found = categoriaRepo.findById(id);
		if (found.isPresent()) {
			categoriaRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos de una categoría existente por su ID.
	 *
	 * @param id      El ID de la categoría a actualizar.
	 * @param newData El {@link CategoriaDTO} con los nuevos datos.
	 * @return {@code 0} si fue exitosa,
	 *         {@code 1} si el nuevo nombre ya está en uso,
	 *         {@code 2} si no existe la categoría,
	 *         {@code 4} si el nuevo nombre no es un valor válido del enum.
	 */
	@Override
	public int updateById(Long id, CategoriaDTO newData) {
		Optional<Categoria> found = categoriaRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Categoria temp = found.get();
		if (newData.getNombre() != null) {
			NombreCategoria nuevoNombre = parseNombre(newData.getNombre());
			if (nuevoNombre == null) {
				return 4;
			}
			if (!nuevoNombre.equals(temp.getNombre())) {
				if (categoriaRepo.existsByNombre(nuevoNombre)) {
					return 1;
				}
				temp.setNombre(nuevoNombre);
			}
		}
		if (newData.getDescripcion() != null) {
			temp.setDescripcion(newData.getDescripcion());
		}
		if (newData.getImagenUrl() != null) {
			temp.setImagenUrl(newData.getImagenUrl());
		}
		if (newData.getIcono() != null) {
			temp.setIcono(newData.getIcono());
		}
		categoriaRepo.save(temp);
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public long count() {
		return categoriaRepo.count();
	}

	/** {@inheritDoc} */
	@Override
	public boolean exist(Long id) {
		return categoriaRepo.existsById(id);
	}

	/** No aplica para Categoria. */
	@Override
	public Categoria encrypt(CategoriaDTO data) {
		Categoria entity = new Categoria();
		entity.setNombre(parseNombre(data.getNombre()));
		entity.setDescripcion(data.getDescripcion());
		entity.setImagenUrl(data.getImagenUrl());
		entity.setIcono(data.getIcono());
		return entity;
	}

	/** No aplica para Categoria. */
	@Override
	public String decrypt(CategoriaDTO data) {
		return null;
	}

	/** No aplica para Categoria. Retorna {@code -1}. */
	@Override
	public int updatePassword(Long id, CategoriaDTO newData) {
		return -1;
	}

	/** No aplica para Categoria. Retorna {@code -1}. */
	@Override
	public int updateCorreo(Long id, CategoriaDTO newData) {
		return -1;
	}

	/** No aplica para Categoria. Retorna {@code -1}. */
	@Override
	public int updateRol(Long id, CategoriaDTO newData) {
		return -1;
	}

	/** No aplica para Categoria. Retorna {@code -1}. */
	@Override
	public int updateCode(Long id, CategoriaDTO newData) {
		return -1;
	}
}
