/**
 * Paquete que contiene las clases para el manejo de excepciones y validaciones
 * de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.exception;

/**
 * Clase utilitaria que contiene métodos de validación para las entidades y
 * datos de entrada de la aplicación CocoTech.
 * <p>
 * Proporciona validaciones reutilizables para correos electrónicos, contraseñas,
 * caracteres peligrosos (HTML injection), roles del sistema y valores numéricos
 * relacionados con el dominio del supermercado (precios, stock, salarios).
 * <p>
 * Todos los métodos son de instancia para mantener consistencia con el estilo
 * del proyecto de referencia. Se recomienda instanciar esta clase donde se
 * necesiten las validaciones (en la capa de servicio).
 */
public class Exceptions {

	/**
	 * Constructor por defecto de la clase {@code Exceptions}.
	 */
	public Exceptions() {
	}

	// ─── Validaciones de texto y formato ──────────────────────────────────────

	/**
	 * Verifica si una cadena de texto contiene caracteres HTML potencialmente
	 * peligrosos que podrían usarse para inyección de código.
	 * <p>
	 * Los caracteres verificados son: {@code < > & " ' / =}.
	 *
	 * @param text La cadena de texto a verificar.
	 * @return {@code true} si la cadena contiene al menos uno de los caracteres
	 *         HTML peligrosos; {@code false} si la cadena es {@code null}, vacía
	 *         o no contiene ninguno de esos caracteres.
	 */
	public boolean containsHtmlSymbols(String text) {
		if (text == null || text.trim().isEmpty()) {
			return false;
		}
		return text.matches(".*[<>&\"'/=].*");
	}

	/**
	 * Verifica si una cadena de texto contiene solo caracteres alfanuméricos,
	 * espacios y caracteres del alfabeto español (tildes, ñ).
	 * <p>
	 * Útil para validar nombres y apellidos de clientes y empleados.
	 *
	 * @param text La cadena a verificar.
	 * @return {@code true} si la cadena solo contiene caracteres permitidos para
	 *         nombres; {@code false} en caso contrario o si es {@code null}.
	 */
	public boolean isValidName(String text) {
		if (text == null || text.trim().isEmpty()) {
			return false;
		}
		return text.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$");
	}

	/**
	 * Verifica si una cadena tiene una longitud dentro del rango permitido
	 * (incluyendo los extremos).
	 *
	 * @param text      La cadena a verificar.
	 * @param minLength La longitud mínima permitida.
	 * @param maxLength La longitud máxima permitida.
	 * @return {@code true} si la longitud de la cadena está dentro del rango;
	 *         {@code false} si la cadena es {@code null} o está fuera del rango.
	 */
	public boolean isLengthValid(String text, int minLength, int maxLength) {
		if (text == null) {
			return false;
		}
		return text.length() >= minLength && text.length() <= maxLength;
	}

	// ─── Validaciones de correo electrónico ───────────────────────────────────

	/**
	 * Valida si una dirección de correo electrónico tiene un formato válido.
	 * <p>
	 * Acepta cualquier dominio (no solo Gmail), incluyendo dominios corporativos
	 * que puedan usar empleados del supermercado.
	 * La validación es insensible a mayúsculas y minúsculas en el dominio.
	 *
	 * @param email La dirección de correo electrónico a validar.
	 * @return {@code true} si la dirección tiene un formato de correo válido;
	 *         {@code false} si es {@code null}, vacía o no cumple el formato.
	 */
	public boolean isValidEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		return email.matches("(?i)^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
	}

	/**
	 * Valida específicamente si una dirección de correo es de dominio Gmail.
	 * <p>
	 * Puede usarse opcionalmente si se quiere restringir el registro de clientes
	 * a cuentas Gmail.
	 *
	 * @param email La dirección de correo electrónico a validar.
	 * @return {@code true} si la dirección tiene formato Gmail válido;
	 *         {@code false} en caso contrario.
	 */
	public boolean isGmailValid(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		return email.matches("(?i)^[a-zA-Z0-9._%+\\-]+@gmail\\.com$");
	}

	// ─── Validaciones de contraseña ───────────────────────────────────────────

	/**
	 * Valida si una contraseña cumple con la política de seguridad de CocoTech:
	 * <ul>
	 * <li>Mínimo 8 caracteres.</li>
	 * <li>Al menos una letra minúscula.</li>
	 * <li>Al menos una letra mayúscula.</li>
	 * <li>Al menos un dígito.</li>
	 * <li>Al menos un símbolo permitido (no puede contener los caracteres
	 * peligrosos: {@code < > & " ' / = } ni espacios).</li>
	 * </ul>
	 *
	 * @param password La contraseña a validar.
	 * @return {@code true} si la contraseña cumple todos los criterios;
	 *         {@code false} si no cumple alguno o si es {@code null}.
	 */
	public boolean isValidPassword(String password) {
		if (password == null || password.length() < 8) {
			return false;
		}

		boolean hasLower = false;
		boolean hasUpper = false;
		boolean hasDigit = false;
		boolean hasValidSymbol = false;

		String disallowedSymbols = "<>&\"'/= ";

		for (char ch : password.toCharArray()) {
			if (Character.isLowerCase(ch)) {
				hasLower = true;
			} else if (Character.isUpperCase(ch)) {
				hasUpper = true;
			} else if (Character.isDigit(ch)) {
				hasDigit = true;
			} else if (disallowedSymbols.indexOf(ch) != -1) {
				return false;
			} else {
				hasValidSymbol = true;
			}
		}

		return hasLower && hasUpper && hasDigit && hasValidSymbol;
	}

	// ─── Validaciones de roles ────────────────────────────────────────────────

	/**
	 * Verifica si un rol dado es válido dentro del sistema CocoTech.
	 * <p>
	 * Los roles válidos son: {@code "ROLE_CLIENTE"} y {@code "ROLE_ADMIN"}.
	 *
	 * @param rol La cadena que representa el rol a verificar.
	 * @return {@code true} si el rol es uno de los roles válidos del sistema;
	 *         {@code false} en cualquier otro caso o si es {@code null}.
	 */
	public boolean isValidRol(String rol) {
		if (rol == null || rol.trim().isEmpty()) {
			return false;
		}
		return rol.equals("ROLE_CLIENTE") || rol.equals("ROLE_ADMIN");
	}

	// ─── Validaciones de valores numéricos del dominio ────────────────────────

	/**
	 * Valida si un precio es un valor positivo mayor a cero.
	 * <p>
	 * Aplica tanto para precios de productos como para salarios de empleados.
	 *
	 * @param precio El precio o valor a validar.
	 * @return {@code true} si el valor es mayor a cero; {@code false} si es
	 *         {@code null}, cero o negativo.
	 */
	public boolean isValidPrice(Double precio) {
		return precio != null && precio > 0;
	}

	/**
	 * Valida si un valor de stock es un número entero no negativo.
	 * <p>
	 * El stock puede ser cero (producto agotado) pero no puede ser negativo.
	 *
	 * @param stock El valor de stock a validar.
	 * @return {@code true} si el stock es mayor o igual a cero; {@code false}
	 *         si es {@code null} o negativo.
	 */
	public boolean isValidStock(Integer stock) {
		return stock != null && stock >= 0;
	}

	/**
	 * Valida si una cantidad de productos en un detalle de venta es un valor
	 * entero positivo (mayor a cero).
	 *
	 * @param cantidad La cantidad a validar.
	 * @return {@code true} si la cantidad es mayor a cero; {@code false} si es
	 *         {@code null}, cero o negativa.
	 */
	public boolean isValidCantidad(Integer cantidad) {
		return cantidad != null && cantidad > 0;
	}

	/**
	 * Valida si un porcentaje de descuento está dentro del rango válido
	 * (entre 0 y 100, inclusive).
	 *
	 * @param porcentaje El porcentaje de descuento a validar.
	 * @return {@code true} si el porcentaje está entre 0 y 100; {@code false}
	 *         si es {@code null} o está fuera del rango.
	 */
	public boolean isValidPorcentajeDescuento(Double porcentaje) {
		return porcentaje != null && porcentaje >= 0 && porcentaje <= 100;
	}

	/**
	 * Verifica si una cadena no es nula y no está vacía (ni solo espacios).
	 * <p>
	 * Método de conveniencia para la validación rápida de campos de texto
	 * requeridos en los DTOs.
	 *
	 * @param text La cadena a verificar.
	 * @return {@code true} si la cadena tiene contenido; {@code false} si es
	 *         {@code null} o está compuesta solo de espacios en blanco.
	 */
	public boolean isNotBlank(String text) {
		return text != null && !text.trim().isEmpty();
	}

	/**
	 * Valida si un número de teléfono tiene un formato básico válido.
	 * <p>
	 * Acepta teléfonos de 7 a 15 dígitos, opcionalmente precedidos por el
	 * símbolo {@code +} para indicar el código de país.
	 *
	 * @param telefono El número de teléfono a validar.
	 * @return {@code true} si el teléfono tiene un formato válido; {@code false}
	 *         si es {@code null}, vacío o no cumple el formato.
	 */
	public boolean isValidPhone(String telefono) {
		if (telefono == null || telefono.trim().isEmpty()) {
			return false;
		}
		return telefono.matches("^\\+?[0-9]{7,15}$");
	}
}
