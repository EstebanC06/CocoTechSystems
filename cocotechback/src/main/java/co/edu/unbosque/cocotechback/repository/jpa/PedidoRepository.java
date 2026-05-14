/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.Pedido;
import co.edu.unbosque.cocotechback.model.Pedido.EstadoPedido;

/**
 * Interfaz de repositorio para la entidad {@link Pedido}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code pedido}. Define consultas derivadas para los tres
 * roles del e-commerce:
 * <ul>
 * <li>Cliente: consulta sus propios pedidos.</li>
 * <li>Empleado: consulta los pedidos de su sucursal.</li>
 * <li>Admin: consulta global, filtrable por estado.</li>
 * </ul>
 */
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	/**
	 * Obtiene todos los pedidos de un cliente, ordenados del más reciente al
	 * más antiguo.
	 *
	 * @param idCliente ID del cliente.
	 * @return Lista de pedidos del cliente.
	 */
	public List<Pedido> findByCliente_IdOrderByFechaCreacionDesc(Long idCliente);

	/**
	 * Obtiene todos los pedidos despachados por una sucursal, ordenados del
	 * más reciente al más antiguo.
	 *
	 * @param idSucursal ID de la sucursal de despacho.
	 * @return Lista de pedidos de la sucursal.
	 */
	public List<Pedido> findBySucursalDespacho_IdSucursalOrderByFechaCreacionDesc(
			Long idSucursal);

	/**
	 * Obtiene todos los pedidos en un estado específico, ordenados del más
	 * reciente al más antiguo.
	 *
	 * @param estado Estado por el cual filtrar.
	 * @return Lista de pedidos en ese estado.
	 */
	public List<Pedido> findByEstadoOrderByFechaCreacionDesc(EstadoPedido estado);

	/**
	 * Obtiene todos los pedidos del sistema ordenados del más reciente al más
	 * antiguo (vista global de administrador).
	 *
	 * @return Lista completa de pedidos.
	 */
	public List<Pedido> findAllByOrderByFechaCreacionDesc();

	/**
	 * Cuenta los pedidos que se encuentran en un estado específico.
	 *
	 * @param estado Estado por el cual contar.
	 * @return Número de pedidos en ese estado.
	 */
	public long countByEstado(EstadoPedido estado);
}