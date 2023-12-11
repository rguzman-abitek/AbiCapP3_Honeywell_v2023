package com.example.camilodesarrollo.abicap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Miguel Bustamante on 04/07/2023.
 */

public class verificarMiscelaneos {

    public Boolean existeEnMaestro(String codi, Context contesto){
        String descrip="SIN RESULTADOS";
        Boolean devolucion = false;
        try{
            SqlHelper sqlHel = new SqlHelper(contesto);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

            String sql = "SELECT * FROM MAEPRODUCTOS WHERE CODIGO = '"+codi+"'";
            Cursor c = db.rawQuery(sql,null);
            if (c.moveToNext()){
                devolucion = true;
            }
            c.close();
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
        return devolucion;
    }

    @SuppressLint("Range")
    public Boolean existeRegistro(String codi, String ubi, Context contesto){
        String descrip="SIN RESULTADOS";
        String cantidad="0";
        Boolean devolucion = false;
        try{
            SqlHelper sqlHel = new SqlHelper(contesto);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

            String sql = "SELECT * FROM MAEDATOS WHERE CODIGO = '"+codi+"' AND UBICACION = '"+ubi+"'";
            Cursor c = db.rawQuery(sql,null);
            if (c.moveToNext()){
                cantidad = c.getString(c.getColumnIndex("cantidad"));
                try {
                    if (Integer.parseInt(cantidad) > 0) {
                        devolucion =  true;
                    }
                }catch (Exception e){
                    devolucion = false;
                }
            }
            c.close();
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
        return devolucion;
    }
}
