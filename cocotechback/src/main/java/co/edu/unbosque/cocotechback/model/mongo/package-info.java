/**
 * Paquete que contiene los documentos MongoDB de la aplicación CocoTech
 * backend.
 * <p>
 * A diferencia de las entidades JPA del paquete
 * {@link co.edu.unbosque.cocotechback.model}, estos documentos no representan
 * la fuente de verdad transaccional sino vistas de lectura intensiva basadas
 * en patrones de diseño de esquemas de MongoDB:
 * <ul>
 *   <li>{@link co.edu.unbosque.cocotechback.model.mongo.FacturaDocumento}
 *       implementa el patrón de Referencia Extendida.</li>
 *   <li>{@link co.edu.unbosque.cocotechback.model.mongo.ReporteVentasMensual}
 *       implementa el patrón Computado.</li>
 * </ul>
 */
package co.edu.unbosque.cocotechback.model.mongo;
