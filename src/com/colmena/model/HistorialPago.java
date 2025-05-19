package com.colmena.model;

import java.sql.Date;

public class HistorialPago {
    private int id;
    private Pedido pedido;
    private Date fechaPago;
    private double monto;
    private String metodoPago;
    private String referencia;

    public HistorialPago(Pedido pedido, Date fechaPago, double monto, String metodoPago, String referencia) {
        this.pedido = pedido;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referencia = referencia;
    }

    public HistorialPago() {
    }

    
    // getters y setters 
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
    public Date getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }
    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }
    public String getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    public String getReferencia() {
        return referencia;
    }
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
