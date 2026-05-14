/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa a un empleado del supermercado.
 * <p>
 * Hereda los atributos comunes de autenticación y datos personales de
 * {@link Usuario}. Por defecto, un empleado nuevo recibe el rol
 * {@code ROLE_EMPLEADO}, que le otorga permisos operativos: gestionar
 * pedidos online de su sucursal, operar el punto de venta físico y consultar
 * el inventario en modo lectura. Los empleados con cargo "Gerente de Sucursal"
 * son elevados a {@code ROLE_ADMIN} desde {@code LoadDatabase}.
 * <p>
 * Según los supuestos del sistema:
 * <ul>
 * <li>Varios empleados son asignados a una sola sucursal (pueden ser
 * transferidos entre sucursales).</li>
 * <li>Cada empleado gestiona una única caja registradora.</li>
 * <li>Un empleado registra varias ventas a lo largo de su jornada laboral.</li>
 * </ul>
 */
@Entity
@Table(name = "empleado")
public class Empleado extends Usuario {

	/**
	 * Identificador único para la serialización de objetos de esta clase.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Cargo o puesto del empleado dentro del supermercado
	 * (ej. Cajero, Gerente, Bodeguero).
	 */
	private String cargo;

	/**
	 * Salario mensual del empleado.
	 */
	private Double salario;

	/**
	 * Sucursal a la que está asignado este empleado.
	 * Muchos empleados pertenecen a una sola sucursal.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sucursal", nullable = false)
	private Sucursal sucursal;

	/**
	 * Caja registradora que gestiona este empleado.
	 * Un empleado gestiona una única caja registradora.
	 */
	@OneToOne(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private CajaRegistradora cajaRegistradora;

	/**
	 * Lista de ventas registradas por este empleado durante su jornada laboral.
	 * Un empleado puede registrar varias ventas.
	 */
	@OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Venta> ventas;

	/**
	 * Constructor por defecto. Asigna automáticamente el rol
	 * {@code ROLE_EMPLEADO} al crear un empleado.
	 * <p>
	 * Un administrador específico debe ser elevado manualmente a
	 * {@code ROLE_ADMIN} mediante el endpoint {@code /empleado/cambiarRol}
	 * o desde el seeder de datos {@link LoadDatabase}.
	 */
	public Empleado() {
		super();
		this.setRol(Rol.ROLE_EMPLEADO);
	}

	/**
	 * Constructor con parámetros para inicializar los datos del empleado.
	 *
	 * @param nombres            Nombres del empleado.
	 * @param apellidos          Apellidos del empleado.
	 * @param correo             Correo electrónico del empleado.
	 * @param contrasena         Contraseña del empleado.
	 * @param codigoVerificacion Código de verificación del empleado.
	 * @param cargo              Cargo del empleado.
	 * @param salario            Salario del empleado.
	 * @param sucursal           Sucursal asignada al empleado.
	 */
	public Empleado(String nombres, String apellidos, String correo, String contrasena,
			String codigoVerificacion, String cargo, Double salario, Sucursal sucursal) {
		super(nombres, apellidos, correo, contrasena, codigoVerificacion);
		this.setRol(Rol.ROLE_EMPLEADO);
		this.cargo = cargo;
		this.salario = salario;
		this.sucursal = sucursal;
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
	 * Obtiene la sucursal asignada al empleado.
	 *
	 * @return La sucursal del empleado.
	 */
	public Sucursal getSucursal() {
		return sucursal;
	}

	/**
	 * Establece la sucursal asignada al empleado.
	 *
	 * @param sucursal La nueva sucursal del empleado.
	 */
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	/**
	 * Obtiene la caja registradora gestionada por el empleado.
	 *
	 * @return La caja registradora del empleado.
	 */
	public CajaRegistradora getCajaRegistradora() {
		return cajaRegistradora;
	}

	/**
	 * Establece la caja registradora gestionada por el empleado.
	 *
	 * @param cajaRegistradora La nueva caja registradora del empleado.
	 */
	public void setCajaRegistradora(CajaRegistradora cajaRegistradora) {
		this.cajaRegistradora = cajaRegistradora;
	}

	/**
	 * Obtiene la lista de ventas registradas por el empleado.
	 *
	 * @return La lista de ventas del empleado.
	 */
	public List<Venta> getVentas() {
		return ventas;
	}

	/**
	 * Establece la lista de ventas registradas por el empleado.
	 *
	 * @param ventas La nueva lista de ventas del empleado.
	 */
	public void setVentas(List<Venta> ventas) {
		this.ventas = ventas;
	}

	/**
	 * Devuelve una representación en cadena del objeto Empleado.
	 *
	 * @return Una cadena con los atributos del empleado.
	 */
	@Override
	public String toString() {
		return "Empleado [id=" + getId() + ", nombres=" + getNombres() + ", apellidos=" + getApellidos()
				+ ", correo=" + getCorreo() + ", cargo=" + cargo + ", salario=" + salario
				+ ", sucursal=" + (sucursal != null ? sucursal.getIdSucursal() : "null")
				+ ", rol=" + getRol() + "]";
	}
}