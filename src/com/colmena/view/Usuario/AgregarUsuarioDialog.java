package com.colmena.view.Usuario;

import com.colmena.controller.UsuarioController;
import com.colmena.model.Rol;
import com.colmena.model.Usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class AgregarUsuarioDialog extends JDialog {

    private final Color COLOR_AMARILLO = new Color(255, 215, 0);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    
    private UsuarioController usuarioController;
    
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;    
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmarPassword;
    private JComboBox<Rol> cmbRol;
      
    private boolean usuarioAgregado = false;
    
    public AgregarUsuarioDialog(Frame parent, UsuarioController controller) {
        super(parent, "Agregar Nuevo Usuario", true);
        this.usuarioController = controller;
        
        setSize(550, 500);
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
        JLabel lblTitulo = new JLabel("Información del Usuario");
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
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNombre.setForeground(COLOR_TEXTO);
        formPanel.add(lblNombre, gbc);
        
        txtNombre = new JTextField(20);
        txtNombre.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        
        // Apellido
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblApellido = new JLabel("Apellido*:");
        lblApellido.setFont(new Font("Arial", Font.PLAIN, 14));
        lblApellido.setForeground(COLOR_TEXTO);
        formPanel.add(lblApellido, gbc);
        
        txtApellido = new JTextField(20);
        txtApellido.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1;
        formPanel.add(txtApellido, gbc);                
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblEmail = new JLabel("Email*:");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEmail.setForeground(COLOR_TEXTO);
        formPanel.add(lblEmail, gbc);
        
        txtEmail = new JTextField(20);
        txtEmail.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        
        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblPassword = new JLabel("Contraseña*:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPassword.setForeground(COLOR_TEXTO);
        formPanel.add(lblPassword, gbc);
        
        txtPassword = new JPasswordField(20);
        txtPassword.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);
        
        // Confirmar Contraseña
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblConfirmarPassword = new JLabel("Confirmar Contraseña*:");
        lblConfirmarPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblConfirmarPassword.setForeground(COLOR_TEXTO);
        formPanel.add(lblConfirmarPassword, gbc);
        
        txtConfirmarPassword = new JPasswordField(20);
        txtConfirmarPassword.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1;
        formPanel.add(txtConfirmarPassword, gbc);
        
        // Rol
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel lblRol = new JLabel("Rol*:");
        lblRol.setFont(new Font("Arial", Font.PLAIN, 14));
        lblRol.setForeground(COLOR_TEXTO);
        formPanel.add(lblRol, gbc);

        cmbRol = new JComboBox<Rol>();
        cmbRol.setPreferredSize(new Dimension(200, 28));
        cmbRol.addItem(new Rol(0, "Seleccione un rol"));
        cmbRol.addItem(new Rol(1, "Administrador"));
        cmbRol.addItem(new Rol(2, "Encargada"));
        cmbRol.addItem(new Rol(3, "Vendedor"));
        
        cmbRol.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Rol rol = (Rol) value;
                    setText(rol.getNombre());
                    if (rol.getId() == 0) {
                        setForeground(Color.GRAY);
                    }
                }
                return this;
            }
        });
        
        gbc.gridx = 1;
        formPanel.add(cmbRol, gbc);
        
        // Nota sobre campos obligatorios
        gbc.gridx = 0;
        gbc.gridy = 7;
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
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.addActionListener(e -> dispose());
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(COLOR_AMARILLO);
        btnGuardar.setForeground(COLOR_TEXTO);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(100, 35));
        btnGuardar.addActionListener(e -> guardarUsuario());
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);
        
        // Agregar paneles al panel principal
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
             
        setContentPane(mainPanel);
    }
    
    private void guardarUsuario() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }
        
        try {
            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(txtNombre.getText().trim());
            nuevoUsuario.setApellido(txtApellido.getText().trim());
            nuevoUsuario.setEmail(txtEmail.getText().trim());
            
            Rol rolSeleccionado = (Rol) cmbRol.getSelectedItem();           
            nuevoUsuario.setRol(rolSeleccionado);
            nuevoUsuario.setActivo(true);
            
            // Obtener contraseña
            String password = new String(txtPassword.getPassword());
            
            // Crear usuario usando el controlador
            boolean resultado = usuarioController.crearUsuario(nuevoUsuario, password);
            
            if (resultado) {
                usuarioAgregado = true;
                JOptionPane.showMessageDialog(this,
                    "Usuario creado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al crear el usuario. Verifique que el email no esté duplicado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error inesperado al crear el usuario: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarCampos() {
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre es obligatorio",
                "Campo obligatorio",
                JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        // Validar apellido
        if (txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El apellido es obligatorio",
                "Campo obligatorio",
                JOptionPane.WARNING_MESSAGE);
            txtApellido.requestFocus();
            return false;
        }
        
        // Validar email
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El email es obligatorio",
                "Campo obligatorio",
                JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar formato de email
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this,
                "El formato del email no es válido",
                "Email inválido",
                JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar contraseña
        String password = new String(txtPassword.getPassword());
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La contraseña es obligatoria",
                "Campo obligatorio",
                JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return false;
        }
        
        // Validar longitud mínima de contraseña
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "La contraseña debe tener al menos 6 caracteres",
                "Contraseña muy corta",
                JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return false;
        }
        
        // Validar confirmación de contraseña
        String confirmarPassword = new String(txtConfirmarPassword.getPassword());
        if (!password.equals(confirmarPassword)) {
            JOptionPane.showMessageDialog(this,
                "Las contraseñas no coinciden",
                "Error de confirmación",
                JOptionPane.WARNING_MESSAGE);
            txtConfirmarPassword.requestFocus();
            return false;
        }
        
        // Validar rol
        if (cmbRol.getSelectedIndex() == 0 || cmbRol.getSelectedItem().toString().equals("Seleccione un rol")) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un rol",
                "Campo obligatorio",
                JOptionPane.WARNING_MESSAGE);
            cmbRol.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void limpiarCampos() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtConfirmarPassword.setText("");
        cmbRol.setSelectedIndex(0);
    }
    
    public boolean isUsuarioAgregado() {
        return usuarioAgregado;
    }
    
    // Método para mostrar el diálogo
    public static boolean mostrarDialog(Frame parent, UsuarioController controller) {
        AgregarUsuarioDialog dialog = new AgregarUsuarioDialog(parent, controller);
        dialog.setVisible(true);
        return dialog.isUsuarioAgregado();
    }
}