package com.example.cvproductos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {


    boolean existe, coincide;
    String token;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /////////////
        token = "abc";

        existe = false;
        coincide = false;

        //Guardar en variables los elementos de la vista
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);

        //Listener de los campos de texto
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Al menos un caracter en cada campo
                boolean usernameText = usernameEditText.getText().toString().length() > 0;
                boolean passwordText = passwordEditText.getText().toString().length() > 0;
                boolean isReady = usernameText && passwordText;
                loginButton.setEnabled(isReady);
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        //Action Listener del botón de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LOGIN", "click");
                //Recoger los campos del formulario
                String user = usernameEditText.getText().toString();
                String pass = passwordEditText.getText().toString();

                //Comprobar si existe el usuario
                comprobarExisteUsuario(user, pass);

                usernameEditText.getText().clear();
                passwordEditText.getText().clear();
            }
        });

    }

    private void procesoLogIn(String user, String saldo) {
        //Proceso de login
        SharedPreferences pref = getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", user);
        editor.putString("saldo", saldo);
        editor.commit();
        //Notificación de bienvenida
        String welcome = "Bienvenid@! ," + user;
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void mostrarDialogoRegistro(String user, String pass) {
        //Usar fragment manager para mostrar el diálogo
        FragmentManager fm = getSupportFragmentManager();
        RegistroDialog alertDialog = RegistroDialog.newInstance(user, pass, token);
        alertDialog.show(fm, "fragment_alert");
    }

    private void mostrarDialogoPass() {
        //Usar fragment manager para mostrar el diálogo
        FragmentManager fm = getSupportFragmentManager();
        PassIncorrectaDialog alertDialog = new PassIncorrectaDialog();
        alertDialog.show(fm, "fragment_alert");
    }

    private void checkDeseaRegistrarse(String user, String pass) {
        mostrarDialogoRegistro(user, pass);
        Log.i("LOGIN", "AFTER DIALOG");
    }

    private void comprobarExisteUsuario(String user, String pass) {
        //Establecer parámetros de la conexión
        Data datos = new Data.Builder()
                .putString("user",user)
                .putString("pass",pass)
                .putString("funcion","existe")
                .putString("accion","login")
                .putString("token","0")
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
                        existe = status.getOutputData().getString("resultado").equals("true");
                        //ejecutar proceso según el resultado
                        existe(user, pass);
                    }
                });

        WorkManager.getInstance(this).enqueue(trabajoPuntual);
    }

    private void comprobarCredenciales(String user, String pass) {

        //Establecer parámetros de la conexión
        Data datos = new Data.Builder()
                .putString("user",user)
                .putString("pass",pass)
                .putString("funcion","coincide")
                .putString("accion","login")
                .putString("token",token)
                .build();

        Log.i("TOKEN", token);

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
                        coincide = !resultado.equals("false");
                        //ejecutar proceso según el resultado
                        coincide(user, pass, resultado);
                    }
                });

        WorkManager.getInstance(this).enqueue(trabajoPuntual);
    }

    public void existe(String user, String pass){
        // Si existe
        if(existe) {
            comprobarCredenciales(user, pass);
        }
        // Si no existe
        else {
            //Preguntar mediante diálogo si desea registrarse
            Log.i("LOGIN", "no existe");
            checkDeseaRegistrarse(user, pass);
        }

    }

    public void coincide(String user, String pass, String saldo){
        // Si coincide
        if(coincide) {
            Log.i("LOGIN", "existe y correcto");
            procesoLogIn(user, saldo);
            //Lanzar siguiente actividad
            finalizarActividad();
        }
        // Si no coincide
        else {
            //Informar mediante diálogo que la contraseña es incorrecta
            Log.i("LOGIN", "existe pero no correcto");
            mostrarDialogoPass();
        }
    }


    private void finalizarActividad(){
        //Lanzar actividad pantall principal del usuario
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
        //Finalizar actividad
        finish();
    }
}
