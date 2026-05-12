/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.util.Objects;

import co.edu.unbosque.cocotechback.model.Usuario.Rol;

/**
 * Clase de Transferencia de Datos (DTO) para representar la información de un
 * empleado del supermercado.
 * <p>
 * Se utiliza para transferir los datos del empleado entre las capas de la
 * aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.Empleado}.
 * La referencia a la sucursal se representa mediante su ID para evitar
 * dependencias circulares en la serialización JSON.
 */
public class EmpleadoDTO {

	/**
	 * Identificador único del empleado.
	 */
	private Long id;

	/**
	 * Nombres del empleado.
	 */
	private String nombres;

	/**
	 * Apellidos del empleado.
	 */
	private String apellidos;

	/**
	 * Correo electrónico del empleado. Utilizado como nombre de usuario
	 * para la autenticación.
	 */
	private String correo;

	/**
	 * Contraseña del empleado. Solo se utiliza en operaciones de creación
	 * y actualización de credenciales; no se devuelve en consultas de lectura.
	 */
	private String contrasena;

	/**
	 * Código de verificación del empleado (para recuperación de cuenta, etc.).
	 */
	private String codigoVerificacion;

	/**
	 * Rol del empleado en el sistema (siempre {@code ROLE_ADMIN}).
	 */
	private Rol rol;

	/**
	 * Cargo o puesto del empleado dentro del supermercado.
	 */
	private String cargo;

	/**
	 * Salario mensual del empleado.
	 */
	private Double salario;

	/**
	 * Identificador de la sucursal a la que está asignado el empleado.
	 * Se usa el ID en lugar del objeto completo para evitar referencias circulares.
	 */
	private Long idSucursal;

	/**
	 * Constructor por defecto de {@code EmpleadoDTO}.
	 */
	public EmpleadoDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar los campos del {@code EmpleadoDTO}.
	 *
	 * @param id                 Identificador del empleado.
	 * @param nombres            Nombres del empleado.
	 * @param apellidos          Apellidos del empleado.
	 * @param correo             Correo electrónico del empleado.
	 * @param contrasena         Contraseña del empleado.
	 * @param codigoVerificacion Código de verificación del empleado.
	 * @param rol                Rol del empleado.
	 * @param cargo              Cargo del empleado.
	 * @param salario            Salario del empleado.
	 * @param idSucursal         ID de la sucursal asignada al empleado.
	 */
	public EmpleadoDTO(Long id, String nombres, String apellidos, String correo, String contrasena,
			String codigoVerificacion, Rol rol, String cargo, Double salario, Long idSucursal) {
		this.id = id;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.correo = correo;
		this.contrasena = contrasena;
		this.codigoVerificacion = codigoVerificacion;
		this.rol = rol;
		this.cargo = cargo;
		this.salario = salario;
		this.idSucursal = idSucursal;
	}

	/**
	 * Obtiene el identificador único del empleado.
	 *
	 * @return El ID del empleado.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador único del empleado.
	 *
	 * @param id El nuevo ID del empleado.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene los nombres del empleado.
	 *
	 * @return Los nombres del empleado.
	 */
	public String getNombres() {
		return nombres;
	}

	/**
	 * Establece los nombres del empleado.
	 *
	 * @param nombres Los nuevos nombres del empleado.
	 */
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	/**
	 * Obtiene los apellidos del empleado.
	 *
	 * @return Los apellidos del empleado.
	 */
	public String getApellidos() {
		return apellidos;
	}

	/**
	 * Establece los apellidos del empleado.
	 *
	 * @param apellidos Los nuevos apellidos del empleado.
	 */
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	/**
	 * Obtiene el correo electrónico del empleado.
	 *
	 * @return El correo del empleado.
	 */
	public String getCorreo() {
		return correo;
	}

	/**
	 * Establece el correo electrónico del empleado.
	 *
	 * @param correo El nuevo correo del empleado.
	 */
	public void setCorreo(String correo) {
		this.correo = correo;
	}

	/**
	 * Obtiene la contraseña del empleado.
	 *
	 * @return La contraseña del empleado.
	 */
	public String getContrasena() {
		return contrasena;
	}

	/**
	 * Establece la contraseña del empleado.
	 *
	 * @param contrasena La nueva contraseña del empleado.
	 */
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	/**
	 * Obtiene el código de verificación del empleado.
	 *
	 * @return El código de verificación.
	 */
	public String getCodigoVerificacion() {
		return codigoVerificacion;
	}

	/**
	 * Establece el código de verificación del empleado.
	 *
	 * @param codigoVerificacion El nuevo código de verificación.
	 */
	public void setCodigoVerificacion(String codigoVerificacion) {
		this.codigoVerificacion = codigoVerificacion;
	}

	/**
	 * Obtiene el rol del empleado en el sistema.
	 *
	 * @return El rol del empleado.
	 */
	public Rol getRol() {
		return rol;
	}

	/**
	 * Establece el rol del empleado en el sistema.
	 *
	 * @param rol El nuevo rol del empleado.
	 */
	public void setRol(Rol rol) {
		this.rol = rol;
	}

	/**
	 * Obtiene el cargo del empleado.
	 *
	 * @return El cargo del empleado.
	 */
	public String getCargo() {
		return cargo;
	}

	/**
	 * Establece el cargo del empleado.
	 *
	 * @param cargo El nuevo cargo del empleado.
	 */
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	/**
	 * Obtiene el salario del empleado.
	 *
	 * @return El salario del empleado.
	 */
	public Double getSalario() {
		return salario;
	}

	/**
	 * Establece el salario del empleado.
	 *
	 * @param salario El nuevo salario del empleado.
	 */
	public void setSalario(Double salario) {
		this.salario = salario;
	}

	/**
	 * Obtiene el ID de la sucursal asignada al empleado.
	 *
	 * @return El ID de la sucursal.
	 */
	public Long getIdSucursal() {
		return idSucursal;
	}

	/**
	 * Establece el ID de la sucursal asignada al empleado.
	 *
	 * @param idSucursal El nuevo ID de la sucursal.
	 */
	public void setIdSucursal(Long idSucursal) {
		this.idSucursal = idSucursal;
	}

	/**
	 * Genera un código hash para el objeto {@code EmpleadoDTO} basado en su ID,
	 * correo y rol.
	 *
	 * @return El código hash del objeto.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, correo, rol);
	}

	/**
	 * Compara este objeto {@code EmpleadoDTO} con otro para determinar igualdad,
	 * basándose en el ID, correo y rol.
	 *
	 * @param obj El objeto a comparar.
	 * @return {@code true} si los objetos son iguales, {@code false} en caso contrario.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmpleadoDTO other = (EmpleadoDTO) obj;
		return Objects.equals(id, other.id) && Objects.equals(correo, other.correo)
				&& rol == other.rol;
	}

	/**
	 * Devuelve una representación en cadena del objeto {@code EmpleadoDTO}.
	 *
	 * @return Una cadena con los atributos del DTO del empleado.
	 */
	@Override
	public String toString() {
		return "EmpleadoDTO [id=" + id + ", nombres=" + nombres + ", apellidos=" + apellidos
				+ ", correo=" + correo + ", codigoVerificacion=" + codigoVerificacion
				+ ", rol=" + rol + ", cargo=" + cargo + ", salario=" + salario
				+ ", idSucursal=" + idSucursal + "]";
	}
}
