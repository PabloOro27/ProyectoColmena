package com.colmena.view.Pedido;

import com.colmena.controller.PedidoController;
import com.colmena.controller.ProductoController;
import com.colmena.model.Cliente;
import com.colmena.model.DetallePedido;
import com.colmena.model.Pedido;
import com.colmena.model.Producto;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class NuevoPedidoDialog extends JDialog {

    private final Color COLOR_AMARILLO = new Color(255, 215, 0);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);

    private PedidoController pedidoController;
    private ProductoController productoController;
    private Cliente cliente;

    private JTable tablaProductos;
    private DefaultTableModel modeloTablaProductos;
    private JTable tablaDetalles;
    private DefaultTableModel modeloTablaDetalles;

    private JTextField txtBuscarProducto;
    private JComboBox<String> cmbFiltroCategoria;
    private JLabel lblClienteNombre;
    private JLabel lblTotal;
    private JButton btnAgregar;
    private JButton btnQuitar;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private List<Producto> productos;
    private List<DetallePedido> detallesPedido;

    private NumberFormat formatoMoneda;
    private double totalPedido = 0.0;

    private boolean pedidoCreado = false;
    private int pedidoId = -1;

    public NuevoPedidoDialog(Frame parent, Cliente cliente) {
        super(parent, "Nuevo Pedido", true);
        this.cliente = cliente;
        this.pedidoController = new PedidoController();
        this.productoController = new ProductoController();
        this.formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "GT"));
        this.detallesPedido = new ArrayList<>();

        setSize(1000, 700);
        setLocationRelativeTo(parent);

        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_FONDO);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior con información del cliente
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Panel central con selección de productos y detalles del pedido
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con botones y total
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        // Información del cliente
        JPanel clientePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientePanel.setBackground(COLOR_FONDO);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(new Font("Arial", Font.BOLD, 14));
        lblCliente.setForeground(COLOR_TEXTO);

        lblClienteNombre = new JLabel(cliente.getNombre() + " " + cliente.getApellido());
        lblClienteNombre.setFont(new Font("Arial", Font.PLAIN, 14));
        lblClienteNombre.setForeground(COLOR_TEXTO);

        clientePanel.add(lblCliente);
        clientePanel.add(lblClienteNombre);

        // Fecha actual
        JPanel fechaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        fechaPanel.setBackground(COLOR_FONDO);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(new Font("Arial", Font.BOLD, 14));
        lblFecha.setForeground(COLOR_TEXTO);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        JLabel lblFechaValor = new JLabel(sdf.format(new java.util.Date()));
        lblFechaValor.setFont(new Font("Arial", Font.PLAIN, 14));
        lblFechaValor.setForeground(COLOR_TEXTO);

        fechaPanel.add(lblFecha);
        fechaPanel.add(lblFechaValor);

        panel.add(clientePanel, BorderLayout.WEST);
        panel.add(fechaPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);

        // Panel superior con filtros y selección de productos
        JPanel productosPanel = new JPanel(new BorderLayout(0, 10));
        productosPanel.setBackground(COLOR_FONDO);

        // Panel de filtros
        JPanel filtrosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtrosPanel.setBackground(COLOR_FONDO);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.PLAIN, 14));

        txtBuscarProducto = new JTextField(20);
        txtBuscarProducto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarProductos();
            }
        });

        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setFont(new Font("Arial", Font.PLAIN, 14));

        cmbFiltroCategoria = new JComboBox<>();
        cmbFiltroCategoria.setPreferredSize(new Dimension(150, 25));
        cmbFiltroCategoria.addActionListener(e -> filtrarProductos());

        filtrosPanel.add(lblBuscar);
        filtrosPanel.add(txtBuscarProducto);
        filtrosPanel.add(lblCategoria);
        filtrosPanel.add(cmbFiltroCategoria);

        // Título de la sección
        JLabel lblProductos = new JLabel("Productos Disponibles");
        lblProductos.setFont(new Font("Arial", Font.BOLD, 14));
        lblProductos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Panel con título y filtros
        JPanel headerProductosPanel = new JPanel(new BorderLayout());
        headerProductosPanel.setBackground(COLOR_FONDO);
        headerProductosPanel.add(lblProductos, BorderLayout.WEST);
        headerProductosPanel.add(filtrosPanel, BorderLayout.EAST);

        productosPanel.add(headerProductosPanel, BorderLayout.NORTH);

        // Tabla de productos
        modeloTablaProductos = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modeloTablaProductos.addColumn("Código");
        modeloTablaProductos.addColumn("Producto");
        modeloTablaProductos.addColumn("Categoría");
        modeloTablaProductos.addColumn("Precio");
        modeloTablaProductos.addColumn("Stock");

        tablaProductos = new JTable(modeloTablaProductos);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setRowHeight(25);
        tablaProductos.getTableHeader().setReorderingAllowed(false);

        // Configurar renderizadores
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Renderer para moneda
        tablaProductos.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(formatoMoneda.format((Double) value));
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return this;
            }
        });

        // Renderer para stock (colorea en rojo si es bajo)
        tablaProductos.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Integer) {
                    int stock = (Integer) value;
                    if (stock <= 0) {
                        setForeground(Color.RED);
                    } else if (stock < 10) {
                        setForeground(new Color(255, 140, 0)); // Naranja
                    } else {
                        setForeground(isSelected ? Color.WHITE : COLOR_TEXTO);
                    }
                }
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });

        // Ajustar tamaños de columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(80); // Código
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(250); // Producto
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(100); // Categoría
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(80); // Precio
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(60); // Stock

        // Añadir doble clic para agregar producto
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    agregarProducto();
                }
            }
        });

        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        productosPanel.add(scrollProductos, BorderLayout.CENTER);

        // Panel de botones entre tablas
        JPanel botonesPanel = new JPanel();
        botonesPanel.setLayout(new BoxLayout(botonesPanel, BoxLayout.Y_AXIS));
        botonesPanel.setBackground(COLOR_FONDO);
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnAgregar = new JButton("Agregar >");
        btnAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregar.setBackground(COLOR_AMARILLO);
        btnAgregar.setForeground(COLOR_TEXTO);
        btnAgregar.setBorderPainted(false);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregar.setMaximumSize(new Dimension(100, 30));
        btnAgregar.addActionListener(e -> agregarProducto());

        btnQuitar = new JButton("< Quitar");
        btnQuitar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnQuitar.setFont(new Font("Arial", Font.BOLD, 12));
        btnQuitar.setBackground(new Color(220, 220, 220));
        btnQuitar.setForeground(COLOR_TEXTO);
        btnQuitar.setBorderPainted(false);
        btnQuitar.setFocusPainted(false);
        btnQuitar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuitar.setMaximumSize(new Dimension(100, 30));
        btnQuitar.addActionListener(e -> quitarProducto());

        botonesPanel.add(btnAgregar);
        botonesPanel.add(Box.createVerticalStrut(10));
        botonesPanel.add(btnQuitar);

        // Panel inferior con detalles del pedido
        JPanel detallesPanel = new JPanel(new BorderLayout(0, 10));
        detallesPanel.setBackground(COLOR_FONDO);

        JLabel lblDetalles = new JLabel("Detalles del Pedido");
        lblDetalles.setFont(new Font("Arial", Font.BOLD, 14));
        lblDetalles.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        detallesPanel.add(lblDetalles, BorderLayout.NORTH);

        // Tabla de detalles
        modeloTablaDetalles = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Solo la columna cantidad es editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) { // Columna cantidad
                    return Integer.class;
                } else if (columnIndex == 4 || columnIndex == 5) { // Columnas de precio y subtotal
                    return Double.class;
                }
                return String.class;
            }
        };

        modeloTablaDetalles.addColumn("Código");
        modeloTablaDetalles.addColumn("Producto");
        modeloTablaDetalles.addColumn("Categoría");
        modeloTablaDetalles.addColumn("Cantidad");
        modeloTablaDetalles.addColumn("Precio Unit.");
        modeloTablaDetalles.addColumn("Subtotal");

        tablaDetalles = new JTable(modeloTablaDetalles);
        tablaDetalles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDetalles.setRowHeight(25);
        tablaDetalles.getTableHeader().setReorderingAllowed(false);

        // Configurar renderizadores y editores
        // Renderer para moneda
        DefaultTableCellRenderer monedaRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(formatoMoneda.format((Double) value));
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return this;
            }
        };

        tablaDetalles.getColumnModel().getColumn(4).setCellRenderer(monedaRenderer);
        tablaDetalles.getColumnModel().getColumn(5).setCellRenderer(monedaRenderer);

        // Editor para cantidad
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 999, 1);
        JSpinner spinner = new JSpinner(spinnerModel);
        DefaultCellEditor cantidadEditor = new DefaultCellEditor(new JTextField()) {
            private JSpinner spinner;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                    int column) {
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBackground(Color.WHITE);

                spinner = new JSpinner(new SpinnerNumberModel(
                        value instanceof Integer ? (Integer) value : 1,
                        1, 999, 1));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                panel.add(spinner, gbc);

                return panel;
            }

            @Override
            public Object getCellEditorValue() {
                return spinner.getValue();
            }
        };

        tablaDetalles.getColumnModel().getColumn(3).setCellEditor(cantidadEditor);

        // Listener para actualizar subtotal al cambiar cantidad
        tablaDetalles.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                int row = e.getFirstRow();
                int cantidad = (Integer) modeloTablaDetalles.getValueAt(row, 3);
                double precioUnitario = (Double) modeloTablaDetalles.getValueAt(row, 4);
                double subtotal = cantidad * precioUnitario;

                // Actualizar subtotal en la tabla sin disparar otro evento
                modeloTablaDetalles.removeTableModelListener(tableModelListener -> {
                });
                modeloTablaDetalles.setValueAt(subtotal, row, 5);
                modeloTablaDetalles.addTableModelListener(tableModelListener -> {
                });

                // Actualizar el detalle en la lista
                actualizarDetallePedido(row, cantidad);

                // Recalcular total
                calcularTotal();
            }
        });

        // Ajustar tamaños de columnas
        tablaDetalles.getColumnModel().getColumn(0).setPreferredWidth(80); // Código
        tablaDetalles.getColumnModel().getColumn(1).setPreferredWidth(250); // Producto
        tablaDetalles.getColumnModel().getColumn(2).setPreferredWidth(100); // Categoría
        tablaDetalles.getColumnModel().getColumn(3).setPreferredWidth(80); // Cantidad
        tablaDetalles.getColumnModel().getColumn(4).setPreferredWidth(100); // Precio Unit.
        tablaDetalles.getColumnModel().getColumn(5).setPreferredWidth(100); // Subtotal

        JScrollPane scrollDetalles = new JScrollPane(tablaDetalles);
        detallesPanel.add(scrollDetalles, BorderLayout.CENTER);

        // Crear panel dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, productosPanel, detallesPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);

        // Agregar componentes al panel principal
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(botonesPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));

        // Panel izquierdo con total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setBackground(COLOR_FONDO);

        JLabel lblTotalText = new JLabel("Total:");
        lblTotalText.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalText.setForeground(COLOR_TEXTO);

        lblTotal = new JLabel(formatoMoneda.format(0.0));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(COLOR_AMARILLO.darker());

        totalPanel.add(lblTotalText);
        totalPanel.add(lblTotal);

        // Panel derecho con botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesPanel.setBackground(COLOR_FONDO);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(220, 220, 220));
        btnCancelar.setForeground(COLOR_TEXTO);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar = new JButton("Guardar Pedido");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGuardar.setBackground(COLOR_AMARILLO);
        btnGuardar.setForeground(COLOR_TEXTO);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarPedido());

        botonesPanel.add(btnCancelar);
        botonesPanel.add(btnGuardar);

        panel.add(totalPanel, BorderLayout.WEST);
        panel.add(botonesPanel, BorderLayout.EAST);

        return panel;
    }

    private void cargarDatos() {
        // Cargar productos
        productos = productoController.obtenerProductos();

        // Cargar categorías en el combo
        cmbFiltroCategoria.addItem("Todas las categorías");
        List<com.colmena.model.Categoria> categorias = productoController.obtenerCategorias();
        for (com.colmena.model.Categoria categoria : categorias) {
            cmbFiltroCategoria.addItem(categoria.getNombre());
        }

        // Mostrar productos en la tabla
        mostrarProductos(productos);
    }

    private void mostrarProductos(List<Producto> listaProductos) {
        modeloTablaProductos.setRowCount(0);

        for (Producto producto : listaProductos) {
            modeloTablaProductos.addRow(new Object[] {
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getCategoria().getNombre(),
                    producto.getPrecio(),
                    producto.getStock()
            });
        }
    }

    private void filtrarProductos() {
        String textoBusqueda = txtBuscarProducto.getText().toLowerCase().trim();
        String categoriaSeleccionada = (String) cmbFiltroCategoria.getSelectedItem();

        List<Producto> productosFiltrados = new ArrayList<>();

        for (Producto producto : productos) {
            boolean cumpleBusqueda = textoBusqueda.isEmpty() ||
                    producto.getNombre().toLowerCase().contains(textoBusqueda) ||
                    producto.getCodigo().toLowerCase().contains(textoBusqueda);

            boolean cumpleCategoria = categoriaSeleccionada.equals("Todas las categorías") ||
                    producto.getCategoria().getNombre().equals(categoriaSeleccionada);

            if (cumpleBusqueda && cumpleCategoria) {
                productosFiltrados.add(producto);
            }
        }

        mostrarProductos(productosFiltrados);
    }

    private void agregarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un producto para agregar al pedido",
                    "Agregar Producto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir índice si la tabla está ordenada
        int modelRow = tablaProductos.convertRowIndexToModel(filaSeleccionada);

        // Obtener datos del producto
        String codigo = (String) modeloTablaProductos.getValueAt(modelRow, 0);
        int stock = (Integer) modeloTablaProductos.getValueAt(modelRow, 4);

        // Verificar si hay stock disponible
        if (stock <= 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay stock disponible para este producto",
                    "Sin Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Buscar el producto en la lista
        Producto productoSeleccionado = null;
        for (Producto p : productos) {
            if (p.getCodigo().equals(codigo)) {
                productoSeleccionado = p;
                break;
            }
        }

        if (productoSeleccionado != null) {
            // Verificar si el producto ya está en el pedido
            for (int i = 0; i < modeloTablaDetalles.getRowCount(); i++) {
                String codigoExistente = (String) modeloTablaDetalles.getValueAt(i, 0);
                if (codigoExistente.equals(codigo)) {
                    // Incrementar cantidad
                    int cantidadActual = (Integer) modeloTablaDetalles.getValueAt(i, 3);
                    if (cantidadActual < stock) {
                        modeloTablaDetalles.setValueAt(cantidadActual + 1, i, 3);
                        // El evento TableModelListener se encargará de actualizar subtotal y total
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No hay suficiente stock disponible para agregar más unidades",
                                "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                    }
                    return;
                }
            }

            // Producto no existe en el pedido, agregarlo
            int cantidad = 1;
            double precioUnitario = productoSeleccionado.getPrecio();
            double subtotal = cantidad * precioUnitario;

            // Agregar a la tabla
            modeloTablaDetalles.addRow(new Object[] {
                    productoSeleccionado.getCodigo(),
                    productoSeleccionado.getNombre(),
                    productoSeleccionado.getCategoria().getNombre(),
                    cantidad,
                    precioUnitario,
                    subtotal
            });

            // Crear detalle y agregarlo a la lista
            DetallePedido detalle = new DetallePedido(productoSeleccionado, cantidad, precioUnitario);
            detallesPedido.add(detalle);

            // Recalcular total
            calcularTotal();
        }
    }

    private void quitarProducto() {
        int filaSeleccionada = tablaDetalles.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un producto para quitar del pedido",
                    "Quitar Producto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir índice si la tabla está ordenada
        int modelRow = tablaDetalles.convertRowIndexToModel(filaSeleccionada);

        // Remover de la lista de detalles
        detallesPedido.remove(modelRow);

        // Remover de la tabla
        modeloTablaDetalles.removeRow(modelRow);

        // Recalcular total
        calcularTotal();
    }

    private void actualizarDetallePedido(int row, int cantidad) {
        if (row >= 0 && row < detallesPedido.size()) {
            DetallePedido detalle = detallesPedido.get(row);
            detalle.setCantidad(cantidad);
            detalle.calcularSubtotal();
        }
    }

    private void calcularTotal() {
        totalPedido = 0.0;

        for (DetallePedido detalle : detallesPedido) {
            totalPedido += detalle.getSubtotal();
        }

        lblTotal.setText(formatoMoneda.format(totalPedido));

        // Habilitar o deshabilitar botón de guardar según si hay productos
        btnGuardar.setEnabled(!detallesPedido.isEmpty());
    }

    private void guardarPedido() {
        if (detallesPedido.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se puede guardar un pedido sin productos",
                    "Pedido Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear pedido
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setCliente(cliente);
        nuevoPedido.setFecha(new Date(System.currentTimeMillis()));
        nuevoPedido.setEstado("Pendiente");
        nuevoPedido.setTotal(totalPedido);
        nuevoPedido.setDetalles(detallesPedido);

        // Guardar en la base de datos
        pedidoId = pedidoController.crearPedido(nuevoPedido);

        if (pedidoId > 0) {
            pedidoCreado = true;
            JOptionPane.showMessageDialog(this,
                    "Pedido creado correctamente",
                    "Pedido Creado", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al crear el pedido",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isPedidoCreado() {
        return pedidoCreado;
    }

    public int getPedidoId() {
        return pedidoId;
    }
}