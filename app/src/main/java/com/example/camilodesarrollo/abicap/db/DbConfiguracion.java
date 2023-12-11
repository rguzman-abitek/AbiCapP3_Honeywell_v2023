package com.example.camilodesarrollo.abicap.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.camilodesarrollo.abicap.models.Configuracion;

public class DbConfiguracion extends DbHelper {

    Context context;
    public DbConfiguracion(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public Configuracion getConfiguracion(){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Configuracion configuracion = new Configuracion();
        Cursor cursorUA;

        cursorUA = db.rawQuery("SELECT ip, puerto, id_capturador FROM " + TABLA_CONF + " WHERE id = 1", null);

        if(cursorUA.moveToFirst()){
            configuracion.setIp(cursorUA.getString(0));
            configuracion.setPuerto(cursorUA.getString(1));
            configuracion.setId_capturador(cursorUA.getString(2));
        }
        cursorUA.close();
        return configuracion;
    }

    public String getIP(String ip ){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String ip_fromdb = "";
        Cursor cursorUA;

        cursorUA = db.rawQuery("SELECT ip FROM " + TABLA_CONF + " WHERE id = 1", null);

        if(cursorUA.moveToFirst()){

            ip_fromdb = cursorUA.getString(0);
        }
        cursorUA.close();
        return ip_fromdb;
    }

    public String getPuerto(String puerto){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String puerto_fromdb = "";
        Cursor cursorUA;

        cursorUA = db.rawQuery("SELECT puerto FROM " + TABLA_CONF + " WHERE id = 1", null);

        if(cursorUA.moveToFirst()){

            puerto_fromdb = cursorUA.getString(1);
        }
        cursorUA.close();
        return puerto_fromdb;
    }

    public int getIdCapturador(int id){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int id_capturador = -1;
        Cursor cursorUA;

        cursorUA = db.rawQuery("SELECT id_capturador FROM " + TABLA_CONF + " WHERE id = 1", null);

        if(cursorUA.moveToFirst()){

            id_capturador = cursorUA.getInt(1);
        }
        cursorUA.close();
        return id_capturador;
    }

    public long editarIP(String ip){
        long id = 0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLA_CONF + " SET ip = '" + ip + "' WHERE id = 1;");
        }catch (Exception ex){
            ex.toString();
        }
        return id;
    }

    public long editarPuerto(String puerto){
        long id = 0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLA_CONF + " SET puerto = '" + puerto + "' WHERE id = 1;");
        }catch (Exception ex){
            ex.toString();
        }
        return id;
    }

    public long editarIdCapturador(String id_capturador){
        long id = 0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLA_CONF + " SET id_capturador = '" + id_capturador + "' WHERE id = 1;");
        }catch (Exception ex){
            ex.toString();
        }
        return id;
    }

}
