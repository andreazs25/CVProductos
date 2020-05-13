package com.example.cvproductos;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class RegistroDialog extends DialogFragment {


    String user, pass, token;

    //La constructora debe estar vacía
    public RegistroDialog() {

    }

    //Gestionar parámetros
    public static RegistroDialog newInstance(String user, String pass, String token) {
        RegistroDialog dial = new RegistroDialog();
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("pass", pass);
        args.putString("token", token);
        dial.setArguments(args);
        return dial;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        user = getArguments().getString("user");
        pass = getArguments().getString("pass");
        token = getArguments().getString("token");
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Usuario incorrecto");
        builder.setMessage("El usuario introducido no existe. ¿Desea registrarse?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Botón OK: registrar usuario y cerrar diálogo
                addUsuario(user, pass, token);
                //Proceso de log in
                procesoLogIn(user, "0");
                Log.i("LOGIN", "DESEA TRUE");
                //Lanzar actividad pantalla principal del usuario
                Intent intent = new Intent(getActivity(), Menu.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Botón Cancelar: cerrar diálogo
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private void procesoLogIn(String user, String saldo) {
        //Proceso de login
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", user);
        editor.putString("saldo", saldo);
        editor.commit();
        //Notificación de bienvenida
        String welcome = "Registro completado satisfactoriamente. " + '\n' + "Bienvenid@! ," + user;
        Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void addUsuario(String user, String pass, String token){
        //Establecer parámetros de la conexión
        Data datos = new Data.Builder()
                .putString("user",user)
                .putString("pass",pass)
                .putString("funcion","insertar")
                .putString("accion","login")
                .putString("token",token)
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
        WorkManager.getInstance(getActivity()).enqueue(trabajoPuntual);
    }

}
