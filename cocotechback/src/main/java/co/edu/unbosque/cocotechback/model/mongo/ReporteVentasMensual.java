/**
 * Paquete que contiene los documentos MongoDB utilizados en la aplicación
 * CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model.mongo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento MongoDB que almacena un resumen pre-calculado de ventas por
 * sucursal y mes (Patrón Computado de MongoDB).
 * <p>
 * En lugar de recalcular agregaciones costosas en cada consulta de
 * dashboard, los reportes se actualizan al cerrar el mes (o por job
 * programado) y se consultan con una sola lectura O(1).
 * <p>
 * Aplica el patrón Computado: reduce la carga de CPU de cálculos
 * frecuentes y hace que las consultas sean directas.
 *
 * @see <a href="https://www.mongodb.com/blog/post/building-with-patterns-the-computed-pattern">Computed Pattern</a>
 */
@Document(collection = "reportes_ventas_mensuales")
public class ReporteVentasMensual {

	/**
	 * Identificador único del reporte en MongoDB.
	 */
	@Id
	private String id;

	/**
	 * Año del reporte (ej. 2026).
	 */
	@Indexed
	private Integer anio;

	/**
	 * Mes del reporte (1-12).
	 */
	@Indexed
	private Integer mes;

	/**
	 * ID de la sucursal a la que pertenece el reporte (referencia a MySQL).
	 */
	@Indexed
	private Long idSucursal;

	/**
	 * Nombre de la sucursal (denormalizado para evitar JOINs en lectura).
	 */
	private String nombreSucursal;

	/**
	 * Cantidad total de facturas emitidas en el periodo.
	 */
	private Long cantidadFacturas;

	/**
	 * Suma total de ingresos brutos del periodo.
	 */
	private Double ingresoBruto;

	/**
	 * Suma total de impuestos recaudados en el periodo.
	 */
	private Double totalImpuestos;

	/**
	 * Ticket promedio del periodo (ingresoBruto / cantidadFacturas).
	 */
	private Double ticketPromedio;

	/**
	 * Fecha y hora de la última actualización del reporte.
	 */
	private LocalDateTime actualizadoEn;

	/**
	 * Constructor por defecto requerido por Spring Data MongoDB.
	 */
	public ReporteVentasMensual() {
	}

	/**
	 * Constructor con parámetros para inicializar todos los campos del
	 * reporte.
	 *
	 * @param anio             año del reporte.
	 * @param mes              mes del reporte (1-12).
	 * @param idSucursal       ID de la sucursal en MySQL.
	 * @param nombreSucursal   nombre denormalizado de la sucursal.
	 * @param cantidadFacturas cantidad de facturas emitidas.
	 * @param ingresoBruto     ingreso bruto del periodo.
	 * @param totalImpuestos   total de impuestos recaudados.
	 * @param ticketPromedio   ticket promedio del periodo.
	 */
	public ReporteVentasMensual(Integer anio, Integer mes, Long idSucursal,
			String nombreSucursal, Long cantidadFacturas, Double ingresoBruto,
			Double totalImpuestos, Double ticketPromedio) {
		this.anio = anio;
		this.mes = mes;
		this.idSucursal = idSucursal;
		this.nombreSucursal = nombreSucursal;
		this.cantidadFacturas = cantidadFacturas;
		this.ingresoBruto = ingresoBruto;
		this.totalImpuestos = totalImpuestos;
		this.ticketPromedio = ticketPromedio;
		this.actualizadoEn = LocalDateTime.now();
	}

	/**
	 * Obtiene el ID interno del documento.
	 * @return el ID del documento.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Establece el ID interno del documento.
	 * @param id el nuevo ID.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Obtiene el año del reporte.
	 * @return el año.
	 */
	public Integer getAnio() {
		return anio;
	}

	/**
	 * Establece el año del reporte.
	 * @param anio el nuevo año.
	 */
	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	/**
	 * Obtiene el mes del reporte.
	 * @return el mes (1-12).
	 */
	public Integer getMes() {
		return mes;
	}

	/**
	 * Establece el mes del reporte.
	 * @param mes el nuevo mes (1-12).
	 */
	public void setMes(Integer mes) {
		this.mes = mes;
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
	 * Obtiene el nombre denormalizado de la sucursal.
	 * @return el nombre de la sucursal.
	 */
	public String getNombreSucursal() {
		return nombreSucursal;
	}

	/**
	 * Establece el nombre denormalizado de la sucursal.
	 * @param nombreSucursal el nuevo nombre.
	 */
	public void setNombreSucursal(String nombreSucursal) {
		this.nombreSucursal = nombreSucursal;
	}

	/**
	 * Obtiene la cantidad de facturas.
	 * @return la cantidad de facturas.
	 */
	public Long getCantidadFacturas() {
		return cantidadFacturas;
	}

	/**
	 * Establece la cantidad de facturas.
	 * @param cantidadFacturas la nueva cantidad.
	 */
	public void setCantidadFacturas(Long cantidadFacturas) {
		this.cantidadFacturas = cantidadFacturas;
	}

	/**
	 * Obtiene el ingreso bruto.
	 * @return el ingreso bruto.
	 */
	public Double getIngresoBruto() {
		return ingresoBruto;
	}

	/**
	 * Establece el ingreso bruto.
	 * @param ingresoBruto el nuevo valor.
	 */
	public void setIngresoBruto(Double ingresoBruto) {
		this.ingresoBruto = ingresoBruto;
	}

	/**
	 * Obtiene el total de impuestos recaudados.
	 * @return el total de impuestos.
	 */
	public Double getTotalImpuestos() {
		return totalImpuestos;
	}

	/**
	 * Establece el total de impuestos.
	 * @param totalImpuestos el nuevo total.
	 */
	public void setTotalImpuestos(Double totalImpuestos) {
		this.totalImpuestos = totalImpuestos;
	}

	/**
	 * Obtiene el ticket promedio.
	 * @return el ticket promedio.
	 */
	public Double getTicketPromedio() {
		return ticketPromedio;
	}

	/**
	 * Establece el ticket promedio.
	 * @param ticketPromedio el nuevo ticket promedio.
	 */
	public void setTicketPromedio(Double ticketPromedio) {
		this.ticketPromedio = ticketPromedio;
	}

	/**
	 * Obtiene la fecha de última actualización.
	 * @return la fecha de actualización.
	 */
	public LocalDateTime getActualizadoEn() {
		return actualizadoEn;
	}

	/**
	 * Establece la fecha de última actualización.
	 * @param actualizadoEn la nueva fecha de actualización.
	 */
	public void setActualizadoEn(LocalDateTime actualizadoEn) {
		this.actualizadoEn = actualizadoEn;
	}

	/**
	 * Devuelve una representación textual del reporte.
	 * @return cadena con los atributos principales.
	 */
	@Override
	public String toString() {
		return "ReporteVentasMensual [anio=" + anio + ", mes=" + mes
				+ ", sucursal=" + nombreSucursal + ", facturas=" + cantidadFacturas
				+ ", ingresoBruto=" + ingresoBruto + ", impuestos=" + totalImpuestos
				+ ", ticketPromedio=" + ticketPromedio + "]";
	}
}
