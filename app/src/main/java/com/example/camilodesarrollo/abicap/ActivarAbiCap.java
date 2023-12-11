package com.example.camilodesarrollo.abicap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ActivarAbiCap extends Activity {

    private boolean respuesta;
    private EditText edCodigo,edDatCone;
    private Button btnActivar,btnMenuAct;
    private ProgressDialog p;
    private String ipglobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activar_abi_cap);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        verificarEstado();
        ipglobal = obtenerDato();
        if(ipglobal.equalsIgnoreCase("ERROR")){
            Intent i = getIntent();
            setResult(RESULT_CANCELED, i);
            finish();
        }
        //CAPA GRAFICA
        //-------------------------------------------------------------------
        edCodigo = (EditText)findViewById(R.id.tvTotalCantidad);
        edDatCone = (EditText)findViewById(R.id.edDatCone);

        btnActivar = (Button)findViewById(R.id.btnActivar);
        btnMenuAct = (Button)findViewById(R.id.btnMenuAct);
        //-------------------------------------------------------------------

        edDatCone.setText(ipglobal);
        edCodigo.requestFocus();
        btnActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edCodigo.getText().toString().trim().equalsIgnoreCase(""))
                {
                    if(edCodigo.getText().toString().trim().equalsIgnoreCase("18535072")){
                        forzarActivacion();
                    }else if (edCodigo.getText().toString().trim().equalsIgnoreCase("174902445")){
                        forzarActivacion();
                    }else if (edCodigo.getText().toString().trim().equalsIgnoreCase("L7054KD1")){//se creo para 1 dispositivo luzagro linares
                        forzarActivacion();
                    }else{
                    InteraccionWebService interac = new InteraccionWebService();
                    interac.execute(edCodigo.getText().toString().trim());
                    }
                }
                else
                {
                    //mensaje favor ingresar codigo
                }
            }
        });

        btnMenuAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public class InteraccionWebService extends AsyncTask<String,Void,Void> {

        private static final String metodo = "registrarInteres";
        // Namespace definido en el servicio web
        private static final String namespace = "http://Servicio/";
        // namespace + metodo
        private static final String accionSoap = "http://Servicio/validadorLicencias/registrarInteresRequest";

        private String url = "http://"+edDatCone.getText().toString().trim()+"/ValidadorLicenciasAbicap/validadorLicencias";

        HttpTransportSE transporte;
        SoapObject request;
        SoapSerializationEnvelope sobre;
        SoapPrimitive resultado;


        @Override
        protected Void doInBackground(String... strings) {

            try {
                respuesta = false;
                request = new SoapObject(namespace, metodo);
                request.addProperty("arg0", strings[0]);
                sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.setOutputSoapObject(request);

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                transporte = new HttpTransportSE(url);
                transporte.call(accionSoap, sobre);
                resultado = (SoapPrimitive) sobre.getResponse();
                respuesta = Boolean.valueOf(resultado.toString());

            } catch (Exception e) {
                respuesta = false;
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            if(respuesta){
                try{
                    SqlHelper sqlHel = new SqlHelper(ActivarAbiCap.this);
                    SQLiteDatabase db = sqlHel.getWritableDatabase();
                    if (db != null) {
                            String SQL = "INSERT INTO MAEUBICACION (codigo,descripcion) VALUES ('VALIDADO','VALIDADO')";
                            db.execSQL(SQL);
                        }
                    db.close();
                    p.dismiss();
                    Intent i = getIntent();
                    setResult(RESULT_OK, i);
                    finish();
                }catch (Exception ex){
                    Log.e(ex.toString(), "Error");
                }
            }else{
                p.dismiss();
                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);
                finish();
            }

        }
        @Override
        protected void onPreExecute(){
            p = ProgressDialog.show(ActivarAbiCap.this, "PROCESANDO", "ACTIVANDO LICENCIA", false);
        }
    }

    private boolean sdDesponible, sdAccesoEscritura;

    public String obtenerDato(){
        verificarEstado();
        String respuesta = "ERROR";
        if (sdAccesoEscritura && sdDesponible) {
            File ruta_sd = Environment.getExternalStoragePublicDirectory("/AbitekConfi/");//Environment.DIRECTORY_DOWNLOADS);
            File f = new File(ruta_sd.getAbsolutePath(), "ipValidador.txt");
            if (f.exists()) {
                try {
                    BufferedReader bufLec =
                            new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                    String tex ;
                    if(!(tex = (bufLec.readLine()).trim()).equals(null)) {
                        respuesta = tex.toString().trim();
                    }else{
                        respuesta = "ERROR";
                    }
                    bufLec.close();
                } catch (Exception ex) {
                    Log.e("Fichero sd", "Error al escribir en la sd");
                    respuesta = "ERROR";
                }
            } else {
                respuesta = "ERROR";
            }
        }
        return respuesta;
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

    public void forzarActivacion(){
        try{
            SqlHelper sqlHel = new SqlHelper(ActivarAbiCap.this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            if (db != null) {
                String SQL = "INSERT INTO MAEUBICACION (codigo,descripcion) VALUES ('VALIDADO','VALIDADO')";
                db.execSQL(SQL);
            }
            db.close();
            Intent i = getIntent();
            setResult(RESULT_OK, i);
            finish();
        }catch (Exception ex){
            Log.e(ex.toString(), "Error");
        }
    }

}