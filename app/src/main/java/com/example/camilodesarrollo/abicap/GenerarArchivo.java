package com.example.camilodesarrollo.abicap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class GenerarArchivo extends Activity {

    private Button btnGenerarArchivoF,btnMenuPrincipalG;
    private ProgressBar pgb;
    private TextView tvProgeso,tvEstatico;
    private ArrayList<String> listaUbicacion,ListaCodigo,ListaDescripcion,ListaCantidad;
    private boolean sdDesponible, sdAccesoEscritura;
    private ProgressDialog p;
    private Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_archivo);
        verificarEstado();

        //CAPA GRAFICA
        //----------------------------------------------------------------------------------------
        btnGenerarArchivoF = (Button)findViewById(R.id.btnGenerarArchivoF);
        btnMenuPrincipalG = (Button)findViewById(R.id.btnMenuPrincipalG);

        pgb = (ProgressBar)findViewById(R.id.pgb);

        tvProgeso = (TextView)findViewById(R.id.tvProgeso);
        tvEstatico = (TextView)findViewById(R.id.tvEstatico);

        //----------------------------------------------------------------------------------------

        leerRegistros leer = new leerRegistros();
        leer.execute();

        btnMenuPrincipalG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnGenerarArchivoF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarPermisos();
            }
        });
    }

    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    private void verificarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes realizar las operaciones de escritura
                crearArchivoInv crear = new crearArchivoInv();
                crear.execute();
            } else {
                // Permiso denegado, muestra un mensaje o realiza alguna acción
                String chao = "false"; //esto es solo para el debug....
            }
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

    private class leerRegistros extends AsyncTask<String,Void,Void> {

        @SuppressLint("Range")
        @Override
        protected Void doInBackground(String... params) {
            listaUbicacion = new ArrayList<String>();
            ListaCodigo = new ArrayList<String>();
            ListaDescripcion = new ArrayList<String>();
            ListaCantidad = new ArrayList<String>();

//
            try{
                SqlHelper sqlHel = new SqlHelper(GenerarArchivo.this);
                SQLiteDatabase db = sqlHel.getReadableDatabase();

                String sql = "SELECT * FROM MAEDATOS ORDER BY UBICACION";
                Cursor c = db.rawQuery(sql,null);
                while (c.moveToNext()){
                    listaUbicacion.add(c.getString(c.getColumnIndex("ubicacion")));
                    ListaCodigo.add(c.getString(c.getColumnIndex("codigo")));
                    ListaDescripcion.add(c.getString(c.getColumnIndex("descripcion")));
                    ListaCantidad.add(c.getString(c.getColumnIndex("cantidad")));
                }
                c.close();
                db.close();
            }catch (Exception ex) {
                Log.e("Fichero sd", "Error");
            }
/*
//LUZAGRO
            try{
                SqlHelper sqlHel = new SqlHelper(GenerarArchivo.this);
                SQLiteDatabase db = sqlHel.getReadableDatabase();



                String sql = "SELECT MAEDATOS.ubicacion AS ubicacion, MAEDATOS.codigo AS codigo, MAEPRODUCTOS.descripcion AS descripcion, MAEDATOS.cantidad AS cantidad FROM MAEPRODUCTOS INNER JOIN MAEDATOS ON MAEPRODUCTOS.codigo=MAEDATOS.codigo ORDER BY MAEDATOS.ubicacion";

                Cursor c = db.rawQuery(sql,null);
                while (c.moveToNext()){
                    listaUbicacion.add(c.getString(c.getColumnIndex("ubicacion")));
                    ListaCodigo.add(c.getString(c.getColumnIndex("codigo")));
                    ListaDescripcion.add(c.getString(c.getColumnIndex("descripcion")));
                    ListaCantidad.add(c.getString(c.getColumnIndex("cantidad")));
                }
                c.close();
                db.close();
            }catch (Exception ex) {
                Log.e("Fichero sd", "Error");
            }*/

            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            tvEstatico.setText(Integer.toString(listaUbicacion.size()));
            p.dismiss();
        }

        @Override
        protected void onPreExecute(){
            p = ProgressDialog.show(GenerarArchivo.this, "PROCESANDO", "LEYENDO REGISTROS", false);
        }

    }

    //CAMILO AQUI HAY QUE ENTRAR A PICAR

    private class crearArchivoInv extends AsyncTask<String,Integer,Void> {

        @Override
        protected Void doInBackground(String... params) {

            if (sdAccesoEscritura && sdDesponible) {
                String idCapt = new BuscarIdCapt().obtenerDato();
                try {
                    File ruta_sd = new File(contexto.getExternalFilesDir(null), "AbitekConfi");
                    //File ruta_sd = Environment.getExternalStorageDirectory();
                    File f = new File(ruta_sd.getAbsolutePath(), "inventario"+idCapt+".csv");
                    if (f.exists()) {
                        f.delete();
                    }
                    OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
                    fout.append("UBICACION" + ";" + "CODIGO"+ ";" + "DESCRIPCION"+ ";" +"CANTIDAD" + "\n");
                    for (int x=0;x<listaUbicacion.size();x++){
                        String dato = listaUbicacion.get(x).trim();
                        fout.append(listaUbicacion.get(x) + ";" + ListaCodigo.get(x)+ ";" + ListaDescripcion.get(x) + ";" + ListaCantidad.get(x) + "\n");
                        publishProgress(1+x);
                    }
                    fout.close();
                }catch(Exception e){
                    Log.e("Escribir","error al excribir en sd");
                }
            }else {

            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            AlertDialog.Builder builder = new AlertDialog.Builder(GenerarArchivo.this);
            builder.setMessage("Carga de datos lista")
                    .setTitle("Listo")
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

        @Override
        protected void onPreExecute(){
            pgb.setMax(listaUbicacion.size());
            pgb.setProgress(0);
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            pgb.setProgress(values[0]);
            tvProgeso.setText(Integer.toString(values[0]));
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        return false;
    }



}
