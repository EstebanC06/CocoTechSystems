/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.Sucursal;

/**
 * Interfaz de repositorio para la entidad {@link Sucursal}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code sucursal} en la base de datos MySQL. Además, define
 * métodos de consulta personalizados para buscar sucursales por nombre y
 * ciudad.
 * <p>
 * Spring Data JPA genera la implementación de todos estos métodos
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

	/**
	 * Busca una sucursal por su nombre exacto.
	 *
	 * @param nombre El nombre de la sucursal a buscar.
	 * @return Un {@link Optional} con la sucursal encontrada, o vacío si no existe
	 *         ninguna sucursal con ese nombre.
	 */
	public Optional<Sucursal> findByNombre(String nombre);

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
	 * @param nombre El nombre a verificar.
	 * @return {@code true} si ya existe una sucursal con ese nombre,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByNombre(String nombre);

	/**
	 * Elimina una sucursal de la base de datos por su nombre.
	 *
	 * @param nombre El nombre de la sucursal a eliminar.
	 */
	public void deleteByNombre(String nombre);
}
