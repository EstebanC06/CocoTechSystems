/**
 * Paquete que contiene las clases para el manejo de excepciones y validaciones
 * de la aplicación CocoTech backend.
 * <p>
 * Este paquete contiene dos clases con responsabilidades distintas pero
 * complementarias:
 * <p>
 * <strong>{@link co.edu.unbosque.cocotechback.exception.Exceptions}</strong>
 * — Clase utilitaria con métodos de validación reutilizables para los datos
 * de entrada de las entidades del supermercado. Los métodos cubren:
 * <ul>
 * <li>Detección de caracteres HTML peligrosos (XSS/injection).</li>
 * <li>Validación de nombres con caracteres del alfabeto español.</li>
 * <li>Validación de formato de correo electrónico y dominio Gmail.</li>
 * <li>Política de contraseñas seguras (longitud, mayúsculas, símbolos).</li>
 * <li>Validación de roles del sistema ({@code ROLE_CLIENTE},
 * {@code ROLE_ADMIN}).</li>
 * <li>Validación de valores numéricos del dominio: precios, stock,
 * cantidades y porcentajes de descuento.</li>
 * <li>Validación de números de teléfono.</li>
 * </ul>
 * <p>
 * <strong>{@link co.edu.unbosque.cocotechback.exception.GlobalExceptionHandler}</strong>
 * — Manejador global de excepciones anotado con
 * {@code @RestControllerAdvice} que intercepta las excepciones no capturadas
 * y las convierte en respuestas HTTP estructuradas con formato JSON
 * consistente. Las categorías de excepciones manejadas son:
 * <ul>
 * <li>Autenticación y autorización: {@code AccessDeniedException},
 * {@code BadCredentialsException}, {@code UsernameNotFoundException}.</li>
 * <li>Tokens JWT: {@code ExpiredJwtException}, {@code MalformedJwtException},
 * {@code SignatureException}.</li>
 * <li>Base de datos: {@code DataIntegrityViolationException}.</li>
 * <li>Argumentos y rutas: {@code MethodArgumentTypeMismatchException},
 * {@code IllegalArgumentException}, {@code NoResourceFoundException}.</li>
 * <li>Errores internos: {@code NullPointerException} y
 * {@code Exception} genérica.</li>
 * </ul>
 */
package co.edu.unbosque.cocotechback.exception;
