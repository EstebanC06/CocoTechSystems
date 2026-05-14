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

import co.edu.unbosque.cocotechback.dto.ProductoDTO;
import co.edu.unbosque.cocotechback.model.Categoria;
import co.edu.unbosque.cocotechback.model.Producto;
import co.edu.unbosque.cocotechback.model.Proveedor;
import co.edu.unbosque.cocotechback.repository.jpa.CategoriaRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ProductoRepository;
import co.edu.unbosque.cocotechback.repository.jpa.ProveedorRepository;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Producto}.
 * <p>
 * Implementa {@link CRUDOperation} para proporcionar las operaciones estándar
 * de creación, lectura, actualización y eliminación de productos del
 * catálogo del supermercado. También expone métodos para los escenarios
 * analíticos del proyecto: producto más vendido por categoría y ranking de
 * productos por ventas.
 */
@Service
public class ProductoService implements CRUDOperation<ProductoDTO, Producto> {

	/**
	 * Repositorio para la gestión de la entidad {@link Producto}.
	 */
	@Autowired
	private ProductoRepository productoRepo;

	/**
	 * Repositorio para resolver la relación con {@link Categoria}.
	 */
	@Autowired
	private CategoriaRepository categoriaRepo;

	/**
	 * Repositorio para resolver la relación con {@link Proveedor}.
	 */
	@Autowired
	private ProveedorRepository proveedorRepo;

	/**
	 * Mapper para la conversión entre objetos DTO y entidades JPA.
	 */
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Constructor por defecto de {@code ProductoService}.
	 */
	public ProductoService() {
	}

	/**
	 * Crea un nuevo producto en la base de datos a partir de un
	 * {@link ProductoDTO}.
	 * <p>
	 * Valida que el nombre, la categoría y el proveedor estén presentes y que
	 * ambas entidades relacionadas existan.
	 *
	 * @param data El {@link ProductoDTO} con la información del nuevo producto.
	 * @param rol  No utilizado en esta implementación.
	 * @return {@code 0} si la creación fue exitosa,
	 *         {@code 1} si ya existe un producto con ese nombre,
	 *         {@code 2} si la categoría o el proveedor no existen,
	 *         {@code 4} si algún campo requerido está ausente.
	 */
	@Override
	public int create(ProductoDTO data, String rol) {
		if (data.getNombre() == null || data.getNombre().isEmpty()
				|| data.getPrecio() == null || data.getIdCategoria() == null
				|| data.getIdProveedor() == null) {
			return 4;
		}
		if (productoRepo.existsByNombre(data.getNombre())) {
			return 1;
		}
		Optional<Categoria> categoriaFound = categoriaRepo.findById(data.getIdCategoria());
		Optional<Proveedor> proveedorFound = proveedorRepo.findById(data.getIdProveedor());
		if (!categoriaFound.isPresent() || !proveedorFound.isPresent()) {
			return 2;
		}
		Producto entity = new Producto();
		entity.setNombre(data.getNombre());
		entity.setPrecio(data.getPrecio());
		entity.setStock(data.getStock() != null ? data.getStock() : 0);
		entity.setFechaVencimiento(data.getFechaVencimiento());
		entity.setCategoria(categoriaFound.get());
		entity.setProveedor(proveedorFound.get());
		// Campos e-commerce (con valores por defecto seguros)
		entity.setImagenUrl(data.getImagenUrl());
		entity.setDescripcion(data.getDescripcion());
		entity.setDescuentoPorcentaje(
				data.getDescuentoPorcentaje() != null ? data.getDescuentoPorcentaje() : 0);
		entity.setDestacado(data.getDestacado() != null ? data.getDestacado() : false);
		entity.setActivo(data.getActivo() != null ? data.getActivo() : true);
		productoRepo.save(entity);
		return 0;
	}

	/**
	 * Obtiene todos los productos registrados en la base de datos.
	 *
	 * @return Una lista de {@link ProductoDTO}. Retorna una lista vacía si no
	 *         hay productos.
	 */
	@Override
	public List<ProductoDTO> getAll() {
		List<Producto> entityList = productoRepo.findAll();
		List<ProductoDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> {
			ProductoDTO dto = modelMapper.map(entity, ProductoDTO.class);
			if (entity.getCategoria() != null) {
				dto.setIdCategoria(entity.getCategoria().getIdCategoria());
			}
			if (entity.getProveedor() != null) {
				dto.setIdProveedor(entity.getProveedor().getIdProveedor());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Obtiene todos los productos activos (visibles en el catálogo público
	 * del e-commerce). Filtra los marcados como {@code activo = false} (baja
	 * lógica).
	 *
	 * @return Lista de productos activos.
	 */
	public List<ProductoDTO> getAllActivos() {
		List<Producto> entityList = productoRepo.findByActivoTrue();
		List<ProductoDTO> dtoList = new ArrayList<>();
		entityList.forEach(entity -> {
			ProductoDTO dto = modelMapper.map(entity, ProductoDTO.class);
			if (entity.getCategoria() != null) {
				dto.setIdCategoria(entity.getCategoria().getIdCategoria());
			}
			if (entity.getProveedor() != null) {
				dto.setIdProveedor(entity.getProveedor().getIdProveedor());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Obtiene un producto por su ID.
	 *
	 * @param id El ID del producto a buscar.
	 * @return Un {@link ProductoDTO} o {@code null} si no existe.
	 */
	public ProductoDTO getById(Long id) {
		Optional<Producto> found = productoRepo.findById(id);
		if (found.isPresent()) {
			Producto entity = found.get();
			ProductoDTO dto = modelMapper.map(entity, ProductoDTO.class);
			if (entity.getCategoria() != null) {
				dto.setIdCategoria(entity.getCategoria().getIdCategoria());
			}
			if (entity.getProveedor() != null) {
				dto.setIdProveedor(entity.getProveedor().getIdProveedor());
			}
			return dto;
		}
		return null;
	}

	/**
	 * Elimina un producto por su ID.
	 *
	 * @param id El ID del producto a eliminar.
	 * @return {@code 0} si fue exitosa, {@code 2} si no existe.
	 */
	@Override
	public int deleteById(Long id) {
		Optional<Producto> found = productoRepo.findById(id);
		if (found.isPresent()) {
			productoRepo.delete(found.get());
			return 0;
		}
		return 2;
	}

	/**
	 * Actualiza los datos de un producto existente por su ID.
	 * <p>
	 * Solo actualiza los campos no nulos del DTO recibido. Verifica que la nueva
	 * categoría y el nuevo proveedor existan si se cambian.
	 *
	 * @param id      El ID del producto a actualizar.
	 * @param newData El {@link ProductoDTO} con los nuevos datos.
	 * @return {@code 0} si fue exitosa,
	 *         {@code 1} si el nuevo nombre ya está en uso,
	 *         {@code 2} si no existe el producto, la categoría o el proveedor.
	 */
	@Override
	public int updateById(Long id, ProductoDTO newData) {
		Optional<Producto> found = productoRepo.findById(id);
		if (!found.isPresent()) {
			return 2;
		}
		Producto temp = found.get();
		if (newData.getNombre() != null && !newData.getNombre().equals(temp.getNombre())) {
			if (productoRepo.existsByNombre(newData.getNombre())) {
				return 1;
			}
			temp.setNombre(newData.getNombre());
		}
		if (newData.getPrecio() != null) {
			temp.setPrecio(newData.getPrecio());
		}
		if (newData.getStock() != null) {
			temp.setStock(newData.getStock());
		}
		if (newData.getFechaVencimiento() != null) {
			temp.setFechaVencimiento(newData.getFechaVencimiento());
		}
		if (newData.getIdCategoria() != null) {
			Optional<Categoria> categoriaFound = categoriaRepo.findById(newData.getIdCategoria());
			if (!categoriaFound.isPresent()) {
				return 2;
			}
			temp.setCategoria(categoriaFound.get());
		}
		if (newData.getIdProveedor() != null) {
			Optional<Proveedor> proveedorFound = proveedorRepo.findById(newData.getIdProveedor());
			if (!proveedorFound.isPresent()) {
				return 2;
			}
			temp.setProveedor(proveedorFound.get());
		}
		// Campos e-commerce (actualización parcial, solo si vienen definidos)
		if (newData.getImagenUrl() != null) {
			temp.setImagenUrl(newData.getImagenUrl());
		}
		if (newData.getDescripcion() != null) {
			temp.setDescripcion(newData.getDescripcion());
		}
		if (newData.getDescuentoPorcentaje() != null) {
			temp.setDescuentoPorcentaje(newData.getDescuentoPorcentaje());
		}
		if (newData.getDestacado() != null) {
			temp.setDestacado(newData.getDestacado());
		}
		if (newData.getActivo() != null) {
			temp.setActivo(newData.getActivo());
		}
		productoRepo.save(temp);
		return 0;
	}

	/**
	 * Retorna el producto más vendido dentro de cada categoría, basado en la
	 * suma de cantidades vendidas en los detalles de venta.
	 * <p>
	 * Satisface el objetivo analítico "Producto más vendido de cada categoría".
	 *
	 * @return Una lista de arreglos {@code Object[]} con:
	 *         [0] nombre de la categoría, [1] nombre del producto,
	 *         [2] total de unidades vendidas.
	 */
	public List<Object[]> getProductoMasVendidoPorCategoria() {
		return productoRepo.findProductoMasVendidoPorCategoria();
	}

	/**
	 * Retorna todos los productos ordenados de mayor a menor por cantidad total
	 * vendida.
	 *
	 * @return Una lista de arreglos {@code Object[]} con:
	 *         [0] nombre del producto, [1] total de unidades vendidas.
	 */
	public List<Object[]> getProductosOrdenadosPorVentas() {
		return productoRepo.findProductosOrdenadosPorVentas();
	}

	/**
	 * Retorna los productos marcados como destacados en el catálogo (para
	 * mostrar en la Home del e-commerce).
	 *
	 * @return Lista de {@link ProductoDTO} con destacado = true y activo = true.
	 */
	public List<ProductoDTO> getDestacados() {
		List<Producto> entities = productoRepo.findByDestacadoTrueAndActivoTrue();
		List<ProductoDTO> dtoList = new ArrayList<>();
		entities.forEach(entity -> {
			ProductoDTO dto = modelMapper.map(entity, ProductoDTO.class);
			if (entity.getCategoria() != null) {
				dto.setIdCategoria(entity.getCategoria().getIdCategoria());
			}
			if (entity.getProveedor() != null) {
				dto.setIdProveedor(entity.getProveedor().getIdProveedor());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Retorna los productos activos de una categoría específica.
	 *
	 * @param idCategoria El ID de la categoría.
	 * @return Lista de {@link ProductoDTO} activos en esa categoría.
	 */
	public List<ProductoDTO> getProductosActivosPorCategoria(Long idCategoria) {
		List<Producto> entities = productoRepo
				.findByCategoria_IdCategoriaAndActivoTrue(idCategoria);
		List<ProductoDTO> dtoList = new ArrayList<>();
		entities.forEach(entity -> {
			ProductoDTO dto = modelMapper.map(entity, ProductoDTO.class);
			if (entity.getCategoria() != null) {
				dto.setIdCategoria(entity.getCategoria().getIdCategoria());
			}
			if (entity.getProveedor() != null) {
				dto.setIdProveedor(entity.getProveedor().getIdProveedor());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Busca productos por término de nombre (case-insensitive) y activos.
	 *
	 * @param q Texto a buscar dentro del nombre.
	 * @return Lista de productos coincidentes activos.
	 */
	public List<ProductoDTO> buscarPorNombre(String q) {
		List<Producto> entities = productoRepo
				.findByNombreContainingIgnoreCaseAndActivoTrue(q);
		List<ProductoDTO> dtoList = new ArrayList<>();
		entities.forEach(entity -> {
			ProductoDTO dto = modelMapper.map(entity, ProductoDTO.class);
			if (entity.getCategoria() != null) {
				dto.setIdCategoria(entity.getCategoria().getIdCategoria());
			}
			if (entity.getProveedor() != null) {
				dto.setIdProveedor(entity.getProveedor().getIdProveedor());
			}
			dtoList.add(dto);
		});
		return dtoList;
	}

	/**
	 * Decrementa el stock de un producto en una cantidad. Verifica disponibilidad.
	 *
	 * @param idProducto El ID del producto.
	 * @param cantidad   La cantidad a decrementar.
	 * @return {@code true} si el decremento fue exitoso,
	 *         {@code false} si no hay stock suficiente o el producto no existe.
	 */
	public boolean decrementarStock(Long idProducto, int cantidad) {
		Optional<Producto> found = productoRepo.findById(idProducto);
		if (!found.isPresent()) return false;
		Producto p = found.get();
		int stockActual = p.getStock() != null ? p.getStock() : 0;
		if (stockActual < cantidad) return false;
		p.setStock(stockActual - cantidad);
		productoRepo.save(p);
		return true;
	}

	/**
	 * Incrementa el stock de un producto en una cantidad. Se usa al cancelar
	 * pedidos para restituir el inventario.
	 *
	 * @param idProducto El ID del producto.
	 * @param cantidad   La cantidad a incrementar.
	 */
	public void incrementarStock(Long idProducto, int cantidad) {
		Optional<Producto> found = productoRepo.findById(idProducto);
		if (found.isPresent()) {
			Producto p = found.get();
			int stockActual = p.getStock() != null ? p.getStock() : 0;
			p.setStock(stockActual + cantidad);
			productoRepo.save(p);
		}
	}

	/** {@inheritDoc} */
	@Override
	public long count() {
		return productoRepo.count();
	}

	/** {@inheritDoc} */
	@Override
	public boolean exist(Long id) {
		return productoRepo.existsById(id);
	}

	/** No aplica para Producto. */
	@Override
	public Producto encrypt(ProductoDTO data) {
		return modelMapper.map(data, Producto.class);
	}

	/** No aplica para Producto. */
	@Override
	public String decrypt(ProductoDTO data) {
		return null;
	}

	/** No aplica para Producto. Retorna {@code -1}. */
	@Override
	public int updatePassword(Long id, ProductoDTO newData) {
		return -1;
	}

	/** No aplica para Producto. Retorna {@code -1}. */
	@Override
	public int updateCorreo(Long id, ProductoDTO newData) {
		return -1;
	}

	/** No aplica para Producto. Retorna {@code -1}. */
	@Override
	public int updateRol(Long id, ProductoDTO newData) {
		return -1;
	}

	/** No aplica para Producto. Retorna {@code -1}. */
	@Override
	public int updateCode(Long id, ProductoDTO newData) {
		return -1;
	}
}