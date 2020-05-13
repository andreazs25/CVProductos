package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class VerFotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_foto);

        GestorProductos gestorProductos = GestorProductos.getGestorProductos();
        Producto producto = gestorProductos.buscarProducto(gestorProductos.getIdActual());

        final ImageView foto = findViewById(R.id.img_foto2);

        //escalar imagen
        Bitmap bitmapFoto = producto.getFoto();

        //int anchoDestino = img.getWidth();
        int anchoDestino = 323;
        //int altoDestino = img.getHeight();
        int altoDestino = 501;
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
        foto.setImageBitmap(bitmapredimensionado);

    }
}
