/**
 * Paquete que contiene los controladores REST de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.cocotechback.model.mongo.ReporteVentasMensual;
import co.edu.unbosque.cocotechback.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para los reportes mensuales pre-calculados (Patrón
 * Computado de MongoDB).
 * <p>
 * Las consultas aquí son O(1) porque los reportes ya están calculados y
 * almacenados; lo único costoso es el endpoint de recálculo, que se ejecuta
 * bajo demanda o de forma programada.
 */
@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081",
		"http://localhost:4200" })
@Tag(name = "Reportes pre-calculados",
		description = "Dashboards rápidos basados en agregaciones almacenadas en MongoDB")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

	/**
	 * Servicio que produce y consulta los reportes.
	 */
	@Autowired
	private ReporteService reporteServ;

	/**
	 * Constructor por defecto.
	 */
	public ReporteController() {
	}

	/**
	 * Dispara el recálculo de los reportes mensuales por sucursal para un
	 * año y mes dados, sobrescribiendo los reportes existentes.
	 *
	 * @param anio año del reporte.
	 * @param mes  mes del reporte (1-12).
	 * @return {@code 200 OK} con la cantidad de reportes generados.
	 */
	@PostMapping("/recalcular/{anio}/{mes}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Recalcular reportes mensuales",
			description = "Agrega las facturas del periodo y persiste el reporte por sucursal")
	public ResponseEntity<?> recalcular(
			@PathVariable Integer anio, @PathVariable Integer mes) {
		int generados = reporteServ.recalcularMes(anio, mes);
		return ResponseEntity.ok(Map.of(
				"message", "Reportes recalculados",
				"reportesGenerados", generados,
				"anio", anio,
				"mes", mes,
				"success", true));
	}

	/**
	 * Obtiene los reportes pre-calculados de todas las sucursales para un
	 * mes y año.
	 *
	 * @param anio año del reporte.
	 * @param mes  mes (1-12).
	 * @return {@code 200 OK} con la lista de reportes.
	 */
	@GetMapping("/mes/{anio}/{mes}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Reportes del mes",
			description = "Lista los reportes pre-calculados de todas las sucursales")
	public ResponseEntity<List<ReporteVentasMensual>> reportesPorMes(
			@PathVariable Integer anio, @PathVariable Integer mes) {
		return ResponseEntity.ok(reporteServ.getReportesPorMes(anio, mes));
	}

	/**
	 * Obtiene la evolución mensual de una sucursal a lo largo de un año.
	 *
	 * @param anio       año a consultar.
	 * @param idSucursal ID de la sucursal en MySQL.
	 * @return {@code 200 OK} con los doce reportes mensuales (los que existan).
	 */
	@GetMapping("/evolucion/{anio}/{idSucursal}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Evolución anual de una sucursal",
			description = "Devuelve los reportes mes a mes para una sucursal dada")
	public ResponseEntity<List<ReporteVentasMensual>> evolucionAnual(
			@PathVariable Integer anio, @PathVariable Long idSucursal) {
		return ResponseEntity.ok(
				reporteServ.getEvolucionAnualPorSucursal(anio, idSucursal));
	}
}
