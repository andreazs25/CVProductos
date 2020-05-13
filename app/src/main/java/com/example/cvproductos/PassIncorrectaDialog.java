package com.example.cvproductos;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class PassIncorrectaDialog extends DialogFragment {

    public PassIncorrectaDialog(){

    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Contraseña incorrecta");
        builder.setMessage("La contraseña introducida para dicho usuario no es correcta. Por favor, inténtelo de nuevo.");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Botón Aceptar: Cerrar diálogo
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
