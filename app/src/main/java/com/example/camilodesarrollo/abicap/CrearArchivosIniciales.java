package com.example.camilodesarrollo.abicap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CrearArchivosIniciales extends AsyncTask<String, Void, Void> {
    private ProgressDialog p;
    private Context contexto;

    public CrearArchivosIniciales(Context c) {
        contexto = c;
    }

    @Override
    protected Void doInBackground(String... strings) {
        crearCarpetaRaiz();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        p.dismiss();
    }

    @Override
    protected void onPreExecute() {
        p = ProgressDialog.show(contexto, "CONFIGURACION", "VERIFICANDO CONFIGURACION INICIAL", false);
    }

    public void crearCarpetaRaiz() {
        File ruta_sd = new File(contexto.getExternalFilesDir(null), "AbitekConfi");
        //File ruta_sd = new File(contexto.getExternalFilesDir("AbitekConfi"), "");
        //File ruta_sd = new File(Environment.getExternalStorageDirectory(), "AbitekConfi");
        //File ruta_sd = Environment.getExternalStoragePublicDirectory("/AbitekConfi");
        if (!ruta_sd.exists()) {
            try {
                ruta_sd.mkdirs();
                escribirArchivosIniciales(ruta_sd);
            } catch (Exception e) {
                Log.e("Folder", "Error al crear carpeta en tarjeta SD");
            }
        } else {
            escribirArchivosIniciales(ruta_sd);
        }
    }

    public void escribirArchivosIniciales(File ruta) {
        escribirArchivo("identificador.txt", "1", ruta);
        escribirArchivo("confubicacion.txt", "0001", ruta);
        escribirArchivo("login.txt", "admin;admin", ruta);
        escribirArchivo("ipValidador.txt", "172.18.8.60:8086", ruta);
        escribirArchivo("chequeoDuplicado.txt", "false", ruta);
        escribirArchivo("validarContraArchivo.txt", "false", ruta);
        escribirArchivo("mostrarDescripcion.txt", "true", ruta);
    }

    private void escribirArchivo(String nombreArchivo, String contenido, File ruta) {
        try {
            File archivo = new File(ruta.getAbsolutePath(), nombreArchivo);
            if (!archivo.exists()) {
                OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(archivo));
                fout.write(contenido);
                fout.close();
            }
        } catch (IOException e) {
            Log.e("Ficheros", "Error al escribir el archivo en la tarjeta SD: " + e.getMessage());
        }
    }
}
