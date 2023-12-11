package com.example.camilodesarrollo.abicap.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NOMBRE = "abicap_db.db";
    public static final String TABLA_UA = "t_ultima_ubicacion";
    public static final String TABLA_CONF = "t_configuracion";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLA_UA + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sector TEXT NOT NULL," +
                "calle TEXT NOT NULL," +
                "pasillo TEXT NOT NULL," +
                "altura TEXT NOT NULL)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLA_CONF + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ip TEXT NOT NULL," +
                "puerto TEXT NOT NULL," +
                "id_capturador TEXT NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE " + TABLA_UA);
        sqLiteDatabase.execSQL("DROP TABLE " + TABLA_CONF);
        onCreate(sqLiteDatabase);
    }
}
