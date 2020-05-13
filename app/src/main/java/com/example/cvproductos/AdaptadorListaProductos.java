package com.example.cvproductos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdaptadorListaProductos extends BaseAdapter { //Es la clase del adaptador de la lista de videojuegos comun a todos los usuarios que se muestra en mostrarVideojuegosDisponibles

    private Context contexto;
    private LayoutInflater inflater;
    private String[] nombres;
    private String[] preciosActual;
    private String[] preciosCompra;
    private String[] fecha;


    public AdaptadorListaProductos(Context pcontext, String[] pnombres, String[] pprecioscompra, String[] ppreciosactual, String[] pfecha){
        contexto = pcontext;
        nombres = pnombres;
        preciosCompra = pprecioscompra;
        preciosActual = ppreciosactual;
        fecha = pfecha;
        inflater= (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return nombres.length;
    } //Numero de elementos de la lista

    @Override
    public Object getItem(int position) { //Elemento en la posicion: position
        return nombres[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    } //Posicion de un elemento

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) { //Como se visualiza un elemento

        view=inflater.inflate(R.layout.producto,null); //Se indica el xml con el layout para cada elemento

        //Se recogen los elementos del layout en variables
        TextView nombre= (TextView) view.findViewById(R.id.nombreProd);
        TextView precioCompra= (TextView) view.findViewById(R.id.precioCompra);
        TextView precioActual= (TextView) view.findViewById(R.id.precioActual);
        TextView fechaFin = (TextView) view.findViewById(R.id.sfecha);

        //Se asigna a cada variable el contenido que se quiere mostrar en ese elemento
        nombre.setText(nombres[position]);
        precioCompra.setText(preciosCompra[position]);
        precioActual.setText(preciosActual[position]);
        fechaFin.setText(fecha[position]);

        return view;
    }
}
