package com.colmena.view.Usuario;

import com.colmena.controller.UsuarioController;
import com.colmena.model.Usuario;
import com.colmena.model.Rol;

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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class EditarUsuarioDialog extends JDialog {

    private final Color COLOR_AMARILLO = new Color(255, 215, 0);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    
    private UsuarioController usuarioController;
    private Usuario usuarioActual;
    
    // Campos de información básica
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;    
    private JComboBox<Rol> cmbRol;
    private JCheckBox chkActivo;
    
    // Campos para cambio de contraseña (opcionales)
    private JCheckBox chkCambiarPassword;
    private JPasswordField txtNuevaPassword;
    private JPasswordField txtConfirmarPassword;
    private JPanel panelPassword;
      
    private boolean usuarioEditado = false;
    
    public EditarUsuarioDialog(Frame parent, UsuarioController controller, Usuario usuario) {
        super(parent, "Editar Usuario", true);
        this.usuarioController = controller;
        this.usuarioActual = usuario;
        
        setSize(450, 550);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initComponents();
        cargarDatosUsuario();
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
        
        // Rol
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblRol = new JLabel("Rol*:");
        lblRol.setFont(new Font("Arial", Font.PLAIN, 14));
        lblRol.setForeground(COLOR_TEXTO);
        formPanel.add(lblRol, gbc);

        cmbRol = new JComboBox<>();
        cmbRol.setPreferredSize(new Dimension(200, 28));
        cargarRoles();
        
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
                    } else if (!isSelected) {
                        setForeground(Color.BLACK);
                    }
                }
                return this;
            }
        });
        
        gbc.gridx = 1;
        formPanel.add(cmbRol, gbc);
        
        // Estado activo
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblActivo = new JLabel("Estado:");
        lblActivo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblActivo.setForeground(COLOR_TEXTO);
        formPanel.add(lblActivo, gbc);
        
        chkActivo = new JCheckBox("Usuario activo");
        chkActivo.setBackground(COLOR_FONDO);
        chkActivo.setForeground(COLOR_TEXTO);
        chkActivo.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        formPanel.add(chkActivo, gbc);
        
        // Separador para sección de contraseña
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 10, 5);
        JSeparator separator = new JSeparator();
        formPanel.add(separator, gbc);
        
        // Título de sección de contraseña
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 10, 5);
        JLabel lblPasswordSection = new JLabel("Cambiar Contraseña");
        lblPasswordSection.setFont(new Font("Arial", Font.BOLD, 14));
        lblPasswordSection.setForeground(COLOR_TEXTO);
        formPanel.add(lblPasswordSection, gbc);
        
        // Checkbox para cambiar contraseña
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        chkCambiarPassword = new JCheckBox("Cambiar contraseña");
        chkCambiarPassword.setBackground(COLOR_FONDO);
        chkCambiarPassword.setForeground(COLOR_TEXTO);
        chkCambiarPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        chkCambiarPassword.addActionListener(e -> togglePasswordFields());
        formPanel.add(chkCambiarPassword, gbc);
        
        // Panel para campos de contraseña (inicialmente oculto)
        panelPassword = new JPanel(new GridBagLayout());
        panelPassword.setBackground(COLOR_FONDO);
        panelPassword.setVisible(false);
        
        GridBagConstraints gbcPass = new GridBagConstraints();
        gbcPass.fill = GridBagConstraints.HORIZONTAL;
        gbcPass.insets = new Insets(5, 5, 5, 5);
        gbcPass.anchor = GridBagConstraints.WEST;
        
        // Nueva contraseña
        gbcPass.gridx = 0;
        gbcPass.gridy = 0;
        JLabel lblNuevaPassword = new JLabel("Nueva contraseña*:");
        lblNuevaPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNuevaPassword.setForeground(COLOR_TEXTO);
        panelPassword.add(lblNuevaPassword, gbcPass);
        
        txtNuevaPassword = new JPasswordField(20);
        txtNuevaPassword.setPreferredSize(new Dimension(200, 28));
        gbcPass.gridx = 1;
        panelPassword.add(txtNuevaPassword, gbcPass);
        
        // Confirmar nueva contraseña
        gbcPass.gridx = 0;
        gbcPass.gridy = 1;
        JLabel lblConfirmarPassword = new JLabel("Confirmar contraseña*:");
        lblConfirmarPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        lblConfirmarPassword.setForeground(COLOR_TEXTO);
        panelPassword.add(lblConfirmarPassword, gbcPass);
        
        txtConfirmarPassword = new JPasswordField(20);
        txtConfirmarPassword.setPreferredSize(new Dimension(200, 28));
        gbcPass.gridx = 1;
        panelPassword.add(txtConfirmarPassword, gbcPass);
        
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        formPanel.add(panelPassword, gbc);
        
        // Nota sobre campos obligatorios
        gbc.gridx = 0;
        gbc.gridy = 10;
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
        
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(COLOR_AMARILLO);
        btnGuardar.setForeground(COLOR_TEXTO);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(130, 35));
        btnGuardar.addActionListener(e -> guardarCambios());
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);
        
        // Agregar paneles al panel principal
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Establecer el panel principal como contenido del diálogo
        setContentPane(mainPanel);
    }
    
    private void cargarRoles() {
        // Agregar roles 
        cmbRol.addItem(new Rol(1, "Administrador"));
        cmbRol.addItem(new Rol(2, "Encargada"));
        cmbRol.addItem(new Rol(3, "Vendedor"));            
    }
    
    private void cargarDatosUsuario() {
        if (usuarioActual != null) {
            txtNombre.setText(usuarioActual.getNombre());
            txtApellido.setText(usuarioActual.getApellido());
            txtEmail.setText(usuarioActual.getEmail());
            chkActivo.setSelected(usuarioActual.isActivo());
            
            // Seleccionar el rol actual
            Rol rolActual = usuarioActual.getRol();
            for (int i = 0; i < cmbRol.getItemCount(); i++) {
                Rol rol = cmbRol.getItemAt(i);
                if (rol.getNombre().equals(rolActual)) {
                    cmbRol.setSelectedItem(rol);
                    break;
                }
            }
        }
    }
    
    private void togglePasswordFields() {
        boolean mostrar = chkCambiarPassword.isSelected();
        panelPassword.setVisible(mostrar);
        
        if (!mostrar) {           
            txtNuevaPassword.setText("");
            txtConfirmarPassword.setText("");
        }
                
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private void guardarCambios() {        
        if (!validarCampos()) {
            return;
        }
        
        try {
            // Actualizar datos del usuario
            usuarioActual.setNombre(txtNombre.getText().trim());
            usuarioActual.setApellido(txtApellido.getText().trim());
            usuarioActual.setEmail(txtEmail.getText().trim());
            usuarioActual.setActivo(chkActivo.isSelected());
            
            Rol rolSeleccionado = (Rol) cmbRol.getSelectedItem();
            usuarioActual.setRol(rolSeleccionado);
                        
            boolean resultado;
            
            if (chkCambiarPassword.isSelected()) {                
                String nuevaPassword = new String(txtNuevaPassword.getPassword());
                resultado = usuarioController.cambiarPassword(usuarioActual.getId(), nuevaPassword);
                
                if (resultado) {                    
                    resultado = usuarioController.actualizarUsuario(usuarioActual);
                }
            } else {                
                resultado = usuarioController.actualizarUsuario(usuarioActual);
            }
            
            if (resultado) {
                usuarioEditado = true;
                JOptionPane.showMessageDialog(this,
                    "Usuario actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar el usuario. Verifique que el email no esté duplicado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error inesperado al actualizar el usuario: " + e.getMessage(),
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
        
        // Validar rol
        Rol rolSeleccionado = (Rol) cmbRol.getSelectedItem();
        if (rolSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un rol",
                "Campo obligatorio",
                JOptionPane.WARNING_MESSAGE);
            cmbRol.requestFocus();
            return false;
        }
        
        // Validar contraseñas solo si se marcó la opción de cambiar
        if (chkCambiarPassword.isSelected()) {
            String nuevaPassword = new String(txtNuevaPassword.getPassword());
            if (nuevaPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "La nueva contraseña es obligatoria",
                    "Campo obligatorio",
                    JOptionPane.WARNING_MESSAGE);
                txtNuevaPassword.requestFocus();
                return false;
            }
            
            // Validar longitud mínima de contraseña
            if (nuevaPassword.length() < 6) {
                JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 6 caracteres",
                    "Contraseña muy corta",
                    JOptionPane.WARNING_MESSAGE);
                txtNuevaPassword.requestFocus();
                return false;
            }
            
            // Validar confirmación de contraseña
            String confirmarPassword = new String(txtConfirmarPassword.getPassword());
            if (!nuevaPassword.equals(confirmarPassword)) {
                JOptionPane.showMessageDialog(this,
                    "Las contraseñas no coinciden",
                    "Error de confirmación",
                    JOptionPane.WARNING_MESSAGE);
                txtConfirmarPassword.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isUsuarioEditado() {
        return usuarioEditado;
    }
    
    // Método para mostrar el diálogo
    public static boolean mostrarDialog(Frame parent, UsuarioController controller, Usuario usuario) {
        EditarUsuarioDialog dialog = new EditarUsuarioDialog(parent, controller, usuario);
        dialog.setVisible(true);
        return dialog.isUsuarioEditado();
    }
}