/**
 * Paquete que contiene las clases de utilidad de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración que define el bean singleton de
 * {@link ModelMapper} para toda la aplicación CocoTech.
 * <p>
 * {@link ModelMapper} es la librería utilizada en las capas de servicio para
 * convertir objetos entre las entidades JPA del paquete
 * {@code co.edu.unbosque.cocotechback.model} y los DTOs del paquete
 * {@code co.edu.unbosque.cocotechback.dto}, evitando la copia manual de campos
 * entre objetos y reduciendo el código boilerplate.
 * <p>
 * La estrategia de mapeo configurada es {@link MatchingStrategies#STRICT}, que
 * exige que los nombres de los campos coincidan exactamente entre la fuente y
 * el destino. Esto previene mapeos incorrectos en casos donde los objetos tienen
 * campos con nombres similares pero semántica diferente (ej. {@code id} en
 * diferentes entidades), lo que es especialmente importante en un modelo con
 * múltiples entidades relacionadas como el de CocoTech.
 * <p>
 * Al declarar el bean en esta clase de configuración separada (en lugar de
 * dentro de cada servicio), se garantiza que todos los servicios compartan la
 * misma instancia configurada, reduciendo el consumo de memoria y asegurando
 * consistencia en el comportamiento del mapper en toda la aplicación.
 */
@Configuration
public class ModelMapperConfig {

	/**
	 * Constructor por defecto de {@code ModelMapperConfig}.
	 */
	public ModelMapperConfig() {
	}

	/**
	 * Crea y configura el bean singleton de {@link ModelMapper}.
	 * <p>
	 * Configuraciones aplicadas:
	 * <ul>
	 * <li>{@link MatchingStrategies#STRICT}: Los campos solo se mapean cuando
	 * los nombres coinciden exactamente, evitando mapeos ambiguos entre
	 * entidades del modelo relacional de CocoTech.</li>
	 * <li>{@code setSkipNullEnabled(true)}: Los campos nulos en el objeto fuente
	 * no sobreescriben los valores existentes en el objeto destino. Esto es
	 * esencial para los métodos de actualización parcial ({@code updateById})
	 * en los servicios, donde solo se deben actualizar los campos que el
	 * cliente explícitamente envió.</li>
	 * </ul>
	 *
	 * @return Un {@link ModelMapper} configurado con estrategia estricta y
	 *         omisión de nulos habilitada.
	 */
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
				.setMatchingStrategy(MatchingStrategies.STRICT)
				.setSkipNullEnabled(true);
		return modelMapper;
	}
}
