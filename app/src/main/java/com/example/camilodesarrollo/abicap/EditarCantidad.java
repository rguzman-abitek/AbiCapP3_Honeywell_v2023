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

public class EditarCantidad extends Activity {

    private TextView tvUbicacionEditar,tvCantidadEditar,tvCodigoLeidoEditar,tvDescripcionEditar;
    private Button btnCambiarUbiEditar,btnIngresarCodigoEditar,btnEditarCantidad,btnMenuPrincipalEditar,btnTotalUbicacionEditar;
    private EditText edCodigoCapturado,edCantidadEditar;
    private String ubicacionGlobal,codigoBarridoObtenido;
    public Beeper b;
    public verificarMiscelaneos verMis;
    public verificarCheck verifChk;
    public UbicacionDato ubiDat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cantidad);

        //INTERFAZ GRAFICA
        //------------------------------------------------------------------------------------------------

        tvUbicacionEditar = (TextView)findViewById(R.id.tvUbicacionEditar);
        tvCantidadEditar = (TextView)findViewById(R.id.tvCantidadEditar);
        tvCodigoLeidoEditar = (TextView)findViewById(R.id.tvCodigoLeidoEditar);
        tvDescripcionEditar = (TextView)findViewById(R.id.tvDescripcionEditar);

        btnCambiarUbiEditar = (Button)findViewById(R.id.btnCambiarUbiEditar);
        btnIngresarCodigoEditar = (Button)findViewById(R.id.btnIngresarCodigoEditar);
        btnEditarCantidad = (Button)findViewById(R.id.btnEditarCantidad);
        btnMenuPrincipalEditar = (Button)findViewById(R.id.btnMenuPrincipalEditar);
        btnTotalUbicacionEditar = (Button)findViewById(R.id.btnTotalUbicacionEditar);

        edCodigoCapturado = (EditText)findViewById(R.id.edCodigoCapturado);
        edCantidadEditar = (EditText)findViewById(R.id.edCantidadEditar);


        edCodigoCapturado.requestFocus();
        //------------------------------------------------------------------------------------------------
        //DECLARO VARIABLES GLOBALES
        //---------------------------------------------------------------------------------------
        b = new Beeper();
        verMis = new verificarMiscelaneos();
        verifChk = new verificarCheck();
        ubiDat = new UbicacionDato();
        //---------------------------------------------------------------------------------------

        ubicacionGlobal = ubiDat.obtenerDato();
        //tvUbicacionEditar.setText(ubicacionGlobal);
        tvUbicacionEditar.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());

        btnCambiarUbiEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditarCantidad.this ,Ubicacion.class);
                startActivityForResult(i,1);
            }
        });

        btnIngresarCodigoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ubicacion = ubicacionActual.getActual1()+ubicacionActual.getActual2()+ubicacionActual.getActual3()+ubicacionActual.getActual4();
                getDatoporCodigo(edCodigoCapturado.getText().toString(), ubicacion);
            }
        });

        btnMenuPrincipalEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnTotalUbicacionEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ubicacionGlobal = ubiDat.obtenerDato();
                Intent i = new Intent(EditarCantidad.this ,TotalUbicacion.class);
                i.putExtra("codigoUbicacion",ubicacionGlobal);
                startActivity(i);
            }
        });

        btnEditarCantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvCodigoLeidoEditar.getText().toString().trim().equalsIgnoreCase("")){
                    mensaje("Favor Ingresar o Capturar Código","Atención","Aceptar");
                }else{
                    if(tvCantidadEditar.getText().toString().trim().equalsIgnoreCase("0")){
                        mensaje("Para Editar Cantidad Producto Debe Tener mas de 1 Item","Atención","Aceptar");
                    }else{
                        if(edCantidadEditar.getText().toString().trim().equalsIgnoreCase("")){
                            mensaje("Favor Ingresar Cantidad a Modificar","Atención","Aceptar");
                        }else {
                            String codigo = tvCodigoLeidoEditar.getText().toString().trim();
                            String ubi = ubicacionActual.getActual1()+ubicacionActual.getActual2()+ubicacionActual.getActual3()+ubicacionActual.getActual4();
                            String cant = edCantidadEditar.getText().toString().trim();

                            editarCantidad(codigo, Integer.valueOf(cant), ubi);
                            Toast.makeText(EditarCantidad.this, "Operacion Exitosa", Toast.LENGTH_SHORT)
                                    .show();
                            limpiarInterfaz();
                            edCodigoCapturado.requestFocus();
                        }
                    }
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        tvUbicacionEditar.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == RESULT_OK) {
            ubicacionGlobal = ubiDat.obtenerDato();
            //tvUbicacionEditar.setText(ubicacionGlobal);
            tvUbicacionEditar.setText(ubicacionActual.getActual1() + "-" + ubicacionActual.getActual2());
            edCodigoCapturado.setText("");
            edCantidadEditar.setText("");
            Toast.makeText(this, "Operacion Exitosa", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    @SuppressLint("Range")
    public String buscarCodigoEditar(String codi){
        String descrip="SIN RESULTADOS";
        try{
            SqlHelper sqlHel = new SqlHelper(EditarCantidad.this);
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        edCodigoCapturado.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        //Toast.makeText(this, "keycode: "+keyCode, Toast.LENGTH_SHORT).show();
        //if ((keyCode == 240)||(keyCode == 241) ||(keyCode == 10036))
        if ((keyCode == 290)||(keyCode == 291)||(keyCode == 292))
        {
            if(!edCodigoCapturado.getText().toString().equalsIgnoreCase("")) {
                b.activar(this);
                BuscarCantidad busc = new BuscarCantidad();
                String cantidad = busc.obtenerCantidadProducto(EditarCantidad.this,edCodigoCapturado.getText().toString(),ubiDat.obtenerDato());
                tvCantidadEditar.setText(cantidad);

                String codigoLeido = edCodigoCapturado.getText().toString().trim();
                String ubicacionFinal = ubiDat.obtenerDato();

                String descipcion = "NO EXISTE";
                descipcion = buscarCodigoEditar(codigoLeido);
                tvDescripcionEditar.setText(descipcion);
                tvCodigoLeidoEditar.setText(codigoLeido);
                edCodigoCapturado.setText("");
                edCantidadEditar.requestFocus();
            }
        }
        return false;
    }

    public void limpiarInterfaz(){
        tvCantidadEditar.setText("0");
        tvCodigoLeidoEditar.setText("");
        edCantidadEditar.setText("");
        tvDescripcionEditar.setText("");
    }

    public void mensaje(String contenido, String titulo, String botonNom){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarCantidad.this);
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

                tvCantidadEditar.setText(d.get(0).getCantidad().toString());
                tvCodigoLeidoEditar.setText(d.get(0).getCodigo().toString());
                tvDescripcionEditar.setText(d.get(0).getDescripcion().toString());
            }

            @Override
            public void onFailure(Call<List<Datos>> call, Throwable t) {

            }
        });
    }

    public void editarCantidad(String codigo, int cantidad, String ubicacion) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://"+configuracion.getIp()+":"+ configuracion.getPuerto() +"/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DatosAPI datosAPI = retrofit.create(DatosAPI.class);
        Call<String> call = datosAPI.editarCantidadDato(codigo, cantidad, ubicacion, configuracion.getId_capturador());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    Log.e("RESPONSE", response.message());
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
