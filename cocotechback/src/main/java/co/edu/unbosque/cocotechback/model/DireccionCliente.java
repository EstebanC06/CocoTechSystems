/**
 * Paquete que contiene las clases de Entidad utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa una dirección guardada por un cliente.
 * <p>
 * Un cliente puede tener múltiples direcciones almacenadas (Casa, Oficina,
 * etc.) y marcar una como predeterminada para agilizar el proceso de
 * checkout en el e-commerce.
 * <p>
 * Las direcciones se desnormalizan dentro del {@link Pedido} al momento del
 * checkout para preservar la información histórica del envío incluso si la
 * dirección original es modificada o eliminada posteriormente.
 */
@Entity
@Table(name = "direccion_cliente")
public class DireccionCliente {

	/**
	 * Identificador único de la dirección, generado automáticamente.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDireccion;

	/**
	 * Cliente al que pertenece esta dirección.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente", nullable = false)
	private Cliente cliente;

	/**
	 * Alias corto que identifica la dirección (ej. "Casa", "Oficina", "Mamá").
	 */
	@Column(nullable = false)
	private String alias;

	/**
	 * Calle / dirección principal de envío.
	 */
	@Column(nullable = false)
	private String calle;

	/**
	 * Barrio de la dirección.
	 */
	private String barrio;

	/**
	 * Ciudad de la dirección.
	 */
	@Column(nullable = false)
	private String ciudad;

	/**
	 * Información adicional para encontrar la dirección (torre, apartamento,
	 * indicaciones especiales para el repartidor, etc.).
	 */
	@Column(length = 500)
	private String referencia;

	/**
	 * Indica si esta dirección es la predeterminada del cliente. Solo una
	 * dirección por cliente puede ser predeterminada simultáneamente.
	 */
	private Boolean predeterminada;

	/**
	 * Constructor por defecto de {@code DireccionCliente}.
	 */
	public DireccionCliente() {
	}

	/**
	 * Constructor con parámetros para inicializar la dirección.
	 *
	 * @param cliente        Cliente dueño de la dirección.
	 * @param alias          Alias corto.
	 * @param calle          Calle.
	 * @param barrio         Barrio.
	 * @param ciudad         Ciudad.
	 * @param referencia     Referencia adicional.
	 * @param predeterminada Si es la dirección predeterminada.
	 */
	public DireccionCliente(Cliente cliente, String alias, String calle, String barrio,
			String ciudad, String referencia, Boolean predeterminada) {
		this.cliente = cliente;
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

	/** @param idDireccion Nuevo ID de la dirección. */
	public void setIdDireccion(Long idDireccion) {
		this.idDireccion = idDireccion;
	}

	/** @return Cliente dueño. */
	public Cliente getCliente() {
		return cliente;
	}

	/** @param cliente Nuevo cliente dueño. */
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	/** @return Alias de la dirección. */
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

	/** @return Referencia adicional. */
	public String getReferencia() {
		return referencia;
	}

	/** @param referencia Nueva referencia. */
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	/** @return {@code true} si es la predeterminada. */
	public Boolean getPredeterminada() {
		return predeterminada;
	}

	/** @param predeterminada Marca o desmarca como predeterminada. */
	public void setPredeterminada(Boolean predeterminada) {
		this.predeterminada = predeterminada;
	}

	@Override
	public String toString() {
		return "DireccionCliente [idDireccion=" + idDireccion + ", alias=" + alias
				+ ", calle=" + calle + ", ciudad=" + ciudad + ", predeterminada="
				+ predeterminada + "]";
	}
}