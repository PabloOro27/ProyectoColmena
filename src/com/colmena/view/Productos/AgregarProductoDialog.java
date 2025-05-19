package com.colmena.view.Productos;

import com.colmena.controller.ProductoController;
import com.colmena.model.Categoria;
import com.colmena.model.Producto;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AgregarProductoDialog extends JDialog {
    
    // Colores Base 
    private final Color COLOR_AMARILLO = new Color(255, 215, 0);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    
    // Controlador
    private ProductoController productoController;
    
    // Componentes del formulario
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JSpinner spnPrecio;
    private JSpinner spnStock;
    private JComboBox<Categoria> cmbCategoria;
    
    // Resultado del diálogo
    private boolean productoAgregado = false;
    
    // constructor
    public AgregarProductoDialog(Frame parent, ProductoController controller, List<Categoria> categorias) {
        super(parent, "Agregar Nuevo Producto", true);
        this.productoController = controller;
                
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
                
        initComponents(categorias);
    }
        
    // Inicializa los componentes del diálogo     
    private void initComponents(List<Categoria> categorias) {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_FONDO);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel del formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_FONDO);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel lblTitulo = new JLabel("Información del Producto");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 15, 5);
        formPanel.add(lblTitulo, gbc);
        
        // Código
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel lblCodigo = new JLabel("Código:");
        formPanel.add(lblCodigo, gbc);
        
        txtCodigo = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtCodigo, gbc);
        
        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblNombre = new JLabel("Nombre:");
        formPanel.add(lblNombre, gbc);
        
        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        
        // Descripción
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblDescripcion = new JLabel("Descripción:");
        formPanel.add(lblDescripcion, gbc);
        
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        
        gbc.gridx = 1;
        formPanel.add(scrollDescripcion, gbc);
        
        // Categoría
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblCategoria = new JLabel("Categoría:");
        formPanel.add(lblCategoria, gbc);
        
        cmbCategoria = new JComboBox<>();
        for (Categoria categoria : categorias) {
            cmbCategoria.addItem(categoria);
        }
        cmbCategoria.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(((Categoria) value).getNombre());
                }
                return this;
            }
        });
        
        gbc.gridx = 1;
        formPanel.add(cmbCategoria, gbc);
        
        // Precio
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblPrecio = new JLabel("Precio:");
        formPanel.add(lblPrecio, gbc);
        
        spnPrecio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 0.01));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnPrecio, "0.00");
        spnPrecio.setEditor(editor);
        
        gbc.gridx = 1;
        formPanel.add(spnPrecio, gbc);
        
        // Stock
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel lblStock = new JLabel("Stock:");
        formPanel.add(lblStock, gbc);
        
        spnStock = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        
        gbc.gridx = 1;
        formPanel.add(spnStock, gbc);
        
        // botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(COLOR_FONDO);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.LIGHT_GRAY);
        btnCancelar.setForeground(COLOR_TEXTO);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFocusPainted(false);        
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(COLOR_AMARILLO);
        btnGuardar.setForeground(COLOR_TEXTO);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarProducto());
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);
                
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
                
        setContentPane(mainPanel);
    }
        
    // Guarda el producto en la base de datos     
    private void guardarProducto() {
        // Validar campos
        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarError("El código del producto es obligatorio");
            txtCodigo.requestFocus();
            return;
        }
        
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre del producto es obligatorio");
            txtNombre.requestFocus();
            return;
        }
        
        if (cmbCategoria.getSelectedItem() == null) {
            mostrarError("Debe seleccionar una categoría");
            cmbCategoria.requestFocus();
            return;
        }
        
        // Crear objeto producto
        Producto nuevoProducto = new Producto();
        nuevoProducto.setCodigo(txtCodigo.getText().trim());
        nuevoProducto.setNombre(txtNombre.getText().trim());
        nuevoProducto.setDescripcion(txtDescripcion.getText().trim());
        nuevoProducto.setCategoria((Categoria) cmbCategoria.getSelectedItem());
        nuevoProducto.setPrecio((Double) spnPrecio.getValue());
        nuevoProducto.setStock((Integer) spnStock.getValue());
        nuevoProducto.setActivo(true);
        
        // Guardar en la base de datos
        boolean exito = productoController.guardarProducto(nuevoProducto);
        
        if (exito) {
            productoAgregado = true;
            JOptionPane.showMessageDialog(this, 
                    "Producto agregado correctamente", 
                    "Producto Agregado", 
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            mostrarError("Error al guardar el producto");
        }
    }
        
    // Muestra un mensaje de error     
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, 
                mensaje, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
    }
        
    // Retorna si el producto fue agregado correctamente     
    public boolean isProductoAgregado() {
        return productoAgregado;
    }
}