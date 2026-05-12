/**
 * Paquete que contiene las interfaces de Repositorio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.cocotechback.model.Empleado;

/**
 * Interfaz de repositorio para la entidad {@link Empleado}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar
 * sobre la tabla {@code empleado} en la base de datos MySQL. Además, define
 * métodos de consulta personalizados para buscar empleados por correo, cargo
 * y sucursal asignada.
 * <p>
 * El repositorio de empleados es crítico para el mecanismo de autenticación
 * de Spring Security, ya que los empleados son los usuarios con rol
 * {@code ROLE_ADMIN} del sistema.
 * <p>
 * Spring Data JPA genera la implementación de todos estos métodos
 * automáticamente en tiempo de ejecución a partir de la convención de nombres.
 */
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

	/**
	 * Busca un empleado por su correo electrónico.
	 * <p>
	 * Se utiliza principalmente en el proceso de autenticación para cargar el
	 * {@code UserDetails} del empleado mediante Spring Security.
	 *
	 * @param correo El correo electrónico del empleado a buscar.
	 * @return Un {@link Optional} con el empleado encontrado, o vacío si no existe
	 *         ningún empleado con ese correo.
	 */
	public Optional<Empleado> findByCorreo(String correo);

	/**
	 * Busca todos los empleados que desempeñen un cargo específico.
	 *
	 * @param cargo El cargo por el cual filtrar los empleados (ej. "Cajero",
	 *              "Gerente", "Bodeguero").
	 * @return Una lista de empleados con el cargo indicado. Retorna una lista
	 *         vacía si no hay coincidencias.
	 */
	public List<Empleado> findByCargo(String cargo);

	/**
	 * Busca todos los empleados asignados a una sucursal específica.
	 *
	 * @param idSucursal El identificador de la sucursal por la cual filtrar.
	 * @return Una lista de empleados asignados a la sucursal indicada. Retorna
	 *         una lista vacía si no hay coincidencias.
	 */
	public List<Empleado> findBySucursal_IdSucursal(Long idSucursal);

	/**
	 * Busca todos los empleados que coincidan con el nombre o el apellido
	 * proporcionado (ignorando mayúsculas y minúsculas).
	 *
	 * @param nombres   El nombre a buscar.
	 * @param apellidos El apellido a buscar.
	 * @return Una lista de empleados que coincidan con el nombre o apellido.
	 *         Retorna una lista vacía si no hay coincidencias.
	 */
	public List<Empleado> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
			String nombres, String apellidos);

	/**
	 * Verifica si ya existe un empleado registrado con el correo electrónico
	 * proporcionado.
	 *
	 * @param correo El correo electrónico a verificar.
	 * @return {@code true} si ya existe un empleado con ese correo,
	 *         {@code false} en caso contrario.
	 */
	public boolean existsByCorreo(String correo);

	/**
	 * Elimina un empleado de la base de datos por su correo electrónico.
	 *
	 * @param correo El correo electrónico del empleado a eliminar.
	 */
	public void deleteByCorreo(String correo);
}
