package com.ubb.paseosVirtuales.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class ConnectionSqlLite extends SQLiteOpenHelper {

    final String TBL_PARAMETERS = "CREATE TABLE parametros (id INTEGER, nombre TEXT, value TEXT)";

    public ConnectionSqlLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_PARAMETERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS parametros");
        onCreate(db);
    }
}
