package com.example.camilodesarrollo.abicap.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.camilodesarrollo.abicap.models.UbicacionActual;

public class DbUltimaUbicacion extends DbHelper{

    Context context;
    public DbUltimaUbicacion(@Nullable Context context) {
        super(context);
        this.context = context;
    }
    public long insertarUltimaUbicacion(String sector, String calle, String pasillo, String altura){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("sector", sector);
        contentValues.put("calle", calle);
        contentValues.put("pasillo", pasillo);
        contentValues.put("altura", altura);

        long id = db.insert(TABLA_UA, null, contentValues);
        return id;
    }

    public long editarUltimaUbicacion(String sector, String calle, String pasillo, String altura){
        long id = 0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLA_UA + " SET sector = '"+sector+"', calle='" + calle + "', pasillo = '" + pasillo + "', altura = '" + altura + "' WHERE id = 1;");
        }catch (Exception ex){
            ex.toString();
        }
        return id;
    }

    public UbicacionActual obtenerUbicacionActual(){
        DbHelper dbHelper= new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        UbicacionActual ubicacionActual = null;
        Cursor cursorUA;

        cursorUA = db.rawQuery("SELECT * FROM " + TABLA_UA + " WHERE id = 1", null);
        if(cursorUA.moveToFirst()){
            ubicacionActual = new UbicacionActual();
            ubicacionActual.setActual1(cursorUA.getString(1));
            ubicacionActual.setActual2(cursorUA.getString(2));
            ubicacionActual.setActual3(cursorUA.getString(3));
            ubicacionActual.setActual4(cursorUA.getString(4));
        }
        cursorUA.close();

        return ubicacionActual;
    }
}
