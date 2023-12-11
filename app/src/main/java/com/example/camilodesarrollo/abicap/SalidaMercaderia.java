package com.example.camilodesarrollo.abicap;

import static com.example.camilodesarrollo.abicap.MainActivity.configuracion;
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

import com.example.camilodesarrollo.abicap.interfaces.DatosAPI;
import com.example.camilodesarrollo.abicap.models.Datos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SalidaMercaderia extends Activity {

    private String ubicacionGlobal,codigoBarridoObtenido;
    private TextView tvUbicacionBarrido,tvCantidadBarrido,tvDescripcionProductoBarrido,tvCodigoLeidoBarridoMostrar;
    private EditText edCodigoLeidoBarrido;
    private Button btnIngresarCodigoBarrido,btnMenuPrincipalBarrido,btnCambiarUbiBarrido,btnTotalUbicacionBarrido;
    public Boolean mostrarDes,contraArch,dupli;
    public Beeper b;
    public verificarMiscelaneos verMis;
    public verificarCheck verifChk;
    public UbicacionDato ubiDat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salida_mercaderia);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN );
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
        //tvUbicacionBarrido.setText(ubicacionGlobal);
        tvUbicacionBarrido.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());

        //VERIFICO MISCELANEOS
        //------------------------------------------------------------------------------------------
        mostrarDes = verifChk.checkearDescripcion(this);

        if(!mostrarDes){
            tvDescripcionProductoBarrido.setVisibility(View.GONE);
        }
        contraArch = verifChk.checkearContraArchivo(this);

        dupli = verifChk.checkearDuplicado(this);

        //-----------------------------------------------------------------------------------------

        btnIngresarCodigoBarrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigoObt = edCodigoLeidoBarrido.getText().toString().trim();
                String ubicacion = ubicacionActual.getActual1()+ubicacionActual.getActual2()+ubicacionActual.getActual3()+ubicacionActual.getActual4();
                getDatoporCodigo(codigoObt, ubicacion);


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
                Intent i = new Intent(SalidaMercaderia.this ,Ubicacion.class);
                startActivityForResult(i,1);

            }
        });

        btnTotalUbicacionBarrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LO QUE DEBE HACER EL BOTON MOSTRAR CANTIDAD UBICACION
                ubicacionGlobal = ubiDat.obtenerDato();
                Intent i = new Intent(SalidaMercaderia.this ,TotalUbicacion.class);
                i.putExtra("codigoUbicacion",ubicacionGlobal);
                startActivity(i);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        tvUbicacionBarrido.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        edCodigoLeidoBarrido.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        //Toast.makeText(this, "keycode: "+keyCode, Toast.LENGTH_SHORT).show();
        //if ((keyCode == 240)||(keyCode == 241) ||(keyCode == 10036))
        //if (keyCode == 288)
        if ((keyCode == 290)||(keyCode == 291)||(keyCode == 292))
        {
            String codigoObt = edCodigoLeidoBarrido.getText().toString().trim();
            String ubica = ubiDat.obtenerDato();
            if(!codigoObt.equalsIgnoreCase("")) {
                if(dupli){
                    Boolean esDuplicado = verMis.existeRegistro(codigoObt,ubica,SalidaMercaderia.this);
                    if(esDuplicado){
                        b.activarIncorrecto(SalidaMercaderia.this);
                        mensaje("El codigo ya fue pistoleado","Advertencia","Aceptar");
                    }else{
                        if(contraArch){
                            Boolean respu = verMis.existeEnMaestro(codigoObt,SalidaMercaderia.this);
                            if(respu){
                                metodoAccion(codigoObt,ubica);
                            }else{
                                b.activarIncorrecto(SalidaMercaderia.this);
                                mensaje("No existe Codigo en Archivo","Advertencia","Aceptar");
                            }
                        }else {
                            metodoAccion(codigoObt,ubica);
                        }
                    }
                }else{
                    if(contraArch){
                        Boolean respu = verMis.existeEnMaestro(codigoObt,SalidaMercaderia.this);
                        if(respu){
                            metodoAccion(codigoObt,ubica);
                        }else{
                            b.activarIncorrecto(SalidaMercaderia.this);
                            mensaje("No existe Codigo en Archivo","Advertencia","Aceptar");
                        }
                    }else {
                        metodoAccion(codigoObt,ubica);
                    }
                }

            }
            edCodigoLeidoBarrido.setText("");

            return true;
        }
        edCodigoLeidoBarrido.setText("");
        edCodigoLeidoBarrido.requestFocus();
        return false;
    }

    @SuppressLint("Range")
    public String buscarCodigoBarrido(String codi){
        String descrip="SIN RESULTADOS";
        try{
        SqlHelper sqlHel = new SqlHelper(SalidaMercaderia.this);
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

    public void insertarBarridoEnBase(String codi,String ubic){

        try{
            SqlHelper sqlHel = new SqlHelper(SalidaMercaderia.this);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

            String sql = "SELECT * FROM MAEDATOS WHERE CODIGO = '"+codi+"' AND UBICACION = '"+ubic+"'";
            Cursor c = db.rawQuery(sql,null);
            if (c.moveToNext()){
                @SuppressLint("Range") String cant = c.getString(c.getColumnIndex("cantidad"));
                Integer numFinal = Integer.parseInt(cant);
                actualizarCantidad(numFinal,codi,ubic);
            }else {
                insertarRegistroBarrido(codi,ubic);
            }
            c.close();
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
    }

    public void actualizarCantidad(Integer cantidad,String codig, String ubic) {
        try {
            SqlHelper sqlHel = new SqlHelper(SalidaMercaderia.this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            Integer cantidadActualizada = (cantidad-1);
            String resultado = String.valueOf(cantidadActualizada);
            if ((db != null) && (cantidadActualizada>=0)) {
                String SQL = "update MAEDATOS SET CANTIDAD = '"+resultado+"' WHERE CODIGO = '"+codig+"' AND UBICACION = '"+ubic+"'";
                db.execSQL(SQL);
                db.close();
                tvCantidadBarrido.setText(resultado);
            }else{
                Toast.makeText(this, "No hay Stock!", Toast.LENGTH_SHORT).show();
                tvCantidadBarrido.setText("0");
            }
            tvCodigoLeidoBarridoMostrar.setText(codig);
            }catch(Exception e){
                Log.e("Error bd","error");
            }
        }


    public void insertarRegistroBarrido(String codig, String ubic) {
        try {
            SqlHelper sqlHel = new SqlHelper(SalidaMercaderia.this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            if (db != null) {
                String SQL = "INSERT INTO MAEDATOS (UBICACION,CODIGO,CANTIDAD) VALUES ('"+ubic+"','"+codig+"','1')";
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
            //tvUbicacionBarrido.setText(ubicacionGlobal);
            tvUbicacionBarrido.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());

            edCodigoLeidoBarrido.setText("");
            Toast.makeText(this, "Operacion Exitosa", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public void metodoAccion(String codAc,String ubiAc){
            b.activar(SalidaMercaderia.this);
            String descipcion = "NO EXISTE";
            descipcion = buscarCodigoBarrido(codAc);
            tvDescripcionProductoBarrido.setText(descipcion);
            insertarBarridoEnBase(codAc, ubiAc);
    }

    public void mensaje(String contenido, String titulo, String botonNom){
        AlertDialog.Builder builder = new AlertDialog.Builder(SalidaMercaderia.this);
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

    public void getDatoporCodigo(String codigo, String ubicacion){
        Retrofit retrofit = new Retrofit.Builder()  .baseUrl("http://"+configuracion.getIp()+":"+ configuracion.getPuerto() +"/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DatosAPI datosAPI = retrofit.create(DatosAPI.class);
        Call<List<Datos>> call = datosAPI.getDatosPorCodigo(codigo, ubicacion);

        call.enqueue(new Callback<List<Datos>>() {
            @Override
            public void onResponse(Call<List<Datos>> call, Response<List<Datos>> response) {
                if(!response.isSuccessful()){
                    Log.e("RESPONSE",  response.message());
                    return;
                }
                List<Datos> d = response.body();
                tvCodigoLeidoBarridoMostrar.setText(d.get(0).getCodigo().toString());
                tvCantidadBarrido.setText(String.valueOf(d.get(0).getCantidad() - 1));
                String ubicacion = ubicacionActual.getActual1()+ubicacionActual.getActual2()+ubicacionActual.getActual3()+ubicacionActual.getActual4();
                editarCantidad(d.get(0).getCodigo(), d.get(0).getCantidad()-1, ubicacion);
            }

            @Override
            public void onFailure(Call<List<Datos>> call, Throwable t) {

            }
        });
    }

    public void editarCantidad(String codigo, int cantidad, String ubicacion){
        Retrofit retrofit = new Retrofit.Builder()  .baseUrl("http://"+configuracion.getIp()+":"+ configuracion.getPuerto() +"/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DatosAPI datosAPI = retrofit.create(DatosAPI.class);
        Call<String> call = datosAPI.editarCantidadDato(codigo, cantidad, ubicacion, configuracion.getId_capturador());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    Log.e("RESPONSE",  response.message());
                    return;
                }
                String resp = response.body();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
