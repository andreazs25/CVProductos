package com.example.proyecto3;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.Nullable;

import java.util.Calendar;

public class DialogoHora extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Calendar calendario=Calendar.getInstance();
        int hora=calendario.get(Calendar.HOUR_OF_DAY);
        int minuto=calendario.get(Calendar.MINUTE);
        TimePickerDialog eldialogo= new TimePickerDialog(getActivity(),this, hora,minuto, DateFormat.is24HourFormat(getActivity()));
        return eldialogo;
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String min="";
        if (minute < 10) {
            min="0"+String.valueOf(minute);
        }else{
            min = String.valueOf(minute);
        }
        String hora = String.valueOf(hourOfDay)+":"+min;
        NuevoProducto.setHora(hora);
    }
}
