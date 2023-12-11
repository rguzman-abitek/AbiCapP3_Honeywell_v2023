package com.example.camilodesarrollo.abicap;

import android.annotation.SuppressLint;
import android.app.Activity;
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

public class Ingresar_Producto extends Activity {

    private EditText edCodigoLeido, edDescripcionIngresada;
    String codigoObt =""; //global
    String descriObtenida =""; // global
    private TextView tvCodigo, tvDescripcion, tvCodigoObtenido, tvIngreseCodigo, tvIngreseDescripcion, tvDescripcionObtenida;
    private Button btnIngresarDescripcion,btnMenuPrincipal,btnCambiarUbiBarrido,btnTotalUbicacionBarrido;
    public Boolean mostrarDes,contraArch,dupli;
    public Beeper b;
    public verificarMiscelaneos verMis;
    public verificarCheck verifChk;
    public UbicacionDato ubiDat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_producto);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN );
        //DECLARAR INTERFAZ GRAFICA
        //-----------------------------------------------------------------------------------------------

        edCodigoLeido = (EditText)findViewById(R.id.edCodigoLeido);
        edDescripcionIngresada = (EditText)findViewById(R.id.edDescripcionIngresada);
        btnIngresarDescripcion=(Button)findViewById(R.id.btnIngresarDescripcion);
        btnMenuPrincipal=(Button)findViewById(R.id.btnMenuPrincipalLoteo);
        tvIngreseCodigo = (TextView)findViewById(R.id.tvIngreseCodigo);
        tvIngreseDescripcion = (TextView)findViewById(R.id.tvIngreseDescripcion);
        tvCodigo = (TextView)findViewById(R.id.tvCodigo);
        tvCodigoObtenido = (TextView)findViewById(R.id.tvCodigoObtenido);
        tvDescripcionObtenida = (TextView)findViewById(R.id.tvDescripcionObtenida);
        tvDescripcion = (TextView)findViewById(R.id.tvDescripcion);

        edCodigoLeido.requestFocus();
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
//            tvDescripcionProducto.setVisibility(View.GONE);
        }

        contraArch = verifChk.checkearContraArchivo(this);

       dupli = verifChk.checkearDuplicado(this);

        //------------------------------------------------------------------------------------------
//        edCodigoLeido.setEnabled(false);
        btnIngresarDescripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descriObtenida = edCodigoLeido.getText().toString().trim();
                insertarOActualizarDescripcionEnBase(codigoObt,descriObtenida );
                edCodigoLeido.setText("");

            }
        });

        btnMenuPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void LimpiarVentana(){

        tvIngreseCodigo.setVisibility(View.VISIBLE);


        tvIngreseDescripcion.setVisibility(View.GONE);
        btnIngresarDescripcion.setVisibility(View.GONE);
        tvCodigoObtenido.setVisibility(View.GONE);
        tvDescripcionObtenida.setVisibility(View.GONE);
        tvCodigo.setVisibility(View.GONE);
        tvDescripcion.setVisibility(View.GONE);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        edCodigoLeido.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        //Toast.makeText(this, "keycode: "+keyCode, Toast.LENGTH_SHORT).show();
        //if ((keyCode == 240)||(keyCode == 241) ||(keyCode == 10036))
        //if (keyCode == 288)
        if ((keyCode == 290)||(keyCode == 291)||(keyCode == 292))
        {
            edCodigoLeido.setFocusable(true);
            String codigoObt = edCodigoLeido.getText().toString().trim();
            String ubica = ubiDat.obtenerDato();
            codigoObt = edCodigoLeido.getText().toString().trim();

            if(codigoObt.equalsIgnoreCase("")){
                Toast.makeText(Ingresar_Producto.this, "Se ha pistoleado mal. Intente nuevamente.", Toast.LENGTH_SHORT).show();
                LimpiarVentana();
            }else{
                edCodigoLeido.setText("");

                //hago que se desaparezcan al pistolear
//            edCodigoLeido.setVisibility(View.GONE);
                tvIngreseCodigo.setVisibility(View.GONE);

                //hago que aparezcan al pistolear
//            edDescripcionIngresada.setVisibility(View.VISIBLE);
                tvIngreseDescripcion.setVisibility(View.VISIBLE);
                btnIngresarDescripcion.setVisibility(View.VISIBLE);
                tvCodigoObtenido.setVisibility(View.VISIBLE);
                tvDescripcionObtenida.setVisibility(View.VISIBLE);
                tvCodigo.setVisibility(View.VISIBLE);
                tvDescripcion.setVisibility(View.VISIBLE);

//            edDescripcionIngresada.setFocusable(true);
                descriObtenida = buscarDescripcion(codigoObt);

                btnIngresarDescripcion.setBackgroundDrawable( getResources().getDrawable(R.drawable.botonverde) );
                btnIngresarDescripcion.setText("Ingresar Descripción");
                if((descriObtenida!="") && (descriObtenida!="SIN DESCRIPCION")){
                    btnIngresarDescripcion.setBackgroundDrawable( getResources().getDrawable(R.drawable.botonamarillo) );
                    btnIngresarDescripcion.setText("Ingresar Descripción");
                }else {
                    btnIngresarDescripcion.setBackgroundDrawable( getResources().getDrawable(R.drawable.botonamarillo) );
                    btnIngresarDescripcion.setText("Ingresar Descripción");
                    edCodigoLeido.setText("");
                }

                tvCodigoObtenido.setText(codigoObt);
                tvDescripcionObtenida.setText(descriObtenida);
            }
        }
        edCodigoLeido.setText("");
        edCodigoLeido.requestFocus();
        return false;
    }

    //Metodo permite obtener la descripcion de un producto existente en tabla MAESPRODUCTOS a traves del codigo pistoleado
    @SuppressLint("Range")
    public String buscarDescripcion(String codi){
        String descrip="SIN DESCRIPCION";
        try{
        SqlHelper sqlHel = new SqlHelper(Ingresar_Producto.this);
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

    //Inserta o Actualiza la descripcion segun exista o no previamente en tabla MAEPRODUCTOS
    public void insertarOActualizarDescripcionEnBase(String codi,String desc){
        try{
            SqlHelper sqlHel = new SqlHelper(Ingresar_Producto.this);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

//            desc=buscarDescripcion(codi);
//            String sql = "SELECT * FROM MAEPRODUCTOS WHERE CODIGO = '"+codi+"'";
            String vacio = edCodigoLeido.getText().toString().trim();
            if (vacio.equalsIgnoreCase("")){
                Toast.makeText(Ingresar_Producto.this, "Debe ingresar una descripción", Toast.LENGTH_SHORT).show();
            }else{
/*
                String descripcion = tvDescripcionObtenida.getText().toString().trim();
                if(descripcion!="SIN DESCRIPCION"){
                    actualizarDescripcion(codi,desc);
                    finish();
                }else{
                    insertarRegistroBarrido(codi,desc);
                    finish();
                }
                */
                String descripcion = tvDescripcionObtenida.getText().toString().trim();
                if(descripcion=="SIN DESCRIPCION") {
                    insertarRegistroBarrido(codi, desc);
                    finish();
                }
            }
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
    }

    public void actualizarDescripcion(String codig, String descr) {
        try {
            SqlHelper sqlHel = new SqlHelper(Ingresar_Producto.this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();

            if (db != null) {
                String SQL = "update MAEPRODUCTOS SET DESCRIPCION = '"+descr+"' WHERE CODIGO = '"+codig+"'";
                db.execSQL(SQL);
                db.close();
            }

        }catch(Exception e){
            Log.e("Error bd","error");
        }
    }

    public void insertarRegistroBarrido(String codig, String desc) {
        try {
            SqlHelper sqlHel = new SqlHelper(Ingresar_Producto.this);
            SQLiteDatabase db = sqlHel.getWritableDatabase();
            if (db != null) {
                String SQL = "INSERT INTO MAEPRODUCTOS (CODIGO, DESCRIPCION) VALUES ('"+codig+"','"+desc+"')";
                db.execSQL(SQL);
                       SQL = "update MAEDATOS SET DESCRIPCION = '"+desc+"' WHERE CODIGO = '"+codig+"'";
                db.execSQL(SQL);
                db.close();
            }
        }catch(Exception e){
            Log.e("Error bd","error");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == RESULT_OK) {

//            tvUbicacionBarrido.setText(ubicacionGlobal);

            edCodigoLeido.setText("");
            Toast.makeText(this, "Operacion Exitosa", Toast.LENGTH_SHORT).show();

        }
    }

}
