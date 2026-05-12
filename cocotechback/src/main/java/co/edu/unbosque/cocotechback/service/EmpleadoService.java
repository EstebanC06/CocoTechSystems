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

import co.edu.unbosque.cocotechback.dto.EmpleadoDTO;
import co.edu.unbosque.cocotechback.model.Empleado;
import co.edu.unbosque.cocotechback.model.Sucursal;
import co.edu.unbosque.cocotechback.model.Usuario.Rol;
import co.edu.unbosque.cocotechback.repository.jpa.EmpleadoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.SucursalRepository;
import co.edu.unbosque.cocotechback.util.AESUtil;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Empleado}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de empleados. Gestiona la
 * encriptación AES de datos sensibles (correo y código de verificación) y la
 * codificación BCrypt de contraseñas.
 * <p>
 * Los empleados poseen el rol {@code ROLE_ADMIN} y son los usuarios con acceso
 * total al sistema de gestión del supermercado.
 */
@Service
public class EmpleadoService implements CRUDOperation<EmpleadoDTO, Empleado> {

	/**
	 * Repositorio para la gestión de la entidad {@link Empleado} en la base de
	 * datos.
	 */
	@Autowired
	private EmpleadoRepository empleadoRepo;

	/**
	 * Repositorio para la gestión de la entidad {@link Sucursal} en la base de
	 * datos. Necesario para resolver la asociación de sucursal del empleado.
	 */
	@Autowired
	private SucursalRepository sucursalRepo;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Codificador de contraseñas BCrypt para la seguridad de las credenciales
	 * de los empleados.
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Constructor por defecto de {@code EmpleadoService}.
	 */
	public EmpleadoService() {
	}

	/**
	 * Crea un nuevo empleado en la base de datos a partir de un
	 * {@link EmpleadoDTO}.
	 * <p>
	 * Valida que los campos obligatorios estén presentes, que el correo no esté
	 * ya registrado y que la sucursal asignada exista. Encripta los datos
	 * sensibles con AES y codifica la contraseña con BCrypt antes de persistir.
	 * El rol {@code ROLE_ADMIN} se asigna automáticamente.
	 *
	 * @param data El {@link EmpleadoDTO} con la información del nuevo empleado.
	 * @param rol  No utilizado en esta implementación (el rol es siempre
	 *             {@code ROLE_ADMIN}).
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si el correo ya está registrado,
	 *         {@code 2} si la sucursal asignada no existe,
	 *         {@code 4} si algún campo requerido está ausente.
	 */
	@Override
	public int create(EmpleadoDTO data, String rol) {
		if (!areRequiredFieldsPresent(data)) {
			return 4;
		}
		if (empleadoRepo.existsByCorreo(data.getCorreo())) {
			return 1;
		}
		Optional<Sucursal> sucursalFound = sucursalRepo.findById(data.getIdSucursal());
		if (!sucursalFound.isPresent()) {
			return 2;
		}
		Empleado entity = new Empleado();
		entity.setNombres(data.getNombres());
		entity.setApellidos(data.getApellidos());
		entity.setCorreo(AESUtil.encrypt(data.getCorreo()));
		entity.setContrasena(passwordEncoder.encode(data.getContrasena()));
		entity.setCodigoVerificacion(AESUtil.encrypt(
				data.getCodigoVerificacion() != null ? data.getCodigoVerificacion() : "0"));
		entity.setCargo(data.getCargo());
		entity.setSalario(data.getSalario());
		entity.setSucursal(sucursalFound.get());
		entity.setRol(Rol.ROLE_ADMIN);
		empleadoRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todos los empleados registrados en la base de datos y los retorna
	 * como lista de {@link EmpleadoDTO} con los datos sensibles desencriptados.
	 * La contraseña nunca se incluye en la respuesta.
	 *
	 * @return Una lista de {@link EmpleadoDTO}. Retorna una lista vacía si no hay
	 *         empleados registrados.
	 */
	@Override
	public List<EmpleadoDTO> getAll() {
		List<Empleado> entityList = empleadoRepo.findAll();
		List<EmpleadoDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> {
			EmpleadoDTO dto = modelMapper.map(entity, EmpleadoDTO.class);
			if (dto.getCorreo() != null) {
				dto.setCorreo(AESUtil.decrypt(dto.getCorreo()));
			}
			if (dto.getCodigoVerificacion() != null) {
				dto.setCodigoVerificacion(AESUtil.decrypt(dto.getCodigoVerificacion()));
			}
			dto.setContrasena(null);
			if (entity.getSucursal() != null) {
				dto.setIdSucursal(entity.getSucursal().getIdSucursal());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Obtiene un empleado por su ID y lo retorna como {@link EmpleadoDTO} con los
	 * datos sensibles desencriptados. La contraseña nunca se incluye.
	 *
	 * @param id El ID del empleado a buscar.
	 * @return Un {@link EmpleadoDTO} con la información del empleado, o
	 *         {@code null} si no existe ningún empleado con ese ID.
	 */
	public EmpleadoDTO getById(Long id) {
		Optional<Empleado> found = empleadoRepo.findById(id);
		if (found.isPresent()) {
			Empleado entity = found.get();
			EmpleadoDTO dto = modelMapper.map(entity, EmpleadoDTO.class);
			if (dto.getCorreo() != null) {
				dto.setCorreo(AESUtil.decrypt(dto.getCorreo()));
			}
			if (dto.getCodigoVerificacion() != null) {
				dto.setCodigoVerificacion(AESUtil.decrypt(dto.getCodigoVerificacion()));
			}
			dto.setContrasena(null);
			if (entity.getSucursal() != null) {
				dto.setIdSucursal(entity.getSucursal().getIdSucursal());
			}
			return dto;
		}
		return null;
	}

	/**
	 * Busca un empleado por su correo electrónico y lo retorna como entidad JPA.
	 * <p>
	 * Utilizado internamente por el mecanismo de autenticación de Spring Security.
	 *
	 * @param correo El correo electrónico del empleado a buscar (sin encriptar).
	 * @return Un {@link Optional} con el {@link Empleado} encontrado, o vacío si
	 *         no existe.
	 */
	public Optional<Empleado> findByCorreo(String correo) {
		return empleadoRepo.findByCorreo(AESUtil.encrypt(correo));
	}

	/**
	 * Elimina un empleado de la base de datos por su ID.
	 *
	 * @param id El ID del empleado a eliminar.
	 * @return {@code 0} si la eliminación fue exitosa,
	 *         {@code 2} si no existe ningún empleado con ese ID.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Empleado> found = empleadoRepo.findById(id);
		if (found.isPresent()) {
			empleadoRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos generales de un empleado existente por su ID.
	 * <p>
	 * Solo actualiza los campos no nulos del DTO recibido. Verifica que el nuevo
	 * correo no esté en uso y que la nueva sucursal exista si se cambia.
	 *
	 * @param id      El ID del empleado a actualizar.
	 * @param newData El {@link EmpleadoDTO} con los nuevos datos del empleado.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 1} si el nuevo correo ya está registrado,
	 *         {@code 2} si no existe el empleado o la nueva sucursal no existe.
	 */
	@Override
	public int updateById(Long id, EmpleadoDTO newData) {
		Optional<Empleado> found = empleadoRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Empleado temp = found.get();
		if (newData.getNombres() != null) {
			temp.setNombres(newData.getNombres());
		}
		if (newData.getApellidos() != null) {
			temp.setApellidos(newData.getApellidos());
		}
		if (newData.getCorreo() != null
				&& !AESUtil.encrypt(newData.getCorreo()).equals(temp.getCorreo())) {
			if (empleadoRepo.existsByCorreo(newData.getCorreo())) {
				return 1;
			}
			temp.setCorreo(AESUtil.encrypt(newData.getCorreo()));
		}
		if (newData.getCargo() != null) {
			temp.setCargo(newData.getCargo());
		}
		if (newData.getSalario() != null) {
			temp.setSalario(newData.getSalario());
		}
		if (newData.getIdSucursal() != null) {
			Optional<Sucursal> sucursalFound = sucursalRepo.findById(newData.getIdSucursal());
			if (!sucursalFound.isPresent()) {
				return 2;
			}
			temp.setSucursal(sucursalFound.get());
		}
		empleadoRepo.save(temp);
		return 0;
	}

	/**
	 * Actualiza la contraseña de un empleado existente por su ID.
	 * <p>
	 * La nueva contraseña se codifica con BCrypt antes de persistirse.
	 *
	 * @param id      El ID del empleado cuya contraseña se va a actualizar.
	 * @param newData El {@link EmpleadoDTO} con la nueva contraseña en texto plano.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 2} si no existe ningún empleado con ese ID,
	 *         {@code 4} si la nueva contraseña es nula o vacía.
	 */
	@Override
	public int updatePassword(Long id, EmpleadoDTO newData) {
		if (newData.getContrasena() == null || newData.getContrasena().isEmpty()) {
			return 4;
		}
		Optional<Empleado> found = empleadoRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Empleado temp = found.get();
		temp.setContrasena(passwordEncoder.encode(newData.getContrasena()));
		empleadoRepo.save(temp);
		return 0;
	}

	/**
	 * Actualiza el correo electrónico de un empleado existente por su ID.
	 * <p>
	 * Verifica que el nuevo correo no esté en uso y lo encripta con AES.
	 *
	 * @param id      El ID del empleado cuyo correo se va a actualizar.
	 * @param newData El {@link EmpleadoDTO} con el nuevo correo electrónico.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 1} si el nuevo correo ya está en uso,
	 *         {@code 2} si no existe ningún empleado con ese ID,
	 *         {@code 4} si el nuevo correo es nulo o vacío.
	 */
	@Override
	public int updateCorreo(Long id, EmpleadoDTO newData) {
		if (newData.getCorreo() == null || newData.getCorreo().isEmpty()) {
			return 4;
		}
		Optional<Empleado> found = empleadoRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		if (empleadoRepo.existsByCorreo(newData.getCorreo())) {
			return 1;
		}
		Empleado temp = found.get();
		temp.setCorreo(AESUtil.encrypt(newData.getCorreo()));
		empleadoRepo.save(temp);
		return 0;
	}

	/**
	 * Actualiza el rol de un empleado existente por su ID.
	 * <p>
	 * Reinicia el código de verificación a "0" (encriptado) tras el cambio de rol.
	 *
	 * @param id      El ID del empleado cuyo rol se va a actualizar.
	 * @param newData El {@link EmpleadoDTO} con el nuevo rol.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 2} si no existe ningún empleado con ese ID.
	 */
	@Override
	public int updateRol(Long id, EmpleadoDTO newData) {
		Optional<Empleado> found = empleadoRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Empleado temp = found.get();
		if (newData.getRol() != null) {
			temp.setRol(newData.getRol());
		}
		temp.setCodigoVerificacion(AESUtil.encrypt("0"));
		empleadoRepo.save(temp);
		return 0;
	}

	/**
	 * Actualiza el código de verificación de un empleado existente por su ID.
	 * <p>
	 * El nuevo código se encripta con AES antes de persistirse.
	 *
	 * @param id      El ID del empleado cuyo código se va a actualizar.
	 * @param newData El {@link EmpleadoDTO} con el nuevo código de verificación.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 2} si no existe ningún empleado con ese ID.
	 */
	@Override
	public int updateCode(Long id, EmpleadoDTO newData) {
		Optional<Empleado> found = empleadoRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Empleado temp = found.get();
		if (newData.getCodigoVerificacion() != null) {
			temp.setCodigoVerificacion(AESUtil.encrypt(newData.getCodigoVerificacion()));
		}
		empleadoRepo.save(temp);
		return 0;
	}

	/**
	 * Cuenta el número total de empleados registrados en la base de datos.
	 *
	 * @return El número total de empleados.
	 */
	@Override
	public long count() {
		return empleadoRepo.count();
	}

	/**
	 * Verifica si existe un empleado con el ID especificado.
	 *
	 * @param id El ID del empleado a verificar.
	 * @return {@code true} si existe, {@code false} en caso contrario.
	 */
	@Override
	public boolean exist(Long id) {
		return empleadoRepo.existsById(id);
	}

	/**
	 * Encripta los datos sensibles de un {@link EmpleadoDTO} y retorna un
	 * {@link Empleado} con los campos cifrados.
	 * <p>
	 * Campos encriptados: correo y código de verificación.
	 *
	 * @param data El {@link EmpleadoDTO} con los datos a encriptar.
	 * @return Un {@link Empleado} con los datos sensibles encriptados.
	 */
	@Override
	public Empleado encrypt(EmpleadoDTO data) {
		Empleado entity = modelMapper.map(data, Empleado.class);
		if (entity.getCorreo() != null) {
			entity.setCorreo(AESUtil.encrypt(entity.getCorreo()));
		}
		if (entity.getCodigoVerificacion() != null) {
			entity.setCodigoVerificacion(AESUtil.encrypt(entity.getCodigoVerificacion()));
		}
		return entity;
	}

	/**
	 * Desencripta los datos sensibles de un {@link EmpleadoDTO}.
	 * <p>
	 * Modifica directamente el objeto DTO desencriptando correo y código de
	 * verificación.
	 *
	 * @param data El {@link EmpleadoDTO} cuyos datos se van a desencriptar.
	 * @return {@code null} (los datos se modifican directamente en el DTO).
	 */
	@Override
	public String decrypt(EmpleadoDTO data) {
		if (data.getCorreo() != null) {
			data.setCorreo(AESUtil.decrypt(data.getCorreo()));
		}
		if (data.getCodigoVerificacion() != null) {
			data.setCodigoVerificacion(AESUtil.decrypt(data.getCodigoVerificacion()));
		}
		return null;
	}

	/**
	 * Recupera la contraseña de un empleado buscándolo por correo electrónico y
	 * actualizando su contraseña con la nueva proporcionada.
	 *
	 * @param data El {@link EmpleadoDTO} con el correo del empleado y la nueva
	 *             contraseña.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 2} si no existe ningún empleado con ese correo,
	 *         {@code 4} si la nueva contraseña es nula o vacía.
	 */
	public int rememberPassword(EmpleadoDTO data) {
		if (data.getContrasena() == null || data.getContrasena().isEmpty()) {
			return 4;
		}
		Optional<Empleado> found = empleadoRepo.findByCorreo(AESUtil.encrypt(data.getCorreo()));
		if (!found.isPresent()) {
			return 2;
		}
		Empleado temp = found.get();
		temp.setContrasena(passwordEncoder.encode(data.getContrasena()));
		empleadoRepo.save(temp);
		return 0;
	}

	/**
	 * Verifica si los campos obligatorios del {@link EmpleadoDTO} están presentes
	 * y no son vacíos.
	 *
	 * @param data El {@link EmpleadoDTO} a validar.
	 * @return {@code true} si todos los campos obligatorios están presentes,
	 *         {@code false} en caso contrario.
	 */
	private boolean areRequiredFieldsPresent(EmpleadoDTO data) {
		return data.getNombres() != null && !data.getNombres().isEmpty()
				&& data.getApellidos() != null && !data.getApellidos().isEmpty()
				&& data.getCorreo() != null && !data.getCorreo().isEmpty()
				&& data.getContrasena() != null && !data.getContrasena().isEmpty()
				&& data.getCargo() != null && !data.getCargo().isEmpty()
				&& data.getIdSucursal() != null;
	}
}
