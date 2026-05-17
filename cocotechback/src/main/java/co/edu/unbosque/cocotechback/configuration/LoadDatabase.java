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
import co.edu.unbosque.cocotechback.model.Categoria;
import co.edu.unbosque.cocotechback.model.Cliente;
import co.edu.unbosque.cocotechback.model.DetallePedido;
import co.edu.unbosque.cocotechback.model.DetalleVenta;
import co.edu.unbosque.cocotechback.model.Empleado;
import co.edu.unbosque.cocotechback.model.Factura;
import co.edu.unbosque.cocotechback.model.Pedido;
import co.edu.unbosque.cocotechback.model.Pedido.EstadoPedido;
import co.edu.unbosque.cocotechback.model.Pedido.MetodoPago;
import co.edu.unbosque.cocotechback.model.Pedido.TipoEntrega;
import co.edu.unbosque.cocotechback.model.Producto;
import co.edu.unbosque.cocotechback.model.Proveedor;
import co.edu.unbosque.cocotechback.model.Sucursal;
import co.edu.unbosque.cocotechback.model.Usuario;
import co.edu.unbosque.cocotechback.model.Venta;
import co.edu.unbosque.cocotechback.repository.jpa.CajaRegistradoraRepository;
import co.edu.unbosque.cocotechback.repository.jpa.CategoriaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ClienteRepository;
import co.edu.unbosque.cocotechback.repository.jpa.DetallePedidoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.DetalleVentaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.EmpleadoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.FacturaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.PedidoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ProductoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ProveedorRepository;
import co.edu.unbosque.cocotechback.repository.jpa.SucursalRepository;
import co.edu.unbosque.cocotechback.repository.jpa.VentaRepository;
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
			PedidoRepository pedidoRepo,
			DetallePedidoRepository detallePedidoRepo,
			PasswordEncoder passwordEncoder) {

		return args -> {

			// ── 1. SUCURSALES ─────────────────────────────────────────────────
			if (sucursalRepo.count() == 0) {
				// Datos: enum NombreSucursal, teléfono, ciudad, barrio, direccion
				Object[][] sucursalesData = {
						{ Sucursal.NombreSucursal.FONTIBON,  "3114561234", "Bogotá", "Fontibón",  "Av. Calle 22 # 96-20" },
						{ Sucursal.NombreSucursal.USAQUEN,   "3006543210", "Bogotá", "Usaquén",   "Cra 7 # 145-30" },
						{ Sucursal.NombreSucursal.CHAPINERO, "3159876543", "Bogotá", "Chapinero", "Cra 13 # 63-45" },
						{ Sucursal.NombreSucursal.SUBA,      "3101234567", "Bogotá", "Suba",      "Calle 140 # 91-12" },
						{ Sucursal.NombreSucursal.ENGATIVA,  "3207654321", "Bogotá", "Engativá",  "Cra 72 # 74-10" }
				};
				for (Object[] d : sucursalesData) {
					Sucursal s = new Sucursal(
							(Sucursal.NombreSucursal) d[0],
							(String) d[1],
							(String) d[2],
							(String) d[3],
							(String) d[4]);
					sucursalRepo.save(s);
				}
				log.info("Precargando 5 sucursales con nombres del enum...");
			} else {
				log.info("Las sucursales ya existen, omitiendo...");
			}


			// ── 2. CATEGORÍAS ─────────────────────────────────────────────────
			if (categoriaRepo.count() == 0) {
				// Datos: enum NombreCategoria, descripción, icono FontAwesome
				Object[][] categoriasData = {
						{ Categoria.NombreCategoria.ASEO,
								"Detergentes, desinfectantes y productos de limpieza del hogar",
								"faSprayCanSparkles" },
						{ Categoria.NombreCategoria.FRUTAS_VERDURAS,
								"Productos frescos de temporada, frutas y hortalizas",
								"faAppleAlt" },
						{ Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES,
								"Lácteos, carnes, embutidos y huevos",
								"faDrumstickBite" },
						{ Categoria.NombreCategoria.BEBIDAS_NO_ALCOHOLICAS,
								"Jugos, gaseosas, aguas y bebidas energéticas",
								"faBottleWater" },
						{ Categoria.NombreCategoria.CONGELADOS,
								"Productos congelados listos para preparar",
								"faSnowflake" },
						{ Categoria.NombreCategoria.PANADERIA_REPOSTERIA,
								"Pan, galletas artesanales, pasteles y repostería",
								"faBreadSlice" },
						{ Categoria.NombreCategoria.DESPENSA,
								"Arroz, granos, pastas, enlatados y aceites",
								"faJar" },
						{ Categoria.NombreCategoria.PAQUETES_GALLETAS,
								"Galletas dulces, saladas y paquetes surtidos",
								"faCookie" },
						{ Categoria.NombreCategoria.DULCES,
								"Chocolates, caramelos y golosinas",
								"faCandyCane" },
						{ Categoria.NombreCategoria.BEBIDAS_ALCOHOLICAS,
								"Cervezas, vinos y licores",
								"faWineBottle" },
						{ Categoria.NombreCategoria.TECNOLOGIA,
								"Accesorios electrónicos y dispositivos",
								"faLaptop" },
						{ Categoria.NombreCategoria.CUIDADO_PERSONAL,
								"Shampoo, jabón, crema dental y productos de higiene",
								"faPumpSoap" },
						{ Categoria.NombreCategoria.ELECTRODOMESTICOS,
								"Pequeños electrodomésticos para el hogar",
								"faPlugCircleBolt" },
						{ Categoria.NombreCategoria.ROPA_MUJER,
								"Prendas de vestir para mujer",
								"faPersonDress" },
						{ Categoria.NombreCategoria.ROPA_HOMBRE,
								"Prendas de vestir para hombre",
								"faShirt" },
						{ Categoria.NombreCategoria.ROPA_NINOS,
								"Prendas de vestir para niños y niñas",
								"faChildren" },
						{ Categoria.NombreCategoria.PRODUCTOS_BEBES,
								"Pañales, fórmulas y artículos para bebés",
								"faBaby" }
				};
				for (Object[] d : categoriasData) {
					Categoria c = new Categoria();
					c.setNombre((Categoria.NombreCategoria) d[0]);
					c.setDescripcion((String) d[1]);
					c.setIcono((String) d[2]);
					categoriaRepo.save(c);
				}
				log.info("Precargando 17 categorías con íconos...");
			} else {
				log.info("Las categorías ya existen, omitiendo...");
			}


			// ── 3. PROVEEDORES ────────────────────────────────────────────────
			if (proveedorRepo.count() == 0) {
				// Datos: enum NombreProveedor, teléfono, direccion, barrio, ciudad
				Object[][] proveedoresData = {
						{ Proveedor.NombreProveedor.P_AND_G,              "6011112222", "Cra 7 # 71-21",     "Chapinero",       "Bogotá" },
						{ Proveedor.NombreProveedor.ORGANICS_COLOMBIA_SAS,"3001234567", "Cll 80 # 11-50",    "Chapinero",       "Bogotá" },
						{ Proveedor.NombreProveedor.ALQUERIA,             "6013334455", "Cll 100 # 8A-49",   "Usaquén",         "Bogotá" },
						{ Proveedor.NombreProveedor.COCACOLA_FEMSA,       "6014445566", "Av. El Dorado #92-3","Fontibón",       "Bogotá" },
						{ Proveedor.NombreProveedor.MCCAIN_FOODS,         "6015556677", "Cll 26 # 92-20",    "Fontibón",        "Bogotá" },
						{ Proveedor.NombreProveedor.BIMBO_COLOMBIA,       "6016667788", "Cra 50 # 13-45",    "Puente Aranda",   "Bogotá" },
						{ Proveedor.NombreProveedor.NESTLE_COLOMBIA,      "6017778899", "Cra 11 # 86-32",    "Chicó",           "Bogotá" },
						{ Proveedor.NombreProveedor.NOEL_NUTRESA,         "6048889900", "Cll 8 Sur # 50-67", "Guayabal",        "Medellín" },
						{ Proveedor.NombreProveedor.COLOMBINA_SA,         "6029990011", "Km 5 vía Cali-Palmira","La Paila",     "Cali" },
						{ Proveedor.NombreProveedor.BAVARIA,              "6010101010", "Cra 53A # 127-35",  "Suba",            "Bogotá" },
						{ Proveedor.NombreProveedor.SAMSUNG_ELECTRONICS,  "6011212121", "Cll 100 # 19-61",   "Chicó Norte",     "Bogotá" },
						{ Proveedor.NombreProveedor.UNILEVER_COLOMBIA,    "6011313131", "Av. Cll 26 # 96-43","Fontibón",        "Bogotá" },
						{ Proveedor.NombreProveedor.LG_ELECTRONICS,       "6011414141", "Cra 9 # 115-30",    "Usaquén",         "Bogotá" },
						{ Proveedor.NombreProveedor.STUDIO_F_CO,          "6011515151", "Cll 82 # 11-37",    "Chicó",           "Bogotá" },
						{ Proveedor.NombreProveedor.ARTURO_CALLE,         "6011616161", "Cra 50 # 17-67",    "Puente Aranda",   "Bogotá" },
						{ Proveedor.NombreProveedor.OFFCORSS,             "6041717171", "Cll 76 # 80-126",   "Calasanz",        "Medellín" },
						{ Proveedor.NombreProveedor.JOHNSON_AND_JOHNSON,  "6011818181", "Av. Cll 26 # 92-32","Fontibón",        "Bogotá" }
				};
				for (Object[] d : proveedoresData) {
					Proveedor p = new Proveedor(
							(Proveedor.NombreProveedor) d[0],
							(String) d[1],
							(String) d[2],
							(String) d[3],
							(String) d[4]);
					proveedorRepo.save(p);
				}
				log.info("Precargando 17 proveedores con nombres del enum...");
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
						// Por defecto el constructor asigna ROLE_EMPLEADO; los
						// Gerentes de Sucursal son ROLE_ADMIN para que puedan
						// administrar el sistema.
						if ("Gerente de Sucursal".equals(cargos[i])) {
							emp.setRol(Usuario.Rol.ROLE_ADMIN);
						}
						empleadoRepo.save(emp);
						empNum++;
					}
				}
				// Admin maestro adicional (acceso fijo "admin@cocotech.co")
				// para garantizar siempre un superusuario disponible.
				Sucursal sucPrincipal = sucursales.get(0);
				Empleado adminMaestro = new Empleado(
						"Admin", "CocoTech",
						AESUtil.encrypt("admin@cocotech.co"),
						passwordEncoder.encode("Admin12345!"),
						AESUtil.encrypt("0"),
						"Administrador del Sistema", 5000000.0, sucPrincipal);
				adminMaestro.setRol(
						Usuario.Rol.ROLE_ADMIN);
				empleadoRepo.save(adminMaestro);
				log.info("Precargando 50 empleados + 1 admin maestro (admin@cocotech.co)...");
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

				// nombre, precio, stock, imagenUrl
		
				Object[][] productosData = {
						// Lácteos (DERIVADOS_DE_ANIMALES)
						{ "Leche Entera 1L", "2800", "150", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "milk,carton" },
						{ "Yogurt Natural 200g", "1500", "120", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "yogurt,cup" },
						{ "Queso Campesino 250g", "4200", "80", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "cheese,white" },
						{ "Mantequilla 125g", "3500", "90", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "butter,stick" },
						{ "Crema de Leche 200ml", "2200", "100", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "cream,bottle" },
						// Bebidas (BEBIDAS_NO_ALCOHOLICAS)
						{ "Agua Mineral 600ml", "1200", "200", Categoria.NombreCategoria.BEBIDAS_NO_ALCOHOLICAS, "water,bottle" },
						{ "Jugo de Naranja 1L", "3800", "100", Categoria.NombreCategoria.BEBIDAS_NO_ALCOHOLICAS, "orange,juice" },
						{ "Gaseosa Cola 2L", "4500", "150", Categoria.NombreCategoria.BEBIDAS_NO_ALCOHOLICAS, "cola,soda" },
						{ "Bebida Energética 250ml", "3200", "80", Categoria.NombreCategoria.BEBIDAS_NO_ALCOHOLICAS, "energy,drink" },
						{ "Té Frío 500ml", "2500", "110", Categoria.NombreCategoria.BEBIDAS_NO_ALCOHOLICAS, "iced,tea" },
						// Carnes (DERIVADOS_DE_ANIMALES)
						{ "Pechuga de Pollo 1Kg", "12000", "60", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "chicken,breast" },
						{ "Carne Molida 500g", "9500", "70", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "ground,beef" },
						{ "Chuleta de Cerdo 1Kg", "14000", "50", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "pork,chop" },
						{ "Salchicha x6 unid", "5800", "90", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "sausage,hotdog" },
						{ "Chorizo Santarrosano 500g", "8500", "65", Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES, "chorizo,sausage" },
						// Frutas y Verduras (FRUTAS_VERDURAS)
						{ "Manzana Roja x3 unid", "3200", "100", Categoria.NombreCategoria.FRUTAS_VERDURAS, "red,apple" },
						{ "Banano x5 unid", "2500", "120", Categoria.NombreCategoria.FRUTAS_VERDURAS, "banana,yellow" },
						{ "Tomate x500g", "2800", "90", Categoria.NombreCategoria.FRUTAS_VERDURAS, "tomato,red" },
						{ "Papa Pastusa x1Kg", "3500", "110", Categoria.NombreCategoria.FRUTAS_VERDURAS, "potato,brown" },
						{ "Cebolla Cabezona x500g", "2000", "130", Categoria.NombreCategoria.FRUTAS_VERDURAS, "onion,white" },
						// Panadería (PANADERIA_REPOSTERIA / PAQUETES_GALLETAS)
						{ "Pan Tajado Grande", "4800", "80", Categoria.NombreCategoria.PANADERIA_REPOSTERIA, "sliced,bread" },
						{ "Galletas Soda x22 unid", "3200", "100", Categoria.NombreCategoria.PAQUETES_GALLETAS, "saltine,crackers" },
						{ "Croissant x4 unid", "5500", "60", Categoria.NombreCategoria.PANADERIA_REPOSTERIA, "croissant" },
						{ "Ponqué Marmoleado", "8900", "40", Categoria.NombreCategoria.PANADERIA_REPOSTERIA, "marble,cake" },
						{ "Almojábana x6 unid", "4200", "70", Categoria.NombreCategoria.PANADERIA_REPOSTERIA, "cheese,bread" },
						// Aseo del Hogar (ASEO)
						{ "Detergente Líquido 1L", "7500", "80", Categoria.NombreCategoria.ASEO, "detergent,bottle" },
						{ "Jabón en Polvo 1Kg", "6200", "90", Categoria.NombreCategoria.ASEO, "laundry,powder" },
						{ "Desinfectante 900ml", "5800", "85", Categoria.NombreCategoria.ASEO, "disinfectant,bottle" },
						{ "Limpiavidrios 500ml", "4500", "75", Categoria.NombreCategoria.ASEO, "glass,cleaner" },
						{ "Suavizante de Ropa 1L", "8200", "70", Categoria.NombreCategoria.ASEO, "fabric,softener" },
						// Aseo Personal (CUIDADO_PERSONAL)
						{ "Shampoo 400ml", "12500", "70", Categoria.NombreCategoria.CUIDADO_PERSONAL, "shampoo,bottle" },
						{ "Jabón de Baño x3 unid", "6800", "90", Categoria.NombreCategoria.CUIDADO_PERSONAL, "bath,soap" },
						{ "Crema Dental 75ml", "5500", "100", Categoria.NombreCategoria.CUIDADO_PERSONAL, "toothpaste,tube" },
						{ "Desodorante Roll-On", "9800", "80", Categoria.NombreCategoria.CUIDADO_PERSONAL, "deodorant,rollon" },
						{ "Papel Higiénico x4 rollos", "7200", "110", Categoria.NombreCategoria.CUIDADO_PERSONAL, "toilet,paper" },
						// Enlatados (DESPENSA)
						{ "Atún en Agua 150g", "4200", "120", Categoria.NombreCategoria.DESPENSA, "tuna,can" },
						{ "Sardinas en Tomate 125g", "3500", "100", Categoria.NombreCategoria.DESPENSA, "sardines,can" },
						{ "Maíz Dulce Enlatado 285g", "4800", "90", Categoria.NombreCategoria.DESPENSA, "corn,can" },
						{ "Frijoles Enlatados 400g", "5200", "85", Categoria.NombreCategoria.DESPENSA, "beans,can" },
						{ "Tomate Enlatado 400g", "4500", "95", Categoria.NombreCategoria.DESPENSA, "tomato,can" },
						// Granos y Cereales (DESPENSA)
						{ "Arroz Diana x500g", "3200", "150", Categoria.NombreCategoria.DESPENSA, "rice,white" },
						{ "Lentejas x500g", "4500", "100", Categoria.NombreCategoria.DESPENSA, "lentils,brown" },
						{ "Fríjoles Rojos x500g", "5800", "90", Categoria.NombreCategoria.DESPENSA, "kidney,beans" },
						{ "Avena en Hojuelas 500g", "5200", "85", Categoria.NombreCategoria.DESPENSA, "oats,bowl" },
						{ "Cereal Integral 500g", "9800", "70", Categoria.NombreCategoria.DESPENSA, "cereal,bowl" },
						// Snacks (PAQUETES_GALLETAS / DULCES)
						{ "Papas Fritas 90g", "3200", "150", Categoria.NombreCategoria.PAQUETES_GALLETAS, "potato,chips" },
						{ "Maíz Pira Microondas x3 unid", "8500", "80", Categoria.NombreCategoria.PAQUETES_GALLETAS, "popcorn" },
						{ "Chocolate Barra 100g", "4800", "120", Categoria.NombreCategoria.DULCES, "chocolate,bar" },
						{ "Gomitas Surtidas 200g", "3500", "100", Categoria.NombreCategoria.DULCES, "gummy,candy" },
						{ "Galletas de Chocolate 170g", "4200", "90", Categoria.NombreCategoria.DULCES, "chocolate,cookies" }
				};
				
				// Indexar categorías por su enum para lookup O(1)
				java.util.Map<Categoria.NombreCategoria, Categoria> catPorNombre = new java.util.HashMap<>();
				for (Categoria c : categorias) {
				    catPorNombre.put(c.getNombre(), c);
				}
				
				for (int i = 0; i < productosData.length; i++) {
				    Categoria.NombreCategoria nombreCat = (Categoria.NombreCategoria) productosData[i][3];
				    Categoria cat = catPorNombre.get(nombreCat);
				    if (cat == null) {
				        log.warn("Categoría {} no encontrada, omitiendo producto '{}'", nombreCat, productosData[i][0]);
				        continue;
				    }
				    Proveedor prov = proveedores.get(i % proveedores.size());
				    boolean perecedero = nombreCat == Categoria.NombreCategoria.FRUTAS_VERDURAS
				            || nombreCat == Categoria.NombreCategoria.DERIVADOS_DE_ANIMALES
				            || nombreCat == Categoria.NombreCategoria.BEBIDAS_NO_ALCOHOLICAS
				            || nombreCat == Categoria.NombreCategoria.CONGELADOS
				            || nombreCat == Categoria.NombreCategoria.PANADERIA_REPOSTERIA
				            || nombreCat == Categoria.NombreCategoria.DESPENSA
				            || nombreCat == Categoria.NombreCategoria.PAQUETES_GALLETAS
				            || nombreCat == Categoria.NombreCategoria.DULCES;
				    LocalDate vencimiento = perecedero
				            ? LocalDate.now().plusMonths(3 + (i % 6))
				            : null;
				    Producto p = new Producto(
				            (String) productosData[i][0],
				            Double.parseDouble((String) productosData[i][1]),
				            Integer.parseInt((String) productosData[i][2]),
				            vencimiento, cat, prov);
				    p.setActivo(true);
				    p.setDestacado(i % 5 == 0);
				    p.setDescuentoPorcentaje(i % 7 == 0 ? 15 : 0);
				    p.setDescripcion("Producto del catálogo CocoTech. " + productosData[i][0]
				            + " de excelente calidad, directo del proveedor.");
				    String keywords = (String) productosData[i][4];
				    p.setImagenUrl("https://loremflickr.com/400/400/" + keywords + "?lock=" + (i + 1));
				    productoRepo.save(p);
				}
				log.info("Precargando " + productosData.length + " productos con categorías e imágenes...");
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

			// ── 11. PEDIDOS DEL E-COMMERCE (15 pedidos de ejemplo) ────────────
			if (pedidoRepo.count() == 0) {
				java.util.List<Cliente> clientes = clienteRepo.findAll();
				java.util.List<Sucursal> sucursales = sucursalRepo.findAll();
				java.util.List<Producto> productos = productoRepo.findAll();
				double tasaIva = 0.19;
				// Estados variados para poblar las distintas vistas del front
				EstadoPedido[] estados = {
						EstadoPedido.RECIBIDO, EstadoPedido.PREPARANDO,
						EstadoPedido.EN_CAMINO, EstadoPedido.LISTO_PARA_ENTREGA,
						EstadoPedido.ENTREGADO, EstadoPedido.RECIBIDO,
						EstadoPedido.CANCELADO, EstadoPedido.PREPARANDO,
						EstadoPedido.ENTREGADO, EstadoPedido.RECIBIDO,
						EstadoPedido.EN_CAMINO, EstadoPedido.PREPARANDO,
						EstadoPedido.RECIBIDO, EstadoPedido.LISTO_PARA_ENTREGA,
						EstadoPedido.ENTREGADO
				};
				for (int i = 0; i < 15; i++) {
					Cliente cli = clientes.get(i % clientes.size());
					Sucursal suc = sucursales.get(i % sucursales.size());
					boolean domicilio = (i % 2 == 0);

					Pedido pedido = new Pedido();
					pedido.setCliente(cli);
					pedido.setSucursalDespacho(suc);
					pedido.setTipoEntrega(domicilio
							? TipoEntrega.DOMICILIO
							: TipoEntrega.RECOGER_EN_SUCURSAL);
					pedido.setMetodoPago(MetodoPago.values()[i % 3]);
					pedido.setEstado(estados[i]);
					pedido.setFechaCreacion(LocalDateTime.now().minusDays(15 - i));
					pedido.setFechaActualizacion(LocalDateTime.now().minusDays(15 - i));
					if (domicilio) {
						pedido.setDireccionEnvio(cli.getCalle());
						pedido.setBarrioEnvio(cli.getBarrio());
						pedido.setCiudadEnvio(cli.getCiudad());
						pedido.setReferenciaEnvio("Apartamento " + (100 + i));
					}
					pedido.setNotasCliente(i % 3 == 0
							? "Por favor llamar antes de entregar." : null);

					// 2 o 3 productos por pedido
					int numLineas = (i % 2) + 2;
					double subtotal = 0.0;
					java.util.List<DetallePedido> detalles = new java.util.ArrayList<>();
					for (int j = 0; j < numLineas; j++) {
						Producto prod = productos.get((i + j) % productos.size());
						int cantidad = (j % 3) + 1;
						double precioBase = prod.getPrecio() != null
								? prod.getPrecio() : 0.0;
						int descuento = prod.getDescuentoPorcentaje() != null
								? prod.getDescuentoPorcentaje() : 0;
						double precioUnitario = descuento > 0
								? precioBase * (1 - descuento / 100.0)
								: precioBase;
						double subLinea = precioUnitario * cantidad;
						subtotal += subLinea;

						DetallePedido det = new DetallePedido();
						det.setPedido(pedido);
						det.setProducto(prod);
						det.setCantidad(cantidad);
						det.setPrecioUnitario(precioUnitario);
						det.setSubtotal(subLinea);
						det.setPromocion(descuento > 0);
						det.setPorcentajeDescuento(descuento);
						detalles.add(det);
					}
					pedido.setDetalles(detalles);

					double iva = subtotal * tasaIva;
					double costoEnvio = domicilio ? 8000.0 : 0.0;
					pedido.setSubtotal(subtotal);
					pedido.setIva(iva);
					pedido.setCostoEnvio(costoEnvio);
					pedido.setTotal(subtotal + iva + costoEnvio);

					pedidoRepo.save(pedido);
				}
				log.info("Precargando 15 pedidos del e-commerce con estados variados...");
			} else {
				log.info("Los pedidos ya existen, omitiendo...");
			}

			log.info("=== Inicialización de base de datos CocoTech completada ===");
		};
	}
}