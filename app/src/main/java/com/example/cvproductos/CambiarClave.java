package com.example.cvproductos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CambiarClave extends AppCompatActivity {
private Button volver;
private Button aceptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_clave);
        volver=findViewById(R.id.cvolver);
        aceptar=findViewById(R.id.caceptar);
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
                //lamaremos al login si es correcto procedemos a actualizar la nueva clave y volvemos al menu

                Intent intent = new Intent(CambiarClave.this, Menu.class);
                startActivity(intent);
                //Finalizar actividad
                finish();
            }
        });

    }
}
