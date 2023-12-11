package com.example.camilodesarrollo.abicap;

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

import com.example.camilodesarrollo.abicap.interfaces.ProductoAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LecturaBarrido extends Activity {

    private String ubicacionGlobal,codigoBarridoObtenido;
    private TextView tvUbicacionBarrido,tvCantidadBarrido,tvDescripcionProductoBarrido,tvCodigoLeidoBarridoMostrar;
    private EditText edCodigoLeidoBarrido;
    private Button btnIngresarCodigoBarrido,btnMenuPrincipalBarrido,btnCambiarUbiBarrido,btnTotalUbicacionBarrido;
    public Boolean mostrarDes,contraArch,dupli, mensajeAbierto;
    public Beeper b;
    public verificarMiscelaneos verMis;
    public verificarCheck verifChk;
    public UbicacionDato ubiDat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lectura_barrido);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN );
        mensajeAbierto = false;
        //DECLARAR INTERFAZ GRAFICA
        //-----------------------------------------------------------------------------------------------

        tvUbicacionBarrido = (TextView)findViewById(R.id.tvUbicacionBarrido);
        tvCantidadBarrido = (TextView)findViewById(R.id.tvCantidadBarrido);
        tvDescripcionProductoBarrido =(TextView)findViewById(R.id.tvDescripcionProductoBarrido);
        tvCodigoLeidoBarridoMostrar = (TextView)findViewById(R.id.tvCodigoLeidoBarridoMostrar);

        edCodigoLeidoBarrido = (EditText)findViewById(R.id.edCodigoLeidoBarrido);

        btnIngresarCodigoBarrido=(Button)findViewById(R.id.btnIngresarCodigoBarrido);
        btnMenuPrincipalBarrido=(Button)findViewById(R.id.btnMenuPrincipalBarrido);
        btnCambiarUbiBarrido=(Button)findViewById(R.id.btnCambiarUbiBarrido);
        btnTotalUbicacionBarrido=(Button)findViewById(R.id.btnTotalUbicacionBarrido);

        edCodigoLeidoBarrido.requestFocus();
        //-----------------------------------------------------------------------------------------------

        //DECLARO VARIABLES GLOBALES
        //---------------------------------------------------------------------------------------
        b = new Beeper();
        verMis = new verificarMiscelaneos();
        verifChk = new verificarCheck();
        ubiDat = new UbicacionDato();
        //---------------------------------------------------------------------------------------
        ubicacionGlobal = ubiDat.obtenerDato();
        tvUbicacionBarrido.setText(ubicacionGlobal);

        //VERIFICO MISCELANEOS
        //------------------------------------------------------------------------------------------
        mostrarDes = verifChk.checkearDescripcion(this);

        if(!mostrarDes){
            tvDescripcionProductoBarrido.setVisibility(View.GONE);
        }
        contraArch = verifChk.checkearContraArchivo(this);

        dupli = verifChk.checkearDuplicado(this);

        //------------------------------------------------------------------------------------------
       // edCodigoLeidoBarrido.requestFocus();

        btnIngresarCodigoBarrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigoObt = edCodigoLeidoBarrido.getText().toString().trim();
                String ubica = ubiDat.obtenerDato();
                if(!codigoObt.equalsIgnoreCase("")) {
                    if(dupli){
                        Boolean esDuplicado = verMis.existeRegistro(codigoObt,ubica,LecturaBarrido.this);
                        if(esDuplicado){
                            b.activarIncorrecto(LecturaBarrido.this);
                            mensaje("El codigo ya fue pistoleado","Advertencia","Aceptar");
                            edCodigoLeidoBarrido.setText("");
                        }else{
                            if(contraArch){
                                Boolean respu = verMis.existeEnMaestro(codigoObt,LecturaBarrido.this);
                                if(respu){
                                    metodoAccion(codigoObt,ubica);
                                }else{
                                    b.activarIncorrecto(LecturaBarrido.this);
                                    mensaje("No existe Codigo en Archivo","Advertencia","Aceptar");
                                    edCodigoLeidoBarrido.setText("");
                                }
                            }else {
                                metodoAccion(codigoObt,ubica);
                            }
                        }
                    }else{
                        if(contraArch){
                            Boolean respu = verMis.existeEnMaestro(codigoObt,LecturaBarrido.this);
                            if(respu){
                                metodoAccion(codigoObt,ubica);
                            }else{
                                b.activarIncorrecto(LecturaBarrido.this);
                                mensaje("No existe Codigo en Archivo","Advertencia","Aceptar");
                                edCodigoLeidoBarrido.setText("");
                            }
                        }else {
                            metodoAccion(codigoObt,ubica);
                        }
                    }

                }
            }
        });

        btnMenuPrincipalBarrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCambiarUbiBarrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LecturaBarrido.this ,Ubicacion.class);
                startActivityForResult(i,1);

            }
        });

        btnTotalUbicacionBarrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LO QUE DEBE HACER EL BOTON MOSTRAR CANTIDAD UBICACION
                ubicacionGlobal = ubiDat.obtenerDato();
                Intent i = new Intent(LecturaBarrido.this ,TotalUbicacion.class);
                i.putExtra("codigoUbicacion",ubicacionGlobal);
                startActivity(i);
            }
        });
        //edCodigoLeidoBarrido.setInputType(InputType.TYPE_NULL);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        //Toast.makeText(this, ""+ keyCode, Toast.LENGTH_SHORT).show();

        if(mensajeAbierto){
            b.activarIncorrecto(LecturaBarrido.this);
            edCodigoLeidoBarrido.setText("");
        }else{
            //edCodigoLeidoBarrido.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
            //Toast.makeText(this, "keycode: "+keyCode, Toast.LENGTH_SHORT).show();
            //if ((keyCode == 240)||(keyCode == 241) ||(keyCode == 10036))
            if ((keyCode == 290)||(keyCode == 291)||(keyCode == 292))
            {
                String codigoObt = edCodigoLeidoBarrido.getText().toString().trim();
                String ubica = ubiDat.obtenerDato();
                if(!codigoObt.equalsIgnoreCase("")) {
                    if(dupli){
                        Boolean esDuplicado = verMis.existeRegistro(codigoObt,ubica,LecturaBarrido.this);
                        if(esDuplicado){
                            b.activarIncorrecto(LecturaBarrido.this);
                            mensaje("El codigo ya fue pistoleado","Advertencia","Aceptar");

                            edCodigoLeidoBarrido.setText("");
                        }else{
                            if(contraArch){
                                Boolean respu = verMis.existeEnMaestro(codigoObt,LecturaBarrido.this);
                                if(respu){
                                    metodoAccion(codigoObt,ubica);
                                }else{
                                    b.activarIncorrecto(LecturaBarrido.this);
                                    mensaje("No existe Codigo en Archivo","Advertencia","Aceptar");
                                    edCodigoLeidoBarrido.setText("");
                                }
                            }else {
                                metodoAccion(codigoObt,ubica);
                            }
                        }
                    }else{
                        if(contraArch){
                            Boolean respu = verMis.existeEnMaestro(codigoObt,LecturaBarrido.this);
                            if(respu){
                                metodoAccion(codigoObt,ubica);
                            }else{
                                b.activarIncorrecto(LecturaBarrido.this);
                                mensaje("No existe Codigo en Archivo","Advertencia","Aceptar");
                                edCodigoLeidoBarrido.setText("");
                            }
                        }else {
                            metodoAccion(codigoObt,ubica);
                        }
                    }

                }
                edCodigoLeidoBarrido.setText("");
                return true;
            }

        }
        edCodigoLeidoBarrido.setText("");
        edCodigoLeidoBarrido.requestFocus();
        return false;

    }

    @SuppressLint("Range")
    public String buscarCodigoBarrido(String codi){
//        String descrip="SIN RESULTADOS";
        String descrip="SIN DESCRIPCION";
        try{
            SqlHelper sqlHel = new SqlHelper(LecturaBarrido.this);
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

    public void insertarBarridoEnBase(String codi,String ubic, String desc){

        try{
            SqlHelper sqlHel = new SqlHelper(LecturaBarrido.this);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

            String sql = "SELECT * FROM MAEDATOS WHERE CODIGO = '"+codi+"' AND UBICACION = '"+ubic+"'";
            Cursor c = db.rawQuery(sql,null);
            if (c.moveToNext()){
                @SuppressLint("Range") String cant = c.getString(c.getColumnIndex("cantidad"));
                Integer numFinal = Integer.parseInt(cant);
                actualizarCantidad(numFinal,codi,ubic);
            }else {
                insertarRegistroBarrido(codi,ubic, desc);
            }
            c.close();
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
    }

    public void actualizarCantidad(Integer cantidad,String codig, String ubic) {
        try {
            SqlHelper sqlHel = new SqlHelper(LecturaBarrido.this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            String resultado = String.valueOf(cantidad + 1);
            if (db != null) {
                String SQL = "update MAEDATOS SET CANTIDAD = '"+resultado+"', DESCRIPCION = '"+tvDescripcionProductoBarrido.getText().toString().trim()+"' WHERE CODIGO = '"+codig+"' AND UBICACION = '"+ubic+"'";
                db.execSQL(SQL);
                db.close();
            }
            tvCantidadBarrido.setText(resultado);
            tvCodigoLeidoBarridoMostrar.setText(codig);
        }catch(Exception e){
            Log.e("Error bd","error");
        }
    }

    public void insertarRegistroBarrido(String codig, String ubic, String desc) {
        try {
            SqlHelper sqlHel = new SqlHelper(LecturaBarrido.this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            if (db != null) {
                String SQL = "INSERT INTO MAEDATOS (UBICACION,CODIGO, DESCRIPCION, CANTIDAD) VALUES ('"+ubic+"','"+codig+"','"+desc+"','1')";
                db.execSQL(SQL);
                db.close();
            }
            tvCantidadBarrido.setText("1");
            tvCodigoLeidoBarridoMostrar.setText(codig);
        }catch(Exception e){
            Log.e("Error bd","error");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == RESULT_OK) {
            ubicacionGlobal = ubiDat.obtenerDato();
            tvUbicacionBarrido.setText(ubicacionGlobal);
            edCodigoLeidoBarrido.setText("");
            Toast.makeText(this, "Operacion Exitosa", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public void metodoAccion(String codAc,String ubiAc){
        b.activar(LecturaBarrido.this);
        String descipcion = "NO EXISTE";
        descipcion = buscarCodigoBarrido(codAc);
        tvDescripcionProductoBarrido.setText(descipcion);
        insertarBarridoEnBase(codAc, ubiAc, descipcion);
    }

    public void mensaje(String contenido, String titulo, String botonNom){
        mensajeAbierto=true;
        AlertDialog.Builder builder = new AlertDialog.Builder(LecturaBarrido.this);
        builder.setMessage(contenido)
                .setTitle(titulo)
                .setCancelable(false)
                .setNeutralButton(botonNom,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mensajeAbierto = false;

                                edCodigoLeidoBarrido.setText("");
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void editarStock(){
        Retrofit retrofit = new Retrofit.Builder()  .baseUrl("http://172.16.10.182:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ProductoAPI productoAPI = retrofit.create(ProductoAPI.class);
        Call<String> call = productoAPI.editarStock(2, "agregar");

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    //txt.setText("Codigo: " + response.code());
                    return;
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
