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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilodesarrollo.abicap.interfaces.DatosAPI;
import com.example.camilodesarrollo.abicap.models.Datos;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LecturaLoteo extends Activity {

    private String ubicacionGlobal,codigoBarridoObtenido;
    private TextView tvUbicacionLoteo,tvCantidadLote,tvDescripcionProductoLoteo,tvTotalCantidad;
    private EditText edCodigoLeidoLoteo,edCantidadLoteo, edComentarioLoteo;
    private Button  btnIngresarCodigoLoteo,
                    btnMenuPrincipalLoteo,
                    btnCambiarUbiLoteo,
                    btnTotalUbicacionLoteo,
                    btnAgregarRegistro;
    public Boolean mostrarDes,contraArch,dupli, mensajeAbierto;
    public Beeper b;
    public verificarMiscelaneos verMis;
    public verificarCheck verifChk;
    public UbicacionDato ubiDat;

    public Boolean existe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura_loteo);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN );
        mensajeAbierto = false;

        //DECLARAR INTERFAZ GRAFICA
        //-----------------------------------------------------------------------------------------------

        tvUbicacionLoteo = (TextView)findViewById(R.id.tvUbicacionLoteo);
        tvCantidadLote = (TextView)findViewById(R.id.tvCantidadLote);
        tvDescripcionProductoLoteo =(TextView)findViewById(R.id.tvDescripcionProductoLoteo);
        tvTotalCantidad = (TextView)findViewById(R.id.tvTotalCantidad);

        edCodigoLeidoLoteo = (EditText)findViewById(R.id.edCodigoLeidoLoteo);
        edCantidadLoteo = (EditText)findViewById(R.id.edCantidadLoteo);
        edComentarioLoteo = findViewById(R.id.editComentario);
        btnIngresarCodigoLoteo=(Button)findViewById(R.id.btnIngresarCodigoLoteo);
        btnMenuPrincipalLoteo=(Button)findViewById(R.id.btnMenuPrincipalLoteo);
        btnCambiarUbiLoteo=(Button)findViewById(R.id.btnCambiarUbiLoteo);
        btnTotalUbicacionLoteo=(Button)findViewById(R.id.btnTotalUbicacionLoteo);
        btnAgregarRegistro = (Button)findViewById(R.id.btnAgregarRegistro);

        edCodigoLeidoLoteo.requestFocus();
        //-----------------------------------------------------------------------------------------------
        //DECLARO VARIABLES GLOBALES
        //---------------------------------------------------------------------------------------
        b = new Beeper();
        verMis = new verificarMiscelaneos();
        verifChk = new verificarCheck();
        ubiDat = new UbicacionDato();
        //---------------------------------------------------------------------------------------
        //VERIFICO MISCELANEOS
        //------------------------------------------------------------------------------------------
        mostrarDes = verifChk.checkearDescripcion(this);

        if(!mostrarDes){
            tvDescripcionProductoLoteo.setVisibility(View.GONE);
        }
        contraArch = verifChk.checkearContraArchivo(this);

        dupli = verifChk.checkearDuplicado(this);

        //------------------------------------------------------------------------------------------

        edCodigoLeidoLoteo.requestFocus();

        tvUbicacionLoteo.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());


        btnCambiarUbiLoteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LecturaLoteo.this ,Ubicacion.class);
                startActivityForResult(i,1);
            }
        });

        btnIngresarCodigoLoteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigoObt = edCodigoLeidoLoteo.getText().toString().trim();
                String ubicacion = ubicacionActual.getActual1()+ubicacionActual.getActual2()+ubicacionActual.getActual3()+ubicacionActual.getActual4();
                getDatoporCodigo(codigoObt, ubicacion);
                if(tvDescripcionProductoLoteo.getText().toString().equals("No existe producto.")){
                    Toast toast = Toast.makeText(getApplicationContext(), "No existe producto.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        btnAgregarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer totalInsertar = 0;
                if (edCantidadLoteo.getText().toString().equalsIgnoreCase("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LecturaLoteo.this);
                    mensaje("Debe ingresar una cantidad","Atención","Aceptar");
                } else {
                    if (edCodigoLeidoLoteo.getText().toString().equalsIgnoreCase("")) {
                        mensaje("Debe ingresar un código","Atención","Aceptar");
                    } else {

                        if (Integer.parseInt(tvTotalCantidad.getText().toString().trim()) < 1) {
                            mensaje("FAVOR INGRESAR UN VALOR MAYOR A 0","Atención","Aceptar");
                        } else {
                            totalInsertar = Integer.parseInt(tvTotalCantidad.getText().toString().trim());
                            int nueva_cantidad = Integer.valueOf(edCantidadLoteo.getText().toString()) + Integer.valueOf(tvCantidadLote.getText().toString());
                            String codigo = edCodigoLeidoLoteo.getText().toString();
                            String ubicacion = ubicacionActual.getActual1() + ubicacionActual.getActual2() + ubicacionActual.getActual3() + ubicacionActual.getActual4();
                            String comentario = "";
                            Log.e("TAG", String.valueOf(edComentarioLoteo.getText().toString().trim().length()));
                            if(edComentarioLoteo.getText().toString().trim().length() > 0){
                                comentario = edComentarioLoteo.getText().toString();
                                editarCantidad(codigo, nueva_cantidad, ubicacion, comentario);
                                Toast toast = Toast.makeText(getApplicationContext(), "OPERACION EXITOSA", Toast.LENGTH_SHORT);
                                toast.show();
                                limpiarGrafica();
                            }else{
                                editarCantidad(codigo, nueva_cantidad, ubicacion, " ");
                                Toast toast = Toast.makeText(getApplicationContext(), "OPERACION EXITOSA", Toast.LENGTH_SHORT);
                                toast.show();
                                limpiarGrafica();
                            }
                        }
                    }
                }
            }
        });

        btnTotalUbicacionLoteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ubicacionGlobal = ubiDat.obtenerDato();
                Intent i = new Intent(LecturaLoteo.this ,TotalUbicacion.class);
                i.putExtra("codigoUbicacion",ubicacionGlobal);
                startActivity(i);
            }
        });

        btnMenuPrincipalLoteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edCantidadLoteo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Integer cantidadInicial = 0;
                Integer cantidadNueva = 0;
                Integer total = 0;

                cantidadInicial = Integer.parseInt(tvCantidadLote.getText().toString().trim());
                if(edCantidadLoteo.getText().toString().trim().equalsIgnoreCase("")){
                    cantidadNueva = 0;
                }else{
                    cantidadNueva = Integer.parseInt(edCantidadLoteo.getText().toString().trim());
                }
                total = cantidadInicial + cantidadNueva;
                tvTotalCantidad.setText(Integer.toString(total));

            }
        });

        //edCodigoLeidoLoteo.setInputType(InputType.TYPE_NULL);
        //edCantidadLoteo.setInputType(InputType.TYPE_NULL);


    }

    @Override
    public void onResume() {
        super.onResume();
        tvUbicacionLoteo.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == RESULT_OK) {
            ubicacionGlobal = ubiDat.obtenerDato();
            tvUbicacionLoteo.setText(ubicacionGlobal);
            edCodigoLeidoLoteo.setText("");
            edCantidadLoteo.setText("");
            Toast.makeText(this, "Operacion Exitosa", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @SuppressLint("Range")
    public String buscarCodigoLoteo(String codi){
        String descrip="SIN RESULTADOS";
        try{
            SqlHelper sqlHel = new SqlHelper(LecturaLoteo.this);
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

    public void insertarLoteoEnBase(String codi,String ubic,String descrip, String cantidad){

        try{
            SqlHelper sqlHel = new SqlHelper(LecturaLoteo.this);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

            String sql = "SELECT * FROM MAEDATOS WHERE CODIGO = '"+codi+"' AND UBICACION = '"+ubic+"'";
            Cursor c = db.rawQuery(sql,null);
            if (c.moveToNext()){
                actualizarCantidad(codi,ubic,descrip, cantidad);
            }else {
                insertarRegistroLote(codi,ubic,descrip,cantidad);
            }
            c.close();
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
    }

    public void actualizarCantidad(String codig, String ubic,String descrip, String totalCantidad) {
        try {
            SqlHelper sqlHel = new SqlHelper(LecturaLoteo.this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            if (db != null) {
                String SQL = "update MAEDATOS SET CANTIDAD = '"+totalCantidad+"', DESCRIPCION = '"+descrip+"' WHERE CODIGO = '"+codig+"' AND UBICACION = '"+ubic+"'";
                db.execSQL(SQL);
                db.close();
            }
        }catch(Exception e){
            Log.e("Error bd","error");
        }
    }


    public void insertarRegistroLote(String codig, String ubic,String descripcion, String cantidad) {
        try {
            SqlHelper sqlHel = new SqlHelper(LecturaLoteo.this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            if (db != null) {
                //String SQL = "INSERT INTO MAEDATOS (UBICACION,CODIGO,DESCRIPCION, CANTIDAD) VALUES ('"+ubic+"','"+codig+"','"+descripcion+"','"+cantidad+"')";
                String SQL = "INSERT INTO MAEDATOS (UBICACION,CODIGO,CANTIDAD) VALUES ('"+ubic+"','"+codig+"','"+cantidad+"')";
                db.execSQL(SQL);
                db.close();
            }
        }catch(Exception e){
            Log.e("Error bd","error");
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event)
    {

        if(mensajeAbierto){
            b.activarIncorrecto(LecturaLoteo.this);
            edCodigoLeidoLoteo.setText("");
        }else{
        String codigoObt = edCodigoLeidoLoteo.getText().toString().trim();
        String ubica = ubiDat.obtenerDato();
            //if (keyCode == 288)
            if ((keyCode == 290)||(keyCode == 291)||(keyCode == 292))
            {

                if(!codigoObt.equalsIgnoreCase("")) {
                    if(dupli){
                        Boolean esDuplicado = verMis.existeRegistro(codigoObt,ubica,LecturaLoteo.this);
                        if(esDuplicado){
                            b.activarIncorrecto(LecturaLoteo.this);
                            mensaje("El codigo ya fue pistoleado","Advertencia","Aceptar");
                            edCodigoLeidoLoteo.setText("");
                        }else{
                            if(contraArch){
                                Boolean respu = verMis.existeEnMaestro(codigoObt,LecturaLoteo.this);
                                if(respu){
                                    metodoAccion(codigoObt,ubica);
                                }else{
                                    b.activarIncorrecto(LecturaLoteo.this);
                                    mensaje("No existe Codigo en Archivo","Advertencia","Aceptar");
                                    edCodigoLeidoLoteo.setText("");
                                }
                            }else {
                                metodoAccion(codigoObt,ubica);
                            }
                        }
                    }else{
                        if(contraArch){
                            Boolean respu = verMis.existeEnMaestro(codigoObt,LecturaLoteo.this);
                            if(respu){
                                metodoAccion(codigoObt,ubica);
                            }else{
                                b.activarIncorrecto(LecturaLoteo.this);
                                mensaje("No existe Codigo en Archivo","Advertencia","Aceptar");
                                edCodigoLeidoLoteo.setText("");
                            }
                        }else {
                            metodoAccion(codigoObt,ubica);
                        }
                    }

                }
                return true;
            }
        }
        return false;
    }

    public void limpiarGrafica(){
        edCodigoLeidoLoteo.setText("");
        tvCantidadLote.setText("0");
        tvTotalCantidad.setText("0");
        tvDescripcionProductoLoteo.setText("xx");
        edCodigoLeidoLoteo.requestFocus();
        edCantidadLoteo.setText("");
        edComentarioLoteo.setText("");
    }

    public void metodoAccion(String codAc,String ubiAc){
            b.activar(this);
            BuscarCantidad busc = new BuscarCantidad();
            String cantidad = busc.obtenerCantidadProducto(LecturaLoteo.this,codAc,ubiAc);
            tvCantidadLote.setText(cantidad);

            String descipcion = "NO EXISTE";
            descipcion = buscarCodigoLoteo(codAc);
            tvDescripcionProductoLoteo.setText(descipcion);
            edCantidadLoteo.requestFocus();

    }

    public void mensaje(String contenido, String titulo, String botonNom){
        mensajeAbierto = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(LecturaLoteo.this);
        builder.setMessage(contenido)
                .setTitle(titulo)
                .setCancelable(false)
                .setNeutralButton(botonNom,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mensajeAbierto = false;

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

                tvCantidadLote.setText(d.get(0).getCantidad().toString());
                edComentarioLoteo.setText(d.get(0).getComentario().toString());
                tvDescripcionProductoLoteo.setText(d.get(0).getDescripcion().toString());

                if(tvDescripcionProductoLoteo.getText().toString().equals("El producto no existe.")){
                    Toast toast = Toast.makeText(getApplicationContext(), "El producto no existe.", Toast.LENGTH_SHORT);
                    toast.show();
                    edCodigoLeidoLoteo.setText("");
                }

            }

            @Override
            public void onFailure(Call<List<Datos>> call, Throwable t) {

            }
        });
    }

    public void editarCantidad(String codigo, int cantidad, String ubicacion, String comentario){
        Retrofit retrofit = new Retrofit.Builder()  .baseUrl("http://"+configuracion.getIp()+":"+ configuracion.getPuerto() +"/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DatosAPI datosAPI = retrofit.create(DatosAPI.class);

        Call<List<Datos>> call_get = datosAPI.getDatosPorCodigo(codigo, ubicacion);

        call_get.enqueue(new Callback<List<Datos>>() {
            @Override
            public void onResponse(Call<List<Datos>> call, Response<List<Datos>> response) {
                if(!response.isSuccessful()){
                    Toast toast = Toast.makeText(getApplicationContext(), "Servidor no encontrado", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                String content = "";
                List<String> sa = new ArrayList<String>();
                List<Datos> d = response.body();
                for (Datos datos: d) {
                    sa.add(datos.getDescripcion());
                    sa.add(String.valueOf(datos.getCantidad()));
                    content += "Id: " + datos.getId() + "\n";
                    content += "Código: " + datos.getCodigo() + "\n";
                    content += "Descripción: " + datos.getDescripcion() + "\n";
                    content += "Cantidad: " + datos.getCantidad() + "\n";
                    content += "Comentario: " + datos.getComentario() + "\n";
                    content += "id_capturador: " + datos.getId_capturador() + "\n\n";
                    //txt.append(content);
                    content = "";
                }
            }

            @Override
            public void onFailure(Call<List<Datos>> call, Throwable t) {

            }
        });

        Call<String> call = datosAPI.editarDatos(codigo, cantidad, ubicacion, comentario, configuracion.getId_capturador());

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
