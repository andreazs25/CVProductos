package com.example.cvproductos;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;

public class CompraDialog extends DialogFragment {

    String username, accion, precioI, precioC;
    int id;
    GestorProductos gestorProductos = GestorProductos.getGestorProductos();

    //La constructora debe estar vacía
    public CompraDialog() {

    }

    //Gestionar parámetros
    public static CompraDialog newInstance(String accion, int id, String precioI, String precioC) {
        CompraDialog dial = new CompraDialog();
        Bundle args = new Bundle();
        args.putString("accion", accion);
        args.putInt("id", id);
        args.putString("precioI", precioI);
        args.putString("precioC", precioC);
        dial.setArguments(args);
        return dial;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Recoger argumentos
        accion = getArguments().getString("accion");
        precioI = getArguments().getString("precioI");
        precioC = getArguments().getString("precioC");
        id = getArguments().getInt("id");
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String titulo = "";
        String mensaje = "";
        if(accion.equals("puja")){
            titulo = "Pujar";
            mensaje = "¿Estás segur@ de que deseas pujar por el producto? Se incrementará un 10% del precio de compra";
        }
        if(accion.equals("compra")){
            titulo = "Comprar";
            mensaje = "¿Estás segur@ de que deseas comprar el producto directamente?";
        }
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton(titulo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Botón OK
                ((VerProductoActivity)((Activity)getContext())).realizarTransaccion();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Botón Cancelar
                dialog.dismiss();
            }
        });
        return builder.create();
    }


}
