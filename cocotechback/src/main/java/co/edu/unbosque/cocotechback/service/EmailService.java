package co.edu.unbosque.cocotechback.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio de envío de correos electrónicos vía SMTP.
 * <p>
 * Encapsula el uso de {@link JavaMailSender} configurado a través de las
 * propiedades {@code spring.mail.*} en {@code application.properties}.
 * <p>
 * En esta versión se envían correos en texto plano con buen formato
 * (separadores, secciones, llamados a la acción claros). Para HTML
 * enriquecido habría que usar {@code MimeMessage} con
 * {@code MimeMessageHelper}.
 */
@Service
public class EmailService {

	/**
	 * Logger para registrar éxitos y fallos del envío.
	 */
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	/**
	 * Sender de correos provisto automáticamente por Spring a partir de
	 * la configuración en {@code application.properties}.
	 */
	@Autowired
	private JavaMailSender mailSender;

	/**
	 * Cuenta remitente. Se inyecta desde {@code spring.mail.username} para
	 * mantener consistencia con la cuenta autenticada en SMTP.
	 */
	@Value("${spring.mail.username}")
	private String remitente;

	/**
	 * Constructor por defecto.
	 */
	public EmailService() {
	}

	/**
	 * Envía un código de 6 dígitos al correo del destinatario en el contexto
	 * del flujo de verificación de cuenta tras un registro.
	 *
	 * @param destinatario Correo del usuario que acaba de registrarse.
	 * @param codigo       Código de 6 dígitos en texto plano.
	 */
	public void enviarCodigoVerificacion(String destinatario, String codigo) {
		String asunto = "Verifica tu cuenta de CocoTech";
		String cuerpo = ""
				+ "============================================================\n"
				+ "                       BIENVENIDO A COCOTECH\n"
				+ "============================================================\n"
				+ "\n"
				+ "Hola,\n"
				+ "\n"
				+ "Gracias por crear una cuenta en CocoTech. Estamos a un paso\n"
				+ "de tenerte adentro: solo nos falta confirmar que este correo\n"
				+ "te pertenece.\n"
				+ "\n"
				+ "------------------------------------------------------------\n"
				+ "  TU CÓDIGO DE VERIFICACIÓN\n"
				+ "------------------------------------------------------------\n"
				+ "\n"
				+ "                          " + codigo + "\n"
				+ "\n"
				+ "------------------------------------------------------------\n"
				+ "\n"
				+ "Ingresa este código en la pantalla de verificación que se\n"
				+ "abrió después del registro. Una vez verificado, podrás\n"
				+ "iniciar sesión y empezar a comprar.\n"
				+ "\n"
				+ "Importante:\n"
				+ "  • El código es de un solo uso. Si lo verificas con éxito,\n"
				+ "    no podrás reutilizarlo.\n"
				+ "  • Si no fuiste tú quien creó esta cuenta, simplemente\n"
				+ "    ignora este correo. La cuenta no se activará sin la\n"
				+ "    verificación.\n"
				+ "\n"
				+ "¿Tienes dudas? Responde a este correo y un miembro del\n"
				+ "equipo te ayudará.\n"
				+ "\n"
				+ "Un saludo,\n"
				+ "El equipo de CocoTech\n"
				+ "\n"
				+ "============================================================\n"
				+ "Este es un mensaje automático. Por favor no compartas tu\n"
				+ "código de verificación con nadie.\n"
				+ "============================================================\n";
		enviar(destinatario, asunto, cuerpo);
	}

	/**
	 * Envía un código de 6 dígitos al correo del destinatario en el contexto
	 * del flujo de recuperación de contraseña.
	 *
	 * @param destinatario Correo del usuario que solicitó la recuperación.
	 * @param codigo       Código de 6 dígitos en texto plano.
	 */
	public void enviarCodigoRecuperacion(String destinatario, String codigo) {
		String asunto = "Restablece tu contraseña de CocoTech";
		String cuerpo = ""
				+ "============================================================\n"
				+ "                  RECUPERACIÓN DE CONTRASEÑA\n"
				+ "============================================================\n"
				+ "\n"
				+ "Hola,\n"
				+ "\n"
				+ "Recibimos una solicitud para restablecer la contraseña de\n"
				+ "tu cuenta de CocoTech. Si tú la pediste, sigue las\n"
				+ "instrucciones de abajo.\n"
				+ "\n"
				+ "------------------------------------------------------------\n"
				+ "  TU CÓDIGO DE RECUPERACIÓN\n"
				+ "------------------------------------------------------------\n"
				+ "\n"
				+ "                          " + codigo + "\n"
				+ "\n"
				+ "------------------------------------------------------------\n"
				+ "\n"
				+ "Cómo continuar:\n"
				+ "  1. Vuelve a la pantalla de recuperación en la app.\n"
				+ "  2. Ingresa el código de arriba.\n"
				+ "  3. Define tu nueva contraseña.\n"
				+ "\n"
				+ "Importante:\n"
				+ "  • Este código es de un solo uso y queda invalidado tras\n"
				+ "    cambiar la contraseña.\n"
				+ "  • Si no solicitaste el cambio, puedes ignorar este\n"
				+ "    correo. Tu contraseña actual seguirá funcionando y\n"
				+ "    nadie podrá acceder a tu cuenta sin este código.\n"
				+ "  • Si crees que alguien está intentando entrar a tu\n"
				+ "    cuenta, te recomendamos cambiar tu contraseña\n"
				+ "    inmediatamente cuando recuperes el acceso.\n"
				+ "\n"
				+ "Un saludo,\n"
				+ "El equipo de CocoTech\n"
				+ "\n"
				+ "============================================================\n"
				+ "Este es un mensaje automático. Nunca compartas tu código\n"
				+ "de recuperación con nadie, ni siquiera con personal de\n"
				+ "CocoTech. Nunca te lo pediremos.\n"
				+ "============================================================\n";
		enviar(destinatario, asunto, cuerpo);
	}

	/**
	 * Envía un correo de texto plano.
	 * <p>
	 * Loggea éxito o fallo, pero NO lanza excepción al caller si el envío
	 * falla: el flujo de registro/recuperación no debe quebrarse porque
	 * SMTP esté caído. El usuario puede solicitar reenvío.
	 *
	 * @param destinatario Dirección del receptor.
	 * @param asunto       Asunto del correo.
	 * @param cuerpo       Cuerpo del correo en texto plano.
	 */
	private void enviar(String destinatario, String asunto, String cuerpo) {
		try {
			SimpleMailMessage mensaje = new SimpleMailMessage();
			mensaje.setFrom(remitente);
			mensaje.setTo(destinatario);
			mensaje.setSubject(asunto);
			mensaje.setText(cuerpo);
			mailSender.send(mensaje);
			log.info("Correo enviado correctamente a {}", destinatario);
		} catch (Exception e) {
			log.error("Error al enviar correo a {}: {}", destinatario, e.getMessage());
		}
	}

	/**
	 * Genera un código numérico aleatorio de 6 dígitos como cadena.
	 *
	 * @return Cadena de 6 dígitos entre "100000" y "999999".
	 */
	public static String generarCodigo6Digitos() {
		int codigo = 100000 + (int) (Math.random() * 900000);
		return String.valueOf(codigo);
	}
}