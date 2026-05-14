/**
 * Paquete que contiene las clases de Transferencia de Datos (DTOs) utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.dto;

import java.util.Objects;

/**
 * Clase de Transferencia de Datos (DTO) para representar una dirección
 * guardada de un cliente.
 * <p>
 * Se utiliza para transferir los datos de dirección entre las capas de la
 * aplicación y a través de la API REST, evitando exponer directamente la
 * entidad JPA {@link co.edu.unbosque.cocotechback.model.DireccionCliente}.
 * La referencia al cliente se representa mediante su ID.
 */
public class DireccionClienteDTO {

	/** Identificador único de la dirección. */
	private Long idDireccion;

	/** Identificador del cliente dueño de la dirección. */
	private Long idCliente;

	/** Alias corto (ej. "Casa", "Oficina"). */
	private String alias;

	/** Calle / dirección principal. */
	private String calle;

	/** Barrio. */
	private String barrio;

	/** Ciudad. */
	private String ciudad;

	/** Referencia adicional (torre, apto, indicaciones). */
	private String referencia;

	/** Si es la dirección predeterminada del cliente. */
	private Boolean predeterminada;

	/** Constructor por defecto. */
	public DireccionClienteDTO() {
	}

	/**
	 * Constructor con parámetros para inicializar la dirección.
	 *
	 * @param idDireccion    ID de la dirección.
	 * @param idCliente      ID del cliente dueño.
	 * @param alias          Alias corto.
	 * @param calle          Calle.
	 * @param barrio         Barrio.
	 * @param ciudad         Ciudad.
	 * @param referencia     Referencia adicional.
	 * @param predeterminada Si es predeterminada.
	 */
	public DireccionClienteDTO(Long idDireccion, Long idCliente, String alias, String calle,
			String barrio, String ciudad, String referencia, Boolean predeterminada) {
		this.idDireccion = idDireccion;
		this.idCliente = idCliente;
		this.alias = alias;
		this.calle = calle;
		this.barrio = barrio;
		this.ciudad = ciudad;
		this.referencia = referencia;
		this.predeterminada = predeterminada;
	}

	/** @return ID de la dirección. */
	public Long getIdDireccion() {
		return idDireccion;
	}

	/** @param idDireccion Nuevo ID. */
	public void setIdDireccion(Long idDireccion) {
		this.idDireccion = idDireccion;
	}

	/** @return ID del cliente dueño. */
	public Long getIdCliente() {
		return idCliente;
	}

	/** @param idCliente Nuevo ID del cliente. */
	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	/** @return Alias. */
	public String getAlias() {
		return alias;
	}

	/** @param alias Nuevo alias. */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/** @return Calle. */
	public String getCalle() {
		return calle;
	}

	/** @param calle Nueva calle. */
	public void setCalle(String calle) {
		this.calle = calle;
	}

	/** @return Barrio. */
	public String getBarrio() {
		return barrio;
	}

	/** @param barrio Nuevo barrio. */
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	/** @return Ciudad. */
	public String getCiudad() {
		return ciudad;
	}

	/** @param ciudad Nueva ciudad. */
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	/** @return Referencia. */
	public String getReferencia() {
		return referencia;
	}

	/** @param referencia Nueva referencia. */
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	/** @return {@code true} si es predeterminada. */
	public Boolean getPredeterminada() {
		return predeterminada;
	}

	/** @param predeterminada Marca o desmarca como predeterminada. */
	public void setPredeterminada(Boolean predeterminada) {
		this.predeterminada = predeterminada;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idDireccion, idCliente, alias);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		DireccionClienteDTO other = (DireccionClienteDTO) obj;
		return Objects.equals(idDireccion, other.idDireccion)
				&& Objects.equals(idCliente, other.idCliente)
				&& Objects.equals(alias, other.alias);
	}

	@Override
	public String toString() {
		return "DireccionClienteDTO [idDireccion=" + idDireccion + ", idCliente=" + idCliente
				+ ", alias=" + alias + ", calle=" + calle + ", ciudad=" + ciudad
				+ ", predeterminada=" + predeterminada + "]";
	}
}