package com.colmena.model;

public class DetallePedido {
    private int id;
    private Pedido pedido;
    
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;      

    public DetallePedido(Producto producto, int cantidad, double precioUnitario) {
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
    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
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
