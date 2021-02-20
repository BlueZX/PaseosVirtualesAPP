package com.ubb.paseosVirtuales.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Modelo {

    public SQLiteDatabase getConn(Context context){
        ConnectionSqlLite conn = new ConnectionSqlLite(context, "dbparametros", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        return db;
    }

    public int insertParametros(Context context, ParametrosDTO dto){
        int res = 0;
        String sql = "INSERT INTO parametros (id, nombre, value) VALUES ('"+dto.getId()+"','"+dto.getNombre()+"','"+dto.getValue()+"')";
        SQLiteDatabase db = this.getConn(context);

        try{
            db.execSQL(sql);
            res = 1;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        db.close();

        return res;
    }

    public String getParametro(Context context, String nombre){
        ConnectionSqlLite conn = new ConnectionSqlLite(context, "dbparametros", null, 1);

        String res = "";

        SQLiteDatabase db = conn.getReadableDatabase();
        String[] param = { nombre };

        try {
            //select * from parametros where id=""
            Cursor cursor = db.rawQuery("SELECT * FROM parametros WHERE nombre=? ",param);

            cursor.moveToFirst();

            res =  cursor.getString(2) ;

            cursor.close();
        }
        catch (Exception e){
            e.printStackTrace();
            res = "";
        }

        db.close();

        return res;
    }

    public boolean dropTableParametros(Context context){
        SQLiteDatabase db = getConn(context);
        boolean res = false;

        try{
            db.execSQL("DROP TABLE IF EXISTS parametros");
            db.execSQL("CREATE TABLE parametros (id INTEGER, nombre TEXT, value TEXT)");
            res = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        db.close();

        return res;
    }

    public boolean updateParametro(Context context, String nombre, ParametrosDTO param){
        boolean res = false;
        SQLiteDatabase db = getConn(context);

        try{
            db.execSQL("UPDATE parametros SET value = '" + param.getValue() + "' WHERE nombre='"+ nombre+"' ");
            res = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        db.close();

        return res;
    }



}
