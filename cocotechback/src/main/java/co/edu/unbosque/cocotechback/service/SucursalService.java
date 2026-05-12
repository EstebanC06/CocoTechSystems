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
import org.springframework.stereotype.Service;

import co.edu.unbosque.cocotechback.dto.SucursalDTO;
import co.edu.unbosque.cocotechback.model.Sucursal;
import co.edu.unbosque.cocotechback.repository.SucursalRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Sucursal}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de sucursales del
 * supermercado.
 * <p>
 * La sucursal no maneja datos sensibles de usuario, por lo que los métodos
 * {@code encrypt}, {@code decrypt}, {@code updatePassword}, {@code updateCorreo},
 * {@code updateRol} y {@code updateCode} no aplican y retornan {@code -1} o
 * {@code null} según corresponda.
 */
@Service
public class SucursalService implements CRUDOperation<SucursalDTO, Sucursal> {

	/**
	 * Repositorio para la gestión de la entidad {@link Sucursal} en la base de
	 * datos.
	 */
	@Autowired
	private SucursalRepository sucursalRepo;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Constructor por defecto de {@code SucursalService}.
	 */
	public SucursalService() {
	}

	/**
	 * Crea una nueva sucursal en la base de datos a partir de un
	 * {@link SucursalDTO}.
	 * <p>
	 * Valida que los campos obligatorios estén presentes y que no exista ya una
	 * sucursal con el mismo nombre.
	 *
	 * @param data El {@link SucursalDTO} con la información de la nueva sucursal.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si ya existe una sucursal con ese nombre,
	 *         {@code 4} si algún campo requerido está ausente.
	 */
	@Override
	public int create(SucursalDTO data, String rol) {
		if (data.getNombre() == null || data.getNombre().isEmpty()
				|| data.getCiudad() == null || data.getCiudad().isEmpty()) {
			return 4;
		}
		if (sucursalRepo.existsByNombre(data.getNombre())) {
			return 1;
		}
		Sucursal entity = modelMapper.map(data, Sucursal.class);
		sucursalRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todas las sucursales registradas en la base de datos.
	 *
	 * @return Una lista de {@link SucursalDTO}. Retorna una lista vacía si no hay
	 *         sucursales registradas.
	 */
	@Override
	public List<SucursalDTO> getAll() {
		List<Sucursal> entityList = sucursalRepo.findAll();
		List<SucursalDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(modelMapper.map(entity, SucursalDTO.class)));
		return dtoList;
	}

	/**
	 * Obtiene una sucursal por su ID.
	 *
	 * @param id El ID de la sucursal a buscar.
	 * @return Un {@link SucursalDTO} con la información de la sucursal, o
	 *         {@code null} si no existe.
	 */
	public SucursalDTO getById(Long id) {
		Optional<Sucursal> found = sucursalRepo.findById(id);
		return found.isPresent() ? modelMapper.map(found.get(), SucursalDTO.class) : null;
	}

	/**
	 * Elimina una sucursal de la base de datos por su ID.
	 *
	 * @param id El ID de la sucursal a eliminar.
	 * @return {@code 0} si la eliminación fue exitosa,
	 *         {@code 2} si no existe ninguna sucursal con ese ID.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Sucursal> found = sucursalRepo.findById(id);
		if (found.isPresent()) {
			sucursalRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos de una sucursal existente por su ID.
	 * <p>
	 * Solo actualiza los campos no nulos del DTO recibido.
	 *
	 * @param id      El ID de la sucursal a actualizar.
	 * @param newData El {@link SucursalDTO} con los nuevos datos.
	 * @return {@code 0} si la actualización fue exitosa,
	 *         {@code 1} si el nuevo nombre ya está en uso,
	 *         {@code 2} si no existe ninguna sucursal con ese ID.
	 */
	@Override
	public int updateById(Long id, SucursalDTO newData) {
		Optional<Sucursal> found = sucursalRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Sucursal temp = found.get();
		if (newData.getNombre() != null && !newData.getNombre().equals(temp.getNombre())) {
			if (sucursalRepo.existsByNombre(newData.getNombre())) {
				return 1;
			}
			temp.setNombre(newData.getNombre());
		}
		if (newData.getTelefonoContacto() != null) {
			temp.setTelefonoContacto(newData.getTelefonoContacto());
		}
		if (newData.getCiudad() != null) {
			temp.setCiudad(newData.getCiudad());
		}
		if (newData.getBarrio() != null) {
			temp.setBarrio(newData.getBarrio());
		}
		if (newData.getCalle() != null) {
			temp.setCalle(newData.getCalle());
		}
		sucursalRepo.save(temp);
		return 0;
	}

	/**
	 * Cuenta el número total de sucursales registradas.
	 *
	 * @return El número total de sucursales.
	 */
	@Override
	public long count() {
		return sucursalRepo.count();
	}

	/**
	 * Verifica si existe una sucursal con el ID especificado.
	 *
	 * @param id El ID de la sucursal a verificar.
	 * @return {@code true} si existe, {@code false} en caso contrario.
	 */
	@Override
	public boolean exist(Long id) {
		return sucursalRepo.existsById(id);
	}

	/** No aplica para Sucursal. Retorna {@code null}. */
	@Override
	public Sucursal encrypt(SucursalDTO data) {
		return modelMapper.map(data, Sucursal.class);
	}

	/** No aplica para Sucursal. Retorna {@code null}. */
	@Override
	public String decrypt(SucursalDTO data) {
		return null;
	}

	/** No aplica para Sucursal. Retorna {@code -1}. */
	@Override
	public int updatePassword(Long id, SucursalDTO newData) {
		return -1;
	}

	/** No aplica para Sucursal. Retorna {@code -1}. */
	@Override
	public int updateCorreo(Long id, SucursalDTO newData) {
		return -1;
	}

	/** No aplica para Sucursal. Retorna {@code -1}. */
	@Override
	public int updateRol(Long id, SucursalDTO newData) {
		return -1;
	}

	/** No aplica para Sucursal. Retorna {@code -1}. */
	@Override
	public int updateCode(Long id, SucursalDTO newData) {
		return -1;
	}
}
