package com.example.proyecto3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;


public class VentaMisProductos extends AppCompatActivity {

    String[] nombresProducto = {};
    String[] precioCompra = {};
    String[] precioActual = {};
    String[] fecha = {};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_mis_productos);

        Data datos = new Data.Builder()
                .putString("funcion", "obtenerProductosEnVentaUsuario")
                .putString("usuario", "mikel")
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionDBWebService.class).setInputData(datos).build();
        WorkManager.getInstance().getWorkInfoByIdLiveData(otwr.getId()).observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo status) {
                        if (status != null && status.getState().isFinished()) {
                            String[] productos = status.getOutputData().getStringArray("productos");
                            if (productos != null && productos.length > 0) {
                                nombresProducto = new String[productos.length];
                                precioCompra = new String[productos.length];
                                precioActual = new String[productos.length];
                                fecha = new String[productos.length];
                                for (int i = 0; i < productos.length; i++) {
                                    String producto = productos[i];
                                    String[] prod = producto.split(",");
                                    nombresProducto[i] = prod[0].substring(2, prod[0].length() - 1);
                                    precioActual[i] = prod[1].substring(1, prod[1].length() - 1);
                                    precioCompra[i] = prod[2].substring(1, prod[2].length() - 1);
                                    String fe = prod[3].substring(1, prod[3].length() - 2);
                                    fecha[i] = fe.replace("|", "  ");
                                    Log.i("Producto: ", "Nombre: "+nombresProducto[i]+ " PrecioC: "+precioCompra[i]+ " PrecioI: "+precioActual[i] +" Fecha: "+fecha[i]);
                                }
                                Log.i("Resul:", "ok");
                                //Se crea la lista personalizada con los elemntos obtenidos anteriormente para mostrarselos al usuario
                                final ListView misProductos = (ListView) VentaMisProductos.this.findViewById(R.id.listaProductos);
                                AdaptadorListaProductos eladap = new AdaptadorListaProductos(VentaMisProductos.this.getApplicationContext(), nombresProducto, precioCompra, precioActual,fecha);
                                misProductos.setAdapter(eladap);
                                misProductos.setClickable(true);
                                //Al pulsar un producto se abre el dialogo compra dónde se pregunta si estas seguro de querer comprarlo
                                misProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    }
                                });
                            }
                        }
                    }
                }
        );
        WorkManager.getInstance().enqueue(otwr);


        Button añadir = findViewById(R.id.añadirP);
        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VentaMisProductos.this, NuevoProducto.class);
                startActivity(i);
                finish();
            }
        });

    }


}
