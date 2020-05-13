package com.example.cvproductos;

import android.graphics.Bitmap;

public class Producto {

    private int id;
    private String producto;
    private String usuario;
    private Bitmap foto;
    private String precioI;
    private String precioC;
    private String descripcion;
    private String fecha;

    public Producto(int id, String producto, String usuario, Bitmap foto, String precioI, String precioC, String descripcion, String fecha) {
        this.id = id;
        this.producto = producto;
        this.usuario = usuario;
        this.foto = foto;
        this.precioI = precioI;
        this.precioC = precioC;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public String getPrecioI() {
        return precioI;
    }

    public void setPrecioI(String precioI) {
        this.precioI = precioI;
    }

    public String getPrecioC() {
        return precioC;
    }

    public void setPrecioC(String precioC) {
        this.precioC = precioC;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
