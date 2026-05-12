/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 * <p>
 * Los DTOs de este paquete son POJOs planos (sin anotaciones JPA) cuya
 * responsabilidad exclusiva es transportar datos entre las capas de la
 * aplicación (servicio ↔ controlador ↔ cliente HTTP). Desacoplan la
 * representación externa de la API de la estructura interna de las entidades
 * JPA, evitando además referencias circulares en la serialización JSON al
 * representar relaciones entre entidades únicamente mediante IDs.
 * <p>
 * DTOs disponibles:
 * <ul>
 * <li>{@link co.edu.unbosque.cocotechback.dto.ClienteDTO}</li>
 * <li>{@link co.edu.unbosque.cocotechback.dto.EmpleadoDTO}</li>
 * <li>{@link co.edu.unbosque.cocotechback.dto.SucursalDTO}</li>
 * <li>{@link co.edu.unbosque.cocotechback.dto.CajaRegistradoraDTO}</li>
 * <li>{@link co.edu.unbosque.cocotechback.dto.CategoriaDTO}</li>
 * <li>{@link co.edu.unbosque.cocotechback.dto.ProveedorDTO}</li>
 * <li>{@link co.edu.unbosque.cocotechback.dto.ProductoDTO}</li>
 * <li>{@link co.edu.unbosque.cocotechback.dto.VentaDTO}</li>
 * <li>{@link co.edu.unbosque.cocotechback.dto.DetalleVentaDTO}</li>
 * <li>{@link co.edu.unbosque.cocotechback.dto.FacturaDTO}</li>
 * </ul>
 */
package co.edu.unbosque.cocotechback.dto;
