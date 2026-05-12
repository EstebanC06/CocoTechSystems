/**
 * Paquete que contiene las clases de configuración de la aplicación CocoTech
 * backend.
 */
package co.edu.unbosque.cocotechback.configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.cocotechback.model.CajaRegistradora;
import co.edu.unbosque.cocotechback.model.CajaRegistradora.Estado;
import co.edu.unbosque.cocotechback.repository.jpa.CajaRegistradoraRepository;
import co.edu.unbosque.cocotechback.repository.jpa.CategoriaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ClienteRepository;
import co.edu.unbosque.cocotechback.repository.jpa.DetalleVentaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.EmpleadoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.FacturaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ProductoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ProveedorRepository;
import co.edu.unbosque.cocotechback.repository.jpa.SucursalRepository;
import co.edu.unbosque.cocotechback.repository.jpa.VentaRepository;
import co.edu.unbosque.cocotechback.model.Categoria;
import co.edu.unbosque.cocotechback.model.Cliente;
import co.edu.unbosque.cocotechback.model.DetalleVenta;
import co.edu.unbosque.cocotechback.model.Empleado;
import co.edu.unbosque.cocotechback.model.Factura;
import co.edu.unbosque.cocotechback.model.Producto;
import co.edu.unbosque.cocotechback.model.Proveedor;
import co.edu.unbosque.cocotechback.model.Sucursal;
import co.edu.unbosque.cocotechback.model.Venta;
import co.edu.unbosque.cocotechback.util.AESUtil;

/**
 * Clase de configuración que inicializa la base de datos con datos de prueba
 * al arrancar la aplicación CocoTech.
 * <p>
 * Implementa el patrón {@link CommandLineRunner} mediante un bean
 * {@code initDatabase} que se ejecuta una sola vez al inicio. Verifica si los
 * datos ya existen antes de crearlos, garantizando idempotencia en reinicios
 * repetidos del servidor.
 * <p>
 * Cumple el objetivo específico del proyecto que exige un mínimo de 50
 * registros por tabla principal. Las cantidades precargadas son:
 * <ul>
 * <li>5 Sucursales</li>
 * <li>10 Categorías</li>
 * <li>10 Proveedores</li>
 * <li>50 Productos (5 por categoría)</li>
 * <li>50 Empleados (10 por sucursal) + 10 Cajas Registradoras</li>
 * <li>50 Clientes</li>
 * <li>50 Ventas</li>
 * <li>50 Detalles de Venta</li>
 * <li>50 Facturas</li>
 * </ul>
 */
@Configuration
public class LoadDatabase {

	/**
	 * Logger para registrar el progreso de la inicialización de la base de datos.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/**
	 * Constructor por defecto de {@code LoadDatabase}.
	 */
	public LoadDatabase() {
	}

	/**
	 * Bean {@link CommandLineRunner} que orquesta la inicialización de datos en
	 * el orden correcto respetando las dependencias entre entidades (FK).
	 * <p>
	 * Orden de creación: Sucursales → Categorías → Proveedores → Empleados →
	 * Cajas → Clientes → Productos → Ventas → Detalles de Venta → Facturas.
	 *
	 * @param sucursalRepo      Repositorio de sucursales.
	 * @param categoriaRepo     Repositorio de categorías.
	 * @param proveedorRepo     Repositorio de proveedores.
	 * @param empleadoRepo      Repositorio de empleados.
	 * @param cajaRepo          Repositorio de cajas registradoras.
	 * @param clienteRepo       Repositorio de clientes.
	 * @param productoRepo      Repositorio de productos.
	 * @param ventaRepo         Repositorio de ventas.
	 * @param detalleVentaRepo  Repositorio de detalles de venta.
	 * @param facturaRepo       Repositorio de facturas.
	 * @param passwordEncoder   Codificador BCrypt para las contraseñas.
	 * @return El {@link CommandLineRunner} con la lógica de inicialización.
	 */
	@Bean
	CommandLineRunner initDatabase(
			SucursalRepository sucursalRepo,
			CategoriaRepository categoriaRepo,
			ProveedorRepository proveedorRepo,
			EmpleadoRepository empleadoRepo,
			CajaRegistradoraRepository cajaRepo,
			ClienteRepository clienteRepo,
			ProductoRepository productoRepo,
			VentaRepository ventaRepo,
			DetalleVentaRepository detalleVentaRepo,
			FacturaRepository facturaRepo,
			PasswordEncoder passwordEncoder) {

		return args -> {

			// ── 1. SUCURSALES ─────────────────────────────────────────────────
			if (sucursalRepo.count() == 0) {
				String[][] sucursalesData = {
						{ "CocoTech Norte", "3101234567", "Bogotá", "Suba", "Calle 140 # 91-12" },
						{ "CocoTech Sur", "3207654321", "Bogotá", "Usme", "Cra 5 # 85-30" },
						{ "CocoTech Oriente", "3159876543", "Bogotá", "San Cristóbal", "Calle 22 Sur # 14-15" },
						{ "CocoTech Occidente", "3114561234", "Bogotá", "Fontibón", "Av. Calle 22 # 96-20" },
						{ "CocoTech Centro", "3006543210", "Bogotá", "La Candelaria", "Cra 7 # 12-80" }
				};
				for (String[] d : sucursalesData) {
					Sucursal s = new Sucursal(d[0], d[1], d[2], d[3], d[4]);
					sucursalRepo.save(s);
				}
				log.info("Precargando 5 sucursales...");
			} else {
				log.info("Las sucursales ya existen, omitiendo...");
			}

			// ── 2. CATEGORÍAS ─────────────────────────────────────────────────
			if (categoriaRepo.count() == 0) {
				String[][] categoriasData = {
						{ "Lácteos", "Leche, yogurt, queso, mantequilla y derivados lácteos" },
						{ "Bebidas", "Jugos, gaseosas, aguas y bebidas energéticas" },
						{ "Carnes", "Res, pollo, cerdo y productos cárnicos procesados" },
						{ "Frutas y Verduras", "Productos frescos de temporada y hortalizas" },
						{ "Panadería", "Pan, galletas, pasteles y productos de repostería" },
						{ "Aseo del Hogar", "Detergentes, desinfectantes y productos de limpieza" },
						{ "Aseo Personal", "Shampoo, jabón, crema dental y productos de higiene" },
						{ "Enlatados", "Conservas, atún, sardinas y productos enlatados" },
						{ "Granos y Cereales", "Arroz, lentejas, fríjoles, avena y cereales" },
						{ "Snacks", "Papas, maíz pira, chocolates y dulces" }
				};
				for (String[] d : categoriasData) {
					Categoria c = new Categoria(d[0], d[1]);
					categoriaRepo.save(c);
				}
				log.info("Precargando 10 categorías...");
			} else {
				log.info("Las categorías ya existen, omitiendo...");
			}

			// ── 3. PROVEEDORES ────────────────────────────────────────────────
			if (proveedorRepo.count() == 0) {
				String[][] proveedoresData = {
						{ "Alimentos del Valle S.A.S", "6014321000", "Bogotá", "Puente Aranda", "Cra 50 # 13-45" },
						{ "Distribuidora Lácteos Bogotá", "6014567890", "Bogotá", "Engativá", "Cra 72 # 74-10" },
						{ "Carnes Premium Ltda", "3158765432", "Bogotá", "Fontibón", "Cll 24 # 110-30" },
						{ "AgriFruver Colombia", "3106543219", "Cundinamarca", "Madrid", "Vereda El Corzo S/N" },
						{ "Panadería Industrial Norte", "3209874321", "Bogotá", "Suba", "Cll 145 # 92-40" },
						{ "CleanHome Distribuciones", "6013219876", "Bogotá", "Puente Aranda", "Cra 42 # 17-20" },
						{ "Higiene y Salud S.A", "3153216547", "Bogotá", "Chapinero", "Cra 13 # 63-45" },
						{ "Conservas del Atlántico", "5754321567", "Barranquilla", "El Prado", "Cll 72 # 45-12" },
						{ "Granos del Llano S.A.S", "3007651234", "Villavicencio", "Centro", "Cra 18 # 12-30" },
						{ "SnackWorld Colombia", "3124567891", "Bogotá", "Usaquén", "Cll 127 # 16-50" }
				};
				for (String[] d : proveedoresData) {
					Proveedor p = new Proveedor(d[0], d[1], d[4], d[3], d[2]);
					proveedorRepo.save(p);
				}
				log.info("Precargando 10 proveedores...");
			} else {
				log.info("Los proveedores ya existen, omitiendo...");
			}

			// ── 4. EMPLEADOS (10 por sucursal = 50 total) ─────────────────────
			if (empleadoRepo.count() == 0) {
				java.util.List<Sucursal> sucursales = sucursalRepo.findAll();
				String[] cargos = { "Cajero", "Cajero", "Bodeguero", "Gerente de Sucursal",
						"Auxiliar de Inventario", "Cajero", "Cajero", "Bodeguero",
						"Asesor Comercial", "Auxiliar de Limpieza" };
				String[] nombres = { "Andrés", "Camila", "Luis", "María", "Jorge",
						"Laura", "Carlos", "Ana", "Felipe", "Valentina" };
				String[] apellidos = { "Gómez", "Rodríguez", "Martínez", "López",
						"García", "Hernández", "Torres", "Ramírez", "Sánchez", "Vargas" };
				double[] salarios = { 1423500, 1423500, 1600000, 3800000, 1550000,
						1423500, 1423500, 1600000, 1800000, 1423500 };
				int empNum = 1;
				for (Sucursal suc : sucursales) {
					for (int i = 0; i < 10; i++) {
						String correoPlano = "empleado" + empNum + "@cocotech.com";
						Empleado emp = new Empleado(
								nombres[i], apellidos[i],
								AESUtil.encrypt(correoPlano),
								passwordEncoder.encode("Cocotech2026@"),
								AESUtil.encrypt("0"),
								cargos[i], salarios[i], suc);
						empleadoRepo.save(emp);
						empNum++;
					}
				}
				log.info("Precargando 50 empleados...");
			} else {
				log.info("Los empleados ya existen, omitiendo...");
			}

			// ── 5. CAJAS REGISTRADORAS (1 por Cajero = 10 cajas aprox) ────────
			if (cajaRepo.count() == 0) {
				java.util.List<Empleado> cajeros = empleadoRepo.findByCargo("Cajero");
				java.util.List<Sucursal> sucursales = sucursalRepo.findAll();
				int cajaNum = 1;
				// Tomamos máximo 10 cajeros para no exceder las sucursales disponibles
				int limit = Math.min(cajeros.size(), 10);
				for (int i = 0; i < limit; i++) {
					Empleado cajero = cajeros.get(i);
					Sucursal suc = cajero.getSucursal() != null
							? cajero.getSucursal()
							: sucursales.get(i % sucursales.size());
					CajaRegistradora caja = new CajaRegistradora(cajaNum++, Estado.ACTIVA,
							cajero, suc);
					cajaRepo.save(caja);
				}
				log.info("Precargando cajas registradoras...");
			} else {
				log.info("Las cajas registradoras ya existen, omitiendo...");
			}

			// ── 6. CLIENTES (50 clientes) ─────────────────────────────────────
			if (clienteRepo.count() == 0) {
				String[] nombresC = { "Sofía", "Mateo", "Isabella", "Sebastián", "Lucía",
						"Nicolás", "Valeria", "Santiago", "Emma", "Tomás",
						"Mariana", "Diego", "Daniela", "Alejandro", "Paula",
						"Andrés", "Natalia", "Julián", "Ana", "David",
						"Gabriela", "Miguel", "Catalina", "Esteban", "Juliana",
						"Camilo", "Sara", "Nicolás", "Manuela", "Sebastián",
						"Carolina", "Mauricio", "Paola", "Hernán", "Lina",
						"Ricardo", "Vanessa", "Gustavo", "Marcela", "Iván",
						"Ángela", "Roberto", "Melissa", "Fernando", "Adriana",
						"Carlos", "Pilar", "Arturo", "Claudia", "Pablo" };
				String[] apellidosC = { "Pérez", "Castro", "Jiménez", "Silva", "Morales",
						"Ríos", "Ospina", "Cano", "Delgado", "Aguilar",
						"Rojas", "Medina", "Suárez", "Pineda", "Vargas",
						"Guzmán", "Parra", "Arango", "Montoya", "Muñoz",
						"Herrera", "Alvarado", "Ortiz", "Reyes", "Vega",
						"Salazar", "Guerrero", "Mora", "Cabrera", "Ruiz",
						"Córdoba", "Mendoza", "Estrada", "Ramos", "Acevedo",
						"Cardona", "Castaño", "Valencia", "Henao", "Arbeláez",
						"Arias", "Ossa", "Betancur", "Tobón", "Calderón",
						"Giraldo", "Osorio", "Toro", "Zapata", "Franco" };
				String[] barrios = { "Chapinero", "Suba", "Usaquén", "Kennedy", "Engativá",
						"Bosa", "Fontibón", "Teusaquillo", "Barrios Unidos", "Puente Aranda" };
				for (int i = 0; i < 50; i++) {
					String correoPlano = "cliente" + (i + 1) + "@gmail.com";
					Cliente c = new Cliente(
							nombresC[i], apellidosC[i],
							AESUtil.encrypt(correoPlano),
							passwordEncoder.encode("Cliente2026@"),
							AESUtil.encrypt("0"),
							"310" + String.format("%07d", i + 1000000),
							"Calle " + (i + 1) + " # " + (i + 2) + "-" + (i + 3),
							barrios[i % barrios.length],
							"Bogotá");
					clienteRepo.save(c);
				}
				log.info("Precargando 50 clientes...");
			} else {
				log.info("Los clientes ya existen, omitiendo...");
			}

			// ── 7. PRODUCTOS (5 por categoría = 50 total) ─────────────────────
			if (productoRepo.count() == 0) {
				java.util.List<Categoria> categorias = categoriaRepo.findAll();
				java.util.List<Proveedor> proveedores = proveedorRepo.findAll();

				String[][] productosData = {
						// Lácteos
						{ "Leche Entera 1L", "2800", "150" },
						{ "Yogurt Natural 200g", "1500", "120" },
						{ "Queso Campesino 250g", "4200", "80" },
						{ "Mantequilla 125g", "3500", "90" },
						{ "Crema de Leche 200ml", "2200", "100" },
						// Bebidas
						{ "Agua Mineral 600ml", "1200", "200" },
						{ "Jugo de Naranja 1L", "3800", "100" },
						{ "Gaseosa Cola 2L", "4500", "150" },
						{ "Bebida Energética 250ml", "3200", "80" },
						{ "Té Frío 500ml", "2500", "110" },
						// Carnes
						{ "Pechuga de Pollo 1Kg", "12000", "60" },
						{ "Carne Molida 500g", "9500", "70" },
						{ "Chuleta de Cerdo 1Kg", "14000", "50" },
						{ "Salchicha x6 unid", "5800", "90" },
						{ "Chorizo Santarrosano 500g", "8500", "65" },
						// Frutas y Verduras
						{ "Manzana Roja x3 unid", "3200", "100" },
						{ "Banano x5 unid", "2500", "120" },
						{ "Tomate x500g", "2800", "90" },
						{ "Papa Pastusa x1Kg", "3500", "110" },
						{ "Cebolla Cabezona x500g", "2000", "130" },
						// Panadería
						{ "Pan Tajado Grande", "4800", "80" },
						{ "Galletas Soda x22 unid", "3200", "100" },
						{ "Croissant x4 unid", "5500", "60" },
						{ "Ponqué Marmoleado", "8900", "40" },
						{ "Almojábana x6 unid", "4200", "70" },
						// Aseo del Hogar
						{ "Detergente Líquido 1L", "7500", "80" },
						{ "Jabón en Polvo 1Kg", "6200", "90" },
						{ "Desinfectante 900ml", "5800", "85" },
						{ "Limpiavidrios 500ml", "4500", "75" },
						{ "Suavizante de Ropa 1L", "8200", "70" },
						// Aseo Personal
						{ "Shampoo 400ml", "12500", "70" },
						{ "Jabón de Baño x3 unid", "6800", "90" },
						{ "Crema Dental 75ml", "5500", "100" },
						{ "Desodorante Roll-On", "9800", "80" },
						{ "Papel Higiénico x4 rollos", "7200", "110" },
						// Enlatados
						{ "Atún en Agua 150g", "4200", "120" },
						{ "Sardinas en Tomate 125g", "3500", "100" },
						{ "Maíz Dulce Enlatado 285g", "4800", "90" },
						{ "Frijoles Enlatados 400g", "5200", "85" },
						{ "Tomate Enlatado 400g", "4500", "95" },
						// Granos y Cereales
						{ "Arroz Diana x500g", "3200", "150" },
						{ "Lentejas x500g", "4500", "100" },
						{ "Fríjoles Rojos x500g", "5800", "90" },
						{ "Avena en Hojuelas 500g", "5200", "85" },
						{ "Cereal Integral 500g", "9800", "70" },
						// Snacks
						{ "Papas Fritas 90g", "3200", "150" },
						{ "Maíz Pira Microondas x3 unid", "8500", "80" },
						{ "Chocolate Barra 100g", "4800", "120" },
						{ "Gomitas Surtidas 200g", "3500", "100" },
						{ "Galletas de Chocolate 170g", "4200", "90" }
				};

				for (int i = 0; i < 50; i++) {
					Categoria cat = categorias.get(i / 5); // 5 productos por categoría
					Proveedor prov = proveedores.get(i / 5); // proveedor alineado a categoría
					LocalDate vencimiento = (i < 30) // Alimentos perecederos tienen vencimiento
							? LocalDate.now().plusMonths(3 + (i % 6))
							: null;
					Producto p = new Producto(
							productosData[i][0],
							Double.parseDouble(productosData[i][1]),
							Integer.parseInt(productosData[i][2]),
							vencimiento, cat, prov);
					productoRepo.save(p);
				}
				log.info("Precargando 50 productos...");
			} else {
				log.info("Los productos ya existen, omitiendo...");
			}

			// ── 8. VENTAS (50 ventas) ─────────────────────────────────────────
			if (ventaRepo.count() == 0) {
				java.util.List<Empleado> empleados = empleadoRepo.findAll();
				java.util.List<Cliente> clientes = clienteRepo.findAll();
				for (int i = 0; i < 50; i++) {
					Empleado emp = empleados.get(i % empleados.size());
					Cliente cli = clientes.get(i);
					LocalDateTime fecha = LocalDateTime.now().minusDays(50 - i);
					// El total se actualiza cuando se creen los detalles;
					// se inicializa en 0 y se recalcula.
					Venta v = new Venta(fecha, 0.0, emp, cli);
					ventaRepo.save(v);
				}
				log.info("Precargando 50 ventas...");
			} else {
				log.info("Las ventas ya existen, omitiendo...");
			}

			// ── 9. DETALLES DE VENTA (50 detalles) ───────────────────────────
			if (detalleVentaRepo.count() == 0) {
				java.util.List<Venta> ventas = ventaRepo.findAll();
				java.util.List<Producto> productos = productoRepo.findAll();
				String[] metodosPago = { "Efectivo", "Tarjeta débito", "Tarjeta crédito",
						"Transferencia", "Efectivo" };
				for (int i = 0; i < 50; i++) {
					Venta venta = ventas.get(i);
					Producto producto = productos.get(i % productos.size());
					int cantidad = (i % 5) + 1;
					double precioUnitario = producto.getPrecio();
					boolean tienePromocion = (i % 7 == 0); // promo cada 7 ventas
					double porcentaje = tienePromocion ? 10.0 : 0.0;
					double precioOriginal = tienePromocion ? precioUnitario : 0.0;
					double precioNuevo = tienePromocion
							? precioUnitario * (1 - porcentaje / 100) : 0.0;
					double subtotal = tienePromocion
							? precioNuevo * cantidad
							: precioUnitario * cantidad;

					DetalleVenta dv = new DetalleVenta(
							cantidad, precioUnitario, subtotal,
							metodosPago[i % metodosPago.length],
							tienePromocion,
							tienePromocion ? porcentaje : null,
							tienePromocion ? precioOriginal : null,
							tienePromocion ? precioNuevo : null,
							venta, producto);
					detalleVentaRepo.save(dv);

					// Actualizar total de la venta
					venta.setTotal(venta.getTotal() + subtotal);
					ventaRepo.save(venta);

					// Descontar stock del producto
					if (producto.getStock() >= cantidad) {
						producto.setStock(producto.getStock() - cantidad);
						productoRepo.save(producto);
					}
				}
				log.info("Precargando 50 detalles de venta...");
			} else {
				log.info("Los detalles de venta ya existen, omitiendo...");
			}

			// ── 10. FACTURAS (50 facturas, una por venta) ─────────────────────
			if (facturaRepo.count() == 0) {
				java.util.List<Venta> ventas = ventaRepo.findAll();
				double tasaIva = 0.19; // IVA Colombia 19%
				for (Venta venta : ventas) {
					// Verificar que la venta no tenga ya una factura
					Optional<co.edu.unbosque.cocotechback.model.Factura> existente =
							facturaRepo.findByVenta_IdVenta(venta.getIdVenta());
					if (existente.isEmpty()) {
						double impuestos = venta.getTotal() * tasaIva;
						double totalConIva = venta.getTotal() + impuestos;
						Factura factura = new Factura(
								venta.getFecha().plusMinutes(2),
								totalConIva, impuestos, venta);
						facturaRepo.save(factura);
					}
				}
				log.info("Precargando 50 facturas...");
			} else {
				log.info("Las facturas ya existen, omitiendo...");
			}

			log.info("=== Inicialización de base de datos CocoTech completada ===");
		};
	}
}
