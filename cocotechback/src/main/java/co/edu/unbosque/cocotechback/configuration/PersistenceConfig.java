/**
 * Paquete que contiene las clases de configuración de la aplicación CocoTech
 * backend.
 */
package co.edu.unbosque.cocotechback.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuración de persistencia que separa los repositorios JPA (MySQL) de
 * los repositorios MongoDB.
 * <p>
 * Esta separación es necesaria porque Spring Data escanea automáticamente
 * los paquetes en busca de interfaces que extiendan {@code Repository}.
 * Si los repositorios JPA y MongoDB conviven en el mismo paquete, ambos
 * módulos intentan crear implementaciones de cada interfaz, generando
 * errores en el arranque del contexto.
 * <p>
 * Esquema de responsabilidades:
 * <ul>
 *   <li><b>MySQL</b>: fuente de verdad transaccional (ventas, productos,
 *       usuarios, inventario, control de stock).</li>
 *   <li><b>MongoDB</b>: almacén de lectura intensiva con datos embebidos
 *       (facturas según el patrón de Referencia Extendida y reportes
 *       pre-calculados según el patrón Computado).</li>
 * </ul>
 */
@Configuration
@EnableJpaRepositories(basePackages = "co.edu.unbosque.cocotechback.repository.jpa")
@EnableMongoRepositories(basePackages = "co.edu.unbosque.cocotechback.repository.mongo")
public class PersistenceConfig {

	/**
	 * Constructor por defecto de {@code PersistenceConfig}.
	 */
	public PersistenceConfig() {
	}
}
