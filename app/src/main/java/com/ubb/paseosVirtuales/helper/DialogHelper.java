package com.ubb.paseosVirtuales.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;


public class DialogHelper {
    Context c;

    public DialogHelper(Context context) {
        c = context;
    }

    public void show(String title, String message) {
        show(title, message, null, null);
    }

    public void show(String title, String message, DialogInterface.OnClickListener listenerOK, DialogInterface.OnClickListener listenerNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, listenerOK)
                .setNegativeButton(android.R.string.no, listenerNo);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void toast(String message, int length) {
        Toast.makeText(c, message, length).show();
    }

    public void toast(String message) {
        toast(message, Toast.LENGTH_LONG);
    }
}
