package com.colmena.view.Usuario;

import com.colmena.controller.ClienteController;
import com.colmena.controller.UsuarioController;
import com.colmena.model.Rol;
import com.colmena.model.Usuario;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.*;

public class UsuariosPanel extends JPanel {

    private final Color COLOR_AMARILLO = new Color(255, 215, 0);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    private final Color COLOR_TEXTO_CLARO = new Color(100, 100, 100);

    private UsuarioController usuarioController;
    private Usuario usuarioActual;

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnRefrescar;
    private JLabel lblTotalUsuario;

    private List<Usuario> usuarios = new ArrayList<>();
    private SimpleDateFormat formatoFecha;

    public UsuariosPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.usuarioController = new UsuarioController();
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
        btnEditar = createButton("Editar", e -> editarUsuario());
        btnAgregar = createButton("Agregar Usuario", e -> agregarUsuario());

        buttonPanel.add(btnRefrescar);
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
        };

        // Definir columnas
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Email");
        modeloTabla.addColumn("Rol");

        // Crear tabla
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setRowHeight(25);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.setAutoCreateRowSorter(true);
        tablaUsuarios.getTableHeader().setReorderingAllowed(false);
        tablaUsuarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        tablaUsuarios.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaUsuarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Tamaño de columnas
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(120); // Nombre
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(120); // Apellido
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(100); // Email
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(150); // Rol

        // doble click para editar
        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarUsuario();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
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

        lblTotalUsuario = new JLabel("Total: 0 Usuarios");
        lblTotalUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTotalUsuario.setForeground(COLOR_TEXTO_CLARO);

        panel.add(lblTotalUsuario, BorderLayout.WEST);

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
        usuarios = usuarioController.obtenerUsuarios();

        modeloTabla.setRowCount(0);

        for (Usuario usuario : usuarios) {
            modeloTabla.addRow(new Object[] {
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    usuario.getRol().getNombre(),
            });
        }

        lblTotalUsuario.setText("Total: " + usuarios.size() + " clientes");
    }

    private void buscarClientes() {
        String textoBusqueda = txtBuscar.getText().trim();

        if (textoBusqueda.isEmpty()) {
            cargarDatos();
            return;
        }

        List<Usuario> usuariosEncontrados = usuarioController.buscarUsuario(textoBusqueda);

        modeloTabla.setRowCount(0);

        for (Usuario usuario : usuariosEncontrados) {
            modeloTabla.addRow(new Object[] {
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    usuario.getRol().getNombre(),
            });
        }

        lblTotalUsuario.setText("Encontrados: " + usuariosEncontrados.size() + " de " + usuarios.size() + " clientes");
    }

    private void agregarUsuario() {
        AgregarUsuarioDialog dialog = new AgregarUsuarioDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                usuarioController);

        dialog.setVisible(true);

        if (dialog.isUsuarioAgregado()) {
            cargarDatos();
        }
    }

    private void editarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para editar",
                    "Editar Cliente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tablaUsuarios.convertRowIndexToModel(filaSeleccionada);

        // Obtener datos del cliente seleccionado
        String nombre = (String) modeloTabla.getValueAt(modelRow, 0);
        String apellido = (String) modeloTabla.getValueAt(modelRow, 1);

        Usuario usuarioSeleccionado = null;
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre) && u.getApellido().equals(apellido)) {
                usuarioSeleccionado = u;
                break;
            }
        }

        if (usuarioSeleccionado != null) {
            EditarUsuarioDialog dialog = new EditarUsuarioDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    usuarioController,
                    usuarioSeleccionado);

            dialog.setVisible(true);

            if (dialog.isUsuarioEditado()) {
                cargarDatos();
            }
        }
    }

}