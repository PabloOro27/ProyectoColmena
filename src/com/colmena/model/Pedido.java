package com.colmena.model;

import java.sql.Date;
import java.util.List;

public class Pedido {
    private int id;
    private Cliente cliente;
    private Usuario vendedor;
    private Date fecha;
    private List<DetallePedido> detalles;
    private double total;
    private String estado;
    private double saldoPagado;
    private String metodoPago;
    private String notas;  

    // contructor
    public Pedido(Date fecha, List<DetallePedido> detalles) {
        this.fecha = fecha;
        this.detalles = detalles;
    }

    public Pedido() {
    }

    // getters y setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public Usuario getVendedor() {
        return vendedor;
    }
    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public List<DetallePedido> getDetalles() {
        return detalles;
    }
    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getSaldoPagado() {
        return saldoPagado;
    }

    public void setSaldoPagado(double saldoPagado) {
        this.saldoPagado = saldoPagado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    // Métodos para ventas 
    public void calcularTotal() {
        this.total = 0;
        for (DetallePedido detalle : detalles) {
            this.total += detalle.getSubtotal();
        }
    }
    
    public void agregarDetalle(DetallePedido detalle) {
        this.detalles.add(detalle);
        calcularTotal();
    }
}
