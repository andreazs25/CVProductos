package com.example.proyecto3;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;

public class conexionDBWebService extends Worker {
    public conexionDBWebService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String funcion = getInputData().getString("funcion");
        if(funcion.equals("subirProducto")){
            String usuario = getInputData().getString("usuario");
            String producto = getInputData().getString("producto");
            String precioInicial = getInputData().getString("precioInicial");
            String precioCompra = getInputData().getString("precioCompra");
            String fechaHora = getInputData().getString("fechahora");

            Log.i("Estado", "Dentro");

            HttpsURLConnection urlConnection = GeneradorConexionesSeguras.getInstance().crearConexionSegura(getApplicationContext(), "https://134.209.235.115/mmerayo002/WEB/proyecto3/añadirProducto.php");
            JSONObject parametrosJSON = new JSONObject();
            try {
                parametrosJSON.put("usuario", usuario);
                parametrosJSON.put("producto", producto);
                parametrosJSON.put("foto", usuario+"_"+producto);
                parametrosJSON.put("precioInicial", precioInicial);
                parametrosJSON.put("precioCompra", precioCompra);
                parametrosJSON.put("fechahora", fechaHora);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(parametrosJSON.toString());
                out.close();
                Log.i("Conexion: ", "Enviada");
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            int statusCode = 0;
            try {
                statusCode = urlConnection.getResponseCode();
                Log.i("status: ", " " + statusCode);
                if (statusCode == 200) {
                    Log.i("resultado: ", "producto subido");
                    return Result.success();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (funcion.equals("subirImagen")){

            String fotoen64 = "";
            String usuario = getInputData().getString("usuario");
            String producto = getInputData().getString("producto");
            String imagenPre = getInputData().getString("foto");
            //Pasar de String a Uri
            Uri imagen = Uri.parse(imagenPre);
            //Cargar imagen desde el URI a Bitmap
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imagen);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Convertir bitmap a string en Base64
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //Formato JPEG y ajustar calidad para reducir tamaños
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] fototransformada = stream.toByteArray();
            fotoen64 = Base64.encodeToString(fototransformada, Base64.NO_WRAP);
            Log.i("fotoen64", fotoen64);

            String parametros = "";
            String name = usuario+"_"+producto;
            //parametros = "name=" + name + "&imagen=" + fotoen64;
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("imagen", fotoen64);
            parametros = builder.build().getEncodedQuery();

            HttpsURLConnection urlConnection = GeneradorConexionesSeguras.getInstance().crearConexionSegura(getApplicationContext(), "https://134.209.235.115/mmerayo002/WEB/proyecto3/subirImagen.php");
            try {

                //Establecer las opciones de la petición
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                //Incluir parámetros
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(parametros);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int statusCode = 0;
            try {
                statusCode = urlConnection.getResponseCode();
                Log.i("status: ", " " + statusCode);
                if (statusCode == 200) {
                    Log.i("resultado: ", "imagen subida");
                    return Result.success();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(funcion.equals("obtenerProductosEnVentaUsuario")){

            String usuario = getInputData().getString("usuario");

            Log.i("Estado", "Dentro");

            HttpsURLConnection urlConnection = GeneradorConexionesSeguras.getInstance().crearConexionSegura(getApplicationContext(), "https://134.209.235.115/mmerayo002/WEB/proyecto3/obtenerProductosUsuario.php");
            JSONObject parametrosJSON = new JSONObject();
            try {
                parametrosJSON.put("usuario", usuario);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(parametrosJSON.toString());
                out.close();
                Log.i("Conexion: ", "Enviada");
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            int statusCode = 0;
            try {
                statusCode = urlConnection.getResponseCode();
                Log.i("status: ", " " + statusCode);
                if (statusCode == 200) {
                    Log.i("resultado: ", "productos obtenidos");
                    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line, result = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    inputStream.close();
                    Log.i("respuesta:", " "+result);

                    JSONParser parser = new JSONParser();
                    JSONArray json = (JSONArray) parser.parse(result);
                    String[] productos = new String[json.size()];
                    for(int i=0; i<json.size(); i++){
                        productos[i]= String.valueOf(json.get(i));
                    }
                    Data resultados = new Data.Builder()
                            .putStringArray("productos", productos)
                            .build();
                    return Result.success(resultados);

                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

        return Result.failure();
    }
}
