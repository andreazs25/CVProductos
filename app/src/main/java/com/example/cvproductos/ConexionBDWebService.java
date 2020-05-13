package com.example.cvproductos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;

public class ConexionBDWebService extends Worker {

    public ConexionBDWebService(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Do the work here--in this case, upload the images.

        //Recoger parámetro de la acción a realizar
        String accion = getInputData().getString("accion");

        //Recoger otros parametros
        String name = getInputData().getString("name");

        //Realizar conexión, escogiendo el fichero correspondiente a la acción
        HttpsURLConnection urlConnection = GeneradorConexionesSeguras.getInstance()
                .crearConexionSegura(getApplicationContext(),
                        "https://134.209.235.115/mmerayo002/WEB/proyecto3/" + accion + ".php");

        String parametros;

        if(accion.equals("puja") || accion.equals("compra")){
            //Recoger campo id producto
            int id = getInputData().getInt("id", 0);

            //Establecer parametros
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("id", String.valueOf(id));
            parametros = builder.build().getEncodedQuery();
        }
        else if(accion.equals("login")){
            //Recoger campos pass, token y funcion
            String pass = getInputData().getString("pass");
            String token = getInputData().getString("token");
            String funcion = getInputData().getString("funcion");
            String user = getInputData().getString("user");

            //Establecer parametros
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("user", user)
                    .appendQueryParameter("pass", pass)
                    .appendQueryParameter("token", token)
                    .appendQueryParameter("funcion", funcion);
            parametros = builder.build().getEncodedQuery();
        }

        else{
            //Establecer parametros
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", name);
            parametros = builder.build().getEncodedQuery();
        }



        Log.i("CONEXION: params", parametros);
        Log.i("CONEXION: accion", accion);

        try {

            //Establecer las opciones de la petición
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Incluir parámetros
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //Obtener código de respuesta
            int statusCode = urlConnection.getResponseCode();

            //Si la conexión ha sido correcta, obtener respuesta
            String line, result = "";
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
            }

            Log.i("CONEXION: statusCode", String.valueOf(statusCode));
            Log.i("CONEXION: result", result);
            Log.i("CONEXION: resultSize", String.valueOf(result.length()));

            //Convertir imagen
            if(accion.equals("download_img")) {
                //Obtener parametros id y ultimo
                int id = getInputData().getInt("id", 0);
                boolean ultimo = getInputData().getBoolean("ultimo", false);

                String img64 = result;
                //Decodificar string en base64
                byte[] decodedString = Base64.decode(img64, Base64.DEFAULT);
                Log.i("IMG DOWNLOAD: byte", decodedString.toString());

                //Convertir array de bytes en Bitmap
                Bitmap elBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                //Establecer imagen
                GestorProductos gestorProductos = GestorProductos.getGestorProductos();
                gestorProductos.buscarProducto(id).setFoto(elBitmap);

                result = String.valueOf(ultimo);
            }

            //Almacenar respuesta
            Data resultados = new Data.Builder()
                    .putString("resultado", result)
                    .build();

            //Devolver respuesta
            return Result.success(resultados);

        } catch (java.net.ProtocolException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        // Indicate whether the task finished successfully with the Result
        return Result.failure();
    }

}
