package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CambiarClave extends AppCompatActivity {
private Button volver;
private Button aceptar;
private EditText ediclave;
private EditText ediclave2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_clave);

        volver=findViewById(R.id.cvolver);
        aceptar=findViewById(R.id.caceptar);
        ediclave=findViewById(R.id.editText7);
        ediclave2=findViewById(R.id.edinuevaClave);
        //action listener volver
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CambiarClave.this, Menu.class);
                startActivity(intent);
                //Finalizar actividad
                finish();
            }
        });
        //action listener aceptar ,actualizara la clave de ususuario en la BD
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String NUEVACLAVE=ediclave.getText().toString();
                final String NUEVACLAVE2=ediclave2.getText().toString();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
                String usuario = pref.getString("username", "");

                if(NUEVACLAVE.length()!=0 && (NUEVACLAVE.equals(NUEVACLAVE2))){
                    //Establecer parámetros de la conexión
                    Data datos = new Data.Builder()
                            .putString("name",usuario)
                            .putString("pass",NUEVACLAVE)
                            .putString("accion","cambiarPass")
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

                    WorkManager.getInstance(getApplicationContext()).enqueue(trabajoPuntual);

                    Intent intent = new Intent(CambiarClave.this, Menu.class);
                    startActivity(intent);
                    //Finalizar actividad
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Por favor inserte una nueva contraseña, rellenando bien los dos campos", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        //Volver al menu
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
        //Finalizar actividad
        finish();
    }

}