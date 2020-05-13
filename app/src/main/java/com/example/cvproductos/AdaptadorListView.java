package com.example.cvproductos;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class AdaptadorListView extends BaseAdapter {

    private Context contexto;
    private LayoutInflater inflater;
    private ArrayList<Producto> listaProductos;

    public AdaptadorListView(Context contexto, ArrayList<Producto> listaProductos) {
        this.contexto = contexto;
        this.listaProductos = listaProductos;
        this.inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listaProductos.size();
    }

    @Override
    public Object getItem(int i) {
        return listaProductos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        //Modifica los campos de la fila i
        view=inflater.inflate(R.layout.adapter_layout,null);
        TextView producto = (TextView) view.findViewById(R.id.titulo);
        TextView usuario = (TextView) view.findViewById(R.id.usuario);
        TextView precioI = (TextView) view.findViewById(R.id.precioI);
        TextView precioC = (TextView) view.findViewById(R.id.precioC);
        TextView fecha = (TextView) view.findViewById(R.id.fecha);
        ImageView img=(ImageView) view.findViewById(R.id.icono);

        producto.setText(listaProductos.get(i).getProducto());
        usuario.setText(listaProductos.get(i).getUsuario());
        precioI.setText(listaProductos.get(i).getPrecioI() + "€  /");
        precioC.setText(listaProductos.get(i).getPrecioC() + '€');
        String[] fechaFormateada = listaProductos.get(i).getFecha().split("\\|");
        fecha.setText(fechaFormateada[0] + ' ' + fechaFormateada[1]);

        //escalar imagen
        Bitmap bitmapFoto = listaProductos.get(i).getFoto();

        //int anchoDestino = img.getWidth();
        int anchoDestino = 73;
        //int altoDestino = img.getHeight();
        int altoDestino = 51;
        int anchoImagen = bitmapFoto.getWidth();
        int altoImagen = bitmapFoto.getHeight();
        float ratioImagen = (float) anchoImagen / (float) altoImagen;
        float ratioDestino = (float) anchoDestino / (float) altoDestino;
        int anchoFinal = anchoDestino;
        int altoFinal = altoDestino;
        if (ratioDestino > ratioImagen) {
            anchoFinal = (int) ((float) altoDestino * ratioImagen);
        } else {
            altoFinal = (int) ((float) anchoDestino / ratioImagen);
        }
        Bitmap bitmapredimensionado = Bitmap.createScaledBitmap(bitmapFoto, anchoFinal, altoFinal, true);

        //Actualizar imagen
        img.setImageBitmap(bitmapredimensionado);

        return view;
    }

}
