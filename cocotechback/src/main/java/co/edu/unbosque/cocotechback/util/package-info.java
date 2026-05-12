/**
 * Paquete que contiene las clases de utilidad de la aplicación CocoTech backend.
 * <p>
 * Este paquete agrupa clases de uso transversal que son consumidas por múltiples
 * capas de la aplicación:
 * <p>
 * <strong>{@link co.edu.unbosque.cocotechback.util.AESUtil}</strong>
 * — Clase estática de cifrado AES/GCM y hashing. Provee dos métodos de
 * conveniencia ({@code encrypt(String)} y {@code decrypt(String)}) que usan
 * la clave e IV específicos de CocoTech, y son llamados directamente por
 * {@link co.edu.unbosque.cocotechback.service.ClienteService},
 * {@link co.edu.unbosque.cocotechback.service.EmpleadoService},
 * {@link co.edu.unbosque.cocotechback.security.UserDetailsServiceImpl} y
 * {@link co.edu.unbosque.cocotechback.configuration.LoadDatabase} para
 * proteger correos y códigos de verificación antes de persistirlos en MySQL.
 * También expone métodos de hashing MD5, SHA-1, SHA-256, SHA-384 y SHA-512
 * para la generación de tokens o verificación de integridad.
 * <p>
 * <strong>{@link co.edu.unbosque.cocotechback.util.ModelMapperConfig}</strong>
 * — Clase de configuración Spring ({@code @Configuration}) que define el
 * bean singleton de {@link org.modelmapper.ModelMapper} con estrategia de
 * mapeo {@code STRICT} y omisión de nulos habilitada. Es consumido por todos
 * los servicios de la capa de negocio para convertir entre entidades JPA y
 * DTOs sin código boilerplate.
 */
package co.edu.unbosque.cocotechback.util;
