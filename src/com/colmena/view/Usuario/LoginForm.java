package com.colmena.view.Usuario;

import com.colmena.controller.UsuarioController;
import com.colmena.model.Usuario;
import com.colmena.view.Principal.PantallaPrincipal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    // Colores principales
    private final Color COLOR_AMARILLO = new Color(255, 215, 0); 
    private final Color COLOR_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    
    // Componentes 
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancelar;
    private JLabel lblLogo;

    // Controlador
    private UsuarioController usuarioController;

    public LoginForm() {
        // Inicialización
        usuarioController = new UsuarioController();
        
        // Configuración de la ventana
        setTitle("Colmena - Iniciar Sesión");
        setSize(375, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Crear componentes
        initComponents();
        
        // Mostrar ventana
        setVisible(true);
    }

    private void initComponents() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(COLOR_BLANCO);

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(COLOR_BLANCO);
        lblLogo = new JLabel("COLMENA");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 28));
        lblLogo.setForeground(COLOR_TEXTO);
                
        JLabel lblSlogan = new JLabel("Sistema de Gestión de Globos");
        lblSlogan.setFont(new Font("Arial", Font.ITALIC, 12));
        lblSlogan.setForeground(COLOR_TEXTO);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(COLOR_BLANCO);
        titlePanel.add(lblLogo);
        titlePanel.add(lblSlogan);
        
        logoPanel.add(titlePanel);
        
        // Linea divisoria
        JSeparator separator = new JSeparator();
        separator.setForeground(COLOR_AMARILLO);
        separator.setBackground(COLOR_AMARILLO);
        
        JPanel separatorPanel = new JPanel(new BorderLayout());
        separatorPanel.setBackground(COLOR_BLANCO);
        separatorPanel.add(separator, BorderLayout.CENTER);
        separatorPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
                
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_BLANCO);
        topPanel.add(logoPanel, BorderLayout.CENTER);
        topPanel.add(separatorPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_BLANCO);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Label Usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(COLOR_TEXTO);
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(lblUsuario, gbc);
        
        // Input Usuario
        txtUsuario = new JTextField(15);
        txtUsuario.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(txtUsuario, gbc);
        
        // Label Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(COLOR_TEXTO);
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(lblPassword, gbc);
        
        // Campo Contraseña
        txtPassword = new JPasswordField(15);
        txtPassword.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(txtPassword, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Input de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(COLOR_BLANCO);
        
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(COLOR_AMARILLO);
        btnLogin.setForeground(COLOR_TEXTO);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(120, 30));
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.LIGHT_GRAY);
        btnCancelar.setForeground(COLOR_TEXTO);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(100, 30));
        
        // Click "Iniciar Sesion"
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        // Login con Enter
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        // Click "Cancelar"
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancelar);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private void login() {
        String nombreUsuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Validar campos vacíos
        if (nombreUsuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, complete todos los campos", 
                "Campos Vacíos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Modificamos el controlador para que busque por nombre de usuario en lugar de email
        Usuario usuario = usuarioController.validarUsuario(nombreUsuario, password);
        
        if (usuario != null) {
            JOptionPane.showMessageDialog(this, 
                "Bienvenido, " + usuario.getNombre(), 
                "Inicio de Sesión Exitoso", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // Abrir ventana principal
            abrirVentanaPrincipal(usuario);
            
            // Cerrar ventana de login
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Usuario o contraseña incorrectos", 
                "Error de Autenticación", 
                JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
    
    private void abrirVentanaPrincipal(Usuario usuario) {
       dispose();

       new PantallaPrincipal(usuario);
    }
    
    // Método main para pruebas
    public static void main(String[] args) {        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Crear y mostrar el formulario de login
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm();
            }
        });
    }
}