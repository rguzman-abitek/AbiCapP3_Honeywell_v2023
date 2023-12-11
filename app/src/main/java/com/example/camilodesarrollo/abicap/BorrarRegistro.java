package com.example.camilodesarrollo.abicap;

import static com.example.camilodesarrollo.abicap.MainActivity.ubicacionActual;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BorrarRegistro extends Activity {

    private String ubicacionGlobal,codigoBarridoObtenido;
    private TextView tvUbicacionBorrado,tvCantidadBorrado,tvCodigoLeidoBorrado,tvDescripcionBorrado;
    private EditText edCodigoLeidoBorrado;
    private Button btnCambiarUbiBorrado,btnIngresarCodigoBorrado,btnBorrarRegistro,btnMenuPrincipalBorrado,btnTotalUbicacionBorrado;
    public Boolean mostrarDes,contraArch,dupli;
    public Beeper b;
    public verificarMiscelaneos verMis;
    public verificarCheck verifChk;
    public UbicacionDato ubiDat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_registro);

        //DEFINICION INTERFAZ GRAFICA
        //------------------------------------------------------------------------------------------

        tvUbicacionBorrado = (TextView)findViewById(R.id.tvUbicacionBorrado);
        tvCantidadBorrado = (TextView)findViewById(R.id.tvCantidadBorrado);
        tvCodigoLeidoBorrado = (TextView)findViewById(R.id.tvCodigoLeidoBorrado);
        tvDescripcionBorrado = (TextView)findViewById(R.id.tvDescripcionBorrado);

        edCodigoLeidoBorrado = (EditText)findViewById(R.id.edCodigoLeidoBorrado);

        btnCambiarUbiBorrado = (Button)findViewById(R.id.btnCambiarUbiBorrado);
        btnIngresarCodigoBorrado = (Button)findViewById(R.id.btnIngresarCodigoBorrado);
        btnBorrarRegistro = (Button)findViewById(R.id.btnBorrarRegistro);
        btnMenuPrincipalBorrado = (Button)findViewById(R.id.btnMenuPrincipalBorrado);
        btnTotalUbicacionBorrado = (Button)findViewById(R.id.btnTotalUbicacionBorrado);


        edCodigoLeidoBorrado.requestFocus();
        //------------------------------------------------------------------------------------------
        //DECLARO VARIABLES GLOBALES
        //---------------------------------------------------------------------------------------
        b = new Beeper();
        verMis = new verificarMiscelaneos();
        verifChk = new verificarCheck();
        ubiDat = new UbicacionDato();
        //---------------------------------------------------------------------------------------

        ubicacionGlobal = ubiDat.obtenerDato();
        //tvUbicacionBorrado.setText(ubicacionGlobal);
        tvUbicacionBorrado.setText(ubicacionActual.getActual1() + "-" + ubicacionActual.getActual2());

        //VERIFICO MISCELANEOS
        //------------------------------------------------------------------------------------------
         mostrarDes = verifChk.checkearDescripcion(this);

        if(!mostrarDes){
            tvDescripcionBorrado.setVisibility(View.GONE);
        }
         contraArch = verifChk.checkearContraArchivo(this);

         dupli = verifChk.checkearDuplicado(this);


        //------------------------------------------------------------------------------------------
        btnIngresarCodigoBorrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigoObt = edCodigoLeidoBorrado.getText().toString().trim();
                String ubica = ubiDat.obtenerDato();
                if (!codigoObt.equalsIgnoreCase("")) {
                    b.activar(BorrarRegistro.this);
                    metodoAccion(codigoObt, ubica);
                }
            }
        });

        btnCambiarUbiBorrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BorrarRegistro.this ,Ubicacion.class);
                startActivityForResult(i,1);
            }
        });

        btnMenuPrincipalBorrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnBorrarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvCantidadBorrado.getText().toString().trim().equalsIgnoreCase("0")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(BorrarRegistro.this);
                    builder.setMessage("Favor Ingresar un Código Existente")
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
                }else{
                    if(!tvCodigoLeidoBorrado.getText().toString().equalsIgnoreCase("")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(BorrarRegistro.this);
                        builder.setMessage("¿Esta seguro que desea eliminar este registro?")
                                .setTitle("Advertencia")
                                .setCancelable(false)
                                .setNegativeButton("Cancelar",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        })
                                .setPositiveButton("Aceptar",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String codigoLeido = tvCodigoLeidoBorrado.getText().toString().trim();
                                                String ubicacionFinal = ubiDat.obtenerDato();
                                                limpiarRegistro(codigoLeido,ubicacionFinal);
                                                edCodigoLeidoBorrado.setText("");
                                                tvCantidadBorrado.setText("0");
                                                tvCodigoLeidoBorrado.setText("");
                                                tvDescripcionBorrado.setText("");
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        });

        btnTotalUbicacionBorrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LO QUE DEBE HACER EL BOTON MOSTRAR CANTIDAD UBICACION
                ubicacionGlobal = ubiDat.obtenerDato();
                Intent i = new Intent(BorrarRegistro.this ,TotalUbicacion.class);
                i.putExtra("codigoUbicacion",ubicacionGlobal);
                startActivity(i);
            }
        });
    }

    @SuppressLint("Range")
    public String buscarCodigo(String codi){
        String descrip="SIN RESULTADOS";
        try{
            SqlHelper sqlHel = new SqlHelper(BorrarRegistro.this);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

            String sql = "SELECT * FROM MAEPRODUCTOS WHERE CODIGO = '"+codi+"'";
            Cursor c = db.rawQuery(sql,null);
            while (c.moveToNext()){
                descrip = c.getString(c.getColumnIndex("descripcion"));
            }
            c.close();
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
        return descrip;
    }

    @SuppressLint("Range")
    public String traerCantidad(String codi, String ubic){
        String respuesta = "0";
        try{
            SqlHelper sqlHel = new SqlHelper(BorrarRegistro.this);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

            String sql = "SELECT * FROM MAEDATOS WHERE CODIGO = '"+codi+"' AND UBICACION = '"+ubic+"'";
            Cursor c = db.rawQuery(sql,null);
            if (c.moveToNext()){
                respuesta = c.getString(c.getColumnIndex("cantidad"));
            }else {
                respuesta = "0";
            }
            c.close();
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
        return respuesta;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        String codigoObt = edCodigoLeidoBorrado.getText().toString().trim();
        String ubica = ubiDat.obtenerDato();

        if ((keyCode == 290)||(keyCode == 291)||(keyCode == 292))
        {
            if (!codigoObt.equalsIgnoreCase("")) {
                b.activar(this);
                metodoAccion(codigoObt, ubica);
            }
            edCodigoLeidoBorrado.setText("");
            edCodigoLeidoBorrado.requestFocus();
            return true;
        }
        edCodigoLeidoBorrado.setText("");
        edCodigoLeidoBorrado.requestFocus();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == RESULT_OK) {
            ubicacionGlobal = ubiDat.obtenerDato();
            //tvUbicacionBorrado.setText(ubicacionGlobal);
            tvUbicacionBorrado.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());

            edCodigoLeidoBorrado.setText("");
            Toast.makeText(this, "Operacion Exitosa", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void limpiarRegistro(String cod, String ubi){
        try{
            SqlHelper sqlHel = new SqlHelper(this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            if(db!= null){
                String SQL = "DELETE FROM MAEDATOS WHERE UBICACION = '"+ubi+"' AND CODIGO = '"+cod+"'";
                db.execSQL(SQL);
            }
            db.close();
            Toast.makeText(this, "Operacion Exitosa", Toast.LENGTH_SHORT)
                    .show();
        }catch (Exception e){
            Toast.makeText(this, "Problemas al Eliminar", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void metodoAccion(String cod, String ubica){
        String descipcion = "NO EXISTE";
        descipcion = buscarCodigo(cod);
        tvDescripcionBorrado.setText(descipcion);
        String cantidadProd = traerCantidad(cod, ubica);
        tvCantidadBorrado.setText(cantidadProd);
        tvCodigoLeidoBorrado.setText(cod);
        edCodigoLeidoBorrado.setText("");
    }

    public void mensaje(String contenido, String titulo, String botonNom){
        AlertDialog.Builder builder = new AlertDialog.Builder(BorrarRegistro.this);
        builder.setMessage(contenido)
                .setTitle(titulo)
                .setCancelable(false)
                .setNeutralButton(botonNom,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
