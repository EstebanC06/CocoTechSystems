/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.Proveedor;

/**
 * Interfaz de repositorio para la entidad {@link Proveedor}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code proveedor} en la base de datos MySQL. Además, define
 * métodos de consulta personalizados para buscar proveedores por nombre y
 * ciudad.
 * <p>
 * Spring Data JPA genera la implementación de todos estos métodos
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

	/**
	 * Busca un proveedor por su nombre o razón social exacta.
	 *
	 * @param nombre El nombre del proveedor a buscar.
	 * @return Un {@link Optional} con el proveedor encontrado, o vacío si no
	 *         existe ningún proveedor con ese nombre.
	 */
	public Optional<Proveedor> findByNombre(String nombre);

	/**
	 * Busca todos los proveedores ubicados en una ciudad específica.
	 *
	 * @param ciudad La ciudad por la cual filtrar los proveedores.
	 * @return Una lista de proveedores ubicados en la ciudad indicada. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<Proveedor> findByCiudad(String ciudad);

	/**
	 * Busca todos los proveedores cuyo nombre contenga la cadena proporcionada,
	 * sin distinguir entre mayúsculas y minúsculas.
	 *
	 * @param nombre La cadena de búsqueda parcial del nombre del proveedor.
	 * @return Una lista de proveedores cuyo nombre contenga la cadena indicada.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

	/**
	 * Verifica si ya existe un proveedor registrado con el nombre proporcionado.
	 *
	 * @param nombre El nombre a verificar.
	 * @return {@code true} si ya existe un proveedor con ese nombre,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByNombre(String nombre);

	/**
	 * Elimina un proveedor de la base de datos por su nombre.
	 *
	 * @param nombre El nombre del proveedor a eliminar.
	 */
	public void deleteByNombre(String nombre);
}
