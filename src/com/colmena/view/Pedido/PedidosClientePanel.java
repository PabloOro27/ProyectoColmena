package com.colmena.view.Pedido;

import com.colmena.controller.PedidoController;
import com.colmena.model.Cliente;
import com.colmena.model.DetallePedido;
import com.colmena.model.Pedido;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.*;

public class PedidosClientePanel extends JPanel {

    private final Color COLOR_AMARILLO = new Color(255, 215, 0);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO = new Color(50, 50, 50);
    private final Color COLOR_TEXTO_CLARO = new Color(100, 100, 100);

    private PedidoController pedidoController;
    private Cliente cliente;

    private JTable tablaPedidos;
    private DefaultTableModel modeloTablaPedidos;
    private JTable tablaDetalles;
    private DefaultTableModel modeloTablaDetalles;

    private JLabel lblClienteNombre;
    private JLabel lblTotalPedidos;
    private JButton btnNuevoPedido;
    private JButton btnVerDetalles;
    private JButton btnRegistrarPago;
    private JButton btnCambiarEstado;

    private List<Pedido> pedidos = new ArrayList<>();
    private NumberFormat formatoMoneda;
    private SimpleDateFormat formatoFecha;

    public PedidosClientePanel(Cliente cliente) {
        this.cliente = cliente;
        this.pedidoController = new PedidoController();
        this.formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "GT"));
        this.formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);

        initComponents();
        cargarPedidos();
    }

    private void initComponents() {
        // Panel superior
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel central
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel izquierdo
        JPanel clientePanel = new JPanel(new GridLayout(2, 1));
        clientePanel.setBackground(COLOR_FONDO);

        lblClienteNombre = new JLabel("Cliente: " + cliente.getNombre() + " " + cliente.getApellido());
        lblClienteNombre.setFont(new Font("Arial", Font.BOLD, 16));
        lblClienteNombre.setForeground(COLOR_TEXTO);

        lblTotalPedidos = new JLabel("Total: 0 pedidos");
        lblTotalPedidos.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTotalPedidos.setForeground(COLOR_TEXTO_CLARO);

        clientePanel.add(lblClienteNombre);
        clientePanel.add(lblTotalPedidos);

        // Panel derecho
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(COLOR_FONDO);

        btnVerDetalles = createButton("Ver Detalles", e -> verDetallesPedido());
        btnRegistrarPago = createButton("Registrar Pago", e -> registrarPago());
        btnCambiarEstado = createButton("Cambiar Estado", e -> cambiarEstadoPedido());
        btnNuevoPedido = createButton("Nuevo Pedido", e -> nuevoPedido());

        buttonPanel.add(btnVerDetalles);
        buttonPanel.add(btnRegistrarPago);
        buttonPanel.add(btnCambiarEstado);
        buttonPanel.add(btnNuevoPedido);

        panel.add(clientePanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // Panel superior
        JPanel pedidosPanel = new JPanel(new BorderLayout());
        pedidosPanel.setBackground(COLOR_FONDO);

        JLabel lblPedidos = new JLabel("Pedidos del Cliente");
        lblPedidos.setFont(new Font("Arial", Font.BOLD, 14));
        lblPedidos.setForeground(COLOR_TEXTO);
        pedidosPanel.add(lblPedidos, BorderLayout.NORTH);

        // Crear modelo de tabla pedidos
        modeloTablaPedidos = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Definir columnas
        modeloTablaPedidos.addColumn("ID");
        modeloTablaPedidos.addColumn("Fecha");
        modeloTablaPedidos.addColumn("Estado");
        modeloTablaPedidos.addColumn("Total");
        modeloTablaPedidos.addColumn("Pagado");
        modeloTablaPedidos.addColumn("Pendiente");

        // Crear tabla
        tablaPedidos = new JTable(modeloTablaPedidos);
        tablaPedidos.setRowHeight(25);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.setAutoCreateRowSorter(true);
        tablaPedidos.getTableHeader().setReorderingAllowed(false);
        tablaPedidos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Configurar tamaño de fuente
        tablaPedidos.setFont(new Font("Arial", Font.PLAIN, 14));

        // Configurar renderizadores para formato
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Renderer para moneda
        TableCellRenderer monedaRenderer = new DefaultTableCellRenderer() {
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

        // Renderer para pendiente (colorea en rojo si tiene pendiente)
        TableCellRenderer pendienteRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(formatoMoneda.format((Double) value));
                    if ((Double) value > 0) {
                        setForeground(isSelected ? new Color(255, 200, 200) : Color.RED);
                    } else {
                        setForeground(isSelected ? Color.WHITE : COLOR_TEXTO);
                    }
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return this;
            }
        };

        // Aplicar renderizadores
        tablaPedidos.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.CENTER);
            }
        });
        tablaPedidos.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.CENTER);
            }
        });
        tablaPedidos.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.CENTER);
            }
        });
        tablaPedidos.getColumnModel().getColumn(3).setCellRenderer(monedaRenderer);
        tablaPedidos.getColumnModel().getColumn(4).setCellRenderer(monedaRenderer);
        tablaPedidos.getColumnModel().getColumn(5).setCellRenderer(pendienteRenderer);

        // Ajustar anchos de columnas
        tablaPedidos.getColumnModel().getColumn(0).setPreferredWidth(60); // ID
        tablaPedidos.getColumnModel().getColumn(1).setPreferredWidth(150); // Fecha
        tablaPedidos.getColumnModel().getColumn(2).setPreferredWidth(100); // Estado
        tablaPedidos.getColumnModel().getColumn(3).setPreferredWidth(100); // Total
        tablaPedidos.getColumnModel().getColumn(4).setPreferredWidth(100); // Pagado
        tablaPedidos.getColumnModel().getColumn(5).setPreferredWidth(100); // Pendiente

        // Listener para selección de pedido
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetallesPedidoSeleccionado();
            }
        });

        JScrollPane scrollPedidos = new JScrollPane(tablaPedidos);
        scrollPedidos.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        pedidosPanel.add(scrollPedidos, BorderLayout.CENTER);

        // Panel inferior con tabla de detalles
        JPanel detallesPanel = new JPanel(new BorderLayout());
        detallesPanel.setBackground(COLOR_FONDO);

        JLabel lblDetalles = new JLabel("Detalles del Pedido");
        lblDetalles.setFont(new Font("Arial", Font.BOLD, 14));
        lblDetalles.setForeground(COLOR_TEXTO);
        detallesPanel.add(lblDetalles, BorderLayout.NORTH);

        // Crear modelo de tabla detalles
        modeloTablaDetalles = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Definir columnas
        modeloTablaDetalles.addColumn("Código");
        modeloTablaDetalles.addColumn("Producto");
        modeloTablaDetalles.addColumn("Categoría");
        modeloTablaDetalles.addColumn("Cantidad");
        modeloTablaDetalles.addColumn("Precio Unit.");
        modeloTablaDetalles.addColumn("Subtotal");

        // Crear tabla
        tablaDetalles = new JTable(modeloTablaDetalles);
        tablaDetalles.setRowHeight(25);
        tablaDetalles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDetalles.setAutoCreateRowSorter(true);
        tablaDetalles.getTableHeader().setReorderingAllowed(false);
        tablaDetalles.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Configurar tamaño de fuente
        tablaDetalles.setFont(new Font("Arial", Font.PLAIN, 14));

        // Aplicar renderizadores
        tablaDetalles.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.CENTER);
            }
        });
        tablaDetalles.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.CENTER);
            }
        });
        tablaDetalles.getColumnModel().getColumn(4).setCellRenderer(monedaRenderer);
        tablaDetalles.getColumnModel().getColumn(5).setCellRenderer(monedaRenderer);

        // Ajustar anchos de columnas
        tablaDetalles.getColumnModel().getColumn(0).setPreferredWidth(80); // Código
        tablaDetalles.getColumnModel().getColumn(1).setPreferredWidth(200); // Producto
        tablaDetalles.getColumnModel().getColumn(2).setPreferredWidth(120); // Categoría
        tablaDetalles.getColumnModel().getColumn(3).setPreferredWidth(80); // Cantidad
        tablaDetalles.getColumnModel().getColumn(4).setPreferredWidth(100); // Precio Unit.
        tablaDetalles.getColumnModel().getColumn(5).setPreferredWidth(100); // Subtotal

        JScrollPane scrollDetalles = new JScrollPane(tablaDetalles);
        scrollDetalles.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        detallesPanel.add(scrollDetalles, BorderLayout.CENTER);

        // Crear panel dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pedidosPanel, detallesPanel);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);

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

    private void cargarPedidos() {
        pedidos = pedidoController.obtenerPedidos(cliente.getId());

        modeloTablaPedidos.setRowCount(0);

        for (Pedido pedido : pedidos) {
            double saldoPendiente = pedido.getTotal() - pedido.getSaldoPagado(); 

            modeloTablaPedidos.addRow(new Object[] {
                    pedido.getId(),
                    formatoFecha.format(pedido.getFecha()),
                    pedido.getEstado(),
                    pedido.getTotal(),
                    pedido.getSaldoPagado(), // Saldo pagado
                    saldoPendiente
            });
        }

        lblTotalPedidos.setText("Total: " + pedidos.size() + " pedidos");

        // Limpiar tabla de detalles
        modeloTablaDetalles.setRowCount(0);
    }

    private void mostrarDetallesPedidoSeleccionado() {
        int filaSeleccionada = tablaPedidos.getSelectedRow();

        // Limpiar tabla de detalles
        modeloTablaDetalles.setRowCount(0);

        if (filaSeleccionada != -1) {
            int modelRow = tablaPedidos.convertRowIndexToModel(filaSeleccionada);

            // Obtener ID del pedido
            int pedidoId = (int) modeloTablaPedidos.getValueAt(modelRow, 0);

            // Buscar el pedido en la lista
            Pedido pedidoSeleccionado = null;
            for (Pedido p : pedidos) {
                if (p.getId() == pedidoId) {
                    pedidoSeleccionado = p;
                    break;
                }
            }

            if (pedidoSeleccionado != null && pedidoSeleccionado.getDetalles() != null) {
                // Mostrar detalles en la tabla
                for (DetallePedido detalle : pedidoSeleccionado.getDetalles()) {
                    modeloTablaDetalles.addRow(new Object[] {
                            detalle.getProducto().getCodigo(),
                            detalle.getProducto().getNombre(),
                            detalle.getProducto().getCategoria().getNombre(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario(),
                            detalle.getSubtotal()
                    });
                }
            }
        }
    }

    private void verDetallesPedido() {
        int filaSeleccionada = tablaPedidos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un pedido para ver sus detalles",
                    "Ver Detalles", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tablaPedidos.convertRowIndexToModel(filaSeleccionada);

        // Obtener ID del pedido
        int pedidoId = (int) modeloTablaPedidos.getValueAt(modelRow, 0);

        // Aquí se podría abrir un diálogo más detallado
        JOptionPane.showMessageDialog(this,
                "Funcionalidad para ver detalles completos en desarrollo",
                "Ver Detalles", JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarPago() {
        int filaSeleccionada = tablaPedidos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un pedido para registrar un pago",
                    "Registrar Pago", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tablaPedidos.convertRowIndexToModel(filaSeleccionada);

        // Obtener datos del pedido
        int pedidoId = (int) modeloTablaPedidos.getValueAt(modelRow, 0);
        double total = (double) modeloTablaPedidos.getValueAt(modelRow, 3);
        double saldoPendiente = (double) modeloTablaPedidos.getValueAt(modelRow, 5);

        // Verificar si hay saldo pendiente
        if (saldoPendiente <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Este pedido ya ha sido pagado completamente",
                    "Registrar Pago", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Crear panel para el formulario de pago
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblMonto = new JLabel("Monto a pagar:");
        JSpinner spnMonto = new JSpinner(new SpinnerNumberModel(saldoPendiente, 0.01, saldoPendiente, 0.01));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnMonto, "0.00");
        spnMonto.setEditor(editor);

        JLabel lblMetodo = new JLabel("Método de pago:");
        JComboBox<String> cmbMetodo = new JComboBox<>(new String[] { "Efectivo", "Tarjeta", "Transferencia", "Otro" });

        JLabel lblReferencia = new JLabel("Referencia:");
        JTextField txtReferencia = new JTextField(15);

        JLabel lblSaldoPendiente = new JLabel("Saldo pendiente:");
        JLabel lblSaldoPendienteValor = new JLabel(formatoMoneda.format(saldoPendiente));
        lblSaldoPendienteValor.setFont(new Font("Arial", Font.BOLD, 12));
        lblSaldoPendienteValor.setForeground(Color.RED);

        panel.add(lblMonto);
        panel.add(spnMonto);
        panel.add(lblMetodo);
        panel.add(cmbMetodo);
        panel.add(lblReferencia);
        panel.add(txtReferencia);
        panel.add(lblSaldoPendiente);
        panel.add(lblSaldoPendienteValor);

        // Mostrar diálogo
        int resultado = JOptionPane.showConfirmDialog(this, panel,
                "Registrar Pago", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            double monto = (double) spnMonto.getValue();
            String metodoPago = (String) cmbMetodo.getSelectedItem();
            String referencia = txtReferencia.getText().trim();

            // Registrar pago
            boolean exito = pedidoController.registrarPago(pedidoId, monto, metodoPago, referencia);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                        "Pago registrado correctamente",
                        "Pago Registrado", JOptionPane.INFORMATION_MESSAGE);

                // Recargar datos
                cargarPedidos();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al registrar el pago",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cambiarEstadoPedido() {
        int filaSeleccionada = tablaPedidos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un pedido para cambiar su estado",
                    "Cambiar Estado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tablaPedidos.convertRowIndexToModel(filaSeleccionada);

        // Obtener datos del pedido
        int pedidoId = (int) modeloTablaPedidos.getValueAt(modelRow, 0);
        String estadoActual = (String) modeloTablaPedidos.getValueAt(modelRow, 2);

        // Opciones de estado
        String[] estados = { "Pendiente", "En proceso", "Completado", "Entregado", "Cancelado" };

        // Encontrar el índice del estado actual
        int estadoIndex = 0;
        for (int i = 0; i < estados.length; i++) {
            if (estados[i].equals(estadoActual)) {
                estadoIndex = i;
                break;
            }
        }

        // Mostrar diálogo de selección
        String nuevoEstado = (String) JOptionPane.showInputDialog(
                this,
                "Seleccione el nuevo estado del pedido:",
                "Cambiar Estado",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estados,
                estados[estadoIndex]);

        if (nuevoEstado != null && !nuevoEstado.equals(estadoActual)) {
            // Actualizar estado
            boolean exito = pedidoController.actualizarEstadoPedido(pedidoId, nuevoEstado);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                        "Estado del pedido actualizado correctamente",
                        "Estado Actualizado", JOptionPane.INFORMATION_MESSAGE);

                // Recargar datos
                cargarPedidos();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al actualizar el estado del pedido",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void nuevoPedido() {
        try {
            Frame parentFrame = JOptionPane.getFrameForComponent(this);

            NuevoPedidoDialog dialog = new NuevoPedidoDialog(
                    parentFrame, 
                    cliente);

            dialog.setVisible(true);

            if (dialog.isPedidoCreado()) {                
                cargarPedidos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error en nuevo pedido Excepcion: " + ex.getMessage());
        }

    }
}