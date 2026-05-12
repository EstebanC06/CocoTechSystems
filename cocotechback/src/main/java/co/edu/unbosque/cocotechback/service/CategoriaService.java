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

import co.edu.unbosque.cocotechback.dto.CategoriaDTO;
import co.edu.unbosque.cocotechback.model.Categoria;
import co.edu.unbosque.cocotechback.repository.CategoriaRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Categoria}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de categorías de productos.
 */
@Service
public class CategoriaService implements CRUDOperation<CategoriaDTO, Categoria> {

	/**
	 * Repositorio para la gestión de la entidad {@link Categoria}.
	 */
	@Autowired
	private CategoriaRepository categoriaRepo;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Constructor por defecto de {@code CategoriaService}.
	 */
	public CategoriaService() {
	}

	/**
	 * Crea una nueva categoría en la base de datos.
	 * <p>
	 * Valida que el nombre esté presente y que no exista ya una categoría con
	 * ese nombre.
	 *
	 * @param data El {@link CategoriaDTO} con la información de la nueva categoría.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si ya existe una categoría con ese nombre,
	 *         {@code 4} si el nombre está ausente.
	 */
	@Override
	public int create(CategoriaDTO data, String rol) {
		if (data.getNombre() == null || data.getNombre().isEmpty()) {
			return 4;
		}
		if (categoriaRepo.existsByNombre(data.getNombre())) {
			return 1;
		}
		Categoria entity = modelMapper.map(data, Categoria.class);
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
		entityList.forEach(entity -> dtoList.add(modelMapper.map(entity, CategoriaDTO.class)));
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
		return found.isPresent() ? modelMapper.map(found.get(), CategoriaDTO.class) : null;
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
	 *         {@code 2} si no existe la categoría.
	 */
	@Override
	public int updateById(Long id, CategoriaDTO newData) {
		Optional<Categoria> found = categoriaRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Categoria temp = found.get();
		if (newData.getNombre() != null && !newData.getNombre().equals(temp.getNombre())) {
			if (categoriaRepo.existsByNombre(newData.getNombre())) {
				return 1;
			}
			temp.setNombre(newData.getNombre());
		}
		if (newData.getDescripcion() != null) {
			temp.setDescripcion(newData.getDescripcion());
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
		return modelMapper.map(data, Categoria.class);
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
