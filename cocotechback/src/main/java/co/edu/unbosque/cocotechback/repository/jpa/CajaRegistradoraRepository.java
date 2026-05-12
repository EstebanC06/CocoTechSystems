/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.CajaRegistradora;
import co.edu.unbosque.cocotechback.model.CajaRegistradora.Estado;

/**
 * Interfaz de repositorio para la entidad {@link CajaRegistradora}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code caja_registradora} en la base de datos MySQL. Además,
 * define métodos de consulta personalizados para buscar cajas por estado,
 * sucursal y empleado asignado.
 * <p>
 * Spring Data JPA genera la implementación de todos estos métodos
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface CajaRegistradoraRepository extends JpaRepository<CajaRegistradora, Long> {

	/**
	 * Busca todas las cajas registradoras que se encuentren en un estado
	 * operativo específico.
	 *
	 * @param estado El estado por el cual filtrar las cajas
	 *               ({@code ACTIVA}, {@code INACTIVA} o {@code EN_MANTENIMIENTO}).
	 * @return Una lista de cajas registradoras en el estado indicado. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<CajaRegistradora> findByEstado(Estado estado);

	/**
	 * Busca todas las cajas registradoras pertenecientes a una sucursal
	 * específica.
	 *
	 * @param idSucursal El identificador de la sucursal por la cual filtrar.
	 * @return Una lista de cajas registradoras de la sucursal indicada. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<CajaRegistradora> findBySucursal_IdSucursal(Long idSucursal);

	/**
	 * Busca la caja registradora asignada a un empleado específico.
	 * <p>
	 * La relación empleado-caja es 1:1, por lo tanto este método retorna a lo
	 * sumo una caja por empleado.
	 *
	 * @param idEmpleado El identificador del empleado cuya caja se desea consultar.
	 * @return Un {@link Optional} con la caja asignada al empleado, o vacío si
	 *         el empleado no tiene caja asignada.
	 */
	public Optional<CajaRegistradora> findByEmpleado_Id(Long idEmpleado);

	/**
	 * Verifica si ya existe una caja registradora asignada al empleado indicado.
	 *
	 * @param idEmpleado El identificador del empleado a verificar.
	 * @return {@code true} si el empleado ya tiene una caja asignada,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByEmpleado_Id(Long idEmpleado);

	/**
	 * Busca una caja registradora por su número físico dentro de una sucursal
	 * específica.
	 *
	 * @param numeroCaja El número físico de la caja.
	 * @param idSucursal El identificador de la sucursal.
	 * @return Un {@link Optional} con la caja encontrada, o vacío si no existe
	 *         ninguna caja con ese número en la sucursal indicada.
	 */
	public Optional<CajaRegistradora> findByNumeroCajaAndSucursal_IdSucursal(Integer numeroCaja,
			Long idSucursal);
}
