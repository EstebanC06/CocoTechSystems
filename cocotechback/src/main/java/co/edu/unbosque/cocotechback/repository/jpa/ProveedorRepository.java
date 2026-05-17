/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.Proveedor;
import co.edu.unbosque.cocotechback.model.Proveedor.NombreProveedor;

/**
 * Interfaz de repositorio para la entidad {@link Proveedor}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code proveedor} en la base de datos MySQL. Define además
 * métodos personalizados para buscar proveedores por su nombre del enum
 * {@link NombreProveedor} o por ciudad.
 */
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

	/**
	 * Busca un proveedor por su nombre exacto del enum.
	 *
	 * @param nombre El valor del enum {@link NombreProveedor} a buscar.
	 * @return Un {@link Optional} con el proveedor encontrado, o vacío si no
	 *         existe ningún proveedor con ese nombre.
	 */
	public Optional<Proveedor> findByNombre(NombreProveedor nombre);

	/**
	 * Busca todos los proveedores ubicados en una ciudad específica.
	 *
	 * @param ciudad La ciudad por la cual filtrar los proveedores.
	 * @return Una lista de proveedores ubicados en la ciudad indicada. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<Proveedor> findByCiudad(String ciudad);

	/**
	 * Verifica si ya existe un proveedor registrado con el nombre proporcionado.
	 *
	 * @param nombre El valor del enum {@link NombreProveedor} a verificar.
	 * @return {@code true} si ya existe un proveedor con ese nombre,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByNombre(NombreProveedor nombre);

	/**
	 * Elimina un proveedor de la base de datos por su nombre.
	 *
	 * @param nombre El valor del enum {@link NombreProveedor} a eliminar.
	 */
	public void deleteByNombre(NombreProveedor nombre);
}
