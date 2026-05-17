/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.Sucursal;
import co.edu.unbosque.cocotechback.model.Sucursal.NombreSucursal;

/**
 * Interfaz de repositorio para la entidad {@link Sucursal}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code sucursal} en la base de datos MySQL. Define además
 * métodos personalizados para buscar sucursales por su nombre del enum
 * {@link NombreSucursal} o por ciudad/barrio.
 */
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

	/**
	 * Busca una sucursal por su nombre exacto del enum.
	 *
	 * @param nombre El valor del enum {@link NombreSucursal} a buscar.
	 * @return Un {@link Optional} con la sucursal encontrada, o vacío si no existe
	 *         ninguna sucursal con ese nombre.
	 */
	public Optional<Sucursal> findByNombre(NombreSucursal nombre);

	/**
	 * Busca todas las sucursales ubicadas en una ciudad específica.
	 *
	 * @param ciudad La ciudad por la cual filtrar las sucursales.
	 * @return Una lista de sucursales ubicadas en la ciudad indicada. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<Sucursal> findByCiudad(String ciudad);

	/**
	 * Busca todas las sucursales ubicadas en un barrio específico.
	 *
	 * @param barrio El barrio por el cual filtrar las sucursales.
	 * @return Una lista de sucursales ubicadas en el barrio indicado. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<Sucursal> findByBarrio(String barrio);

	/**
	 * Verifica si ya existe una sucursal registrada con el nombre proporcionado.
	 *
	 * @param nombre El valor del enum {@link NombreSucursal} a verificar.
	 * @return {@code true} si ya existe una sucursal con ese nombre,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByNombre(NombreSucursal nombre);

	/**
	 * Elimina una sucursal de la base de datos por su nombre.
	 *
	 * @param nombre El valor del enum {@link NombreSucursal} a eliminar.
	 */
	public void deleteByNombre(NombreSucursal nombre);
}
