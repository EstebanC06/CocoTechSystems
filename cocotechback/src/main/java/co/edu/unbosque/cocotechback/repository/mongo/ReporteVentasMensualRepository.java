/**
 * Paquete que contiene los repositorios MongoDB de la aplicación CocoTech
 * backend.
 */
package co.edu.unbosque.cocotechback.repository.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.edu.unbosque.cocotechback.model.mongo.ReporteVentasMensual;

/**
 * Repositorio MongoDB para {@link ReporteVentasMensual}.
 * <p>
 * Los reportes son agregaciones pre-calculadas (Patrón Computado) que se
 * consultan con O(1) en lugar de recalcular sobre las facturas cada vez.
 */
public interface ReporteVentasMensualRepository
		extends MongoRepository<ReporteVentasMensual, String> {

	/**
	 * Obtiene el reporte de una sucursal específica para un mes y año dados.
	 *
	 * @param anio       año del reporte.
	 * @param mes        mes del reporte (1-12).
	 * @param idSucursal ID de la sucursal en MySQL.
	 * @return un {@link Optional} con el reporte si existe.
	 */
	Optional<ReporteVentasMensual> findByAnioAndMesAndIdSucursal(
			Integer anio, Integer mes, Long idSucursal);

	/**
	 * Obtiene todos los reportes de un año y mes específicos (todas las
	 * sucursales).
	 *
	 * @param anio año del reporte.
	 * @param mes  mes del reporte (1-12).
	 * @return lista de reportes por sucursal.
	 */
	List<ReporteVentasMensual> findByAnioAndMes(Integer anio, Integer mes);

	/**
	 * Obtiene la evolución mensual de una sucursal a lo largo de un año.
	 *
	 * @param anio       año a consultar.
	 * @param idSucursal ID de la sucursal.
	 * @return lista de reportes (uno por mes).
	 */
	List<ReporteVentasMensual> findByAnioAndIdSucursalOrderByMesAsc(
			Integer anio, Long idSucursal);
}
