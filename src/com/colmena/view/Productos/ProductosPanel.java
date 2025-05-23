package com.colmena.view.Productos;

import com.colmena.controller.ProductoController;
import com.colmena.model.Categoria;
import com.colmena.model.Producto;
import com.colmena.model.Usuario;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.*;

public class ProductosPanel extends JPanel {

    // Colores Base
    private final Color COLOR_AMARILLO = new Color(255, 215, 0);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    private final Color COLOR_TEXTO_CLARO = new Color(100, 100, 100);

    // Controlador
    private ProductoController productoController;

    // Usuario actual
    private Usuario usuarioActual;

    // Componentes
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<Categoria> cmbCategorias;
    private JComboBox<String> cmbFiltroStock;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnActualizarStock;
    private JButton btnRefrescar;
    private JLabel lblTotalProductos;

    // Lista de productos y categorías
    private List<Producto> productos = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();

    // Formateador de moneda
    private NumberFormat formatoMoneda;

    // Constructor
    public ProductosPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.productoController = new ProductoController();
        this.formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "GT"));

        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);

        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        // Panel superior con filtros y botones
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel central con la tabla de productos
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con información adicional
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // panel superior con filtros y botones
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel izquierdo con filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(COLOR_FONDO);

        // Filtro de búsqueda
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBuscar.setForeground(COLOR_TEXTO);

        txtBuscar = new JTextField(15);
        txtBuscar.setPreferredSize(new Dimension(150, 28));
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarProductos();
            }
        });

        // Filtro por categoría
        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setFont(new Font("Arial", Font.PLAIN, 14));
        lblCategoria.setForeground(COLOR_TEXTO);

        cmbCategorias = new JComboBox<>();
        cmbCategorias.setPreferredSize(new Dimension(150, 28));
        cmbCategorias.addItem(null); // Opción "Todas"
        cmbCategorias.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Todas las categorías");
                } else {
                    setText(((Categoria) value).getNombre());
                }
                return this;
            }
        });
        cmbCategorias.addActionListener(e -> filtrarProductos());

        // Filtro por stock
        JLabel lblStock = new JLabel("Stock:");
        lblStock.setFont(new Font("Arial", Font.PLAIN, 14));
        lblStock.setForeground(COLOR_TEXTO);

        cmbFiltroStock = new JComboBox<>(new String[] { "Todos", "Con stock", "Sin stock", "Stock bajo" });
        cmbFiltroStock.setPreferredSize(new Dimension(120, 28));
        cmbFiltroStock.addActionListener(e -> filtrarProductos());

        filterPanel.add(lblBuscar);
        filterPanel.add(txtBuscar);
        filterPanel.add(lblCategoria);
        filterPanel.add(cmbCategorias);
        filterPanel.add(lblStock);
        filterPanel.add(cmbFiltroStock);

        // Panel derecho con botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(COLOR_FONDO);

        btnRefrescar = createButton("Refrescar", e -> cargarDatos());
        btnAgregar = createButton("Agregar Producto", e -> agregarProducto());
        btnEditar = createButton("Editar", e -> editarProducto());
        btnActualizarStock = createButton("Actualizar Stock", e -> actualizarStock());

        buttonPanel.add(btnRefrescar);
        buttonPanel.add(btnActualizarStock);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnAgregar);

        panel.add(filterPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    // panel central con la tabla de productos
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // Crear modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) {
                    return Integer.class;
                } else if (columnIndex == 4) {
                    return Double.class;
                }
                return String.class;
            }
        };

        // Definir columnas
        modeloTabla.addColumn("Código");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Descripción");
        modeloTabla.addColumn("Categoría");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Stock");
        modeloTabla.addColumn("Unidad");

        // Crear tabla
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(25);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setAutoCreateRowSorter(true);
        tablaProductos.getTableHeader().setReorderingAllowed(false);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tablaProductos.setFont(new Font(tablaProductos.getFont().getName(), 
                                 tablaProductos.getFont().getStyle(), 
                                 15));

        // Configurar renderizadores de celda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Renderer personalizado para precios
        tablaProductos.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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

        // Renderer personalizado para stock
        tablaProductos.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
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

        tablaProductos.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tablaProductos.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(185);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(225);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(110);
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(40);
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(40);
        tablaProductos.getColumnModel().getColumn(6).setPreferredWidth(35);


        // scroll panel
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // panel inferior con información adicional
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        lblTotalProductos = new JLabel("Total: 0 productos");
        lblTotalProductos.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTotalProductos.setForeground(COLOR_TEXTO_CLARO);

        panel.add(lblTotalProductos, BorderLayout.WEST);

        return panel;
    }

    // botón
    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(COLOR_TEXTO);
        button.setBackground(COLOR_AMARILLO);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(text.length() > 10 ? 140 : 120, 28));
        button.addActionListener(listener);
        return button;
    }

    // Carga los datos de la base de datos
    private void cargarDatos() {
        categorias = productoController.obtenerCategorias();

        // combobox de categorías
        cmbCategorias.removeAllItems();
        cmbCategorias.addItem(null);
        for (Categoria categoria : categorias) {
            cmbCategorias.addItem(categoria);
        }
        productos = productoController.obtenerProductos();

        modeloTabla.setRowCount(0);

        // Llenar tabla con productos
        for (Producto producto : productos) {
            modeloTabla.addRow(new Object[] {
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getCategoria().getNombre(),
                    producto.getPrecio(),
                    producto.getStock(),
                    producto.getUnidadVenta()
            });
        }
        lblTotalProductos.setText("Total: " + productos.size() + " productos");
    }

    // Filtra los productos según los criterios seleccionados
    private void filtrarProductos() {
        modeloTabla.setRowCount(0);
        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();
        Categoria categoriaSeleccionada = (Categoria) cmbCategorias.getSelectedItem();
        String filtroStock = (String) cmbFiltroStock.getSelectedItem();

        int contadorFiltrados = 0;

        // Aplicar filtros
        for (Producto producto : productos) {
            boolean cumpleBusqueda = textoBusqueda.isEmpty() ||
                    producto.getNombre().toLowerCase().contains(textoBusqueda) ||
                    producto.getDescripcion().toLowerCase().contains(textoBusqueda) ||
                    producto.getCodigo().toLowerCase().contains(textoBusqueda);

            boolean cumpleCategoria = categoriaSeleccionada == null ||
                    producto.getCategoria().getId() == categoriaSeleccionada.getId();

            boolean cumpleStock = true;
            if (filtroStock.equals("Con stock")) {
                cumpleStock = producto.getStock() > 0;
            } else if (filtroStock.equals("Sin stock")) {
                cumpleStock = producto.getStock() <= 0;
            } else if (filtroStock.equals("Stock bajo")) {
                cumpleStock = producto.getStock() > 0 && producto.getStock() < 10;
            }

            if (cumpleBusqueda && cumpleCategoria && cumpleStock) {
                modeloTabla.addRow(new Object[] {
                        producto.getCodigo(),
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getCategoria().getNombre(),
                        producto.getPrecio(),
                        producto.getStock(),
                        producto.getUnidadVenta()
                });
                contadorFiltrados++;
            }
        }

        // Actualizar etiqueta de total
        lblTotalProductos.setText("Mostrando: " + contadorFiltrados + " de " + productos.size() + " productos");
    }

    // Agregar producto
    private void agregarProducto() {
        AgregarProductoDialog dialog = new AgregarProductoDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                productoController,
                categorias);

        dialog.setVisible(true);

        if (dialog.isProductoAgregado()) {
            cargarDatos();
        }
    }

    // Editar producto
    private void editarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para editar",
                    "Editar Producto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tablaProductos.convertRowIndexToModel(filaSeleccionada);

        // código del producto
        String codigoProducto = (String) modeloTabla.getValueAt(modelRow, 0);

        // Buscar el producto en la lista
        Producto productoSeleccionado = null;
        for (Producto p : productos) {
            if (p.getCodigo().equals(codigoProducto)) {
                productoSeleccionado = p;
                break;
            }
        }

        if (productoSeleccionado != null) {
            EditarProductoDialog dialog = new EditarProductoDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    productoController,
                    categorias,
                    productoSeleccionado);

            dialog.setVisible(true);

            if (dialog.isProductoEditado()) {
                cargarDatos();
            }
        }
    }

    // Muestra el diálogo para actualizar el stock de un producto
    private void actualizarStock() {
        int filaSeleccionada = tablaProductos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un producto para actualizar su stock",
                    "Actualizar Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tablaProductos.convertRowIndexToModel(filaSeleccionada);

        String codigoProducto = (String) modeloTabla.getValueAt(modelRow, 0);
        int stockActual = (Integer) modeloTabla.getValueAt(modelRow, 5);

        Producto productoSeleccionado = null;
        for (Producto p : productos) {
            if (p.getCodigo().equals(codigoProducto)) {
                productoSeleccionado = p;
                break;
            }
        }

        if (productoSeleccionado != null) {
            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Información del producto
            JLabel lblProducto = new JLabel("Producto:");
            JLabel lblProductoValor = new JLabel(productoSeleccionado.getNombre());
            lblProductoValor.setFont(new Font("Arial", Font.BOLD, 16));

            JLabel lblStockActual = new JLabel("Stock Actual:");
            JLabel lblStockActualValor = new JLabel(String.valueOf(stockActual));

            JLabel lblNuevoStock = new JLabel("Nuevo Stock:");
            JSpinner spnNuevoStock = new JSpinner(new SpinnerNumberModel(stockActual, 0, 9999, 1));

            JLabel lblNota = new JLabel("Nota:");
            JTextField txtNota = new JTextField();

            panel.add(lblProducto);
            panel.add(lblProductoValor);
            panel.add(lblStockActual);
            panel.add(lblStockActualValor);
            panel.add(lblNuevoStock);
            panel.add(spnNuevoStock);
            panel.add(lblNota);
            panel.add(txtNota);

            int resultado = JOptionPane.showConfirmDialog(this, panel,
                    "Actualizar Stock", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (resultado == JOptionPane.OK_OPTION) {
                int nuevoStock = (Integer) spnNuevoStock.getValue();
                String nota = txtNota.getText().trim();

                if (nota.isEmpty()) {
                    nota = "Actualización manual de stock";
                }

                // Actualizar stock en la base de datos
                boolean exito = productoController.actualizarStock(
                        productoSeleccionado.getId(),
                        nuevoStock,
                        usuarioActual.getId(),
                        nota);

                if (exito) {
                    JOptionPane.showMessageDialog(this, "Stock actualizado correctamente",
                            "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);

                    // Recargar datos
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el stock",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}