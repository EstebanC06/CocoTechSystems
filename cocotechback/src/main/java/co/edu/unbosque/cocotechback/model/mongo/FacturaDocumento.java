/**
 * Paquete que contiene los documentos MongoDB utilizados en la aplicación
 * CocoTech backend.
 * <p>
 * Estos documentos implementan los patrones de diseño de esquemas
 * recomendados por MongoDB (Referencia Extendida y Computado) y conviven
 * con las entidades JPA del paquete {@link co.edu.unbosque.cocotechback.model}
 * sin sustituirlas.
 */
package co.edu.unbosque.cocotechback.model.mongo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento MongoDB que representa una factura emitida con todos sus datos
 * embebidos (Patrón de Referencia Extendida de MongoDB).
 * <p>
 * Cada factura embebe los datos del cliente, empleado, sucursal y los
 * detalles de venta tal y como existían al momento de la emisión. Este
 * enfoque permite:
 * <ul>
 *   <li>Consultar una factura completa con una sola lectura,
 *       sin operaciones JOIN.</li>
 *   <li>Preservar el estado histórico: si un cliente cambia de correo o
 *       un producto cambia de precio, la factura conserva el dato original
 *       (como debe ser legalmente).</li>
 *   <li>Reducir la carga sobre MySQL en operaciones de reporte contable y
 *       consulta de historial.</li>
 * </ul>
 * El campo {@code idFacturaMySQL} actúa como puente de trazabilidad con la
 * tabla {@code factura} de MySQL, que sigue siendo la fuente de verdad
 * transaccional.
 *
 * @see <a href="https://www.mongodb.com/blog/post/building-with-patterns-the-extended-reference-pattern">Extended Reference Pattern</a>
 */
@Document(collection = "facturas")
public class FacturaDocumento {

	/**
	 * Identificador único del documento en MongoDB.
	 */
	@Id
	private String id;

	/**
	 * Identificador de la factura en la tabla {@code factura} de MySQL.
	 * Actúa como puente de trazabilidad entre ambos motores. Único.
	 */
	@Indexed(unique = true)
	private Long idFacturaMySQL;

	/**
	 * Identificador de la venta asociada en MySQL. Indexado para búsquedas
	 * rápidas por venta.
	 */
	@Indexed
	private Long idVenta;

	/**
	 * Fecha y hora de emisión de la factura. Indexado para los reportes
	 * por rango de fecha.
	 */
	@Indexed
	private LocalDateTime fecha;

	/**
	 * Precio total de la factura (incluye impuestos).
	 */
	private Double precioTotal;

	/**
	 * Valor de los impuestos aplicados en la factura.
	 */
	private Double precioImpuestos;

	/**
	 * Snapshot del cliente al momento de la venta.
	 */
	private ClienteEmbebido cliente;

	/**
	 * Snapshot del empleado que registró la venta.
	 */
	private EmpleadoEmbebido empleado;

	/**
	 * Snapshot de la sucursal donde se realizó la venta.
	 */
	private SucursalEmbebida sucursal;

	/**
	 * Lista de detalles de la venta con los productos embebidos.
	 */
	private List<DetalleEmbebido> detalles;

	/**
	 * Constructor por defecto requerido por Spring Data MongoDB.
	 */
	public FacturaDocumento() {
	}

	// ─── Subdocumento ClienteEmbebido ──────────────────────────────────

	/**
	 * Subdocumento que captura los datos del cliente al momento de la venta.
	 */
	public static class ClienteEmbebido {

		/** ID del cliente en MySQL. */
		private Long idCliente;

		/** Nombres del cliente. */
		private String nombres;

		/** Apellidos del cliente. */
		private String apellidos;

		/** Correo electrónico del cliente (puede venir cifrado con AES). */
		private String correo;

		/** Ciudad de residencia del cliente. */
		private String ciudad;

		/** Constructor por defecto. */
		public ClienteEmbebido() {
		}

		/**
		 * Obtiene el ID del cliente.
		 * @return el ID del cliente.
		 */
		public Long getIdCliente() {
			return idCliente;
		}

		/**
		 * Establece el ID del cliente.
		 * @param idCliente el nuevo ID.
		 */
		public void setIdCliente(Long idCliente) {
			this.idCliente = idCliente;
		}

		/**
		 * Obtiene los nombres del cliente.
		 * @return los nombres del cliente.
		 */
		public String getNombres() {
			return nombres;
		}

		/**
		 * Establece los nombres del cliente.
		 * @param nombres los nuevos nombres.
		 */
		public void setNombres(String nombres) {
			this.nombres = nombres;
		}

		/**
		 * Obtiene los apellidos del cliente.
		 * @return los apellidos del cliente.
		 */
		public String getApellidos() {
			return apellidos;
		}

		/**
		 * Establece los apellidos del cliente.
		 * @param apellidos los nuevos apellidos.
		 */
		public void setApellidos(String apellidos) {
			this.apellidos = apellidos;
		}

		/**
		 * Obtiene el correo del cliente.
		 * @return el correo del cliente.
		 */
		public String getCorreo() {
			return correo;
		}

		/**
		 * Establece el correo del cliente.
		 * @param correo el nuevo correo.
		 */
		public void setCorreo(String correo) {
			this.correo = correo;
		}

		/**
		 * Obtiene la ciudad de residencia.
		 * @return la ciudad del cliente.
		 */
		public String getCiudad() {
			return ciudad;
		}

		/**
		 * Establece la ciudad de residencia.
		 * @param ciudad la nueva ciudad.
		 */
		public void setCiudad(String ciudad) {
			this.ciudad = ciudad;
		}
	}

	// ─── Subdocumento EmpleadoEmbebido ─────────────────────────────────

	/**
	 * Subdocumento que captura los datos del empleado al momento de la venta.
	 */
	public static class EmpleadoEmbebido {

		/** ID del empleado en MySQL. */
		private Long idEmpleado;

		/** Nombres del empleado. */
		private String nombres;

		/** Apellidos del empleado. */
		private String apellidos;

		/** Cargo del empleado al momento de la venta. */
		private String cargo;

		/** Constructor por defecto. */
		public EmpleadoEmbebido() {
		}

		/**
		 * Obtiene el ID del empleado.
		 * @return el ID del empleado.
		 */
		public Long getIdEmpleado() {
			return idEmpleado;
		}

		/**
		 * Establece el ID del empleado.
		 * @param idEmpleado el nuevo ID.
		 */
		public void setIdEmpleado(Long idEmpleado) {
			this.idEmpleado = idEmpleado;
		}

		/**
		 * Obtiene los nombres del empleado.
		 * @return los nombres del empleado.
		 */
		public String getNombres() {
			return nombres;
		}

		/**
		 * Establece los nombres del empleado.
		 * @param nombres los nuevos nombres.
		 */
		public void setNombres(String nombres) {
			this.nombres = nombres;
		}

		/**
		 * Obtiene los apellidos del empleado.
		 * @return los apellidos del empleado.
		 */
		public String getApellidos() {
			return apellidos;
		}

		/**
		 * Establece los apellidos del empleado.
		 * @param apellidos los nuevos apellidos.
		 */
		public void setApellidos(String apellidos) {
			this.apellidos = apellidos;
		}

		/**
		 * Obtiene el cargo del empleado.
		 * @return el cargo del empleado.
		 */
		public String getCargo() {
			return cargo;
		}

		/**
		 * Establece el cargo del empleado.
		 * @param cargo el nuevo cargo.
		 */
		public void setCargo(String cargo) {
			this.cargo = cargo;
		}
	}

	// ─── Subdocumento SucursalEmbebida ─────────────────────────────────

	/**
	 * Subdocumento que captura los datos de la sucursal donde se realizó
	 * la venta.
	 */
	public static class SucursalEmbebida {

		/** ID de la sucursal en MySQL. */
		private Long idSucursal;

		/** Nombre de la sucursal. */
		private String nombre;

		/** Ciudad de la sucursal. */
		private String ciudad;

		/** Constructor por defecto. */
		public SucursalEmbebida() {
		}

		/**
		 * Obtiene el ID de la sucursal.
		 * @return el ID de la sucursal.
		 */
		public Long getIdSucursal() {
			return idSucursal;
		}

		/**
		 * Establece el ID de la sucursal.
		 * @param idSucursal el nuevo ID.
		 */
		public void setIdSucursal(Long idSucursal) {
			this.idSucursal = idSucursal;
		}

		/**
		 * Obtiene el nombre de la sucursal.
		 * @return el nombre de la sucursal.
		 */
		public String getNombre() {
			return nombre;
		}

		/**
		 * Establece el nombre de la sucursal.
		 * @param nombre el nuevo nombre.
		 */
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		/**
		 * Obtiene la ciudad de la sucursal.
		 * @return la ciudad de la sucursal.
		 */
		public String getCiudad() {
			return ciudad;
		}

		/**
		 * Establece la ciudad de la sucursal.
		 * @param ciudad la nueva ciudad.
		 */
		public void setCiudad(String ciudad) {
			this.ciudad = ciudad;
		}
	}

	// ─── Subdocumento DetalleEmbebido ──────────────────────────────────

	/**
	 * Subdocumento que captura una línea de detalle de la venta con el
	 * producto embebido al momento de la transacción.
	 */
	public static class DetalleEmbebido {

		/** ID del producto en MySQL. */
		private Long idProducto;

		/** Nombre del producto al momento de la venta. */
		private String nombreProducto;

		/** Categoría del producto al momento de la venta. */
		private String categoria;

		/** Cantidad vendida del producto. */
		private Integer cantidad;

		/** Precio unitario aplicado en la venta. */
		private Double precioUnitario;

		/** Subtotal de la línea (precio × cantidad, con descuento si aplica). */
		private Double subtotal;

		/** Método de pago usado para este detalle. */
		private String metodoPago;

		/** Indica si se aplicó promoción. */
		private Boolean promocion;

		/** Porcentaje de descuento aplicado si hubo promoción. */
		private Double porcentajeDescuento;

		/** Constructor por defecto. */
		public DetalleEmbebido() {
		}

		/**
		 * Obtiene el ID del producto.
		 * @return el ID del producto.
		 */
		public Long getIdProducto() {
			return idProducto;
		}

		/**
		 * Establece el ID del producto.
		 * @param idProducto el nuevo ID.
		 */
		public void setIdProducto(Long idProducto) {
			this.idProducto = idProducto;
		}

		/**
		 * Obtiene el nombre del producto.
		 * @return el nombre del producto.
		 */
		public String getNombreProducto() {
			return nombreProducto;
		}

		/**
		 * Establece el nombre del producto.
		 * @param nombreProducto el nuevo nombre.
		 */
		public void setNombreProducto(String nombreProducto) {
			this.nombreProducto = nombreProducto;
		}

		/**
		 * Obtiene la categoría del producto.
		 * @return la categoría del producto.
		 */
		public String getCategoria() {
			return categoria;
		}

		/**
		 * Establece la categoría del producto.
		 * @param categoria la nueva categoría.
		 */
		public void setCategoria(String categoria) {
			this.categoria = categoria;
		}

		/**
		 * Obtiene la cantidad vendida.
		 * @return la cantidad vendida.
		 */
		public Integer getCantidad() {
			return cantidad;
		}

		/**
		 * Establece la cantidad vendida.
		 * @param cantidad la nueva cantidad.
		 */
		public void setCantidad(Integer cantidad) {
			this.cantidad = cantidad;
		}

		/**
		 * Obtiene el precio unitario.
		 * @return el precio unitario.
		 */
		public Double getPrecioUnitario() {
			return precioUnitario;
		}

		/**
		 * Establece el precio unitario.
		 * @param precioUnitario el nuevo precio unitario.
		 */
		public void setPrecioUnitario(Double precioUnitario) {
			this.precioUnitario = precioUnitario;
		}

		/**
		 * Obtiene el subtotal de la línea.
		 * @return el subtotal.
		 */
		public Double getSubtotal() {
			return subtotal;
		}

		/**
		 * Establece el subtotal.
		 * @param subtotal el nuevo subtotal.
		 */
		public void setSubtotal(Double subtotal) {
			this.subtotal = subtotal;
		}

		/**
		 * Obtiene el método de pago.
		 * @return el método de pago.
		 */
		public String getMetodoPago() {
			return metodoPago;
		}

		/**
		 * Establece el método de pago.
		 * @param metodoPago el nuevo método de pago.
		 */
		public void setMetodoPago(String metodoPago) {
			this.metodoPago = metodoPago;
		}

		/**
		 * Obtiene el indicador de promoción.
		 * @return {@code true} si hubo promoción.
		 */
		public Boolean getPromocion() {
			return promocion;
		}

		/**
		 * Establece el indicador de promoción.
		 * @param promocion el nuevo indicador.
		 */
		public void setPromocion(Boolean promocion) {
			this.promocion = promocion;
		}

		/**
		 * Obtiene el porcentaje de descuento aplicado.
		 * @return el porcentaje de descuento.
		 */
		public Double getPorcentajeDescuento() {
			return porcentajeDescuento;
		}

		/**
		 * Establece el porcentaje de descuento.
		 * @param porcentajeDescuento el nuevo porcentaje.
		 */
		public void setPorcentajeDescuento(Double porcentajeDescuento) {
			this.porcentajeDescuento = porcentajeDescuento;
		}
	}

	// ─── Getters/Setters de FacturaDocumento ────────────────────────────

	/**
	 * Obtiene el ID interno del documento Mongo.
	 * @return el ID del documento.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Establece el ID interno del documento Mongo.
	 * @param id el nuevo ID.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Obtiene el ID de la factura en MySQL.
	 * @return el ID en MySQL.
	 */
	public Long getIdFacturaMySQL() {
		return idFacturaMySQL;
	}

	/**
	 * Establece el ID de la factura en MySQL.
	 * @param idFacturaMySQL el nuevo ID.
	 */
	public void setIdFacturaMySQL(Long idFacturaMySQL) {
		this.idFacturaMySQL = idFacturaMySQL;
	}

	/**
	 * Obtiene el ID de la venta asociada.
	 * @return el ID de la venta.
	 */
	public Long getIdVenta() {
		return idVenta;
	}

	/**
	 * Establece el ID de la venta asociada.
	 * @param idVenta el nuevo ID.
	 */
	public void setIdVenta(Long idVenta) {
		this.idVenta = idVenta;
	}

	/**
	 * Obtiene la fecha de emisión.
	 * @return la fecha de emisión.
	 */
	public LocalDateTime getFecha() {
		return fecha;
	}

	/**
	 * Establece la fecha de emisión.
	 * @param fecha la nueva fecha.
	 */
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	/**
	 * Obtiene el precio total.
	 * @return el precio total.
	 */
	public Double getPrecioTotal() {
		return precioTotal;
	}

	/**
	 * Establece el precio total.
	 * @param precioTotal el nuevo precio total.
	 */
	public void setPrecioTotal(Double precioTotal) {
		this.precioTotal = precioTotal;
	}

	/**
	 * Obtiene los impuestos.
	 * @return el valor de los impuestos.
	 */
	public Double getPrecioImpuestos() {
		return precioImpuestos;
	}

	/**
	 * Establece los impuestos.
	 * @param precioImpuestos el nuevo valor de impuestos.
	 */
	public void setPrecioImpuestos(Double precioImpuestos) {
		this.precioImpuestos = precioImpuestos;
	}

	/**
	 * Obtiene el cliente embebido.
	 * @return el snapshot del cliente.
	 */
	public ClienteEmbebido getCliente() {
		return cliente;
	}

	/**
	 * Establece el cliente embebido.
	 * @param cliente el nuevo snapshot del cliente.
	 */
	public void setCliente(ClienteEmbebido cliente) {
		this.cliente = cliente;
	}

	/**
	 * Obtiene el empleado embebido.
	 * @return el snapshot del empleado.
	 */
	public EmpleadoEmbebido getEmpleado() {
		return empleado;
	}

	/**
	 * Establece el empleado embebido.
	 * @param empleado el nuevo snapshot del empleado.
	 */
	public void setEmpleado(EmpleadoEmbebido empleado) {
		this.empleado = empleado;
	}

	/**
	 * Obtiene la sucursal embebida.
	 * @return el snapshot de la sucursal.
	 */
	public SucursalEmbebida getSucursal() {
		return sucursal;
	}

	/**
	 * Establece la sucursal embebida.
	 * @param sucursal el nuevo snapshot de la sucursal.
	 */
	public void setSucursal(SucursalEmbebida sucursal) {
		this.sucursal = sucursal;
	}

	/**
	 * Obtiene los detalles embebidos.
	 * @return la lista de detalles.
	 */
	public List<DetalleEmbebido> getDetalles() {
		return detalles;
	}

	/**
	 * Establece los detalles embebidos.
	 * @param detalles la nueva lista de detalles.
	 */
	public void setDetalles(List<DetalleEmbebido> detalles) {
		this.detalles = detalles;
	}

	/**
	 * Devuelve una representación textual del documento.
	 * @return cadena con los atributos principales.
	 */
	@Override
	public String toString() {
		return "FacturaDocumento [id=" + id + ", idFacturaMySQL=" + idFacturaMySQL
				+ ", idVenta=" + idVenta + ", fecha=" + fecha
				+ ", precioTotal=" + precioTotal + ", precioImpuestos=" + precioImpuestos
				+ ", cliente=" + (cliente != null ? cliente.getIdCliente() : "null")
				+ ", empleado=" + (empleado != null ? empleado.getIdEmpleado() : "null")
				+ ", sucursal=" + (sucursal != null ? sucursal.getIdSucursal() : "null")
				+ ", detalles=" + (detalles != null ? detalles.size() : 0) + "]";
	}
}
