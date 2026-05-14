/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.cocotechback.model.DireccionCliente;

/**
 * Interfaz de repositorio para la entidad {@link DireccionCliente}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar.
 * Define métodos personalizados para listar las direcciones de un cliente,
 * encontrar su dirección predeterminada y desmarcar masivamente la
 * predeterminada antes de asignar una nueva.
 */
public interface DireccionClienteRepository extends JpaRepository<DireccionCliente, Long> {

	/**
	 * Obtiene todas las direcciones de un cliente.
	 *
	 * @param idCliente ID del cliente.
	 * @return Lista de direcciones del cliente.
	 */
	List<DireccionCliente> findByCliente_Id(Long idCliente);

	/**
	 * Obtiene la dirección predeterminada de un cliente, si existe.
	 *
	 * @param idCliente ID del cliente.
	 * @return Optional con la dirección predeterminada.
	 */
	Optional<DireccionCliente> findByCliente_IdAndPredeterminadaTrue(Long idCliente);

	/**
	 * Desmarca todas las direcciones predeterminadas de un cliente.
	 * Útil antes de asignar una nueva predeterminada.
	 *
	 * @param idCliente ID del cliente.
	 */
	@Modifying
	@Query("UPDATE DireccionCliente d SET d.predeterminada = false "
			+ "WHERE d.cliente.id = :idCliente AND d.predeterminada = true")
	void desmarcarPredeterminadas(@Param("idCliente") Long idCliente);
}