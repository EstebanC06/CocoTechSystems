/**
 * Paquete que contiene las clases de Servicio utilizadas en la aplicación
 * CocoTech backend.
 */
package co.edu.unbosque.cocotechback.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.MergeOptions;

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
 * <p>
 * El recálculo mensual delega TODA la lógica al motor MongoDB usando un
 * pipeline de agregación con la etapa {@code $merge}: ni los documentos
 * intermedios ni los reportes finales viajan al backend Java.
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
	 * Se conserva para otras consultas del módulo aunque {@code recalcularMes}
	 * ya no lo use directamente.
	 */
	@Autowired
	@SuppressWarnings("unused")
	private FacturaDocumentoRepository facturaDocRepo;

	/**
	 * Plantilla de Mongo para ejecutar pipelines avanzados (como los que
	 * usan {@code $merge}) que Spring Data no expone vía repositorios.
	 */
	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * Constructor por defecto.
	 */
	public ReporteService() {
	}

	/**
	 * Recalcula los reportes mensuales por sucursal para un año y mes
	 * dados, delegando TODA la agregación y persistencia al motor MongoDB
	 * vía pipeline con {@code $merge}.
	 * <p>
	 * A diferencia de la implementación anterior (bucle en Java sobre los
	 * resultados de la agregación), aquí ningún documento viaja al backend:
	 * Mongo lee {@code facturas}, agrupa por sucursal, calcula totales y
	 * escribe directamente en {@code reportes_ventas_mensuales}.
	 *
	 * @param anio año del reporte.
	 * @param mes  mes del reporte (1-12).
	 * @return cantidad de reportes que quedaron en la colección para ese
	 *         periodo tras la ejecución.
	 */
	public int recalcularMes(Integer anio, Integer mes) {
		YearMonth ym = YearMonth.of(anio, mes);
		LocalDateTime inicio = ym.atDay(1).atStartOfDay();
		LocalDateTime fin = ym.atEndOfMonth().atTime(23, 59, 59);

		// Convertir LocalDateTime a Date para que MongoTemplate los acepte
		// como BSON dates dentro del pipeline.
		Date inicioDate = Date.from(
				inicio.atZone(ZoneId.systemDefault()).toInstant());
		Date finDate = Date.from(
				fin.atZone(ZoneId.systemDefault()).toInstant());

		// Construir el pipeline equivalente al manual de Mongo.
		List<Bson> pipeline = new ArrayList<>();

		// 1) $match: filtrar facturas del periodo solicitado.
		pipeline.add(new Document("$match",
				new Document("fecha",
						new Document("$gte", inicioDate)
								.append("$lte", finDate))));

		// 2) $group: agrupar por sucursal y sumar totales.
		pipeline.add(new Document("$group",
				new Document("_id",
						new Document("idSucursal", "$sucursal.idSucursal")
								.append("nombre", "$sucursal.nombre"))
						.append("cantidadFacturas", new Document("$sum", 1))
						.append("ingresoBruto", new Document("$sum", "$precioTotal"))
						.append("totalImpuestos", new Document("$sum", "$precioImpuestos"))));

		// 3) $project: formar el documento final del reporte, agregando
		//    año, mes, ticket promedio y timestamp del servidor.
		pipeline.add(new Document("$project",
				new Document("_id", 0)
						.append("anio", new Document("$literal", anio))
						.append("mes", new Document("$literal", mes))
						.append("idSucursal", "$_id.idSucursal")
						.append("nombreSucursal", "$_id.nombre")
						.append("cantidadFacturas", 1)
						.append("ingresoBruto", 1)
						.append("totalImpuestos", 1)
						.append("ticketPromedio",
								new Document("$cond", List.of(
										new Document("$gt", List.of("$cantidadFacturas", 0)),
										new Document("$divide",
												List.of("$ingresoBruto", "$cantidadFacturas")),
										0)))
						.append("actualizadoEn", "$$NOW")));

		// 4) $merge: escribir directo a reportes_ventas_mensuales usando
		//    como clave compuesta (anio, mes, idSucursal). Si ya existe →
		//    replace, si no existe → insert. Requiere índice único sobre
		//    esas tres columnas (creado en el script de migración Mongo).
		pipeline.add(Aggregates.merge("reportes_ventas_mensuales",
				new MergeOptions()
						.uniqueIdentifier(List.of("anio", "mes", "idSucursal"))
						.whenMatched(MergeOptions.WhenMatched.REPLACE)
						.whenNotMatched(MergeOptions.WhenNotMatched.INSERT)));

		// Ejecutar el pipeline contra la colección "facturas". $merge es
		// terminal: no devuelve documentos, solo escribe en destino.
		mongoTemplate.getCollection("facturas").aggregate(pipeline).toCollection();

		// Contar cuántos reportes quedaron para ese periodo (lo que el
		// pipeline acaba de producir o actualizar).
		int generados = reporteRepo.findByAnioAndMes(anio, mes).size();

		log.info("Reportes mensuales recalculados con $merge para {}-{}: "
				+ "{} sucursales", anio, mes, generados);
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
// ola
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