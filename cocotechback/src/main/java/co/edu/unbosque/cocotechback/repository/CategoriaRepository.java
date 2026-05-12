/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.Categoria;

/**
 * Interfaz de repositorio para la entidad {@link Categoria}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code categoria} en la base de datos MySQL. Además, define
 * métodos de consulta personalizados para buscar categorías por nombre.
 * <p>
 * Spring Data JPA genera la implementación de todos estos métodos
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	/**
	 * Busca una categoría por su nombre exacto.
	 *
	 * @param nombre El nombre de la categoría a buscar.
	 * @return Un {@link Optional} con la categoría encontrada, o vacío si no
	 *         existe ninguna categoría con ese nombre.
	 */
	public Optional<Categoria> findByNombre(String nombre);

	/**
	 * Busca todas las categorías cuyo nombre contenga la cadena proporcionada,
	 * sin distinguir entre mayúsculas y minúsculas.
	 *
	 * @param nombre La cadena de búsqueda parcial del nombre de la categoría.
	 * @return Una lista de categorías cuyo nombre contenga la cadena indicada.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Categoria> findByNombreContainingIgnoreCase(String nombre);

	/**
	 * Verifica si ya existe una categoría registrada con el nombre proporcionado.
	 *
	 * @param nombre El nombre a verificar.
	 * @return {@code true} si ya existe una categoría con ese nombre,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByNombre(String nombre);

	/**
	 * Elimina una categoría de la base de datos por su nombre.
	 *
	 * @param nombre El nombre de la categoría a eliminar.
	 */
	public void deleteByNombre(String nombre);
}
