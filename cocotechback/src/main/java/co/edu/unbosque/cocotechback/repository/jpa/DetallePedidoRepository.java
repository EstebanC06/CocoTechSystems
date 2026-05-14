/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.DetallePedido;

/**
 * Interfaz de repositorio para la entidad {@link DetallePedido}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar.
 * Aunque los detalles se persisten en cascada desde {@link co.edu.unbosque.cocotechback.model.Pedido},
 * este repositorio permite consultarlos individualmente cuando sea necesario.
 */
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

	/**
	 * Obtiene todos los detalles de un pedido específico.
	 *
	 * @param idPedido ID del pedido.
	 * @return Lista de detalles del pedido.
	 */
	public List<DetallePedido> findByPedido_IdPedido(Long idPedido);
}