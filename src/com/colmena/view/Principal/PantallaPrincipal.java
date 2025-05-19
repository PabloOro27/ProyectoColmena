package com.colmena.view.Principal;

import com.colmena.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.colmena.controller.PrincipalController;
import com.colmena.view.Productos.ProductosPanel;
import com.colmena.view.Usuario.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class PantallaPrincipal extends JFrame {

    // Colores del tema
    private final Color COLOR_AMARILLO = new Color(255, 215, 0); // Amarillo dorado
    private final Color COLOR_AMARILLO_CLARO = new Color(255, 235, 120); // Amarillo claro
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    private final Color COLOR_TEXTO_CLARO = new Color(100, 100, 100);
    private final Color COLOR_ACENTO = new Color(40, 40, 40); // Casi negro para acentos

    // Usuario conectado
    private Usuario usuarioActual;

    // Componentes principales
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;

    // Botones del menú
    private JButton btnDashboard;
    private JButton btnProductos;
    private JButton btnVentas;
    private JButton btnClientes;
    private JButton btnReportes;
    private JButton btnUsuarios;
    private JButton btnConfiguracion;
    private JButton btnSalir;

    // Información del usuario
    private JLabel lblUsuario;
    private JLabel lblRol;
    private JLabel lblFecha;

    // Botones activos para control visual
    private JButton botonActivo;

    public PantallaPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;

        // Configuración básica
        setTitle("Colmena - Sistema de Gestión de Globos");
        setSize(1200, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializar componentes
        initComponents();

        // Mostrar ventana
        setVisible(true);
    }

    private void initComponents() {
        // Panel principal
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(COLOR_FONDO);

        // Crear panel de menú lateral
        menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Panel de contenido principal (donde se cargarán las vistas)
        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setBackground(COLOR_FONDO);

        // Panel de encabezado
        headerPanel = createHeaderPanel();
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de contenido
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(COLOR_FONDO);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        rightPanel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // Mostrar panel de bienvenida como inicial
        showDashboardPanel();

        // Establecer el panel principal como contenido de la ventana
        setContentPane(mainPanel);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBackground(COLOR_ACENTO);

        // Logo en la parte superior
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(COLOR_ACENTO);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));

        JLabel lblLogo = new JLabel("COLMENA");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        lblLogo.setForeground(COLOR_AMARILLO);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTagline = new JLabel("Sistema de Gestión");
        lblTagline.setFont(new Font("Arial", Font.ITALIC, 12));
        lblTagline.setForeground(Color.WHITE);
        lblTagline.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel logoTextPanel = new JPanel(new GridLayout(2, 1));
        logoTextPanel.setBackground(COLOR_ACENTO);
        logoTextPanel.add(lblLogo);
        logoTextPanel.add(lblTagline);

        logoPanel.add(logoTextPanel, BorderLayout.CENTER);

        // Separador
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(70, 70, 70));
        separator.setBackground(new Color(70, 70, 70));

        JPanel separatorPanel = new JPanel(new BorderLayout());
        separatorPanel.setBackground(COLOR_ACENTO);
        separatorPanel.add(separator, BorderLayout.CENTER);
        separatorPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        // Panel para el logo completo
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_ACENTO);
        topPanel.add(logoPanel, BorderLayout.CENTER);
        topPanel.add(separatorPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // Panel para los botones del menú
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(COLOR_ACENTO);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Crear botones del menú
        btnDashboard = createMenuButton("Principal", "dashboard.png", e -> showDashboardPanel());
        btnProductos = createMenuButton("Productos", "products.png", e -> showProductosPanel());
        btnVentas = createMenuButton("Ventas", "sales.png", e -> showVentasPanel());
        btnClientes = createMenuButton("Clientes", "clients.png", e -> showClientesPanel());
        btnReportes = createMenuButton("Reportes", "reports.png", e -> showReportesPanel());
        btnUsuarios = createMenuButton("Usuarios", "users.png", e -> showUsuariosPanel());
        btnConfiguracion = createMenuButton("Configuración", "settings.png", e -> showConfiguracionPanel());
        btnSalir = createMenuButton("Cerrar Sesión", "logout.png", e -> cerrarSesion());

        // Agregar botones al panel
        buttonsPanel.add(Box.createVerticalStrut(10));
        addMenuButton(buttonsPanel, btnDashboard);
        addMenuButton(buttonsPanel, btnProductos);
        addMenuButton(buttonsPanel, btnVentas);
        addMenuButton(buttonsPanel, btnClientes);
        addMenuButton(buttonsPanel, btnReportes);

        // Solo mostrar botones de administración si el usuario es admin
        if (usuarioActual.getRol().equalsIgnoreCase("Administrador")) {
            addMenuButton(buttonsPanel, btnUsuarios);
            addMenuButton(buttonsPanel, btnConfiguracion);
        }

        buttonsPanel.add(Box.createVerticalGlue()); // Espacio flexible
        addMenuButton(buttonsPanel, btnSalir);
        buttonsPanel.add(Box.createVerticalStrut(20));

        // Inicialmente el dashboard está activo
        activateButton(btnDashboard);

        JScrollPane scrollPane = new JScrollPane(buttonsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel de información de usuario en la parte inferior
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setBackground(new Color(60, 60, 60));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        lblUsuario = new JLabel(usuarioActual.getNombre() + " " + usuarioActual.getApellido());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 13));
        lblUsuario.setForeground(Color.WHITE);

        lblRol = new JLabel(usuarioActual.getRol());
        lblRol.setFont(new Font("Arial", Font.PLAIN, 12));
        lblRol.setForeground(COLOR_AMARILLO_CLARO);

        userInfoPanel.add(lblUsuario);
        userInfoPanel.add(lblRol);

        panel.add(userInfoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        // Título de la sección
        JLabel lblTitle = new JLabel("Principal");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_TEXTO);

        panel.add(lblTitle, BorderLayout.WEST);

        // Panel derecho con fecha y hora
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(COLOR_FONDO);

        // Fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy");
        String fechaFormateada = dateFormat.format(new Date());

        lblFecha = new JLabel(fechaFormateada);
        lblFecha.setFont(new Font("Arial", Font.PLAIN, 13));
        lblFecha.setForeground(COLOR_TEXTO_CLARO);

        rightPanel.add(lblFecha);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JButton createMenuButton(String text, String iconName, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(COLOR_ACENTO);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);

        // Configurar apariencia del botón
        button.setUI(new BasicButtonUI());

        // Añadir el listener
        button.addActionListener(e -> {
            // Desactivar botón activo anterior y activar el nuevo
            if (botonActivo != null) {
                desactivateButton(botonActivo);
            }
            activateButton(button);
            botonActivo = button;

            // Actualizar título en el header
            JLabel lblTitle = (JLabel) headerPanel.getComponent(0);
            lblTitle.setText(text);

            // Ejecutar la acción específica del botón
            listener.actionPerformed(e);
        });

        return button;
    }

    private void addMenuButton(JPanel panel, JButton button) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setPreferredSize(new Dimension(200, 40));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonWrapper = new JPanel(new BorderLayout());
        buttonWrapper.setBackground(COLOR_ACENTO);
        buttonWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buttonWrapper.add(button, BorderLayout.CENTER);

        panel.add(buttonWrapper);
        panel.add(Box.createVerticalStrut(5));
    }

    private void activateButton(JButton button) {
        button.setBackground(COLOR_AMARILLO);
        button.setForeground(COLOR_ACENTO);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void desactivateButton(JButton button) {
        button.setBackground(COLOR_ACENTO);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    // Métodos para mostrar los panenles
    private void showDashboardPanel() {
        contentPanel.removeAll();

        // Instanciamos el controlador del dashboard
        PrincipalController dashboardController = new PrincipalController();

        // Obtenemos las estadísticas
        Map<String, Object> estadisticas = dashboardController.obtenerEstadisticas();

        JPanel dashboardPanel = new JPanel(new BorderLayout(20, 20));
        dashboardPanel.setBackground(COLOR_FONDO);

        // Panel de bienvenida
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(COLOR_AMARILLO_CLARO);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblWelcome = new JLabel("¡Bienvenido al Sistema de Gestión de Globos Colmena!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        lblWelcome.setForeground(COLOR_TEXTO);

        JLabel lblSubWelcome = new JLabel(
                "<html>Seleccione una opción del menú lateral para comenzar a gestionar su inventario y ventas.</html>");
        lblSubWelcome.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubWelcome.setForeground(COLOR_TEXTO);

        JPanel textPanel = new JPanel(new BorderLayout(0, 10));
        textPanel.setBackground(COLOR_AMARILLO_CLARO);
        textPanel.add(lblWelcome, BorderLayout.NORTH);
        textPanel.add(lblSubWelcome, BorderLayout.CENTER);

        welcomePanel.add(textPanel, BorderLayout.CENTER);

        // Panel de accesos rápidos
        JPanel quickAccessPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        quickAccessPanel.setBackground(COLOR_FONDO);

        // Tarjetas de acceso rápido
        quickAccessPanel.add(createQuickAccessCard("Nueva Venta", "sales.png", e -> showVentasPanel()));
        quickAccessPanel.add(createQuickAccessCard("Ver Inventario", "products.png", e -> showProductosPanel()));
        quickAccessPanel.add(createQuickAccessCard("Agregar Cliente", "clients.png", e -> showClientesPanel()));
        quickAccessPanel.add(createQuickAccessCard("Reportes", "reports.png", e -> showReportesPanel()));

        // Formateador para valores monetarios
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "GT"));

        // Panel de estadísticas - Ahora con datos dinámicos
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBackground(COLOR_FONDO);

        int totalProductos = (int) estadisticas.getOrDefault("totalProductos", 0);
        int productosConStock = (int) estadisticas.getOrDefault("productosConStock", 0);

        statsPanel.add(createStatCard("Productos",
                String.valueOf(totalProductos),
                productosConStock + " con stock disponible"));

        int ventasHoy = (int) estadisticas.getOrDefault("ventasHoy", 0);
        double montoTotalHoy = (double) estadisticas.getOrDefault("montoTotalHoy", 0.0);

        statsPanel.add(createStatCard("Ventas del Día",
                String.valueOf(ventasHoy),
                "Total: " + formatoMoneda.format(montoTotalHoy)));

        int totalClientes = (int) estadisticas.getOrDefault("totalClientes", 0);
        statsPanel.add(createStatCard("Clientes",
                String.valueOf(totalClientes),
                "Registrados"));

        int totalCategorias = (int) estadisticas.getOrDefault("totalCategorias", 0);
        int productosBajoStock = (int) estadisticas.getOrDefault("productosBajoStock", 0);
        statsPanel.add(createStatCard("Categorías",
                String.valueOf(totalCategorias),
                productosBajoStock + " productos con bajo stock"));

        // Dashboard
        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(COLOR_FONDO);
        topPanel.add(welcomePanel, BorderLayout.NORTH);
        topPanel.add(quickAccessPanel, BorderLayout.CENTER);

        dashboardPanel.add(topPanel, BorderLayout.NORTH);
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);

        // Agregar panel de productos más vendidos si está disponible
        Map<String, Integer> productosMasVendidos = dashboardController.obtenerProductosMasVendidos();
        if (!productosMasVendidos.isEmpty()) {
            JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
            chartsPanel.setBackground(COLOR_FONDO);

            JPanel topProductsPanel = createTopProductsPanel(productosMasVendidos);
            chartsPanel.add(topProductsPanel);

            // Si también tenemos datos de ventas por categoría
            Map<String, Double> ventasPorCategoria = dashboardController.obtenerVentasPorCategoria();
            if (!ventasPorCategoria.isEmpty()) {
                JPanel categorySalesPanel = createCategorySalesPanel(ventasPorCategoria);
                chartsPanel.add(categorySalesPanel);
            }

            dashboardPanel.add(chartsPanel, BorderLayout.SOUTH);
        }

        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createQuickAccessCard(String title, String iconName, ActionListener listener) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_FONDO);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JButton btnCard = new JButton(title);
        btnCard.setFont(new Font("Arial", Font.BOLD, 14));
        btnCard.setForeground(COLOR_TEXTO);
        btnCard.setBackground(Color.WHITE);
        btnCard.setBorderPainted(false);
        btnCard.setFocusPainted(false);
        btnCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCard.setHorizontalAlignment(SwingConstants.CENTER);

        btnCard.addActionListener(listener);

        card.add(btnCard, BorderLayout.CENTER);

        // Hacer que toda la tarjeta sea clickeable
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_AMARILLO, 1, true),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                btnCard.doClick();
            }
        });

        return card;
    }

    private JPanel createStatCard(String title, String value, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(COLOR_FONDO);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_TEXTO);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblValue.setForeground(COLOR_AMARILLO.darker());

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitle.setForeground(COLOR_TEXTO_CLARO);

        JPanel textPanel = new JPanel(new GridLayout(3, 1));
        textPanel.setBackground(COLOR_FONDO);
        textPanel.add(lblTitle);
        textPanel.add(lblValue);
        textPanel.add(lblSubtitle);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private void showProductosPanel() {
        contentPanel.removeAll();

        // Crear el panel de productos y pasarle el usuario actual
        ProductosPanel productosPanel = new ProductosPanel(usuarioActual);

        contentPanel.add(productosPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showVentasPanel() {
        contentPanel.removeAll();

        // Aquí crearías e insertarías tu panel de gestión de ventas
        JPanel constructionPanel = createConstructionPanel("Registro de Ventas",
                "<html>Aquí podrás registrar y gestionar las ventas.<br>"
                        + "• Crear nuevas ventas<br>"
                        + "• Ver historial de ventas<br>"
                        + "• Gestionar pagos pendientes<br>"
                        + "• Generar facturas</html>");

        contentPanel.add(constructionPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showClientesPanel() {
        contentPanel.removeAll();

        // Aquí crearías e insertarías tu panel de gestión de clientes
        JPanel constructionPanel = createConstructionPanel("Gestión de Clientes",
                "<html>Aquí podrás administrar la información de tus clientes.<br>"
                        + "• Ver lista de clientes<br>"
                        + "• Añadir nuevos clientes<br>"
                        + "• Actualizar datos de contacto<br>"
                        + "• Ver historial de compras</html>");

        contentPanel.add(constructionPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showReportesPanel() {
        contentPanel.removeAll();

        // Aquí crearías e insertarías tu panel de reportes
        JPanel constructionPanel = createConstructionPanel("Reportes y Estadísticas",
                "<html>Aquí podrás generar informes y visualizar estadísticas.<br>"
                        + "• Ventas por período<br>"
                        + "• Productos más vendidos<br>"
                        + "• Inventario bajo mínimo<br>"
                        + "• Reportes financieros</html>");

        contentPanel.add(constructionPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUsuariosPanel() {
        contentPanel.removeAll();

        // Aquí crearías e insertarías tu panel de gestión de usuarios
        JPanel constructionPanel = createConstructionPanel("Administración de Usuarios",
                "<html>Aquí podrás administrar los usuarios del sistema.<br>"
                        + "• Ver usuarios registrados<br>"
                        + "• Crear nuevos usuarios<br>"
                        + "• Asignar roles y permisos<br>"
                        + "• Activar/desactivar cuentas</html>");

        contentPanel.add(constructionPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showConfiguracionPanel() {
        contentPanel.removeAll();

        // Aquí crearías e insertarías tu panel de configuración
        JPanel constructionPanel = createConstructionPanel("Configuración del Sistema",
                "<html>Aquí podrás configurar los parámetros del sistema.<br>"
                        + "• Configuración general<br>"
                        + "• Parámetros de facturación<br>"
                        + "• Ajustes de inventario<br>"
                        + "• Copias de seguridad</html>");

        contentPanel.add(constructionPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createConstructionPanel(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setBackground(COLOR_FONDO);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(new Color(240, 240, 240));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXTO);
        infoPanel.add(lblTitle, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);

        JLabel lblConstruction = new JLabel("Esta sección está en construcción");
        lblConstruction.setFont(new Font("Arial", Font.ITALIC, 16));
        lblConstruction.setForeground(COLOR_TEXTO_CLARO);
        infoPanel.add(lblConstruction, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 25, 0);

        JLabel lblDescription = new JLabel(description);
        lblDescription.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDescription.setForeground(COLOR_TEXTO);
        infoPanel.add(lblDescription, gbc);

        contentWrapper.add(infoPanel);
        panel.add(contentWrapper, BorderLayout.CENTER);

        return panel;
    }

    private void cerrarSesion() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            dispose(); // Cerrar ventana actual
            new LoginForm().setVisible(true); // Mostrar pantalla de login
        }
    }

    private JPanel createTopProductsPanel(Map<String, Integer> productosMasVendidos) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblTitle = new JLabel("Productos Más Vendidos (Último Mes)");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_TEXTO);

        panel.add(lblTitle, BorderLayout.NORTH);

        // Panel para los productos
        JPanel productsListPanel = new JPanel();
        productsListPanel.setLayout(new BoxLayout(productsListPanel, BoxLayout.Y_AXIS));
        productsListPanel.setBackground(COLOR_FONDO);

        // Encontrar el máximo valor para calcular porcentajes de las barras
        int maxValue = productosMasVendidos.values().stream().max(Integer::compare).orElse(1);

        // Agregar cada producto con una barra de progreso
        for (Map.Entry<String, Integer> entry : productosMasVendidos.entrySet()) {
            String producto = entry.getKey();
            int cantidad = entry.getValue();

            JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
            itemPanel.setBackground(COLOR_FONDO);

            JLabel lblProducto = new JLabel(producto);
            lblProducto.setFont(new Font("Arial", Font.PLAIN, 12));
            lblProducto.setForeground(COLOR_TEXTO);

            JLabel lblCantidad = new JLabel(String.valueOf(cantidad));
            lblCantidad.setFont(new Font("Arial", Font.BOLD, 12));
            lblCantidad.setForeground(COLOR_AMARILLO.darker());

            // Barra de progreso
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue((int) (((double) cantidad / maxValue) * 100));
            progressBar.setStringPainted(false);
            progressBar.setForeground(COLOR_AMARILLO);
            progressBar.setBackground(new Color(240, 240, 240));

            itemPanel.add(lblProducto, BorderLayout.WEST);
            itemPanel.add(progressBar, BorderLayout.CENTER);
            itemPanel.add(lblCantidad, BorderLayout.EAST);

            productsListPanel.add(itemPanel);
            productsListPanel.add(Box.createVerticalStrut(10)); // Espacio entre elementos
        }

        JScrollPane scrollPane = new JScrollPane(productsListPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCategorySalesPanel(Map<String, Double> ventasPorCategoria) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblTitle = new JLabel("Ventas por Categoría (Último Mes)");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_TEXTO);

        panel.add(lblTitle, BorderLayout.NORTH);

        // Panel para las categorías
        JPanel categoriesListPanel = new JPanel();
        categoriesListPanel.setLayout(new BoxLayout(categoriesListPanel, BoxLayout.Y_AXIS));
        categoriesListPanel.setBackground(COLOR_FONDO);

        // Formateador para valores monetarios
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "GT"));

        // Encontrar el máximo valor para calcular porcentajes de las barras
        double maxValue = ventasPorCategoria.values().stream().max(Double::compare).orElse(1.0);

        // Agregar cada categoría con una barra de progreso
        for (Map.Entry<String, Double> entry : ventasPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            double monto = entry.getValue();

            JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
            itemPanel.setBackground(COLOR_FONDO);

            JLabel lblCategoria = new JLabel(categoria);
            lblCategoria.setFont(new Font("Arial", Font.PLAIN, 12));
            lblCategoria.setForeground(COLOR_TEXTO);

            JLabel lblMonto = new JLabel(formatoMoneda.format(monto));
            lblMonto.setFont(new Font("Arial", Font.BOLD, 12));
            lblMonto.setForeground(COLOR_AMARILLO.darker());

            // Barra de progreso
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue((int) (((double) monto / maxValue) * 100));
            progressBar.setStringPainted(false);
            progressBar.setForeground(COLOR_AMARILLO);
            progressBar.setBackground(new Color(240, 240, 240));

            itemPanel.add(lblCategoria, BorderLayout.WEST);
            itemPanel.add(progressBar, BorderLayout.CENTER);
            itemPanel.add(lblMonto, BorderLayout.EAST);

            categoriesListPanel.add(itemPanel);
            categoriesListPanel.add(Box.createVerticalStrut(10)); // Espacio entre elementos
        }

        JScrollPane scrollPane = new JScrollPane(categoriesListPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Método para ajustar la interfaz cuando se redimensiona la ventana
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        revalidate();
        repaint();
    }

    // Método main para pruebas
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // usuario de prueba
            Usuario testUser = new Usuario();
            testUser.setId(1);
            testUser.setNombre("Admin");
            testUser.setApellido("Sistema");
            testUser.setEmail("admin@colmena.com");
            testUser.setRol("Administrador");
            testUser.setActivo(true);

            // Mostrar la ventana principal
            new PantallaPrincipal(testUser);
        });
    }
}