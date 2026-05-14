/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO para representar un pedido del e-commerce de CocoTech.
 * <p>
 * Transporta la información del pedido entre la API REST y el frontend.
 * Las relaciones (cliente, sucursal) se representan por ID. Los enums se
 * transportan como {@code String} para facilitar la serialización JSON y la
 * interoperabilidad con el frontend TypeScript.
 * <p>
 * Incluye campos hidratados ({@code nombreCliente}, {@code nombreSucursal})
 * que el backend rellena en las respuestas para evitar peticiones adicionales.
 */
public class PedidoDTO {

	/** Identificador único del pedido. */
	private Long idPedido;

	/** Fecha y hora de creación del pedido. */
	private LocalDateTime fechaCreacion;

	/** Fecha y hora de la última actualización de estado. */
	private LocalDateTime fechaActualizacion;

	/**
	 * Estado del pedido como String. Valores válidos: RECIBIDO, PREPARANDO,
	 * LISTO_PARA_ENTREGA, EN_CAMINO, ENTREGADO, CANCELADO.
	 */
	private String estado;

	/**
	 * Tipo de entrega como String. Valores válidos: DOMICILIO,
	 * RECOGER_EN_SUCURSAL.
	 */
	private String tipoEntrega;

	/**
	 * Método de pago como String. Valores válidos: EFECTIVO_CONTRA_ENTREGA,
	 * TARJETA_SIMULADA, PSE_SIMULADO.
	 */
	private String metodoPago;

	/** Suma de subtotales de los detalles, antes de IVA y envío. */
	private Double subtotal;

	/** Valor del IVA (19%) calculado sobre el subtotal. */
	private Double iva;

	/** Costo del envío a domicilio (0 si se recoge en sucursal). */
	private Double costoEnvio;

	/** Total a pagar: subtotal + iva + costoEnvio. */
	private Double total;

	/** Identificador del cliente que realizó el pedido. */
	private Long idCliente;

	/** Identificador de la sucursal que despacha el pedido. */
	private Long idSucursalDespacho;

	/** Dirección de envío (solo para DOMICILIO). */
	private String direccionEnvio;

	/** Barrio de envío (solo para DOMICILIO). */
	private String barrioEnvio;

	/** Ciudad de envío (solo para DOMICILIO). */
	private String ciudadEnvio;

	/** Referencia de la dirección de envío (solo para DOMICILIO). */
	private String referenciaEnvio;

	/** Notas opcionales del cliente para el pedido. */
	private String notasCliente;

	/** Lista de detalles (líneas de producto) del pedido. */
	private List<DetallePedidoDTO> detalles;

	/** Nombre completo del cliente (hidratado por el backend). */
	private String nombreCliente;

	/** Nombre de la sucursal de despacho (hidratado por el backend). */
	private String nombreSucursal;

	/** ID de la venta generada al entregar el pedido (hidratado). */
	private Long idVentaGenerada;

	/** Constructor por defecto. Inicializa la lista de detalles. */
	public PedidoDTO() {
		this.detalles = new ArrayList<>();
	}

	/** @return ID del pedido. */
	public Long getIdPedido() { return idPedido; }
	/** @param idPedido Nuevo ID del pedido. */
	public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

	/** @return Fecha de creación. */
	public LocalDateTime getFechaCreacion() { return fechaCreacion; }
	/** @param fechaCreacion Nueva fecha de creación. */
	public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

	/** @return Fecha de actualización. */
	public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
	/** @param fechaActualizacion Nueva fecha de actualización. */
	public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	/** @return Estado del pedido. */
	public String getEstado() { return estado; }
	/** @param estado Nuevo estado. */
	public void setEstado(String estado) { this.estado = estado; }

	/** @return Tipo de entrega. */
	public String getTipoEntrega() { return tipoEntrega; }
	/** @param tipoEntrega Nuevo tipo de entrega. */
	public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }

	/** @return Método de pago. */
	public String getMetodoPago() { return metodoPago; }
	/** @param metodoPago Nuevo método de pago. */
	public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

	/** @return Subtotal del pedido. */
	public Double getSubtotal() { return subtotal; }
	/** @param subtotal Nuevo subtotal. */
	public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

	/** @return IVA del pedido. */
	public Double getIva() { return iva; }
	/** @param iva Nuevo IVA. */
	public void setIva(Double iva) { this.iva = iva; }

	/** @return Costo de envío. */
	public Double getCostoEnvio() { return costoEnvio; }
	/** @param costoEnvio Nuevo costo de envío. */
	public void setCostoEnvio(Double costoEnvio) { this.costoEnvio = costoEnvio; }

	/** @return Total a pagar. */
	public Double getTotal() { return total; }
	/** @param total Nuevo total. */
	public void setTotal(Double total) { this.total = total; }

	/** @return ID del cliente. */
	public Long getIdCliente() { return idCliente; }
	/** @param idCliente Nuevo ID del cliente. */
	public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

	/** @return ID de la sucursal de despacho. */
	public Long getIdSucursalDespacho() { return idSucursalDespacho; }
	/** @param idSucursalDespacho Nuevo ID de la sucursal. */
	public void setIdSucursalDespacho(Long idSucursalDespacho) {
		this.idSucursalDespacho = idSucursalDespacho;
	}

	/** @return Dirección de envío. */
	public String getDireccionEnvio() { return direccionEnvio; }
	/** @param direccionEnvio Nueva dirección de envío. */
	public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

	/** @return Barrio de envío. */
	public String getBarrioEnvio() { return barrioEnvio; }
	/** @param barrioEnvio Nuevo barrio de envío. */
	public void setBarrioEnvio(String barrioEnvio) { this.barrioEnvio = barrioEnvio; }

	/** @return Ciudad de envío. */
	public String getCiudadEnvio() { return ciudadEnvio; }
	/** @param ciudadEnvio Nueva ciudad de envío. */
	public void setCiudadEnvio(String ciudadEnvio) { this.ciudadEnvio = ciudadEnvio; }

	/** @return Referencia de envío. */
	public String getReferenciaEnvio() { return referenciaEnvio; }
	/** @param referenciaEnvio Nueva referencia de envío. */
	public void setReferenciaEnvio(String referenciaEnvio) {
		this.referenciaEnvio = referenciaEnvio;
	}

	/** @return Notas del cliente. */
	public String getNotasCliente() { return notasCliente; }
	/** @param notasCliente Nuevas notas del cliente. */
	public void setNotasCliente(String notasCliente) { this.notasCliente = notasCliente; }

	/** @return Lista de detalles del pedido. */
	public List<DetallePedidoDTO> getDetalles() { return detalles; }
	/** @param detalles Nueva lista de detalles. */
	public void setDetalles(List<DetallePedidoDTO> detalles) { this.detalles = detalles; }

	/** @return Nombre del cliente. */
	public String getNombreCliente() { return nombreCliente; }
	/** @param nombreCliente Nuevo nombre del cliente. */
	public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

	/** @return Nombre de la sucursal. */
	public String getNombreSucursal() { return nombreSucursal; }
	/** @param nombreSucursal Nuevo nombre de la sucursal. */
	public void setNombreSucursal(String nombreSucursal) { this.nombreSucursal = nombreSucursal; }

	/** @return ID de la venta generada. */
	public Long getIdVentaGenerada() { return idVentaGenerada; }
	/** @param idVentaGenerada Nuevo ID de la venta generada. */
	public void setIdVentaGenerada(Long idVentaGenerada) { this.idVentaGenerada = idVentaGenerada; }

	@Override
	public int hashCode() {
		return Objects.hash(idPedido);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		PedidoDTO other = (PedidoDTO) obj;
		return Objects.equals(idPedido, other.idPedido);
	}

	@Override
	public String toString() {
		return "PedidoDTO [idPedido=" + idPedido + ", estado=" + estado + ", tipoEntrega="
				+ tipoEntrega + ", total=" + total + ", idCliente=" + idCliente
				+ ", detalles=" + (detalles != null ? detalles.size() : 0) + "]";
	}
}