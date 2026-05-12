/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.Cliente;

/**
 * Interfaz de repositorio para la entidad {@link Cliente}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * (Crear, Leer, Actualizar, Eliminar) sobre la tabla {@code cliente} en la
 * base de datos MySQL. Además, define métodos de consulta personalizados para
 * buscar y eliminar clientes por correo electrónico, ciudad y barrio.
 * <p>
 * Spring Data JPA genera la implementación de todos estos métodos
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	/**
	 * Busca un cliente por su correo electrónico.
	 * <p>
	 * Se utiliza principalmente en el proceso de autenticación para cargar el
	 * {@code UserDetails} del cliente mediante Spring Security.
	 *
	 * @param correo El correo electrónico del cliente a buscar.
	 * @return Un {@link Optional} con el cliente encontrado, o vacío si no existe
	 *         ningún cliente con ese correo.
	 */
	public Optional<Cliente> findByCorreo(String correo);

	/**
	 * Busca todos los clientes que residan en una ciudad específica.
	 *
	 * @param ciudad La ciudad por la cual filtrar los clientes.
	 * @return Una lista de clientes que residen en la ciudad indicada.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Cliente> findByCiudad(String ciudad);

	/**
	 * Busca todos los clientes que residan en un barrio específico.
	 *
	 * @param barrio El barrio por el cual filtrar los clientes.
	 * @return Una lista de clientes que residen en el barrio indicado.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Cliente> findByBarrio(String barrio);

	/**
	 * Busca todos los clientes que coincidan con el nombre o el apellido
	 * proporcionado (ignorando mayúsculas y minúsculas).
	 *
	 * @param nombres   El nombre a buscar.
	 * @param apellidos El apellido a buscar.
	 * @return Una lista de clientes que coincidan con el nombre o apellido.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Cliente> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
			String nombres, String apellidos);

	/**
	 * Verifica si ya existe un cliente registrado con el correo electrónico
	 * proporcionado.
	 *
	 * @param correo El correo electrónico a verificar.
	 * @return {@code true} si ya existe un cliente con ese correo,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByCorreo(String correo);

	/**
	 * Elimina un cliente de la base de datos por su correo electrónico.
	 *
	 * @param correo El correo electrónico del cliente a eliminar.
	 */
	public void deleteByCorreo(String correo);
}
