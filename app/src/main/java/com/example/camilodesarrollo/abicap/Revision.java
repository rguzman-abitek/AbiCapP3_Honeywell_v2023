package com.example.camilodesarrollo.abicap;

import static com.example.camilodesarrollo.abicap.MainActivity.configuracion;
import static com.example.camilodesarrollo.abicap.MainActivity.ubicacionActual;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.camilodesarrollo.abicap.interfaces.DatosAPI;
import com.example.camilodesarrollo.abicap.models.Datos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Revision extends Activity {
    private String ubicacionGlobal,codigoBarridoObtenido;
    private TextView tvUbicacionRevision,tvCantidadRevision,tvDescripcionProductoRevision,tvCodigoLeidoBarridoRevision,tvCodigoCapturado;
    private EditText edCodigoLeidoRevision;
    private Button btnIngresarCodigoRevision,btnMenuPrincipalRevision,btnCambiarUbiRevision,btnTotalUbicacionRevision;
    public Beeper b;
    public verificarMiscelaneos verMis;
    public verificarCheck verifChk;
    public UbicacionDato ubiDat;

    public Datos datosRevision;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN );

        //DECLARAR INTERFAZ GRAFICA
        //-----------------------------------------------------------------------------------------------

        tvUbicacionRevision = (TextView)findViewById(R.id.tvUbicacionRevision);
        tvCantidadRevision = (TextView)findViewById(R.id.tvCantidadRevision);
        tvDescripcionProductoRevision =(TextView)findViewById(R.id.tvDescripcionProductoRevision);
        tvCodigoLeidoBarridoRevision = (TextView)findViewById(R.id.tvCodigoLeidoBarridoRevision);

        edCodigoLeidoRevision = (EditText)findViewById(R.id.edCodigoLeidoRevision);

        btnIngresarCodigoRevision=(Button)findViewById(R.id.btnIngresarCodigoRevision);
        btnMenuPrincipalRevision=(Button)findViewById(R.id.btnMenuPrincipalRevision);
        btnCambiarUbiRevision=(Button)findViewById(R.id.btnCambiarUbiRevision);
        btnTotalUbicacionRevision=(Button)findViewById(R.id.btnTotalUbicacionRevision);

        tvUbicacionRevision.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());

        edCodigoLeidoRevision.requestFocus();
        //-----------------------------------------------------------------------------------------------
        //DECLARO VARIABLES GLOBALES
        //---------------------------------------------------------------------------------------
        b = new Beeper();
        tvUbicacionRevision.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());

        btnIngresarCodigoRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatoporCodigo(edCodigoLeidoRevision.getText().toString(), ubicacionActual.getActual1()+ubicacionActual.getActual2()+ubicacionActual.getActual3()+ubicacionActual.getActual4());
            }
        });

        btnMenuPrincipalRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCambiarUbiRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Revision.this ,Ubicacion.class);
                startActivityForResult(i,1);

            }
        });

        btnTotalUbicacionRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LO QUE DEBE HACER EL BOTON MOSTRAR CANTIDAD UBICACION
                ubicacionGlobal = ubicacionActual.getActual1();
                Intent i = new Intent(Revision.this ,TotalUbicacion.class);
                i.putExtra("codigoUbicacion",ubicacionGlobal);
                startActivity(i);
            }
        });

        //edCodigoLeidoRevision.setInputType(InputType.TYPE_NULL);
    }

    @Override
    public void onResume() {
        super.onResume();
        tvUbicacionRevision.setText(ubicacionActual.getActual1() + ubicacionActual.getActual2()+ ubicacionActual.getActual3()+ ubicacionActual.getActual4());
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

                if(d.get(0).getCantidad().intValue() < 0){
                    tvCantidadRevision.setText("0");
                }else{
                    tvCantidadRevision.setText(d.get(0).getCantidad().toString());
                }
                tvCodigoLeidoBarridoRevision.setText(d.get(0).getCodigo().toString());
                tvDescripcionProductoRevision.setText(d.get(0).getDescripcion().toString());
            }

            @Override
            public void onFailure(Call<List<Datos>> call, Throwable t) {

            }
        });
    }


}
