package com.example.camilodesarrollo.abicap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CargaMaestro extends Activity {
    ProgressBar pgb;
    private Button btnLeerArchivo,btnGuardar,btnMenuPrincipal;
    private TextView tvCantidadRegistros,tvNumeroFinal,tvNumeroCarga;
    private boolean sdDesponible, sdAccesoEscritura;
    private ArrayList<String> listaCargar1,listaCargar2;
    private ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_maestro);
        verificarEstado();

        //DECLARAMOS COMPONENTES GRAFICOS

        pgb = (ProgressBar)findViewById(R.id.progressBar);

        btnLeerArchivo = (Button)findViewById(R.id.btnLeerArchivo);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        btnMenuPrincipal = (Button)findViewById(R.id.btnMenuPrincipalLoteo);

        tvCantidadRegistros = (TextView)findViewById(R.id.tvCantidadRegistros);
        tvNumeroFinal = (TextView)findViewById(R.id.tvNumeroFinal);
        tvNumeroCarga = (TextView)findViewById(R.id.tvNumeroCarga);
        //-------------------------------------------------------------------------------

        btnLeerArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerRegistros lec = new leerRegistros();
                lec.execute();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarRegistros c = new cargarRegistros();
                c.execute();
            }
        });

        btnMenuPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class leerRegistros extends AsyncTask<String,Void,Void> {

        @SuppressLint("LongLogTag")
        @Override
        protected Void doInBackground(String... params) {
            if (sdAccesoEscritura && sdDesponible) {//     /sdcard/AbiConf.csv
                File ruta_sd = Environment.getExternalStorageDirectory();
                File f = new File(ruta_sd.getAbsolutePath(), "AbiConf.txt");
                if (f.exists()) {
                    try {
                        listaCargar1 = new ArrayList<String>();
                        listaCargar2 = new ArrayList<String>();
                        BufferedReader bufLec =
                                new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                        String tex ;
                        String codMaes;
                        String descripMaes;
                        Integer pro =0;
                        while (!(tex = (bufLec.readLine()).trim()).equals(null)) {
                            if(tex.contains(";")) {
                                String[] dividir = tex.split(";");
                                try {
                                    codMaes = dividir[0].trim();
                                }catch (Exception ex){
                                    codMaes = "SINREGISTRO";
                                }
                                try {
                                    descripMaes = dividir[1].trim();
                                }catch (Exception ex){
                                    descripMaes = "SINREGISTRO";
                                }
                                listaCargar1.add(codMaes);
                                listaCargar2.add(descripMaes);
                                pro++;
                                Log.e("leyendo archivo numero: "+Integer.toString(pro)+" /", tex);
                            }
                        }
                        bufLec.close();
                    } catch (Exception ex) {
                        Log.e("Fichero sd", "Error al escribir en la sd");
                    }

                } else {
                    //cambiar Toast a mensaje
                    Toast toast = Toast.makeText(getApplicationContext(), "No existe archivo dentro del equipo", Toast.LENGTH_SHORT);
                    toast.show();

                }
            }else{
                //cambiar Toast a mensaje
                Toast toast = Toast.makeText(getApplicationContext(), "No posees permisos necesarios", Toast.LENGTH_SHORT);
                toast.show();
            }

            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            tvCantidadRegistros.setText(Integer.toString(listaCargar1.size()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date());
            //textView13.setText(currentTimeStamp);
            p.dismiss();
        }

        @Override
        protected void onPreExecute(){

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date());
            //textView12.setText(currentTimeStamp);
            p = ProgressDialog.show(CargaMaestro.this, "PROCESANDO", "LEYENDO REGISTROS", false);
        }

    }

    public void verificarEstado(){
        String estado = Environment.getExternalStorageState();

        if(estado.equals(Environment.MEDIA_MOUNTED))
        {
            sdDesponible = true;
            sdAccesoEscritura = true;
        }
        else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
        {
            sdDesponible = true;
            sdAccesoEscritura = false;

        }else{
            sdDesponible = false;
            sdAccesoEscritura = false;
        }
    }


    private class cargarRegistros extends AsyncTask<String,Integer,Void> {

        @Override
        protected Void doInBackground(String... params) {
            try{

                SqlHelper sqlHel = new SqlHelper(CargaMaestro.this);
                SQLiteDatabase db = sqlHel.getWritableDatabase();
                if (db != null) {
                    for(int i = 0; i < listaCargar1.size();i++){

                        String SQL = "INSERT INTO MAEPRODUCTOS (codigo,descripcion) VALUES ('" + listaCargar1.get(i).replace("'","") + "','" + listaCargar2.get(i).replace("'","") + "')";
                        try {
                        db.execSQL(SQL);
                        }catch (SQLiteException ex){
                           AlertDialog.Builder builder = new AlertDialog.Builder(CargaMaestro.this);
                           builder.setMessage("El Registro numero "+ i+1 +" tiene problemas")
                                   .setTitle("Advertencia")
                                   .setCancelable(false)
                                   .setNeutralButton("Aceptar",
                                           new DialogInterface.OnClickListener() {
                                               public void onClick(DialogInterface dialog, int id) {
                                                   dialog.cancel();
                                               }
                                           });
                           AlertDialog alert = builder.create();
                           alert.show();
                        }
                        publishProgress(i+1);
                    }

                }
                db.close();
            }catch (Exception ex){
                Log.e(ex.toString(), "Error");
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
                tvCantidadRegistros.setText("0");
                pgb.setProgress(0);
            tvNumeroCarga.setText("0");
            tvNumeroFinal.setText("0");

            btnLeerArchivo.setVisibility(View.VISIBLE);
            btnMenuPrincipal.setVisibility(View.VISIBLE);
            btnGuardar.setVisibility(View.VISIBLE);

            AlertDialog.Builder builder = new AlertDialog.Builder(CargaMaestro.this);
            builder.setMessage("Carga de datos lista")
                    .setTitle("Listo")
                    .setCancelable(false)
                    .setNeutralButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            
        }

        @Override
        protected void onPreExecute(){
            btnLeerArchivo.setVisibility(View.GONE);
            btnMenuPrincipal.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);

            limpiarTabla();
            pgb.setMax(listaCargar1.size());
            tvNumeroFinal.setText(Integer.toString(listaCargar1.size()));
            pgb.setProgress(0);
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            pgb.setProgress(values[0]);
            tvNumeroCarga.setText(Integer.toString(values[0]));
        }

    }

    public void limpiarTabla(){
        try {
            SqlHelper sqlHel = new SqlHelper(this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            if (db != null) {
                String SQL = "DELETE FROM MAEPRODUCTOS";
                db.execSQL(SQL);

                String SQL2 = "UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='MAEPRODUCTOS'";
                db.execSQL(SQL2);
            }
            db.close();
        }catch (Exception ex){
            Toast toast = Toast.makeText(getApplicationContext(), "ERROR CON LA BD AL LIMPIAR", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        return false;
    }


}
