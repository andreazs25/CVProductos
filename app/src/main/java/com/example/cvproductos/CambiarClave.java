package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_clave);

        volver=findViewById(R.id.cvolver);
        aceptar=findViewById(R.id.caceptar);
        ediclave=findViewById(R.id.edinuevaClave);
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
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
                SharedPreferences.Editor editor=pref.edit();
                String usuario = pref.getString("username", "");
                SharedPreferences pref2 = getSharedPreferences("MyPref", 0);
                String clave=pref2.getString("pass","");
                SharedPreferences.Editor editor2 = pref2.edit();
                editor.putString("username", usuario);
                editor.putString("pass", clave);
                editor.commit();

                if(NUEVACLAVE.length()!=0){
                    Data datos = new Data.Builder()
                            .putString("usuario", usuario)
                            .putString("pass",clave)
                            .putString("funcion", "cambiar").build();
                    OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(conexionDBWebService.class).setInputData(datos).build();
                    WorkManager.getInstance().enqueue(otwr2);
                }else{
                    Toast.makeText(getApplicationContext(), "Por favor añada un saldo", Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(CambiarClave.this, Menu.class);
                startActivity(intent);
                //Finalizar actividad
                finish();
            }
        });

    }
}
