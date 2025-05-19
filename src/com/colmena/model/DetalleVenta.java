package com.colmena.model;

public class DetalleVenta {
    private int id;
    private Venta venta;
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleVenta(Producto producto, int cantidad, double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    // getterws y setters 
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Venta getVenta() {
        return venta;
    }
    public void setVenta(Venta venta) {
        this.venta = venta;
    }
    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public double getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    public double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    // metodo para calcular subtotal 
    public void calcularSubtotal() {
        this.subtotal = this.cantidad * this.precioUnitario;
    }
}
