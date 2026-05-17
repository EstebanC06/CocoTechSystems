/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.Categoria;
import co.edu.unbosque.cocotechback.model.Categoria.NombreCategoria;

/**
 * Interfaz de repositorio para la entidad {@link Categoria}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code categoria} en la base de datos MySQL. Define además
 * métodos personalizados para buscar categorías por su nombre del enum
 * {@link NombreCategoria}.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	/**
	 * Busca una categoría por su nombre exacto del enum.
	 *
	 * @param nombre El valor del enum {@link NombreCategoria} a buscar.
	 * @return Un {@link Optional} con la categoría encontrada, o vacío si no
	 *         existe ninguna categoría con ese nombre.
	 */
	public Optional<Categoria> findByNombre(NombreCategoria nombre);

	/**
	 * Verifica si ya existe una categoría registrada con el nombre proporcionado.
	 *
	 * @param nombre El valor del enum {@link NombreCategoria} a verificar.
	 * @return {@code true} si ya existe una categoría con ese nombre,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByNombre(NombreCategoria nombre);

	/**
	 * Elimina una categoría de la base de datos por su nombre.
	 *
	 * @param nombre El valor del enum {@link NombreCategoria} a eliminar.
	 */
	public void deleteByNombre(NombreCategoria nombre);
}
