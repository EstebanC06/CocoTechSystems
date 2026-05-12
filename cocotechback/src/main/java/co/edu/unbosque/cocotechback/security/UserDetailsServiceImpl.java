/**
 * Paquete que contiene las clases relacionadas con la seguridad de la
 * aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.unbosque.cocotechback.repository.jpa.ClienteRepository;
import co.edu.unbosque.cocotechback.repository.jpa.EmpleadoRepository;
import co.edu.unbosque.cocotechback.util.AESUtil;

/**
 * Implementación del servicio {@link UserDetailsService} de Spring Security
 * para la aplicación CocoTech.
 * <p>
 * Este servicio es el puente entre Spring Security y la base de datos de
 * CocoTech. Es responsable de cargar los detalles de un usuario a partir de
 * su nombre de usuario (que en este sistema es el <strong>correo electrónico
 * encriptado con AES</strong> almacenado en la base de datos).
 * <p>
 * A diferencia del proyecto de referencia (que tenía una sola entidad
 * {@code User}), CocoTech tiene dos tipos de usuario en jerarquía separada:
 * <ul>
 * <li>{@link co.edu.unbosque.cocotechback.model.Empleado} con
 * {@code ROLE_ADMIN} — se busca primero.</li>
 * <li>{@link co.edu.unbosque.cocotechback.model.Cliente} con
 * {@code ROLE_CLIENTE} — se busca si no se encontró como empleado.</li>
 * </ul>
 * El método {@code loadUserByUsername} recibe el correo ya encriptado (tal
 * como viaja en el token JWT y se almacena en BD), lo busca en ambos
 * repositorios y retorna el {@link UserDetails} correspondiente.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	/**
	 * Repositorio para acceder a la información de los empleados.
	 */
	private final EmpleadoRepository empleadoRepository;

	/**
	 * Repositorio para acceder a la información de los clientes.
	 */
	private final ClienteRepository clienteRepository;

	/**
	 * Constructor con inyección de dependencias para los repositorios de
	 * empleados y clientes.
	 *
	 * @param empleadoRepository Repositorio de empleados.
	 * @param clienteRepository  Repositorio de clientes.
	 */
	public UserDetailsServiceImpl(EmpleadoRepository empleadoRepository,
			ClienteRepository clienteRepository) {
		this.empleadoRepository = empleadoRepository;
		this.clienteRepository = clienteRepository;
	}

	/**
	 * Carga los detalles de un usuario por su nombre de usuario (correo
	 * encriptado con AES).
	 * <p>
	 * El flujo de búsqueda es:
	 * <ol>
	 * <li>Busca en {@code EmpleadoRepository} por el correo encriptado recibido.
	 * Si se encuentra, retorna el {@link co.edu.unbosque.cocotechback.model.Empleado}
	 * (que implementa {@link UserDetails} con {@code ROLE_ADMIN}).</li>
	 * <li>Si no se encuentra como empleado, busca en {@code ClienteRepository}.
	 * Si se encuentra, retorna el {@link co.edu.unbosque.cocotechback.model.Cliente}
	 * (que implementa {@link UserDetails} con {@code ROLE_CLIENTE}).</li>
	 * <li>Si no se encuentra en ninguno de los dos repositorios, lanza
	 * {@link UsernameNotFoundException}.</li>
	 * </ol>
	 * <p>
	 * <strong>Nota importante:</strong> el parámetro {@code correoEncriptado}
	 * debe llegar ya cifrado con AES (es el subject del token JWT). No se
	 * re-encripta aquí porque el token ya lo almacena encriptado desde la
	 * generación.
	 *
	 * @param correoEncriptado El correo electrónico del usuario encriptado con
	 *                         AES, tal como se almacena en la base de datos y se
	 *                         usa como subject en el token JWT.
	 * @return Un objeto {@link UserDetails} que representa al empleado o cliente
	 *         encontrado.
	 * @throws UsernameNotFoundException Si no se encuentra ningún usuario
	 *                                   (empleado ni cliente) con ese correo.
	 */
	@Override
	public UserDetails loadUserByUsername(String correoEncriptado)
			throws UsernameNotFoundException {

		// Primero busca como Empleado (ROLE_ADMIN)
		return empleadoRepository.findByCorreo(correoEncriptado)
				.<UserDetails>map(empleado -> empleado)
				// Si no es empleado, busca como Cliente (ROLE_CLIENTE)
				.orElseGet(() -> clienteRepository.findByCorreo(correoEncriptado)
						.<UserDetails>map(cliente -> cliente)
						.orElseThrow(() -> new UsernameNotFoundException(
								"Usuario no encontrado con correo: "
										+ AESUtil.decrypt(correoEncriptado))));
	}
}
