package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().setTitle("Login");
        final EditText Email=(EditText) findViewById(R.id.editLogin);
        final EditText clave=(EditText) findViewById(R.id.editClave);
        final Button entrar=(Button) findViewById(R.id.login);
        final Button registro=(Button) findViewById(R.id.registro);
        //fallo al conectarse a internet

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registro=new Intent(Login.this, Registro.class);
                Login.this.startActivity(registro);
                finish();
            }
        });
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creamos dos variables para guardar textos de entrada
                final String EMAIL = Email.getText().toString();
                final String CLAVE = clave.getText().toString();
                //Enviamos al metodo Login las variables de entrada
                login(EMAIL,CLAVE);
            }
        });


    }

    //Este metodo lo usamos para conectarnos a la BD y saber si el login es correcto
    private void login(final String email, final String password){
        //IP bridge entre Android y el PC para conectarnos a localhost
        String LOGIN_REQUEST_URL = "https://134.209.235.115/mmerayo002/WEB/proyecto3/login2.php";

        // JSON data
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("email", email);
            jsonObject.put("clave", password);
            System.out.println(jsonObject.toString());
        } catch (JSONException e){
            e.printStackTrace();
        }

        // Json request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                LOGIN_REQUEST_URL,
                jsonObject,
                new Response.Listener<JSONObject>(){
                    private static final String TAG = "Respuesta";
                    @Override
                    public void onResponse(JSONObject response){
                        try{
                            //Usa la respuesta JSONObject en este log
                            Log.d(TAG, response.getString("success"));
                            boolean success = response.getBoolean("success");
                            if (success) {
                                System.out.println("Logeado!!");
                                Intent i=new Intent (Login.this,formulario.class);
                                Login.this.startActivity(i);
                            }
                        } catch (JSONException e) {
                            System.out.println("Error logging in");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                    Toast.makeText(Login.this, "Can't connect to Internet. Please check your connection.", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof ServerError) {
                    Toast.makeText(Login.this, "Unable to login. Either the username or password is incorrect.", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof ParseError) {
                    Toast.makeText(Login.this, "Parsing error. Please try again.", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof NoConnectionError) {
                    Toast.makeText(Login.this, "Can't connect to internet. Please check your connection.", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof TimeoutError) {
                    Toast.makeText(Login.this, "Connection timed out. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }

                //Do other stuff if you want
                error.printStackTrace();
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3600,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
