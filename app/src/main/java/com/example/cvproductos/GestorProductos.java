package com.example.cvproductos;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

public class GestorProductos {

    private ArrayList<Producto> listaProductos;
    private int IdActual;


    //Patrón Singleton
    private static GestorProductos miGestorProductos;


    public  static GestorProductos getGestorProductos() {
        if (miGestorProductos == null) {
            miGestorProductos = new GestorProductos();
        }
        return miGestorProductos;
    }

    private GestorProductos() {
        this.listaProductos = new ArrayList<>();
        this.IdActual = -1;
    }

    public int getIdActual() {
        return IdActual;
    }

    public void setIdActual(int idActual) {
        IdActual = idActual;
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public void addProducto(int id, String prod, String user, Bitmap foto, String precioI, String precioC, String descr, String fecha){
        Producto producto = new Producto(id, prod, user, foto, precioI, precioC, descr, fecha);
        listaProductos.add(producto);
    }

    public Producto buscarProducto(int id){
        for (int i = 0; i < listaProductos.size(); i++){
            if(listaProductos.get(i).getId() == id){
                return listaProductos.get(i);
            }
        }
        return null;
    }

    public void cambiarPrecioI(String precio, int id){
        Producto producto = buscarProducto(id);
        if (producto != null){
            producto.setPrecioI(precio);
        }
    }

    public void eliminarProducto(int id){
        Producto producto = buscarProducto(id);
        if (producto != null){
            listaProductos.remove(producto);
        }
    }

    public boolean existeProducto(int id){
        if(buscarProducto(id) == null){
            return false;
        }
        else return true;
    }

    public void resetearLista(){
        this.listaProductos = new ArrayList<>();
        this.IdActual = -1;
    }

}
