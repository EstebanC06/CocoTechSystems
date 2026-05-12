/**
 * Paquete que contiene las clases de Servicio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.cocotechback.dto.ClienteDTO;
import co.edu.unbosque.cocotechback.model.Cliente;
import co.edu.unbosque.cocotechback.model.Usuario.Rol;
import co.edu.unbosque.cocotechback.repository.jpa.ClienteRepository;
import co.edu.unbosque.cocotechback.util.AESUtil;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Cliente}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de clientes. También
 * gestiona la encriptación AES de datos sensibles (correo y código de
 * verificación) y la codificación BCrypt de contraseñas.
 * <p>
 * Los clientes poseen el rol {@code ROLE_CLIENTE} y son asignados
 * automáticamente al momento de su creación.
 */
@Service
public class ClienteService implements CRUDOperation<ClienteDTO, Cliente> {

	/**
	 * Repositorio para la gestión de la entidad {@link Cliente} en la base de
	 * datos.
	 */
	@Autowired
	private ClienteRepository clienteRepo;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Codificador de contraseñas BCrypt para la seguridad de las credenciales
	 * de los clientes.
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Constructor por defecto de {@code ClienteService}.
	 */
	public ClienteService() {
	}

	/**
	 * Crea un nuevo cliente en la base de datos a partir de un {@link ClienteDTO}.
	 * <p>
	 * Valida que los campos obligatorios estén presentes, que el correo no esté
	 * ya registrado, encripta los datos sensibles con AES y codifica la contraseña
	 * con BCrypt antes de persistir. El rol {@code ROLE_CLIENTE} se asigna
	 * automáticamente.
	 *
	 * @param data El {@link ClienteDTO} con la información del nuevo cliente.
	 * @param rol  No utilizado en esta implementación (el rol es siempre
	 *             {@code ROLE_CLIENTE}).
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si el correo ya está registrado,
	 *         {@code 4} si algún campo requerido está ausente o tiene formato
	 *         inválido.
	 */
	@Override
	public int create(ClienteDTO data, String rol) {
		if (!areRequiredFieldsPresent(data)) {
			return 4;
		}
		if (clienteRepo.existsByCorreo(data.getCorreo())) {
			return 1;
		}
		Cliente entity = new Cliente();
		entity.setNombres(data.getNombres());
		entity.setApellidos(data.getApellidos());
		entity.setCorreo(AESUtil.encrypt(data.getCorreo()));
		entity.setContrasena(passwordEncoder.encode(data.getContrasena()));
		entity.setCodigoVerificacion(AESUtil.encrypt(
				data.getCodigoVerificacion() != null ? data.getCodigoVerificacion() : "0"));
		entity.setTelefono(data.getTelefono());
		entity.setCalle(data.getCalle());
		entity.setBarrio(data.getBarrio());
		entity.setCiudad(data.getCiudad());
		entity.setRol(Rol.ROLE_CLIENTE);
		clienteRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todos los clientes registrados en la base de datos y los retorna
	 * como lista de {@link ClienteDTO} con los datos sensibles desencriptados.
	 * La contraseña nunca se incluye en la respuesta.
	 *
	 * @return Una lista de {@link ClienteDTO}. Retorna una lista vacía si no hay
	 *         clientes registrados.
	 */
	@Override
	public List<ClienteDTO> getAll() {
		List<Cliente> entityList = clienteRepo.findAll();
		List<ClienteDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> {
			ClienteDTO dto = modelMapper.map(entity, ClienteDTO.class);
			if (dto.getCorreo() != null) {
				dto.setCorreo(AESUtil.decrypt(dto.getCorreo()));
			}
			if (dto.getCodigoVerificacion() != null) {
				dto.setCodigoVerificacion(AESUtil.decrypt(dto.getCodigoVerificacion()));
			}
			dto.setContrasena(null);
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Obtiene un cliente por su ID y lo retorna como {@link ClienteDTO} con los
	 * datos sensibles desencriptados. La contraseña nunca se incluye.
	 *
	 * @param id El ID del cliente a buscar.
	 * @return Un {@link ClienteDTO} con la información del cliente, o {@code null}
	 *         si no existe ningún cliente con ese ID.
	 */
	public ClienteDTO getById(Long id) {
		Optional<Cliente> found = clienteRepo.findById(id);
		if (found.isPresent()) {
			ClienteDTO dto = modelMapper.map(found.get(), ClienteDTO.class);
			if (dto.getCorreo() != null) {
				dto.setCorreo(AESUtil.decrypt(dto.getCorreo()));
			}
			if (dto.getCodigoVerificacion() != null) {
				dto.setCodigoVerificacion(AESUtil.decrypt(dto.getCodigoVerificacion()));
			}
			dto.setContrasena(null);
			return dto;
		}
		return null;
	}

	/**
	 * Busca un cliente por su correo electrónico y lo retorna como entidad JPA.
	 * <p>
	 * Utilizado internamente por el mecanismo de autenticación de Spring Security.
	 *
	 * @param correo El correo electrónico del cliente a buscar (sin encriptar).
	 * @return Un {@link Optional} con el {@link Cliente} encontrado, o vacío si
	 *         no existe.
	 */
	public Optional<Cliente> findByCorreo(String correo) {
		return clienteRepo.findByCorreo(AESUtil.encrypt(correo));
	}

	/**
	 * Elimina un cliente de la base de datos por su ID.
	 *
	 * @param id El ID del cliente a eliminar.
	 * @return {@code 0} si la eliminación fue exitosa,
	 *         {@code 2} si no existe ningún cliente con ese ID.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Cliente> found = clienteRepo.findById(id);
		if (found.isPresent()) {
			clienteRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos generales de un cliente existente por su ID.
	 * <p>
	 * Solo actualiza los campos no nulos del DTO recibido. El correo se encripta
	 * si cambia, y se verifica que el nuevo correo no esté en uso por otro cliente.
	 *
	 * @param id      El ID del cliente a actualizar.
	 * @param newData El {@link ClienteDTO} con los nuevos datos del cliente.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 1} si el nuevo correo ya está registrado por otro cliente,
	 *         {@code 2} si no existe ningún cliente con ese ID.
	 */
	@Override
	public int updateById(Long id, ClienteDTO newData) {
		Optional<Cliente> found = clienteRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Cliente temp = found.get();
		if (newData.getNombres() != null) {
			temp.setNombres(newData.getNombres());
		}
		if (newData.getApellidos() != null) {
			temp.setApellidos(newData.getApellidos());
		}
		if (newData.getCorreo() != null
				&& !AESUtil.encrypt(newData.getCorreo()).equals(temp.getCorreo())) {
			if (clienteRepo.existsByCorreo(newData.getCorreo())) {
				return 1;
			}
			temp.setCorreo(AESUtil.encrypt(newData.getCorreo()));
		}
		if (newData.getTelefono() != null) {
			temp.setTelefono(newData.getTelefono());
		}
		if (newData.getCalle() != null) {
			temp.setCalle(newData.getCalle());
		}
		if (newData.getBarrio() != null) {
			temp.setBarrio(newData.getBarrio());
		}
		if (newData.getCiudad() != null) {
			temp.setCiudad(newData.getCiudad());
		}
		clienteRepo.save(temp);
		return 0;
	}

	/**
	 * Actualiza la contraseña de un cliente existente por su ID.
	 * <p>
	 * La nueva contraseña se codifica con BCrypt antes de persistirse.
	 *
	 * @param id      El ID del cliente cuya contraseña se va a actualizar.
	 * @param newData El {@link ClienteDTO} con la nueva contraseña en texto plano.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 2} si no existe ningún cliente con ese ID,
	 *         {@code 4} si la nueva contraseña es nula o vacía.
	 */
	@Override
	public int updatePassword(Long id, ClienteDTO newData) {
		if (newData.getContrasena() == null || newData.getContrasena().isEmpty()) {
			return 4;
		}
		Optional<Cliente> found = clienteRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Cliente temp = found.get();
		temp.setContrasena(passwordEncoder.encode(newData.getContrasena()));
		clienteRepo.save(temp);
		return 0;
	}

	/**
	 * Actualiza el correo electrónico de un cliente existente por su ID.
	 * <p>
	 * Verifica que el nuevo correo no esté en uso y lo encripta con AES antes
	 * de persistirlo.
	 *
	 * @param id      El ID del cliente cuyo correo se va a actualizar.
	 * @param newData El {@link ClienteDTO} con el nuevo correo electrónico.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 1} si el nuevo correo ya está en uso por otro cliente,
	 *         {@code 2} si no existe ningún cliente con ese ID,
	 *         {@code 4} si el nuevo correo es nulo o vacío.
	 */
	@Override
	public int updateCorreo(Long id, ClienteDTO newData) {
		if (newData.getCorreo() == null || newData.getCorreo().isEmpty()) {
			return 4;
		}
		Optional<Cliente> found = clienteRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		if (clienteRepo.existsByCorreo(newData.getCorreo())) {
			return 1;
		}
		Cliente temp = found.get();
		temp.setCorreo(AESUtil.encrypt(newData.getCorreo()));
		clienteRepo.save(temp);
		return 0;
	}

	/**
	 * Actualiza el rol de un cliente existente por su ID.
	 * <p>
	 * Reinicia el código de verificación a "0" (encriptado) tras el cambio de rol.
	 *
	 * @param id      El ID del cliente cuyo rol se va a actualizar.
	 * @param newData El {@link ClienteDTO} con el nuevo rol.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 2} si no existe ningún cliente con ese ID.
	 */
	@Override
	public int updateRol(Long id, ClienteDTO newData) {
		Optional<Cliente> found = clienteRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Cliente temp = found.get();
		if (newData.getRol() != null) {
			temp.setRol(newData.getRol());
		}
		temp.setCodigoVerificacion(AESUtil.encrypt("0"));
		clienteRepo.save(temp);
		return 0;
	}

	/**
	 * Actualiza el código de verificación de un cliente existente por su ID.
	 * <p>
	 * El nuevo código se encripta con AES antes de persistirse.
	 *
	 * @param id      El ID del cliente cuyo código se va a actualizar.
	 * @param newData El {@link ClienteDTO} con el nuevo código de verificación.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 2} si no existe ningún cliente con ese ID.
	 */
	@Override
	public int updateCode(Long id, ClienteDTO newData) {
		Optional<Cliente> found = clienteRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Cliente temp = found.get();
		if (newData.getCodigoVerificacion() != null) {
			temp.setCodigoVerificacion(AESUtil.encrypt(newData.getCodigoVerificacion()));
		}
		clienteRepo.save(temp);
		return 0;
	}

	/**
	 * Cuenta el número total de clientes registrados en la base de datos.
	 *
	 * @return El número total de clientes.
	 */
	@Override
	public long count() {
		return clienteRepo.count();
	}

	/**
	 * Verifica si existe un cliente con el ID especificado.
	 *
	 * @param id El ID del cliente a verificar.
	 * @return {@code true} si existe, {@code false} en caso contrario.
	 */
	@Override
	public boolean exist(Long id) {
		return clienteRepo.existsById(id);
	}

	/**
	 * Encripta los datos sensibles de un {@link ClienteDTO} y retorna un
	 * {@link Cliente} con los campos cifrados.
	 * <p>
	 * Campos encriptados: correo y código de verificación.
	 * La contraseña se codifica por separado con BCrypt, no en este método.
	 *
	 * @param data El {@link ClienteDTO} con los datos a encriptar.
	 * @return Un {@link Cliente} con los datos sensibles encriptados.
	 */
	@Override
	public Cliente encrypt(ClienteDTO data) {
		Cliente entity = modelMapper.map(data, Cliente.class);
		if (entity.getCorreo() != null) {
			entity.setCorreo(AESUtil.encrypt(entity.getCorreo()));
		}
		if (entity.getCodigoVerificacion() != null) {
			entity.setCodigoVerificacion(AESUtil.encrypt(entity.getCodigoVerificacion()));
		}
		return entity;
	}

	/**
	 * Desencripta los datos sensibles de un {@link ClienteDTO}.
	 * <p>
	 * Modifica directamente el objeto DTO proporcionado desencriptando correo y
	 * código de verificación.
	 *
	 * @param data El {@link ClienteDTO} cuyos datos se van a desencriptar.
	 * @return {@code null} (los datos se modifican directamente en el DTO).
	 */
	@Override
	public String decrypt(ClienteDTO data) {
		if (data.getCorreo() != null) {
			data.setCorreo(AESUtil.decrypt(data.getCorreo()));
		}
		if (data.getCodigoVerificacion() != null) {
			data.setCodigoVerificacion(AESUtil.decrypt(data.getCodigoVerificacion()));
		}
		return null;
	}

	/**
	 * Recupera la contraseña de un cliente buscándolo por correo electrónico y
	 * actualizando su contraseña con la nueva proporcionada.
	 * <p>
	 * Utilizado en el flujo de recuperación de cuenta.
	 *
	 * @param data El {@link ClienteDTO} con el correo del cliente y la nueva
	 *             contraseña.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 2} si no existe ningún cliente con ese correo,
	 *         {@code 4} si la nueva contraseña es nula o vacía.
	 */
	public int rememberPassword(ClienteDTO data) {
		if (data.getContrasena() == null || data.getContrasena().isEmpty()) {
			return 4;
		}
		Optional<Cliente> found = clienteRepo.findByCorreo(AESUtil.encrypt(data.getCorreo()));
		if (!found.isPresent()) {
			return 2;
		}
		Cliente temp = found.get();
		temp.setContrasena(passwordEncoder.encode(data.getContrasena()));
		clienteRepo.save(temp);
		return 0;
	}

	/**
	 * Verifica si los campos obligatorios del {@link ClienteDTO} están presentes
	 * y no son vacíos.
	 *
	 * @param data El {@link ClienteDTO} a validar.
	 * @return {@code true} si todos los campos obligatorios están presentes,
	 *         {@code false} en caso contrario.
	 */
	private boolean areRequiredFieldsPresent(ClienteDTO data) {
		return data.getNombres() != null && !data.getNombres().isEmpty()
				&& data.getApellidos() != null && !data.getApellidos().isEmpty()
				&& data.getCorreo() != null && !data.getCorreo().isEmpty()
				&& data.getContrasena() != null && !data.getContrasena().isEmpty();
	}
}
