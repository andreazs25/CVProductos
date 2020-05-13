package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MeterDinero extends AppCompatActivity {
    private Activity actividadactual = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_dinero);
        getSupportActionBar().setTitle("METER DINERO");
          final EditText email=(EditText)findViewById(R.id.editEmail);
         final EditText clave=(EditText) findViewById(R.id.editClave);
         final EditText saldo=(EditText)findViewById(R.id.editDinero);
         final Button aceptar=(Button)findViewById(R.id.aceptarButton);
        final Button volver=(Button) findViewById(R.id.volverButton);

        //action listener aceptar,se comprueba su email y se actuliza su saldo y vueleve al menu
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String EMAIL = email.getText().toString();
                final String CLAVE = clave.getText().toString();
                final String SALDO=saldo.getText().toString();
                //Enviamos al metodo Login las variables de
                AccesoInternet internet = new AccesoInternet(actividadactual);
                System.out.println("verficamos el acceso a  intenet"+internet.verificarConexion());
                internet.handleSSLHandshake();
                //saldo(EMAIL,CLAVE,SALDO);
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
