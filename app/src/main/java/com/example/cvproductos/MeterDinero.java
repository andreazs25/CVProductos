package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MeterDinero extends AppCompatActivity {
    String accion = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_dinero);
        getSupportActionBar().setTitle("METER DINERO");
         final EditText saldo=(EditText)findViewById(R.id.editDinero);
         final Button aceptar=(Button)findViewById(R.id.aceptarButton);
        final Button volver=(Button) findViewById(R.id.volverButton);

        //action listener aceptar, se actuliza su saldo y vueleve al menu
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String SALDO=saldo.getText().toString();
                //Recoger nombre de usuario y el saldo
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
                SharedPreferences.Editor editor=pref.edit();
                String usuario = pref.getString("username", "");
                SharedPreferences pref2 = getApplicationContext().getSharedPreferences("MyPref",0);
                String saldoAnterior = pref2.getString("saldo", "0");
                String nuevoSaldo = saldoAnterior+SALDO;
                editor.putString("saldo", nuevoSaldo);
                editor.commit();

                if(SALDO.length()!=0){
                    Data datos = new Data.Builder()
                            .putString("usuario", usuario)
                            .putString("saldo",nuevoSaldo)
                            .putString("funcion", "recargar").build();
                    OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(conexionDBWebService.class).setInputData(datos).build();
                    WorkManager.getInstance(getApplicationContext()).enqueue(otwr2);
                }else{
                    Toast.makeText(getApplicationContext(), "Por favor añada un saldo", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(MeterDinero.this, Menu.class);
                startActivity(intent);
                //Finalizar actividad
                finish();
            }
        });



        //action listener volver
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeterDinero.this, Menu.class);
                startActivity(intent);
                //Finalizar actividad
                finish();
            }
        });



    }





        }
