package com.example.camilodesarrollo.abicap;

import static com.example.camilodesarrollo.abicap.MainActivity.configuracion;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.camilodesarrollo.abicap.db.DbConfiguracion;

public class Miscelaneos extends Activity {
    private Button btnGuardarConf, btnMenuPrincipalMiscelaneos;
    private EditText txtIP1, txtIP2, txtIP3, txtIP4, txtPuerto, txtIDCapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miscelaneos);
        //INTERFAZ GRAFICA
        //--------------------------------------------------------------------------------------------------
        btnGuardarConf = findViewById(R.id.btnGuardarMisc);
        btnMenuPrincipalMiscelaneos = (Button)findViewById(R.id.btnMenuPrincipalMiscelaneos);

        txtIP1 = findViewById(R.id.txtIP1);
        txtIP2 = findViewById(R.id.txtIP2);
        txtIP3 = findViewById(R.id.txtIP3);
        txtIP4 = findViewById(R.id.txtIP4);
        txtPuerto = findViewById(R.id.txtPuerto);
        txtIDCapt = findViewById(R.id.txtIDCapt);

        //--------------------------------------------------------------------------------------------------

        //carga de datos de configuracion
        String ip_str = configuracion.getIp();
        String[] ip = null;
        if(ip_str != null){
            ip = ip_str.split("\\.");
        }

        if(ip == null){
            txtIP1.setText("");
            txtIP2.setText("");
            txtIP3.setText("");
            txtIP4.setText("");
            txtPuerto.setText("");
            txtIDCapt.setText("");
        }else{
            txtIP1.setText(ip[0].toString());
            txtIP2.setText(ip[1].toString());
            txtIP3.setText(ip[2].toString());
            txtIP4.setText(ip[3].toString());
            txtPuerto.setText(configuracion.getPuerto().toString());

            txtIDCapt.setText(configuracion.getId_capturador().toString());
        }


        btnGuardarConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbConfiguracion dbConfiguracion = new DbConfiguracion(getApplicationContext());
                dbConfiguracion.editarIP(txtIP1.getText().toString() + "." + txtIP2.getText().toString() + "." + txtIP3.getText().toString() + "." + txtIP4.getText().toString());
                dbConfiguracion.editarPuerto(txtPuerto.getText().toString());
                dbConfiguracion.editarIdCapturador(txtIDCapt.getText().toString());
                configuracion.setIp(txtIP1.getText().toString() + "." + txtIP2.getText().toString() + "." + txtIP3.getText().toString() + "." + txtIP4.getText().toString());
                configuracion.setPuerto(txtPuerto.getText().toString());
                configuracion.setId_capturador(txtIDCapt.getText().toString());
                finish();
                finish();

            }
        });

        btnMenuPrincipalMiscelaneos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        return false;
    }
}
