package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final Button miCuenta=findViewById(R.id.micuenta);
        final Button verproductos=findViewById(R.id.verProductos);
        final Button list = findViewById(R.id.verSubastas);
        final Button pago=findViewById(R.id.Pago);

        //action listener mi Cuenta
        miCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, CambiarClave.class);
                startActivity(intent);
                //Finalizar actividad
                finish();
            }
        });
        //action listener ver productos
        verproductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, VerProductoActivity.class);
                startActivity(intent);
                //Finalizar actividad
                finish();
            }
        });

        //Action listener versubastas
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GestorProductos.getGestorProductos().getListaProductos().size() == 0) {
                    obtenerListaProductos();
                }
                else{
                    lanzarActividad(ListActivity.class);
                }
                Toast.makeText(Menu.this, "Cargando información...", Toast.LENGTH_SHORT).show();
            }
        });

        //action listener pago
        pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, MeterDinero.class);
                startActivity(intent);
                //Finalizar actividad
                finish();
            }
        });


    }

    public void obtenerListaProductos(){

        //Establecer parámetros de la conexión
        Data datos = new Data.Builder()
                .putString("accion","obtenerLista")
                .putString("name","")
                .build();

        //Restricción de conexión
        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Construir conexión
        OneTimeWorkRequest trabajoPuntual =
                new OneTimeWorkRequest.Builder(ConexionBDWebService.class)
                        .setConstraints(restricciones)
                        .setInputData(datos)
                        .build();

        //Listener de la respuesta
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(trabajoPuntual.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        //obtener resultado
                        String resultado = status.getOutputData().getString("resultado");

                        Log.i("productos", "res: " + resultado);

                        //Parsear json
                        Gson gson = new Gson();
                        String[][] lista = gson.fromJson(resultado, String[][].class);

                        //Generar objetos
                        GestorProductos gestorProductos = GestorProductos.getGestorProductos();
                        String nombre, usuario, precioI, precioC, descr, fecha;
                        int id;
                        boolean ultimo = false;
                        Bitmap foto = null;
                        for(int i = 0; i < lista.length; i++){
                            id = Integer.parseInt(lista[i][0]);
                            nombre = lista[i][1];
                            usuario = lista[i][2];
                            precioI = lista[i][4];
                            precioC = lista[i][5];
                            descr = lista[i][6];
                            fecha = lista[i][7];
                            gestorProductos.addProducto(id, nombre, usuario, foto, precioI,
                                    precioC, descr, fecha);
                            if(i == lista.length - 1){
                                ultimo = true;
                            }
                            obtenerImagen(lista[i][3], id, ultimo);
                        }


                    }

                });

        WorkManager.getInstance(this).enqueue(trabajoPuntual);

    }

    public void obtenerImagen(String name, int id, boolean ultimo){

        //Establecer parámetros de la conexión
        Data datos = new Data.Builder()
                .putString("accion","download_img")
                .putString("name",name)
                .putInt("id",id)
                .putBoolean("ultimo",ultimo)
                .build();

        //Restricción de conexión
        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Construir conexión
        OneTimeWorkRequest trabajoPuntual =
                new OneTimeWorkRequest.Builder(ConexionBDWebService.class)
                        .setConstraints(restricciones)
                        .setInputData(datos)
                        .build();

        //Listener de la respuesta
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(trabajoPuntual.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        //obtener resultado
                        String resultado = status.getOutputData().getString("resultado");
                        if(resultado.equals("true")){
                            lanzarActividad(ListActivity.class);
                        }
                    }

                });

        WorkManager.getInstance(this).enqueue(trabajoPuntual);

    }

    public void lanzarActividad(Class activity){
        Intent intent = new Intent(getApplicationContext(), activity);
        startActivity(intent);
    }
}
