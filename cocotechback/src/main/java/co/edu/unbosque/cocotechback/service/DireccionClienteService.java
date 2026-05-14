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
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.cocotechback.dto.DireccionClienteDTO;
import co.edu.unbosque.cocotechback.model.Cliente;
import co.edu.unbosque.cocotechback.model.DireccionCliente;
import co.edu.unbosque.cocotechback.repository.jpa.ClienteRepository;
import co.edu.unbosque.cocotechback.repository.jpa.DireccionClienteRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link DireccionCliente}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de direcciones de cliente.
 * Gestiona la unicidad de la dirección predeterminada por cliente.
 */
@Service
public class DireccionClienteService implements CRUDOperation<DireccionClienteDTO, DireccionCliente> {

	/** Repositorio de direcciones. */
	@Autowired
	private DireccionClienteRepository direccionRepo;

	/** Repositorio de clientes para resolver la relación. */
	@Autowired
	private ClienteRepository clienteRepo;

	/** Mapper para la conversión DTO ↔ entidad. */
	@Autowired
	private ModelMapper modelMapper;

	/** Constructor por defecto. */
	public DireccionClienteService() {
	}

	/**
	 * Crea una nueva dirección para un cliente.
	 * <p>
	 * Si la dirección se marca como predeterminada, primero se desmarcan todas
	 * las anteriores del cliente.
	 *
	 * @param data DTO con los datos de la dirección.
	 * @param rol  No utilizado.
	 * @return {@code 0} si fue exitoso,
	 *         {@code 2} si el cliente no existe,
	 *         {@code 4} si faltan campos requeridos.
	 */
	@Override
	@Transactional
	public int create(DireccionClienteDTO data, String rol) {
		if (data.getIdCliente() == null || data.getAlias() == null || data.getAlias().isEmpty()
				|| data.getCalle() == null || data.getCalle().isEmpty()
				|| data.getCiudad() == null || data.getCiudad().isEmpty()) {
			return 4;
		}
		Optional<Cliente> clienteFound = clienteRepo.findById(data.getIdCliente());
		if (!clienteFound.isPresent()) {
			return 2;
		}
		// Si será predeterminada, desmarcar las anteriores
		if (Boolean.TRUE.equals(data.getPredeterminada())) {
			direccionRepo.desmarcarPredeterminadas(data.getIdCliente());
		}
		DireccionCliente entity = new DireccionCliente();
		entity.setCliente(clienteFound.get());
		entity.setAlias(data.getAlias());
		entity.setCalle(data.getCalle());
		entity.setBarrio(data.getBarrio());
		entity.setCiudad(data.getCiudad());
		entity.setReferencia(data.getReferencia());
		entity.setPredeterminada(
				data.getPredeterminada() != null ? data.getPredeterminada() : false);
		direccionRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todas las direcciones del sistema (uso administrativo).
	 *
	 * @return Lista de todas las direcciones.
	 */
	@Override
	public List<DireccionClienteDTO> getAll() {
		List<DireccionCliente> entityList = direccionRepo.findAll();
		List<DireccionClienteDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(toDTO(entity)));
		return dtoList;
	}

	/**
	 * Obtiene todas las direcciones de un cliente específico.
	 *
	 * @param idCliente ID del cliente.
	 * @return Lista de direcciones del cliente.
	 */
	public List<DireccionClienteDTO> getByCliente(Long idCliente) {
		List<DireccionCliente> entityList = direccionRepo.findByCliente_Id(idCliente);
		List<DireccionClienteDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(toDTO(entity)));
		return dtoList;
	}

	/**
	 * Obtiene una dirección por su ID.
	 *
	 * @param id ID de la dirección.
	 * @return DTO de la dirección o {@code null} si no existe.
	 */
	public DireccionClienteDTO getById(Long id) {
		Optional<DireccionCliente> found = direccionRepo.findById(id);
		return found.isPresent() ? toDTO(found.get()) : null;
	}

	/**
	 * Actualiza una dirección existente.
	 *
	 * @param id      ID de la dirección a actualizar.
	 * @param newData DTO con los nuevos datos.
	 * @return {@code 0} si fue exitoso, {@code 2} si no existe.
	 */
	@Override
	@Transactional
	public int updateById(Long id, DireccionClienteDTO newData) {
		Optional<DireccionCliente> found = direccionRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		DireccionCliente temp = found.get();
		if (newData.getAlias() != null) temp.setAlias(newData.getAlias());
		if (newData.getCalle() != null) temp.setCalle(newData.getCalle());
		if (newData.getBarrio() != null) temp.setBarrio(newData.getBarrio());
		if (newData.getCiudad() != null) temp.setCiudad(newData.getCiudad());
		if (newData.getReferencia() != null) temp.setReferencia(newData.getReferencia());
		if (newData.getPredeterminada() != null) {
			if (Boolean.TRUE.equals(newData.getPredeterminada())
					&& !Boolean.TRUE.equals(temp.getPredeterminada())) {
				direccionRepo.desmarcarPredeterminadas(temp.getCliente().getId());
			}
			temp.setPredeterminada(newData.getPredeterminada());
		}
		direccionRepo.save(temp);
		return 0;
	}

	/**
	 * Marca una dirección como predeterminada (y desmarca las demás del cliente).
	 *
	 * @param id ID de la dirección a marcar.
	 * @return {@code 0} si fue exitoso, {@code 2} si no existe.
	 */
	@Transactional
	public int marcarPredeterminada(Long id) {
		Optional<DireccionCliente> found = direccionRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		DireccionCliente dir = found.get();
		direccionRepo.desmarcarPredeterminadas(dir.getCliente().getId());
		dir.setPredeterminada(true);
		direccionRepo.save(dir);
		return 0;
	}

	/**
	 * Elimina una dirección por su ID.
	 *
	 * @param id ID de la dirección.
	 * @return {@code 0} si fue exitoso, {@code 2} si no existe.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<DireccionCliente> found = direccionRepo.findById(id);
		if (found.isPresent()) {
			direccionRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/** Convierte una entidad a DTO. */
	private DireccionClienteDTO toDTO(DireccionCliente entity) {
		DireccionClienteDTO dto = modelMapper.map(entity, DireccionClienteDTO.class);
		if (entity.getCliente() != null) {
			dto.setIdCliente(entity.getCliente().getId());
		}
		return dto;
	}

	/** {@inheritDoc} */
	@Override
	public long count() {
		return direccionRepo.count();
	}

	/** {@inheritDoc} */
	@Override
	public boolean exist(Long id) {
		return direccionRepo.existsById(id);
	}

	/** No aplica. */
	@Override
	public DireccionCliente encrypt(DireccionClienteDTO data) {
		return modelMapper.map(data, DireccionCliente.class);
	}

	/** No aplica. */
	@Override
	public String decrypt(DireccionClienteDTO data) {
		return null;
	}

	/** No aplica. */
	@Override
	public int updatePassword(Long id, DireccionClienteDTO newData) {
		return -1;
	}

	/** No aplica. */
	@Override
	public int updateCorreo(Long id, DireccionClienteDTO newData) {
		return -1;
	}

	/** No aplica. */
	@Override
	public int updateRol(Long id, DireccionClienteDTO newData) {
		return -1;
	}

	/** No aplica. */
	@Override
	public int updateCode(Long id, DireccionClienteDTO newData) {
		return -1;
	}
}