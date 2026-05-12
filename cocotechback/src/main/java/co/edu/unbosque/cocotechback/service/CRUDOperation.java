/**
 * Paquete que contiene las clases de Servicio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.service;

import java.util.List;

/**
 * Interfaz genérica que define las operaciones CRUD (Crear, Leer, Actualizar,
 * Eliminar) básicas para las entidades de la aplicación CocoTech.
 * <p>
 * Proporciona un contrato común para todos los servicios del sistema,
 * garantizando consistencia en los métodos expuestos por cada capa de negocio.
 * Define operaciones estándar de persistencia, así como métodos de soporte
 * para encriptación de datos sensibles y actualizaciones parciales de campos
 * críticos (contraseña, correo y código de verificación) para las entidades
 * que heredan de {@link co.edu.unbosque.cocotechback.model.Usuario}.
 * <p>
 * Los métodos retornan códigos enteros para indicar el resultado de cada
 * operación, siguiendo la misma convención del proyecto de referencia:
 * <ul>
 * <li>{@code 0} - Operación exitosa.</li>
 * <li>{@code 1} - Conflicto de datos (ya existe un registro con esos datos).</li>
 * <li>{@code 2} - Entidad no encontrada.</li>
 * <li>{@code 3} - Error genérico / no manejado.</li>
 * <li>{@code 4} - Validación fallida (datos con formato incorrecto).</li>
 * </ul>
 *
 * @param <T> El tipo del objeto DTO utilizado para transferir datos hacia y
 *            desde la capa de servicio.
 * @param <E> El tipo de la entidad JPA gestionada por el servicio.
 */
public interface CRUDOperation<T, E> {

	/**
	 * Crea una nueva entidad a partir de los datos del DTO proporcionado.
	 * <p>
	 * El parámetro {@code rol} se mantiene por compatibilidad con los servicios
	 * de usuario ({@code ClienteService} y {@code EmpleadoService}); en los
	 * demás servicios se puede pasar {@code null} o ignorar.
	 *
	 * @param data El DTO con los datos para la creación de la entidad.
	 * @param rol  El rol a asignar (aplica solo a entidades de tipo usuario).
	 * @return Un código de resultado indicando el éxito o el motivo del fallo.
	 */
	public int create(T data, String rol);

	/**
	 * Obtiene todos los registros de la entidad y los devuelve como una lista
	 * de DTOs.
	 *
	 * @return Una lista de DTOs que representan todos los registros encontrados.
	 *         Retorna una lista vacía si no hay registros.
	 */
	public List<T> getAll();

	/**
	 * Elimina una entidad de la base de datos por su ID.
	 *
	 * @param id El ID de la entidad a eliminar.
	 * @return Un código de resultado indicando el éxito o el motivo del fallo.
	 */
	public int deleteById(Long id);

	/**
	 * Actualiza una entidad existente por su ID utilizando los datos del DTO.
	 *
	 * @param id      El ID de la entidad a actualizar.
	 * @param newData El DTO con los nuevos datos para la entidad.
	 * @return Un código de resultado indicando el éxito o el motivo del fallo.
	 */
	public int updateById(Long id, T newData);

	/**
	 * Cuenta el número total de registros de la entidad en la base de datos.
	 *
	 * @return El número total de registros.
	 */
	public long count();

	/**
	 * Verifica si una entidad con el ID especificado existe en la base de datos.
	 *
	 * @param id El ID de la entidad a verificar.
	 * @return {@code true} si la entidad existe, {@code false} en caso contrario.
	 */
	public boolean exist(Long id);

	/**
	 * Encripta los datos sensibles del DTO y retorna la entidad correspondiente
	 * con los campos cifrados, lista para ser persistida.
	 *
	 * @param data El DTO cuyos datos sensibles se van a encriptar.
	 * @return Una entidad con los datos sensibles encriptados.
	 */
	public E encrypt(T data);

	/**
	 * Desencripta los datos sensibles de un DTO.
	 * <p>
	 * Dependiendo de la implementación, puede modificar el DTO directamente o
	 * retornar los datos desencriptados como cadena.
	 *
	 * @param data El DTO cuyos datos se van a desencriptar.
	 * @return Una cadena con los datos desencriptados, o {@code null} si la
	 *         operación se realiza directamente sobre el DTO.
	 */
	public String decrypt(T data);

	/**
	 * Actualiza la contraseña de una entidad de tipo usuario.
	 * <p>
	 * La nueva contraseña debe ser codificada con BCrypt antes de persistirse.
	 * Solo aplica a servicios de {@code Cliente} y {@code Empleado}.
	 *
	 * @param id      El ID de la entidad a actualizar.
	 * @param newData El DTO con la nueva contraseña.
	 * @return Un código de resultado indicando el éxito o el motivo del fallo.
	 */
	public int updatePassword(Long id, T newData);

	/**
	 * Actualiza el correo electrónico de una entidad de tipo usuario.
	 * <p>
	 * Solo aplica a servicios de {@code Cliente} y {@code Empleado}.
	 *
	 * @param id      El ID de la entidad a actualizar.
	 * @param newData El DTO con el nuevo correo electrónico.
	 * @return Un código de resultado indicando el éxito o el motivo del fallo.
	 */
	public int updateCorreo(Long id, T newData);

	/**
	 * Actualiza el rol de una entidad de tipo usuario.
	 * <p>
	 * Solo aplica a servicios de {@code Cliente} y {@code Empleado}.
	 *
	 * @param id      El ID de la entidad a actualizar.
	 * @param newData El DTO con el nuevo rol.
	 * @return Un código de resultado indicando el éxito o el motivo del fallo.
	 */
	public int updateRol(Long id, T newData);

	/**
	 * Actualiza el código de verificación de una entidad de tipo usuario.
	 * <p>
	 * El código se encripta con AES antes de persistirse.
	 * Solo aplica a servicios de {@code Cliente} y {@code Empleado}.
	 *
	 * @param id      El ID de la entidad a actualizar.
	 * @param newData El DTO con el nuevo código de verificación.
	 * @return Un código de resultado indicando el éxito o el motivo del fallo.
	 */
	public int updateCode(Long id, T newData);
}
