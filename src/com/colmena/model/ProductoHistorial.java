package com.colmena.model;

import java.sql.Date;

public class ProductoHistorial {
    private int id;
    private Producto producto;
    private Date fechaModificacion;
    private int stockAnterior;
    private int stockNuevo;
    private String tipoMovimiento;
    private Usuario usuario;
    private String nota;

    public ProductoHistorial() {
    }

    public ProductoHistorial(Producto producto, Date fechaModificacion, int stockAnterior, int stockNuevo,
            String tipoMovimiento, Usuario usuario, String nota) {
        this.producto = producto;
        this.fechaModificacion = fechaModificacion;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.tipoMovimiento = tipoMovimiento;
        this.usuario = usuario;
        this.nota = nota;
    }

    // gettes y setter 
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    public Date getFechaModificacion() {
        return fechaModificacion;
    }
    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
    public int getStockAnterior() {
        return stockAnterior;
    }
    public void setStockAnterior(int stockAnterior) {
        this.stockAnterior = stockAnterior;
    }
    public int getStockNuevo() {
        return stockNuevo;
    }
    public void setStockNuevo(int stockNuevo) {
        this.stockNuevo = stockNuevo;
    }
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public String getNota() {
        return nota;
    }
    public void setNota(String nota) {
        this.nota = nota;
    }
}
