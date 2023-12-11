package com.example.camilodesarrollo.abicap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Miguel Bustamante on 04/07/2023.
 * */

public class ObtenerLicencia {

    public Boolean obtenerLicencia(Context cont){
        try{
            SqlHelper sqlHel = new SqlHelper(cont);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

            String sql = "SELECT * FROM MAEUBICACION WHERE CODIGO = 'VALIDADO'";
            Cursor c = db.rawQuery(sql,null);
            if (c.moveToNext()){
                return true;
            }
            c.close();
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
        return true;
        //return false;
    }

}
