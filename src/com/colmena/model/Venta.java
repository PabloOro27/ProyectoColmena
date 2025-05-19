package com.colmena.model;

import java.sql.Date;
import java.util.List;

public class Venta {
    private int id;
    private Cliente cliente;
    private Usuario vendedor;
    private Date fecha;
    private List<DetalleVenta> detalles;
    private double total;
    private String estado;

    // contructor
    public Venta(Date fecha, List<DetalleVenta> detalles) {
        this.fecha = fecha;
        this.detalles = detalles;
    }

    public Venta() {
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
    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
    public void setDetalles(List<DetalleVenta> detalles) {
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

    // Métodos para ventas 
    public void calcularTotal() {
        this.total = 0;
        for (DetalleVenta detalle : detalles) {
            this.total += detalle.getSubtotal();
        }
    }
    
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        calcularTotal();
    }
}
