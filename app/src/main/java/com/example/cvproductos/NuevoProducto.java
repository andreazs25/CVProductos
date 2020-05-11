package com.example.proyecto3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NuevoProducto extends AppCompatActivity {

    static String fecha ="";
    static String hora="";
    final int CODIGO_FOTO_ARCHIVO=1;
    Uri uriimagen= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_producto);
        Button elegirFecha = (Button) findViewById(R.id.elegirFecha);
        //Al pulsar en cambiar foto
        elegirFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoFecha dialogoFecha= new DialogoFecha();
                dialogoFecha.show(getSupportFragmentManager(), "fecha");
            }
        });

        Button elegirHora = (Button) findViewById(R.id.elegirHora);
        //Al pulsar en cambiar foto
        elegirHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoHora dialogoHora= new DialogoHora();
                dialogoHora.show(getSupportFragmentManager(), "hora");
            }
        });

        final File directorio=this.getFilesDir();
        Button cambiarFoto = (Button) findViewById(R.id.cambiarFoto);
        //Al pulsar en cambiar foto
        cambiarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se crea la uri con la imagen sacada
                String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String nombrefich= "IMG_" + timeStamp+ "_";
                File fichImg= null;

                try {
                    fichImg= File.createTempFile(nombrefich, ".jpg",directorio);
                    uriimagen= FileProvider.getUriForFile(getApplicationContext(), "com.example.proyecto1.provider", fichImg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent elIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                elIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimagen);
                startActivityForResult(elIntent, CODIGO_FOTO_ARCHIVO);
            }
        });

        Button confirmar = findViewById(R.id.confirmar);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText prod =(EditText) findViewById(R.id.nombreP);
                String producto = prod.getText().toString();

                EditText precioI =(EditText) findViewById(R.id.sPrecioInicial2);
                String precioInicial = precioI.getText().toString();

                EditText precioC =(EditText) findViewById(R.id.precioCompra2);
                String precioCompra = precioC.getText().toString();
                Log.i("fecha-hora: ", fecha+"|"+hora);
                if(uriimagen!=null && producto.length()!=0 && precioI.length()!=0 && precioC.length()!=0 && fecha.length()!=0 && hora.length()!=0){
                    Data datos = new Data.Builder()
                            .putString("usuario", "mikel")
                            .putString("producto",producto)
                            .putString("precioInicial", precioInicial)
                            .putString("precioCompra", precioCompra)
                            .putString("fechahora", fecha+"|"+hora)
                            .putString("funcion", "subirProducto")
                            .build();
                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionDBWebService.class).setInputData(datos).build();
                    WorkManager.getInstance().enqueue(otwr);

                    Data datos2 = new Data.Builder()
                            .putString("funcion", "subirImagen")
                            .putString("usuario", "mikel")
                            .putString("producto",producto)
                            .putString("foto", uriimagen.toString())
                            .build();
                    OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(conexionDBWebService.class).setInputData(datos2).build();
                    WorkManager.getInstance().enqueue(otwr2);

                    Intent i = new Intent(NuevoProducto.this, VentaMisProductos.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Por favor añada una foto del producto y rellene todos los campos", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_FOTO_ARCHIVO && resultCode == RESULT_OK) {
            ImageView perfil = (ImageView) findViewById(R.id.imagen2);
            //Se pone la foto sacada en el image view
            perfil.setImageURI(uriimagen);
        }
    }

    public static void setFecha(String pfecha){
        fecha=pfecha;
    }

    public static void setHora(String phora){
        hora=phora;
    }



    @Override
    protected void onSaveInstanceState(Bundle outState){ //Metodo para guardar los valores en caso de girar, pausar... la app
        super.onSaveInstanceState(outState);


        EditText producto = (EditText) findViewById(R.id.nombreP);
        String nProd = producto.getText().toString();
        outState.putString("nProd", nProd);

        EditText precioInicial = (EditText) findViewById(R.id.sPrecioInicial2);
        String preI = precioInicial.getText().toString();
        outState.putString("preI", preI);

        EditText precioCompra = (EditText) findViewById(R.id.precioCompra2);
        String preC = precioCompra.getText().toString();
        outState.putString("preC", preC);


    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){ //Metodo para restablecer los valores guardados
        super.onRestoreInstanceState(savedInstanceState);

        String nProd = savedInstanceState.getString("nProd");
        EditText producto = (EditText) findViewById(R.id.nombreP);
        producto.setText(String.valueOf(nProd));

        String preI = savedInstanceState.getString("preI");
        EditText precioInicial = (EditText) findViewById(R.id.sPrecioInicial2);
        precioInicial.setText(String.valueOf(preI));

        String preC = savedInstanceState.getString("preC");
        EditText precioCompra = (EditText) findViewById(R.id.precioCompra2);
        precioCompra.setText(String.valueOf(preC));
    }

}
