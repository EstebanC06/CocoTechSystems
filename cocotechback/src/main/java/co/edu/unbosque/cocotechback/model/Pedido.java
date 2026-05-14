/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa un pedido del e-commerce de CocoTech.
 * <p>
 * Un pedido es generado por un {@link Cliente} al finalizar el checkout y es
 * preparado por una {@link Sucursal} de despacho. A diferencia de la
 * {@link Venta} (que representa una transacción de punto de venta físico ya
 * consumada), el pedido modela el ciclo de vida completo de una compra online:
 * desde que se recibe hasta que se entrega.
 * <p>
 * Flujo de estados:
 * <pre>
 *   RECIBIDO → PREPARANDO → LISTO_PARA_ENTREGA → (recoger en sucursal)
 *   RECIBIDO → PREPARANDO → EN_CAMINO → ENTREGADO  (domicilio)
 *   RECIBIDO → CANCELADO  (solo se puede cancelar en estado RECIBIDO)
 * </pre>
 * Cuando un pedido pasa a {@code ENTREGADO}, el sistema genera automáticamente
 * la {@link Venta}, los {@link DetalleVenta} y la {@link Factura}
 * correspondientes, integrando el e-commerce con el módulo de ventas existente.
 * <p>
 * Los datos de la dirección de envío se desnormalizan (se copian como columnas
 * planas) para preservar un snapshot histórico, de modo que el pedido conserve
 * la dirección usada aunque el cliente luego edite o elimine esa dirección.
 */
@Entity
@Table(name = "pedido")
public class Pedido {

	/**
	 * Estados posibles de un pedido a lo largo de su ciclo de vida.
	 */
	public enum EstadoPedido {
		/** El pedido fue creado y está pendiente de ser preparado. */
		RECIBIDO,
		/** Un empleado está alistando los productos del pedido. */
		PREPARANDO,
		/** El pedido está listo para que el cliente lo recoja en sucursal. */
		LISTO_PARA_ENTREGA,
		/** El pedido salió a reparto a domicilio. */
		EN_CAMINO,
		/** El pedido fue entregado al cliente; genera Venta + Factura. */
		ENTREGADO,
		/** El pedido fue cancelado; se restituye el stock. */
		CANCELADO
	}

	/**
	 * Modalidad de entrega elegida por el cliente en el checkout.
	 */
	public enum TipoEntrega {
		/** Envío a la dirección indicada por el cliente. */
		DOMICILIO,
		/** El cliente recoge el pedido en una sucursal. */
		RECOGER_EN_SUCURSAL
	}

	/**
	 * Método de pago seleccionado. Todos son simulados (proyecto académico).
	 */
	public enum MetodoPago {
		/** Pago en efectivo al momento de recibir el pedido. */
		EFECTIVO_CONTRA_ENTREGA,
		/** Pago con tarjeta simulado al confirmar el pedido. */
		TARJETA_SIMULADA,
		/** Pago PSE / transferencia simulado al confirmar el pedido. */
		PSE_SIMULADO
	}

	/**
	 * Identificador único del pedido, generado automáticamente.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPedido;

	/**
	 * Fecha y hora en que se creó el pedido (al finalizar el checkout).
	 */
	private LocalDateTime fechaCreacion;

	/**
	 * Fecha y hora de la última actualización de estado del pedido.
	 */
	private LocalDateTime fechaActualizacion;

	/**
	 * Estado actual del pedido dentro de su ciclo de vida.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoPedido estado;

	/**
	 * Modalidad de entrega elegida (domicilio o recoger en sucursal).
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoEntrega tipoEntrega;

	/**
	 * Método de pago simulado seleccionado por el cliente.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MetodoPago metodoPago;

	/**
	 * Suma de los subtotales de los detalles, antes de IVA y envío.
	 */
	private Double subtotal;

	/**
	 * Valor del IVA (19%) calculado sobre el subtotal.
	 */
	private Double iva;

	/**
	 * Costo del envío a domicilio. Es 0 si se recoge en sucursal.
	 */
	private Double costoEnvio;

	/**
	 * Total a pagar por el pedido: {@code subtotal + iva + costoEnvio}.
	 */
	private Double total;

	/**
	 * Cliente que realizó el pedido.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente", nullable = false)
	private Cliente cliente;

	/**
	 * Sucursal encargada de preparar y despachar el pedido.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sucursal_despacho", nullable = false)
	private Sucursal sucursalDespacho;

	/**
	 * Snapshot de la calle/dirección de envío (solo para DOMICILIO).
	 */
	private String direccionEnvio;

	/**
	 * Snapshot del barrio de envío (solo para DOMICILIO).
	 */
	private String barrioEnvio;

	/**
	 * Snapshot de la ciudad de envío (solo para DOMICILIO).
	 */
	private String ciudadEnvio;

	/**
	 * Snapshot de la referencia de la dirección (solo para DOMICILIO).
	 */
	@Column(length = 500)
	private String referenciaEnvio;

	/**
	 * Notas opcionales que el cliente deja para el pedido.
	 */
	@Column(length = 500)
	private String notasCliente;

	/**
	 * Venta generada automáticamente cuando el pedido pasa a ENTREGADO.
	 * Es {@code null} mientras el pedido no haya sido entregado.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_venta_generada")
	private Venta ventaGenerada;

	/**
	 * Lista de detalles (líneas de producto) que componen el pedido.
	 */
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
			orphanRemoval = true)
	private List<DetallePedido> detalles;

	/**
	 * Constructor por defecto. Inicializa la lista de detalles y los timestamps.
	 */
	public Pedido() {
		this.detalles = new ArrayList<>();
		this.fechaCreacion = LocalDateTime.now();
		this.fechaActualizacion = LocalDateTime.now();
		this.estado = EstadoPedido.RECIBIDO;
	}

	/** @return ID del pedido. */
	public Long getIdPedido() {
		return idPedido;
	}

	/** @param idPedido Nuevo ID del pedido. */
	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
	}

	/** @return Fecha de creación del pedido. */
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	/** @param fechaCreacion Nueva fecha de creación. */
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	/** @return Fecha de la última actualización de estado. */
	public LocalDateTime getFechaActualizacion() {
		return fechaActualizacion;
	}

	/** @param fechaActualizacion Nueva fecha de actualización. */
	public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	/** @return Estado actual del pedido. */
	public EstadoPedido getEstado() {
		return estado;
	}

	/** @param estado Nuevo estado del pedido. */
	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}

	/** @return Modalidad de entrega. */
	public TipoEntrega getTipoEntrega() {
		return tipoEntrega;
	}

	/** @param tipoEntrega Nueva modalidad de entrega. */
	public void setTipoEntrega(TipoEntrega tipoEntrega) {
		this.tipoEntrega = tipoEntrega;
	}

	/** @return Método de pago. */
	public MetodoPago getMetodoPago() {
		return metodoPago;
	}

	/** @param metodoPago Nuevo método de pago. */
	public void setMetodoPago(MetodoPago metodoPago) {
		this.metodoPago = metodoPago;
	}

	/** @return Subtotal del pedido. */
	public Double getSubtotal() {
		return subtotal;
	}

	/** @param subtotal Nuevo subtotal. */
	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	/** @return IVA del pedido. */
	public Double getIva() {
		return iva;
	}

	/** @param iva Nuevo IVA. */
	public void setIva(Double iva) {
		this.iva = iva;
	}

	/** @return Costo de envío. */
	public Double getCostoEnvio() {
		return costoEnvio;
	}

	/** @param costoEnvio Nuevo costo de envío. */
	public void setCostoEnvio(Double costoEnvio) {
		this.costoEnvio = costoEnvio;
	}

	/** @return Total a pagar. */
	public Double getTotal() {
		return total;
	}

	/** @param total Nuevo total. */
	public void setTotal(Double total) {
		this.total = total;
	}

	/** @return Cliente que hizo el pedido. */
	public Cliente getCliente() {
		return cliente;
	}

	/** @param cliente Nuevo cliente. */
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	/** @return Sucursal de despacho. */
	public Sucursal getSucursalDespacho() {
		return sucursalDespacho;
	}

	/** @param sucursalDespacho Nueva sucursal de despacho. */
	public void setSucursalDespacho(Sucursal sucursalDespacho) {
		this.sucursalDespacho = sucursalDespacho;
	}

	/** @return Dirección de envío (snapshot). */
	public String getDireccionEnvio() {
		return direccionEnvio;
	}

	/** @param direccionEnvio Nueva dirección de envío. */
	public void setDireccionEnvio(String direccionEnvio) {
		this.direccionEnvio = direccionEnvio;
	}

	/** @return Barrio de envío (snapshot). */
	public String getBarrioEnvio() {
		return barrioEnvio;
	}

	/** @param barrioEnvio Nuevo barrio de envío. */
	public void setBarrioEnvio(String barrioEnvio) {
		this.barrioEnvio = barrioEnvio;
	}

	/** @return Ciudad de envío (snapshot). */
	public String getCiudadEnvio() {
		return ciudadEnvio;
	}

	/** @param ciudadEnvio Nueva ciudad de envío. */
	public void setCiudadEnvio(String ciudadEnvio) {
		this.ciudadEnvio = ciudadEnvio;
	}

	/** @return Referencia de la dirección de envío (snapshot). */
	public String getReferenciaEnvio() {
		return referenciaEnvio;
	}

	/** @param referenciaEnvio Nueva referencia de envío. */
	public void setReferenciaEnvio(String referenciaEnvio) {
		this.referenciaEnvio = referenciaEnvio;
	}

	/** @return Notas del cliente. */
	public String getNotasCliente() {
		return notasCliente;
	}

	/** @param notasCliente Nuevas notas del cliente. */
	public void setNotasCliente(String notasCliente) {
		this.notasCliente = notasCliente;
	}

	/** @return Venta generada al entregar el pedido (o {@code null}). */
	public Venta getVentaGenerada() {
		return ventaGenerada;
	}

	/** @param ventaGenerada Nueva venta generada. */
	public void setVentaGenerada(Venta ventaGenerada) {
		this.ventaGenerada = ventaGenerada;
	}

	/** @return Lista de detalles del pedido. */
	public List<DetallePedido> getDetalles() {
		return detalles;
	}

	/** @param detalles Nueva lista de detalles. */
	public void setDetalles(List<DetallePedido> detalles) {
		this.detalles = detalles;
	}

	@Override
	public String toString() {
		return "Pedido [idPedido=" + idPedido + ", estado=" + estado
				+ ", tipoEntrega=" + tipoEntrega + ", total=" + total
				+ ", cliente=" + (cliente != null ? cliente.getId() : "null")
				+ ", sucursalDespacho="
				+ (sucursalDespacho != null ? sucursalDespacho.getIdSucursal() : "null")
				+ ", detalles=" + (detalles != null ? detalles.size() : 0) + "]";
	}
}