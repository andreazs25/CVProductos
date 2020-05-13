package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.cvproductos.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {

    ArrayList<Producto> listaProductos;

    AdaptadorListView eladap;

    GestorProductos gestorProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        gestorProductos = GestorProductos.getGestorProductos();

        SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
        String nuevo_saldo = pref.getString("saldo", "0");
        Log.i("NUEVO_SALDO", nuevo_saldo);

        // Recoge las estructuras que serán usadas en el list view
        listaProductos = gestorProductos.getListaProductos();

        final Button btnBack = findViewById(R.id.btn_back);
        final Button btnBottom = findViewById(R.id.btn_bottom);

        // Se pasan las estructuas al adaptador para que use las funciones de BaseAdapter
        final ListView losProductos = (ListView) findViewById(R.id.lista);
        eladap= new AdaptadorListView(getApplicationContext(), listaProductos);
        losProductos.setAdapter(eladap);
        losProductos.setClickable(true);

        //Listener click en una fila de la lista
        losProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                mostrarView(position);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                losProductos.smoothScrollToPosition(eladap.getCount());
            }
        });
    }

    private void mostrarView(int position){
        gestorProductos.setIdActual(listaProductos.get(position).getId());
        Intent intent = new Intent(this, VerProductoActivity.class);
        startActivity(intent);
        finish();
    }

}
