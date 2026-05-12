/**
 * Paquete que contiene las clases de utilidad de la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.util;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Clase de utilidad para operaciones de cifrado AES y hashing en la aplicación
 * CocoTech.
 * <p>
 * Utiliza el modo <strong>AES/GCM/NoPadding</strong> (Galois/Counter Mode) que
 * proporciona cifrado autenticado — garantiza tanto la confidencialidad como la
 * integridad de los datos cifrados. Se usa para proteger datos sensibles de
 * usuarios antes de persistirlos en la base de datos MySQL:
 * <ul>
 * <li>Correos electrónicos de {@link co.edu.unbosque.cocotechback.model.Cliente}
 * y {@link co.edu.unbosque.cocotechback.model.Empleado}.</li>
 * <li>Códigos de verificación de usuarios.</li>
 * </ul>
 * <p>
 * La clave ({@code key}) y el vector de inicialización ({@code iv}) específicos
 * de CocoTech tienen exactamente 16 bytes cada uno, requeridos por AES-128.
 * <p>
 * <strong>Importante:</strong> En un entorno de producción real, la clave y el
 * IV deben externalizarse en variables de entorno o en un gestor de secretos
 * (AWS Secrets Manager, HashiCorp Vault, etc.) y no hardcodearse en el código
 * fuente. Para el alcance académico de este proyecto se mantienen como
 * constantes.
 * <p>
 * También expone métodos de hashing (MD5, SHA-1, SHA-256, SHA-384, SHA-512)
 * usando la librería Apache Commons Codec, útiles para generar tokens de
 * verificación o comparar checksums.
 */
public class AESUtil {

	/**
	 * Nombre del algoritmo de cifrado AES.
	 */
	private static final String ALGORITMO = "AES";

	/**
	 * Tipo de cifrado: AES en modo GCM sin padding.
	 */
	private static final String TIPO_CIFRADO = "AES/GCM/NoPadding";

	/**
	 * Vector de inicialización (IV) de 16 bytes específico de CocoTech.
	 * <p>
	 * <strong>Nota de seguridad:</strong> En producción, el IV debe ser aleatorio
	 * y único por operación. Para el alcance académico del proyecto se usa
	 * un IV fijo.
	 */
	private static final String IV = "cocotechIVsecure";

	/**
	 * Clave de cifrado AES de 16 bytes específica de CocoTech.
	 * <p>
	 * <strong>Nota de seguridad:</strong> En producción, esta clave debe
	 * externalizarse en variables de entorno o un gestor de secretos.
	 */
	private static final String KEY = "cocoKeySecured16";

	/**
	 * Encripta un texto plano con AES/GCM usando la clave e IV proporcionados.
	 *
	 * @param llave La clave AES de 16 bytes.
	 * @param iv    El vector de inicialización de 16 bytes.
	 * @param texto El texto plano a encriptar.
	 * @return El texto encriptado codificado en Base64, o {@code null} si ocurre
	 *         un error durante el cifrado.
	 */
	public static String encrypt(String llave, String iv, String texto) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(TIPO_CIFRADO);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());

		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		byte[] encrypted = null;
		try {
			encrypted = cipher.doFinal(texto.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return new String(encodeBase64(encrypted));
	}

	/**
	 * Desencripta un texto cifrado en Base64 con AES/GCM usando la clave e IV
	 * proporcionados.
	 *
	 * @param llave     La clave AES de 16 bytes.
	 * @param iv        El vector de inicialización de 16 bytes.
	 * @param encrypted El texto cifrado en formato Base64.
	 * @return El texto plano desencriptado, o {@code null} si ocurre un error.
	 */
	public static String decrypt(String llave, String iv, String encrypted) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(TIPO_CIFRADO);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());

		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		byte[] enc = decodeBase64(encrypted);
		byte[] decrypted = null;
		try {
			decrypted = cipher.doFinal(enc);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return new String(decrypted);
	}

	/**
	 * Encripta un texto plano usando la clave e IV predefinidos de CocoTech.
	 * <p>
	 * Método de conveniencia utilizado en toda la aplicación para cifrar correos
	 * y códigos de verificación antes de persistirlos en la base de datos.
	 *
	 * @param textoPlano El texto plano a encriptar.
	 * @return El texto encriptado en formato Base64.
	 */
	public static String encrypt(String textoPlano) {
		return encrypt(KEY, IV, textoPlano);
	}

	/**
	 * Desencripta un texto cifrado en Base64 usando la clave e IV predefinidos
	 * de CocoTech.
	 * <p>
	 * Método de conveniencia utilizado en toda la aplicación para descifrar
	 * correos y códigos de verificación al leerlos de la base de datos.
	 *
	 * @param textoCifrado El texto cifrado en formato Base64.
	 * @return El texto plano desencriptado.
	 */
	public static String decrypt(String textoCifrado) {
		return decrypt(KEY, IV, textoCifrado);
	}

	// ─── Métodos de hashing ───────────────────────────────────────────────────

	/**
	 * Calcula el hash MD5 del contenido proporcionado.
	 *
	 * @param content El contenido a hashear.
	 * @return El hash MD5 en formato hexadecimal.
	 */
	public static String hashingToMD5(String content) {
		return DigestUtils.md5Hex(content);
	}

	/**
	 * Calcula el hash SHA-1 del contenido proporcionado.
	 *
	 * @param content El contenido a hashear.
	 * @return El hash SHA-1 en formato hexadecimal.
	 */
	public static String hashingToSHA1(String content) {
		return DigestUtils.sha1Hex(content);
	}

	/**
	 * Calcula el hash SHA-256 del contenido proporcionado.
	 *
	 * @param content El contenido a hashear.
	 * @return El hash SHA-256 en formato hexadecimal.
	 */
	public static String hashingToSHA256(String content) {
		return DigestUtils.sha256Hex(content);
	}

	/**
	 * Calcula el hash SHA-384 del contenido proporcionado.
	 *
	 * @param content El contenido a hashear.
	 * @return El hash SHA-384 en formato hexadecimal.
	 */
	public static String hashingToSHA384(String content) {
		return DigestUtils.sha384Hex(content);
	}

	/**
	 * Calcula el hash SHA-512 del contenido proporcionado.
	 *
	 * @param content El contenido a hashear.
	 * @return El hash SHA-512 en formato hexadecimal.
	 */
	public static String hashingToSHA512(String content) {
		return DigestUtils.sha512Hex(content);
	}
}
