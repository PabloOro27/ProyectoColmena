package com.colmena.view.Clientes;

import com.colmena.controller.ClienteController;
import com.colmena.model.Cliente;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AgregarClienteDialog extends JDialog {
    
    private final Color COLOR_AMARILLO = new Color(255, 215, 0);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    
    private ClienteController clienteController;
    
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextArea txtDireccion;
    
    private boolean clienteAgregado = false;
    private int clienteId = -1;
    
    public AgregarClienteDialog(Frame parent, ClienteController controller) {
        super(parent, "Agregar Nuevo Cliente", true);
        this.clienteController = controller;
        
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initComponents();
    }
    
    private void initComponents() {
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
        JLabel lblTitulo = new JLabel("Información del Cliente");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 15, 5);
        formPanel.add(lblTitulo, gbc);
        
        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel lblNombre = new JLabel("Nombre*:");
        formPanel.add(lblNombre, gbc);
        
        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        
        // Apellido
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblApellido = new JLabel("Apellido*:");
        formPanel.add(lblApellido, gbc);
        
        txtApellido = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtApellido, gbc);
        
        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblTelefono = new JLabel("Teléfono*:");
        formPanel.add(lblTelefono, gbc);
        
        txtTelefono = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtTelefono, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblEmail = new JLabel("Email:");
        formPanel.add(lblEmail, gbc);
        
        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        
        // Dirección
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblDireccion = new JLabel("Dirección:");
        formPanel.add(lblDireccion, gbc);
        
        txtDireccion = new JTextArea(4, 20);
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        JScrollPane scrollDireccion = new JScrollPane(txtDireccion);
        
        gbc.gridx = 1;
        formPanel.add(scrollDireccion, gbc);
        
        // Nota informativa
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 12));
        lblNota.setForeground(Color.GRAY);
        formPanel.add(lblNota, gbc);
        
        // Panel de botones
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
        btnGuardar.addActionListener(e -> guardarCliente());
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);
        
        // Agregar paneles al panel principal
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Establecer el panel principal como contenido del diálogo
        setContentPane(mainPanel);
    }
    
    private void guardarCliente() {
        // Validar campos obligatorios
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            txtNombre.requestFocus();
            return;
        }
        
        if (txtApellido.getText().trim().isEmpty()) {
            mostrarError("El apellido es obligatorio");
            txtApellido.requestFocus();
            return;
        }
        
        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarError("El teléfono es obligatorio");
            txtTelefono.requestFocus();
            return;
        }
                
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(txtNombre.getText().trim());
        nuevoCliente.setApellido(txtApellido.getText().trim());
        nuevoCliente.setTelefono(txtTelefono.getText().trim());
        nuevoCliente.setEmail(txtEmail.getText().trim());
        nuevoCliente.setDireccion(txtDireccion.getText().trim());
        
        // Guardar en la base de datos
        clienteId = clienteController.guardarCliente(nuevoCliente);
        
        if (clienteId > 0) {
            clienteAgregado = true;
            JOptionPane.showMessageDialog(this, 
                    "Cliente agregado correctamente", 
                    "Cliente Agregado", 
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            mostrarError("Error al guardar el cliente");
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, 
                mensaje, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isClienteAgregado() {
        return clienteAgregado;
    }
    
    public int getClienteId() {
        return clienteId;
    }
}