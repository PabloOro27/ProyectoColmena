package com.colmena.view.Clientes;

import com.colmena.controller.ClienteController;
import com.colmena.model.Cliente;
import com.colmena.model.Usuario;
import com.colmena.view.Pedido.PedidosClientePanel;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.*;

public class ClientesPanel extends JPanel {

    private final Color COLOR_AMARILLO = new Color(255, 215, 0);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    private final Color COLOR_TEXTO_CLARO = new Color(100, 100, 100);

    private ClienteController clienteController;
    private Usuario usuarioActual;

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnVerPedidos;
    private JButton btnRefrescar;
    private JLabel lblTotalClientes;

    private List<Cliente> clientes = new ArrayList<>();
    private NumberFormat formatoMoneda;
    private SimpleDateFormat formatoFecha;

    public ClientesPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.clienteController = new ClienteController();
        this.formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "GT"));
        this.formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);

        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        // Panel superior
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel central
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel izquierdo
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(COLOR_FONDO);

        // Filtro de búsqueda
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.PLAIN, 12));
        lblBuscar.setForeground(COLOR_TEXTO);

        txtBuscar = new JTextField(20);
        txtBuscar.setPreferredSize(new Dimension(200, 28));
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscarClientes();
            }
        });

        filterPanel.add(lblBuscar);
        filterPanel.add(txtBuscar);

        // Panel derecho botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(COLOR_FONDO);

        btnRefrescar = createButton("Refrescar", e -> cargarDatos());
        btnVerPedidos = createButton("Ver Pedidos", e -> verPedidosCliente());
        btnEditar = createButton("Editar", e -> editarCliente());
        btnAgregar = createButton("Agregar Cliente", e -> agregarCliente());

        buttonPanel.add(btnRefrescar);
        buttonPanel.add(btnVerPedidos);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnAgregar);

        panel.add(filterPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // Modelo de tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) { // Saldo pendiente
                    return Double.class;
                }
                return String.class;
            }
        };

        // Definir columnas
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Teléfono");
        modeloTabla.addColumn("Email");
        modeloTabla.addColumn("Dirección");
        modeloTabla.addColumn("Saldo Pendiente");
        modeloTabla.addColumn("Fecha Registro");

        // Crear tabla
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(25);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setAutoCreateRowSorter(true);
        tablaClientes.getTableHeader().setReorderingAllowed(false);
        tablaClientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        tablaClientes.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaClientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Renderer moneda
        tablaClientes.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(formatoMoneda.format((Double) value));

                    // Color rojo si tiene saldo pendiente
                    if ((Double) value > 0) {
                        setForeground(isSelected ? new Color(255, 200, 200) : Color.RED);
                    } else {
                        setForeground(isSelected ? Color.WHITE : COLOR_TEXTO);
                    }
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return this;
            }
        });

        // Renderer fecha
        tablaClientes.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof String) {
                    setText((String) value);
                }
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });

        // Tamaño de columnas
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(120); // Nombre
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(120); // Apellido
        tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(100); // Teléfono
        tablaClientes.getColumnModel().getColumn(3).setPreferredWidth(150); // Email
        tablaClientes.getColumnModel().getColumn(4).setPreferredWidth(200); // Dirección
        tablaClientes.getColumnModel().getColumn(5).setPreferredWidth(100); // Saldo
        tablaClientes.getColumnModel().getColumn(6).setPreferredWidth(100); // Fecha

        // doble click para editar
        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarCliente();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        lblTotalClientes = new JLabel("Total: 0 clientes");
        lblTotalClientes.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTotalClientes.setForeground(COLOR_TEXTO_CLARO);

        panel.add(lblTotalClientes, BorderLayout.WEST);

        JLabel lblInfoSaldo = new JLabel("Rojo = Con saldo pendiente");
        lblInfoSaldo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfoSaldo.setForeground(COLOR_TEXTO_CLARO);

        panel.add(lblInfoSaldo, BorderLayout.EAST);

        return panel;
    }

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

    private void cargarDatos() {
        clientes = clienteController.obtenerClientes();

        modeloTabla.setRowCount(0);

        for (Cliente cliente : clientes) {
            modeloTabla.addRow(new Object[] {
                    cliente.getNombre(),
                    cliente.getApellido(),
                    cliente.getTelefono(),
                    cliente.getEmail(),
                    cliente.getDireccion(),
                    cliente.getSaldoPendiente(),
                    cliente.getFechaRegistro() != null ? formatoFecha.format(cliente.getFechaRegistro()) : ""
            });
        }

        lblTotalClientes.setText("Total: " + clientes.size() + " clientes");
    }

    private void buscarClientes() {
        String textoBusqueda = txtBuscar.getText().trim();

        if (textoBusqueda.isEmpty()) {
            cargarDatos();
            return;
        }

        List<Cliente> clientesEncontrados = clienteController.buscarClientes(textoBusqueda);

        modeloTabla.setRowCount(0);

        for (Cliente cliente : clientesEncontrados) {
            modeloTabla.addRow(new Object[] {
                    cliente.getNombre(),
                    cliente.getApellido(),
                    cliente.getTelefono(),
                    cliente.getEmail(),
                    cliente.getDireccion(),
                    cliente.getSaldoPendiente(),
                    cliente.getFechaRegistro() != null ? formatoFecha.format(cliente.getFechaRegistro()) : ""
            });
        }

        lblTotalClientes.setText("Encontrados: " + clientesEncontrados.size() + " de " + clientes.size() + " clientes");
    }

    private void agregarCliente() {
        AgregarClienteDialog dialog = new AgregarClienteDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                clienteController);

        dialog.setVisible(true);

        if (dialog.isClienteAgregado()) {
            cargarDatos();
        }
    }

    private void editarCliente() {
        int filaSeleccionada = tablaClientes.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para editar",
                    "Editar Cliente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tablaClientes.convertRowIndexToModel(filaSeleccionada);

        // Obtener datos del cliente seleccionado
        String nombre = (String) modeloTabla.getValueAt(modelRow, 0);
        String apellido = (String) modeloTabla.getValueAt(modelRow, 1);

        Cliente clienteSeleccionado = null;
        for (Cliente c : clientes) {
            if (c.getNombre().equals(nombre) && c.getApellido().equals(apellido)) {
                clienteSeleccionado = c;
                break;
            }
        }

        if (clienteSeleccionado != null) {
            EditarClienteDialog dialog = new EditarClienteDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    clienteController,
                    clienteSeleccionado);

            dialog.setVisible(true);

            if (dialog.isClienteEditado()) {
                cargarDatos();
            }
        }
    }
    
    private void verPedidosCliente() {
        int filaSeleccionada = tablaClientes.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para ver sus pedidos",
                    "Ver Pedidos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tablaClientes.convertRowIndexToModel(filaSeleccionada);

        // Obtener datos del cliente seleccionado
        String nombre = (String) modeloTabla.getValueAt(modelRow, 0);
        String apellido = (String) modeloTabla.getValueAt(modelRow, 1);

        // Buscar el cliente en la lista
        Cliente clienteSeleccionado = null;
        for (Cliente c : clientes) {
            if (c.getNombre().equals(nombre) && c.getApellido().equals(apellido)) {
                clienteSeleccionado = c;
                break;
            }
        }

        if (clienteSeleccionado != null) {
            PedidosClientePanel pedidosPanel = new PedidosClientePanel(clienteSeleccionado);

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Pedidos de " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido(), true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSize(900, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setContentPane(pedidosPanel);
            dialog.setVisible(true);
        }
    }
}