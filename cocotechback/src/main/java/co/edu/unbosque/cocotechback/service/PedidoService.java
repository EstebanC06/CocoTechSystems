/**
 * Paquete que contiene las clases de Servicio utilizadas
 * en la aplicación CocoTech backend.
 */
package co.edu.unbosque.cocotechback.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.cocotechback.dto.DetallePedidoDTO;
import co.edu.unbosque.cocotechback.dto.PedidoDTO;
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
import co.edu.unbosque.cocotechback.model.Sucursal;
import co.edu.unbosque.cocotechback.model.Venta;
import co.edu.unbosque.cocotechback.repository.jpa.ClienteRepository;
import co.edu.unbosque.cocotechback.repository.jpa.DetallePedidoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.DetalleVentaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.EmpleadoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.FacturaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.PedidoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ProductoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.SucursalRepository;
import co.edu.unbosque.cocotechback.repository.jpa.VentaRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Pedido}, el núcleo del e-commerce de CocoTech.
 * <p>
 * Responsabilidades principales:
 * <ul>
 * <li><b>Crear pedido</b>: valida stock disponible, lo decrementa de forma
 * transaccional y persiste el pedido con sus detalles.</li>
 * <li><b>Cambiar estado</b>: valida que la transición de estado sea legal
 * según el flujo del pedido. Al pasar a {@code ENTREGADO}, genera
 * automáticamente la {@link Venta}, los {@link DetalleVenta} y la
 * {@link Factura} correspondientes.</li>
 * <li><b>Cancelar pedido</b>: solo permitido en estado {@code RECIBIDO};
 * restituye el stock decrementado al crear el pedido.</li>
 * </ul>
 * <p>
 * Todas las operaciones que tocan múltiples tablas están anotadas con
 * {@code @Transactional} para garantizar atomicidad: si algo falla, no queda
 * el stock decrementado a medias ni pedidos huérfanos.
 * <p>
 * Convención de códigos de retorno (alineada con el resto del proyecto):
 * {@code 0} éxito, {@code 1} conflicto / transición inválida, {@code 2} no
 * encontrado, {@code 3} stock insuficiente, {@code 4} validación fallida.
 */
@Service
public class PedidoService {

	/** IVA aplicado a los pedidos (19%). */
	private static final double IVA = 0.19;

	/** Repositorio de pedidos. */
	@Autowired
	private PedidoRepository pedidoRepo;

	/** Repositorio de detalles de pedido. */
	@Autowired
	private DetallePedidoRepository detallePedidoRepo;

	/** Repositorio de clientes para resolver la relación. */
	@Autowired
	private ClienteRepository clienteRepo;

	/** Repositorio de sucursales para resolver la relación. */
	@Autowired
	private SucursalRepository sucursalRepo;

	/** Repositorio de productos para validar y ajustar stock. */
	@Autowired
	private ProductoRepository productoRepo;

	/** Repositorio de empleados (para asociar la venta generada). */
	@Autowired
	private EmpleadoRepository empleadoRepo;

	/** Repositorio de ventas (para generar la venta al entregar). */
	@Autowired
	private VentaRepository ventaRepo;

	/** Repositorio de detalles de venta (para generar la venta al entregar). */
	@Autowired
	private DetalleVentaRepository detalleVentaRepo;

	/** Repositorio de facturas (para generar la factura al entregar). */
	@Autowired
	private FacturaRepository facturaRepo;

	/** Constructor por defecto de {@code PedidoService}. */
	public PedidoService() {
	}

	// ─── CREAR PEDIDO ─────────────────────────────────────────────────────────

	/**
	 * Crea un nuevo pedido a partir de un {@link PedidoDTO}.
	 * <p>
	 * Operación transaccional: valida que el cliente y la sucursal existan,
	 * que cada producto del detalle exista y tenga stock suficiente; luego
	 * decrementa el stock de cada producto y persiste el pedido con sus
	 * detalles. Si cualquier validación falla, no se persiste nada.
	 * <p>
	 * El total se recalcula en el servidor a partir de los detalles para no
	 * confiar en los montos enviados por el cliente.
	 *
	 * @param data DTO con la información del pedido y sus detalles.
	 * @return {@code 0} si fue exitoso,
	 *         {@code 2} si el cliente, la sucursal o algún producto no existen,
	 *         {@code 3} si algún producto no tiene stock suficiente,
	 *         {@code 4} si faltan campos requeridos o el pedido no tiene detalles.
	 */
	@Transactional
	public int create(PedidoDTO data) {
		// Validación de campos requeridos
		if (data.getIdCliente() == null || data.getIdSucursalDespacho() == null
				|| data.getTipoEntrega() == null || data.getMetodoPago() == null
				|| data.getDetalles() == null || data.getDetalles().isEmpty()) {
			return 4;
		}

		// Validar enums
		TipoEntrega tipoEntrega;
		MetodoPago metodoPago;
		try {
			tipoEntrega = TipoEntrega.valueOf(data.getTipoEntrega());
			metodoPago = MetodoPago.valueOf(data.getMetodoPago());
		} catch (IllegalArgumentException e) {
			return 4;
		}

		// Resolver relaciones
		Optional<Cliente> clienteFound = clienteRepo.findById(data.getIdCliente());
		Optional<Sucursal> sucursalFound = sucursalRepo.findById(data.getIdSucursalDespacho());
		if (!clienteFound.isPresent() || !sucursalFound.isPresent()) {
			return 2;
		}

		// Validar existencia y stock de TODOS los productos antes de tocar nada
		List<Producto> productosResueltos = new ArrayList<>();
		for (DetallePedidoDTO det : data.getDetalles()) {
			if (det.getIdProducto() == null || det.getCantidad() == null
					|| det.getCantidad() <= 0) {
				return 4;
			}
			Optional<Producto> prodFound = productoRepo.findById(det.getIdProducto());
			if (!prodFound.isPresent()) {
				return 2;
			}
			Producto prod = prodFound.get();
			int stockActual = prod.getStock() != null ? prod.getStock() : 0;
			if (stockActual < det.getCantidad()) {
				return 3; // stock insuficiente
			}
			productosResueltos.add(prod);
		}

		// Construir el pedido
		Pedido pedido = new Pedido();
		pedido.setCliente(clienteFound.get());
		pedido.setSucursalDespacho(sucursalFound.get());
		pedido.setTipoEntrega(tipoEntrega);
		pedido.setMetodoPago(metodoPago);
		pedido.setEstado(EstadoPedido.RECIBIDO);
		pedido.setFechaCreacion(LocalDateTime.now());
		pedido.setFechaActualizacion(LocalDateTime.now());
		// Datos de envío solo si es a domicilio
		if (tipoEntrega == TipoEntrega.DOMICILIO) {
			pedido.setDireccionEnvio(data.getDireccionEnvio());
			pedido.setBarrioEnvio(data.getBarrioEnvio());
			pedido.setCiudadEnvio(data.getCiudadEnvio());
			pedido.setReferenciaEnvio(data.getReferenciaEnvio());
		}
		pedido.setNotasCliente(data.getNotasCliente());

		// Construir detalles y calcular subtotal en el servidor
		double subtotalCalculado = 0.0;
		List<DetallePedido> detalles = new ArrayList<>();
		for (int i = 0; i < data.getDetalles().size(); i++) {
			DetallePedidoDTO detDTO = data.getDetalles().get(i);
			Producto prod = productosResueltos.get(i);

			// Precio unitario: si el front no lo manda, se calcula del producto
			double precioBase = prod.getPrecio() != null ? prod.getPrecio() : 0.0;
			int descuento = prod.getDescuentoPorcentaje() != null
					? prod.getDescuentoPorcentaje() : 0;
			double precioUnitario;
			if (detDTO.getPrecioUnitario() != null) {
				precioUnitario = detDTO.getPrecioUnitario();
			} else {
				precioUnitario = descuento > 0
						? precioBase * (1 - descuento / 100.0)
						: precioBase;
			}
			double subtotalLinea = precioUnitario * detDTO.getCantidad();
			subtotalCalculado += subtotalLinea;

			DetallePedido det = new DetallePedido();
			det.setPedido(pedido);
			det.setProducto(prod);
			det.setCantidad(detDTO.getCantidad());
			det.setPrecioUnitario(precioUnitario);
			det.setSubtotal(subtotalLinea);
			det.setPromocion(descuento > 0);
			det.setPorcentajeDescuento(descuento);
			detalles.add(det);
		}
		pedido.setDetalles(detalles);

		// Totales recalculados en el servidor
		double ivaCalculado = subtotalCalculado * IVA;
		double costoEnvio = data.getCostoEnvio() != null ? data.getCostoEnvio() : 0.0;
		// Si recoge en sucursal, no hay costo de envío
		if (tipoEntrega == TipoEntrega.RECOGER_EN_SUCURSAL) {
			costoEnvio = 0.0;
		}
		pedido.setSubtotal(subtotalCalculado);
		pedido.setIva(ivaCalculado);
		pedido.setCostoEnvio(costoEnvio);
		pedido.setTotal(subtotalCalculado + ivaCalculado + costoEnvio);

		// Decrementar stock (ya validado arriba que alcanza)
		for (int i = 0; i < detalles.size(); i++) {
			Producto prod = productosResueltos.get(i);
			int stockActual = prod.getStock() != null ? prod.getStock() : 0;
			prod.setStock(stockActual - detalles.get(i).getCantidad());
			productoRepo.save(prod);
		}

		// Persistir el pedido (los detalles van en cascada)
		pedidoRepo.save(pedido);
		return 0;
	}

	// ─── CONSULTAS ────────────────────────────────────────────────────────────

	/**
	 * Obtiene todos los pedidos del sistema (vista de administrador).
	 *
	 * @return Lista de todos los pedidos, del más reciente al más antiguo.
	 */
	public List<PedidoDTO> getAll() {
		List<Pedido> entityList = pedidoRepo.findAllByOrderByFechaCreacionDesc();
		return mapList(entityList);
	}

	/**
	 * Obtiene un pedido por su ID.
	 *
	 * @param id ID del pedido.
	 * @return DTO del pedido o {@code null} si no existe.
	 */
	public PedidoDTO getById(Long id) {
		Optional<Pedido> found = pedidoRepo.findById(id);
		return found.isPresent() ? toDTO(found.get()) : null;
	}

	/**
	 * Obtiene los pedidos de un cliente específico.
	 *
	 * @param idCliente ID del cliente.
	 * @return Lista de pedidos del cliente.
	 */
	public List<PedidoDTO> getByCliente(Long idCliente) {
		return mapList(pedidoRepo.findByCliente_IdOrderByFechaCreacionDesc(idCliente));
	}

	/**
	 * Obtiene los pedidos despachados por una sucursal específica.
	 *
	 * @param idSucursal ID de la sucursal de despacho.
	 * @return Lista de pedidos de la sucursal.
	 */
	public List<PedidoDTO> getBySucursal(Long idSucursal) {
		return mapList(pedidoRepo
				.findBySucursalDespacho_IdSucursalOrderByFechaCreacionDesc(idSucursal));
	}

	/**
	 * Obtiene los pedidos que se encuentran en un estado específico.
	 *
	 * @param estadoStr Estado como String (RECIBIDO, PREPARANDO, etc.).
	 * @return Lista de pedidos en ese estado, o lista vacía si el estado no
	 *         es válido.
	 */
	public List<PedidoDTO> getByEstado(String estadoStr) {
		EstadoPedido estado;
		try {
			estado = EstadoPedido.valueOf(estadoStr);
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}
		return mapList(pedidoRepo.findByEstadoOrderByFechaCreacionDesc(estado));
	}

	// ─── CAMBIAR ESTADO ───────────────────────────────────────────────────────

	/**
	 * Cambia el estado de un pedido, validando que la transición sea legal
	 * dentro del flujo del ciclo de vida del pedido.
	 * <p>
	 * Transiciones permitidas:
	 * <pre>
	 *   RECIBIDO            → PREPARANDO, CANCELADO
	 *   PREPARANDO          → LISTO_PARA_ENTREGA, EN_CAMINO
	 *   LISTO_PARA_ENTREGA  → ENTREGADO
	 *   EN_CAMINO           → ENTREGADO
	 * </pre>
	 * Al pasar a {@code ENTREGADO}, se genera automáticamente la venta y la
	 * factura asociadas (ver {@link #generarVentaYFactura}).
	 *
	 * @param idPedido    ID del pedido a actualizar.
	 * @param nuevoEstado Nuevo estado como String.
	 * @param idEmpleado  ID del empleado que ejecuta el cambio (puede ser
	 *                    {@code null}; se usa para registrar la venta al
	 *                    entregar — si es null se toma un empleado de la
	 *                    sucursal de despacho).
	 * @return {@code 0} si fue exitoso,
	 *         {@code 1} si la transición de estado no es válida,
	 *         {@code 2} si el pedido no existe,
	 *         {@code 4} si el estado recibido no es un valor válido.
	 */
	@Transactional
	public int cambiarEstado(Long idPedido, String nuevoEstado, Long idEmpleado) {
		Optional<Pedido> found = pedidoRepo.findById(idPedido);
		if (!found.isPresent()) {
			return 2;
		}
		EstadoPedido destino;
		try {
			destino = EstadoPedido.valueOf(nuevoEstado);
		} catch (IllegalArgumentException e) {
			return 4;
		}
		Pedido pedido = found.get();
		EstadoPedido actual = pedido.getEstado();

		if (!esTransicionValida(actual, destino)) {
			return 1;
		}

		// Si se cancela, restituir stock
		if (destino == EstadoPedido.CANCELADO) {
			restituirStock(pedido);
		}

		// Si se entrega, generar venta + factura
		if (destino == EstadoPedido.ENTREGADO) {
			generarVentaYFactura(pedido, idEmpleado);
		}

		pedido.setEstado(destino);
		pedido.setFechaActualizacion(LocalDateTime.now());
		pedidoRepo.save(pedido);
		return 0;
	}

	/**
	 * Cancela un pedido. Es un atajo de {@link #cambiarEstado} que solo
	 * permite cancelar pedidos en estado {@code RECIBIDO} y restituye el stock.
	 *
	 * @param idPedido ID del pedido a cancelar.
	 * @return {@code 0} si fue exitoso,
	 *         {@code 1} si el pedido no está en estado RECIBIDO,
	 *         {@code 2} si el pedido no existe.
	 */
	@Transactional
	public int cancelar(Long idPedido) {
		Optional<Pedido> found = pedidoRepo.findById(idPedido);
		if (!found.isPresent()) {
			return 2;
		}
		Pedido pedido = found.get();
		if (pedido.getEstado() != EstadoPedido.RECIBIDO) {
			return 1; // solo se puede cancelar en RECIBIDO
		}
		restituirStock(pedido);
		pedido.setEstado(EstadoPedido.CANCELADO);
		pedido.setFechaActualizacion(LocalDateTime.now());
		pedidoRepo.save(pedido);
		return 0;
	}

	// ─── LÓGICA DE NEGOCIO INTERNA ────────────────────────────────────────────

	/**
	 * Determina si una transición de estado es válida según el flujo del
	 * ciclo de vida del pedido.
	 *
	 * @param actual  Estado actual del pedido.
	 * @param destino Estado al que se quiere mover.
	 * @return {@code true} si la transición está permitida.
	 */
	private boolean esTransicionValida(EstadoPedido actual, EstadoPedido destino) {
		if (actual == destino) {
			return false;
		}
		switch (actual) {
		case RECIBIDO:
			return destino == EstadoPedido.PREPARANDO
					|| destino == EstadoPedido.CANCELADO;
		case PREPARANDO:
			return destino == EstadoPedido.LISTO_PARA_ENTREGA
					|| destino == EstadoPedido.EN_CAMINO;
		case LISTO_PARA_ENTREGA:
			return destino == EstadoPedido.ENTREGADO;
		case EN_CAMINO:
			return destino == EstadoPedido.ENTREGADO;
		case ENTREGADO:
		case CANCELADO:
		default:
			return false; // estados terminales
		}
	}

	/**
	 * Restituye al inventario el stock que se había descontado al crear el
	 * pedido. Se invoca al cancelar un pedido.
	 *
	 * @param pedido El pedido cuyo stock se va a restituir.
	 */
	private void restituirStock(Pedido pedido) {
		if (pedido.getDetalles() == null) {
			return;
		}
		for (DetallePedido det : pedido.getDetalles()) {
			Producto prod = det.getProducto();
			if (prod != null) {
				int stockActual = prod.getStock() != null ? prod.getStock() : 0;
				prod.setStock(stockActual + det.getCantidad());
				productoRepo.save(prod);
			}
		}
	}

	/**
	 * Genera automáticamente la {@link Venta}, sus {@link DetalleVenta} y la
	 * {@link Factura} a partir de un pedido que se está marcando como
	 * {@code ENTREGADO}, integrando el e-commerce con el módulo de ventas.
	 * <p>
	 * El empleado registrado en la venta es el que ejecutó el cambio de
	 * estado. Si no se proporciona, se usa cualquier empleado de la sucursal
	 * de despacho como fallback (un pedido entregado siempre debe quedar
	 * asociado a un empleado, ya que la entidad {@link Venta} lo exige).
	 *
	 * @param pedido     El pedido que se está entregando.
	 * @param idEmpleado ID del empleado que ejecuta la entrega (puede ser null).
	 */
	private void generarVentaYFactura(Pedido pedido, Long idEmpleado) {
		// Resolver el empleado que registra la venta
		Empleado empleado = null;
		if (idEmpleado != null) {
			empleado = empleadoRepo.findById(idEmpleado).orElse(null);
		}
		if (empleado == null) {
			// Fallback: primer empleado de la sucursal de despacho
			List<Empleado> empleadosSucursal = empleadoRepo
					.findBySucursal_IdSucursal(pedido.getSucursalDespacho().getIdSucursal());
			if (!empleadosSucursal.isEmpty()) {
				empleado = empleadosSucursal.get(0);
			}
		}
		// Si aún no hay empleado, no se puede generar la venta: se omite
		// silenciosamente (el pedido igual queda ENTREGADO). En la práctica
		// siempre hay empleados por el seed de LoadDatabase.
		if (empleado == null) {
			return;
		}

		// 1. Crear la venta
		Venta venta = new Venta();
		venta.setFecha(LocalDateTime.now());
		venta.setTotal(pedido.getTotal());
		venta.setEmpleado(empleado);
		venta.setCliente(pedido.getCliente());
		ventaRepo.save(venta);

		// 2. Crear los detalles de venta a partir de los detalles del pedido
		for (DetallePedido detPed : pedido.getDetalles()) {
			DetalleVenta detVenta = new DetalleVenta();
			detVenta.setVenta(venta);
			detVenta.setProducto(detPed.getProducto());
			detVenta.setCantidadProductos(detPed.getCantidad());
			detVenta.setPrecioUnitario(detPed.getPrecioUnitario());
			detVenta.setSubtotal(detPed.getSubtotal());
			detVenta.setMetodoPago(pedido.getMetodoPago().name());
			detVenta.setPromocion(detPed.getPromocion());
			// DetalleVenta usa Double para el porcentaje; DetallePedido usa Integer
			detVenta.setPorcentajeDescuento(
					detPed.getPorcentajeDescuento() != null
							? detPed.getPorcentajeDescuento().doubleValue()
							: 0.0);
			if (Boolean.TRUE.equals(detPed.getPromocion())
					&& detPed.getProducto() != null) {
				detVenta.setPrecioOriginal(detPed.getProducto().getPrecio());
				detVenta.setPrecioNuevo(detPed.getPrecioUnitario());
			}
			detalleVentaRepo.save(detVenta);
		}

		// 3. Crear la factura
		Factura factura = new Factura();
		factura.setFecha(LocalDateTime.now());
		factura.setPrecioTotal(pedido.getTotal());
		factura.setPrecioImpuestos(pedido.getIva());
		factura.setVenta(venta);
		facturaRepo.save(factura);

		// 4. Enlazar la venta generada al pedido
		pedido.setVentaGenerada(venta);
	}

	// ─── MAPPERS ──────────────────────────────────────────────────────────────

	/**
	 * Convierte una lista de entidades {@link Pedido} a una lista de DTOs.
	 *
	 * @param entityList Lista de entidades.
	 * @return Lista de DTOs equivalente.
	 */
	private List<PedidoDTO> mapList(List<Pedido> entityList) {
		List<PedidoDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> dtoList.add(toDTO(entity)));
		return dtoList;
	}

	/**
	 * Convierte una entidad {@link Pedido} a su {@link PedidoDTO}, hidratando
	 * los campos derivados (nombres de cliente y sucursal, ID de venta) y
	 * mapeando los detalles.
	 *
	 * @param entity La entidad a convertir.
	 * @return El DTO equivalente.
	 */
	private PedidoDTO toDTO(Pedido entity) {
		PedidoDTO dto = new PedidoDTO();
		dto.setIdPedido(entity.getIdPedido());
		dto.setFechaCreacion(entity.getFechaCreacion());
		dto.setFechaActualizacion(entity.getFechaActualizacion());
		dto.setEstado(entity.getEstado() != null ? entity.getEstado().name() : null);
		dto.setTipoEntrega(
				entity.getTipoEntrega() != null ? entity.getTipoEntrega().name() : null);
		dto.setMetodoPago(
				entity.getMetodoPago() != null ? entity.getMetodoPago().name() : null);
		dto.setSubtotal(entity.getSubtotal());
		dto.setIva(entity.getIva());
		dto.setCostoEnvio(entity.getCostoEnvio());
		dto.setTotal(entity.getTotal());
		dto.setDireccionEnvio(entity.getDireccionEnvio());
		dto.setBarrioEnvio(entity.getBarrioEnvio());
		dto.setCiudadEnvio(entity.getCiudadEnvio());
		dto.setReferenciaEnvio(entity.getReferenciaEnvio());
		dto.setNotasCliente(entity.getNotasCliente());

		if (entity.getCliente() != null) {
			dto.setIdCliente(entity.getCliente().getId());
			dto.setNombreCliente(entity.getCliente().getNombres() + " "
					+ entity.getCliente().getApellidos());
		}
		if (entity.getSucursalDespacho() != null) {
			dto.setIdSucursalDespacho(entity.getSucursalDespacho().getIdSucursal());
			dto.setNombreSucursal(entity.getSucursalDespacho().getNombre());
		}
		if (entity.getVentaGenerada() != null) {
			dto.setIdVentaGenerada(entity.getVentaGenerada().getIdVenta());
		}

		// Mapear detalles
		List<DetallePedidoDTO> detallesDTO = new ArrayList<>();
		if (entity.getDetalles() != null) {
			for (DetallePedido det : entity.getDetalles()) {
				DetallePedidoDTO detDTO = new DetallePedidoDTO();
				detDTO.setIdDetallePedido(det.getIdDetallePedido());
				detDTO.setIdPedido(entity.getIdPedido());
				detDTO.setCantidad(det.getCantidad());
				detDTO.setPrecioUnitario(det.getPrecioUnitario());
				detDTO.setSubtotal(det.getSubtotal());
				detDTO.setPromocion(det.getPromocion());
				detDTO.setPorcentajeDescuento(det.getPorcentajeDescuento());
				if (det.getProducto() != null) {
					detDTO.setIdProducto(det.getProducto().getIdProducto());
					detDTO.setNombreProducto(det.getProducto().getNombre());
					detDTO.setImagenUrl(det.getProducto().getImagenUrl());
				}
				detallesDTO.add(detDTO);
			}
		}
		dto.setDetalles(detallesDTO);
		return dto;
	}

	/**
	 * Cuenta el número total de pedidos en el sistema.
	 *
	 * @return Número total de pedidos.
	 */
	public long count() {
		return pedidoRepo.count();
	}

	/**
	 * Cuenta los pedidos que se encuentran en un estado específico.
	 *
	 * @param estadoStr Estado como String.
	 * @return Número de pedidos en ese estado, o {@code 0} si el estado no es
	 *         válido.
	 */
	public long countByEstado(String estadoStr) {
		try {
			return pedidoRepo.countByEstado(EstadoPedido.valueOf(estadoStr));
		} catch (IllegalArgumentException e) {
			return 0;
		}
	}
}