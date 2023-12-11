package com.example.camilodesarrollo.abicap;

import static com.example.camilodesarrollo.abicap.MainActivity.ubicacionActual;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.camilodesarrollo.abicap.db.DbUltimaUbicacion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Ubicacion extends Activity {
    private Button btnGuardarNuevaUbicacion,btnMenuPrinc;
    private TextView tvUbicacionActual;
    private EditText edNuevaUbicacion;
    private boolean sdDesponible, sdAccesoEscritura;

    public Beeper b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        b = new Beeper();

        //DECLARACION DE COMPONENTES GRAFICOS
        //----------------------------------------------------------------------------
        btnGuardarNuevaUbicacion=(Button)findViewById(R.id.btnGuandarNuevaUbicacion);
        btnMenuPrinc=(Button)findViewById(R.id.btnMenuPrincipalLoteo);

        tvUbicacionActual=(TextView)findViewById(R.id.tvUbicacionActual);
        //tvUbicacionActual2=(TextView)findViewById(R.id.tvUbicacionActual2);

        edNuevaUbicacion=(EditText)findViewById(R.id.edNuevaUbicacion);

        //----------------------------------------------------------------------------
        verificarEstado();

        tvUbicacionActual.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());
        //tvUbicacionActual2.setText(ubicacionActual.getActual2());
        //leerArchivoInicial();
        edNuevaUbicacion.requestFocus();

        btnGuardarNuevaUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edNuevaUbicacion.getText().toString().equalsIgnoreCase("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Ubicacion.this);
                    builder.setMessage("FAVOR INGRESAR DATO")
                            .setTitle("Atención!!")
                            .setCancelable(false)
                            .setNeutralButton("Aceptar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    //reemplazarArchivo();


                    ubicacionActual.setActual1(edNuevaUbicacion.getText().toString().substring(0,3));
                    ubicacionActual.setActual2(edNuevaUbicacion.getText().toString().substring(3,6));
                    ubicacionActual.setActual3(edNuevaUbicacion.getText().toString().substring(6,9));
                    ubicacionActual.setActual4(edNuevaUbicacion.getText().toString().substring(9,12));
                    DbUltimaUbicacion db_ua = new DbUltimaUbicacion(view.getContext());
                    if(ubicacionActual.getActual4() == null){
                        db_ua.insertarUltimaUbicacion(ubicacionActual.getActual1(), ubicacionActual.getActual2(), ubicacionActual.getActual3(), ubicacionActual.getActual4());
                    }
                    else {
                        db_ua.editarUltimaUbicacion(ubicacionActual.getActual1(), ubicacionActual.getActual2(), ubicacionActual.getActual3(), ubicacionActual.getActual4());
                    }




                    Intent i = getIntent();
                    setResult(RESULT_OK, i);

                    finish();
                }
            }
        });


        btnMenuPrinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void leerArchivoInicial(){
        if (sdAccesoEscritura && sdDesponible) {
            File ruta_sd = Environment.getExternalStoragePublicDirectory("/AbitekConfi/");//Environment.DIRECTORY_DOWNLOADS);
            File f = new File(ruta_sd.getAbsolutePath(), "confubicacion.txt");
            if (f.exists()) {
                try {
                    BufferedReader bufLec =
                            new BufferedReader(new InputStreamReader(new FileInputStream(f)));

                    String tex ;
                    while (!(tex = (bufLec.readLine()).trim()).equals(null)) {
                        tvUbicacionActual.setText(tex);
                        Log.e("leyendo archivo", tex);
                    }
                    bufLec.close();

                } catch (Exception ex) {
                    Log.e("Fichero sd", "Error al escribir en la sd");
                }

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No se encuentra archivo con ubicacion actual")
                        .setTitle("Atención!!")
                        .setCancelable(false)
                        .setNeutralButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                finish();
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

    public void reemplazarArchivo() {
        if (sdAccesoEscritura && sdDesponible) {
            File ruta_sd = Environment.getExternalStoragePublicDirectory("/AbitekConfi/");
            File f = new File(ruta_sd.getAbsolutePath(), "confubicacion.txt");
            if (f.exists()) {
                try {
                    OutputStreamWriter fout =
                            new OutputStreamWriter(
                                    new FileOutputStream(f));

                    fout.write(edNuevaUbicacion.getText().toString().trim());
                    fout.close();
                } catch (Exception ex) {
                    Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
                }
            }
        }
    }

    public void cambiarUbicacion(){

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        return false;
    }
}
