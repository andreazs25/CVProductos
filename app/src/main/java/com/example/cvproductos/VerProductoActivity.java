package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class VerProductoActivity extends AppCompatActivity {

    Producto producto;
    String username;
    GestorProductos gestorProductos;
    String accion = "";
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_producto);

        final Button btnPujar = findViewById(R.id.btn_Pujar);
        final Button btnComprar = findViewById(R.id.btn_Comprar);
        final Button btnBack = findViewById(R.id.btn_form_cancel);
        final Button btnFoto = findViewById(R.id.button_img);

        final TextView txt_title = findViewById(R.id.txt_title);
        final TextView txt_fecha = findViewById(R.id.txt_fecha);
        final TextView txt_usuario = findViewById(R.id.txt_usuario);
        final TextView txt_descr = findViewById(R.id.txt_descr);
        final TextView txt_precioI = findViewById(R.id.view_precioI);
        final TextView txt_precioC = findViewById(R.id.view_precioC);

        final ImageView foto = findViewById(R.id.img_foto);


        //Rellenar los campos con el producto
        GestorProductos gestorProductos = GestorProductos.getGestorProductos();

        producto = gestorProductos.buscarProducto(gestorProductos.getIdActual());

        txt_descr.setText(producto.getDescripcion());
        String[] fechaFormateada = producto.getFecha().split("\\|");
        txt_fecha.setText(fechaFormateada[0] + ' ' + fechaFormateada[1]);
        txt_precioC.setText(producto.getPrecioC() + '€');
        txt_precioI.setText(producto.getPrecioI() + '€');
        txt_title.setText(producto.getProducto());
        txt_usuario.setText(producto.getUsuario());

        //escalar imagen
        Bitmap bitmapFoto = producto.getFoto();

        //int anchoDestino = img.getWidth();
        int anchoDestino = 121;
        //int altoDestino = img.getHeight();
        int altoDestino = 95;
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

        //Comprobar si saldo disponible suficiente
        SharedPreferences pref = getSharedPreferences("MyPref", 0);
        String saldoStr = pref.getString("saldo", "0");
        float saldo = Float.valueOf(saldoStr);

        Log.i("SALDO", String.valueOf(saldo));

        float precioI = Float.valueOf(producto.getPrecioI());
        float precioC = Float.valueOf(producto.getPrecioC());

        if(saldo >= precioI){
            btnPujar.setEnabled(true);
        }
        if(saldo >= precioC){
            btnComprar.setEnabled(true);
        }


        btnPujar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pujar();
            }
        });

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprar();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverAtras();
            }
        });

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VerFotoActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed(){
        volverAtras();
    }

    private void volverAtras(){
        //Volver a lanzar la list view actualizada
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        finish();
    }

    private void pujar(){
        accion = "puja";
        mostrarDialogoConfirmar("puja", producto.getId());
    }

    private void comprar(){
        accion = "compra";
        mostrarDialogoConfirmar("compra", producto.getId());
    }


    private void mostrarDialogoConfirmar(String accion, int id){
        //Usar fragment manager para mostrar el diálogo
        FragmentManager fm = getSupportFragmentManager();
        CompraDialog alertDialog = CompraDialog.newInstance(accion, id, producto.getPrecioI(), producto.getPrecioC());
        alertDialog.show(fm, "fragment_alert");
        //goBack();
    }

    public void realizarTransaccion(){

        gestorProductos = GestorProductos.getGestorProductos();
        producto = gestorProductos.buscarProducto(gestorProductos.getIdActual());

        //Recoger nombre de usuario
        pref = getSharedPreferences("MyPref", 0);
        username = pref.getString("username", "");

        //Mensaje de notificación
        Toast.makeText(this, "Procesando transaccion", Toast.LENGTH_LONG).show();

        //Establecer parámetros de la conexión
        Data datos = new Data.Builder()
                .putString("accion", accion)
                .putString("name", username)
                .putInt("id", producto.getId())
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

                        pref = this.getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        String saldo = pref.getString("saldo", "0");

                        //Si transaccion correcta, actualizar saldo y precio y/o eliminar producto
                        if(!resultado.equals("-1")){
                            if (accion.equals("puja")){
                                // actualizar saldo
                                String nuevoSaldo = String.valueOf(Float.valueOf(saldo) - Float.valueOf(producto.getPrecioI()));
                                editor.putString("saldo", nuevoSaldo);
                                // actualizar precio puja
                                String nuevoPrecioI = String.valueOf(Float.valueOf(producto.getPrecioI()) + Float.valueOf(producto.getPrecioC()) * 0.1);
                                gestorProductos.cambiarPrecioI(nuevoPrecioI, producto.getId());
                            }
                            else if (accion.equals("compra")){
                                // actualzar saldo
                                String nuevoSaldo = String.valueOf(Float.valueOf(saldo) - Float.valueOf(producto.getPrecioC()));
                                editor.putString("saldo", nuevoSaldo);
                                // eliminar producto
                                gestorProductos.eliminarProducto(producto.getId());
                            }
                            editor.commit();
                            Toast.makeText(this, "Transacción completada satisfactoriamente!", Toast.LENGTH_LONG).show();

                            //Lanzar actividad list view
                            Intent intent = new Intent(this, ListActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        //Si transaccion incorrecta, informar
                        else{
                            Toast.makeText(this, "Lo sentimos, la subasta del producto ya ha finalizado", Toast.LENGTH_LONG).show();
                        }

                    }

                });

        WorkManager.getInstance(this).enqueue(trabajoPuntual);

    }

}
