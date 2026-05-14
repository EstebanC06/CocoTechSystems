/**
 * Paquete que contiene las clases de Servicio utilizadas en la aplicación
 * CocoTech backend.
 */
package co.edu.unbosque.cocotechback.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.cocotechback.model.mongo.ReporteVentasMensual;
import co.edu.unbosque.cocotechback.repository.mongo.FacturaDocumentoRepository;
import co.edu.unbosque.cocotechback.repository.mongo.ReporteVentasMensualRepository;

/**
 * Servicio que gestiona los reportes pre-calculados en MongoDB (Patrón
 * Computado).
 * <p>
 * Los reportes se recalculan bajo demanda o de manera programada y se
 * persisten para que los dashboards y consultas posteriores los obtengan
 * con una sola lectura O(1), en lugar de recalcular agregaciones cada vez.
 */
@Service
public class ReporteService {

	/**
	 * Logger para registrar el flujo de cómputo de reportes.
	 */
	private static final Logger log = LoggerFactory.getLogger(ReporteService.class);

	/**
	 * Repositorio de reportes pre-calculados.
	 */
	@Autowired
	private ReporteVentasMensualRepository reporteRepo;

	/**
	 * Repositorio de facturas embebidas, fuente para el cómputo.
	 */
	@Autowired
	private FacturaDocumentoRepository facturaDocRepo;

	/**
	 * Constructor por defecto.
	 */
	public ReporteService() {
	}

	/**
	 * Recalcula los reportes mensuales por sucursal para un año y mes
	 * dados, y los persiste en MongoDB.
	 * <p>
	 * Sobrescribe los reportes existentes para ese periodo.
	 *
	 * @param anio año del reporte.
	 * @param mes  mes del reporte (1-12).
	 * @return cantidad de reportes generados.
	 */
	public int recalcularMes(Integer anio, Integer mes) {
		YearMonth ym = YearMonth.of(anio, mes);
		LocalDateTime inicio = ym.atDay(1).atStartOfDay();
		LocalDateTime fin = ym.atEndOfMonth().atTime(23, 59, 59);

		List<Document> agregados = facturaDocRepo.ingresoBrutoPorSucursal(inicio, fin);
		int generados = 0;

		for (Document agg : agregados) {
			// El pipeline agrupa por un _id compuesto { idSucursal, nombre },
			// de modo que cada reporte queda correctamente asociado al ID de
			// la sucursal en MySQL y no solo a su nombre.
			Document idGroup = agg.get("_id", Document.class);
			Long idSucursal = idGroup != null ? readLong(idGroup, "idSucursal") : null;
			String nombreSucursal = idGroup != null
					? idGroup.getString("nombre")
					: null;

			Double ingresoBruto = readDouble(agg, "ingresoBruto");
			Double impuestos = readDouble(agg, "impuestos");
			Long cantidadFacturas = readLong(agg, "cantidadFacturas");
			Double ticketPromedio = (cantidadFacturas != null && cantidadFacturas > 0)
					? ingresoBruto / cantidadFacturas
					: 0.0;

			// Se busca un reporte existente para ese periodo y sucursal; si
			// existe se actualiza, si no, se crea uno nuevo (upsert lógico).
			Optional<ReporteVentasMensual> existente = reporteRepo
					.findByAnioAndMesAndIdSucursal(anio, mes, idSucursal);

			ReporteVentasMensual r = existente.orElseGet(ReporteVentasMensual::new);
			r.setAnio(anio);
			r.setMes(mes);
			r.setIdSucursal(idSucursal);
			r.setNombreSucursal(nombreSucursal);
			r.setCantidadFacturas(cantidadFacturas);
			r.setIngresoBruto(ingresoBruto);
			r.setTotalImpuestos(impuestos);
			r.setTicketPromedio(ticketPromedio);
			r.setActualizadoEn(LocalDateTime.now());

			reporteRepo.save(r);
			generados++;
		}

		log.info("Reportes mensuales recalculados para {}-{}: {} sucursales",
				anio, mes, generados);
		return generados;
	}

	/**
	 * Obtiene todos los reportes pre-calculados de un mes y año.
	 *
	 * @param anio año del reporte.
	 * @param mes  mes (1-12).
	 * @return lista de reportes (uno por sucursal).
	 */
	public List<ReporteVentasMensual> getReportesPorMes(Integer anio, Integer mes) {
		return reporteRepo.findByAnioAndMes(anio, mes);
	}

	/**
	 * Obtiene la evolución mensual de una sucursal durante un año.
	 *
	 * @param anio       año a consultar.
	 * @param idSucursal ID de la sucursal en MySQL.
	 * @return lista de reportes ordenados por mes ascendente.
	 */
	public List<ReporteVentasMensual> getEvolucionAnualPorSucursal(
			Integer anio, Long idSucursal) {
		return reporteRepo.findByAnioAndIdSucursalOrderByMesAsc(anio, idSucursal);
	}

	// ─── Helpers de lectura del Document de Mongo ────────────────────

	/**
	 * Lee un campo numérico como Double, tolerando que venga como Integer.
	 *
	 * @param doc   documento BSON.
	 * @param campo nombre del campo.
	 * @return el valor convertido a Double, o {@code 0.0} si no existe.
	 */
	private Double readDouble(Document doc, String campo) {
		Object value = doc.get(campo);
		if (value instanceof Number n) {
			return n.doubleValue();
		}
		return 0.0;
	}

	/**
	 * Lee un campo numérico como Long.
	 *
	 * @param doc   documento BSON.
	 * @param campo nombre del campo.
	 * @return el valor convertido a Long, o {@code 0L} si no existe.
	 */
	private Long readLong(Document doc, String campo) {
		Object value = doc.get(campo);
		if (value instanceof Number n) {
			return n.longValue();
		}
		return 0L;
	}

	/**
	 * Convierte el resultado de una agregación a un Map plano apto para
	 * serializar como JSON en el controller.
	 *
	 * @param doc documento BSON.
	 * @return mapa con las claves y valores del documento.
	 */
	public Map<String, Object> toMap(Document doc) {
		return Map.copyOf(doc);
	}
}