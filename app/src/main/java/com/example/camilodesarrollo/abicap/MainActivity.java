package com.example.camilodesarrollo.abicap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.camilodesarrollo.abicap.db.DbConfiguracion;
import com.example.camilodesarrollo.abicap.db.DbHelper;
import com.example.camilodesarrollo.abicap.db.DbUltimaUbicacion;
import com.example.camilodesarrollo.abicap.models.Configuracion;
import com.example.camilodesarrollo.abicap.models.UbicacionActual;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    private Button btnMiscelaneos, btnPorBarrido, btnActivarLicencia, cargaMaestro, btnNuevaUbicacion, btnPorLotes, btnAbrirConf, btnRevision, btnSalidaMercaderia, btnBorrarRegistro, btnBorrarInventarioMen, btnGenerarArchivoInventario, btnEditarCantidad, btnIngresarProducto;
    private String lecturaRAW;
    private boolean sdDesponible, sdAccesoEscritura;
    private ProgressDialog p;

    //Ubicación por defecto
    public static UbicacionActual ubicacionActual = new UbicacionActual();

    public static Configuracion configuracion = new Configuracion();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        //CONTROLES GRAFICOS INICIALIZACION
        //--------------------------------------------------------------------------------------
        //btnPorBarrido = (Button) findViewById(R.id.btnPorBarrido);
        //cargaMaestro = (Button) findViewById(R.id.btnCargarMaestro);
        btnNuevaUbicacion = (Button) findViewById(R.id.btnNuevaUbicacion);
        btnPorLotes = (Button) findViewById(R.id.btnPorLotes);
        btnAbrirConf = (Button) findViewById(R.id.btnAbrirConf);
        btnRevision = (Button) findViewById(R.id.btnRevision);
        btnSalidaMercaderia = (Button) findViewById(R.id.btnSalidaMercaderia);
        //btnBorrarRegistro = (Button) findViewById(R.id.btnBorrarRegistro);
        //btnBorrarInventarioMen = (Button) findViewById(R.id.btnBorrarInventarioMen);
        //btnGenerarArchivoInventario = (Button) findViewById(R.id.btnGenerarArchivoInventario);
        btnEditarCantidad = (Button) findViewById(R.id.btnEditarCantidad);
        btnActivarLicencia = (Button) findViewById(R.id.btnActivarLicencia);
        btnMiscelaneos = (Button) findViewById(R.id.btnMiscelaneos);
        //btnIngresarProducto = (Button)findViewById(R.id.btnIngreoProducto);
        //--------------------------------------------------------------------------------------
        //btnPorBarrido.setVisibility(View.GONE);
        CheckearPermisos c = new CheckearPermisos();
        c.checkear(this);

        CrearArchivosIniciales a = new CrearArchivosIniciales(this);
        a.execute();

        //Cargamos la base de datos
        iniciarDB();

        //Obtenemos última ubicación guardada

        try{
            DbUltimaUbicacion db_ua = new DbUltimaUbicacion(this);
            UbicacionActual ubicacionActual_from_db = new UbicacionActual();
            ubicacionActual_from_db = db_ua.obtenerUbicacionActual();

            ubicacionActual.setActual1(ubicacionActual_from_db.getActual1());
            ubicacionActual.setActual2(ubicacionActual_from_db.getActual2());
            ubicacionActual.setActual3(ubicacionActual_from_db.getActual3());
            ubicacionActual.setActual4(ubicacionActual_from_db.getActual4());
        }catch (Exception ex){
            Log.e("ERROR", ex.getMessage().toString());
        }


        //Obtenemos la configuracion

        DbConfiguracion dbConfiguracion = new DbConfiguracion(this);
        //Configuracion configuracion1 = new Configuracion();
        configuracion = dbConfiguracion.getConfiguracion();


        //VALIDAR LICENCIA
        //------------------------------------------------------------------------
        Boolean licenciado = new ObtenerLicencia().obtenerLicencia(this);
        if (licenciado) {
            desbloquearInterfaz();
        } else {
            bloquearApp();
        }
        //------------------------------------------------------------------------
        /**
         btnPorBarrido.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        Intent i = new Intent(MainActivity.this ,LecturaBarrido.class);
        startActivity(i);
        }
        }
         );*/
/*
        cargaMaestro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(MainActivity.this ,Login.class);
                //startActivityForResult(i,1);
                Intent i = new Intent(MainActivity.this, CargaMaestro.class);
                startActivity(i);
            }
        });*/

        btnNuevaUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Ubicacion.class);
                startActivity(i);
            }
        });
        btnPorLotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LecturaLoteo.class);
                startActivity(i);
            }
        });
        btnAbrirConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(MainActivity.this ,Login.class);
                //startActivityForResult(i,2);
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });

        btnRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Revision.class);
                startActivity(i);
            }
        });

        btnSalidaMercaderia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SalidaMercaderia.class);
                startActivity(i);
            }
        });
        /*
        btnBorrarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, BorrarRegistro.class);
                startActivity(i);
            }
        });

        btnBorrarInventarioMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(MainActivity.this ,Login.class);
                //startActivityForResult(i,3);

                Intent i = new Intent(MainActivity.this, BorrarInventario.class);
                startActivity(i);
            }
        });*/
/*
        btnGenerarArchivoInventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GenerarArchivo.class);
                startActivity(i);
            }
        });*/

        btnEditarCantidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EditarCantidad.class);
                startActivity(i);
            }
        });

        btnActivarLicencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ActivarAbiCap.class);
                startActivityForResult(i, 4);
            }
        });
        btnMiscelaneos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(MainActivity.this ,Login.class);
                //startActivityForResult(i,5);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_dialog_log_miscelaneo, null);
                EditText password = (EditText)mView.findViewById(R.id.passwordMisc);
                Button ingresarLogMisc = (Button)mView.findViewById(R.id.btnIngresarDialogMisc);
                Button cancelarLogMisc = (Button)mView.findViewById(R.id.btnCancelarDialogMisc);


                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

                ingresarLogMisc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(password.getText().toString().equals(".,b0d3g4")){
                            dialog.cancel();
                            Intent i = new Intent(MainActivity.this, Miscelaneos.class);
                            startActivity(i);
                        }
                    }
                });
                cancelarLogMisc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

            }
        });
        /*
        btnIngresarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this ,Ingresar_Producto.class);
                startActivity(i);
            }
        });*/
    }

    public void InsertarModo1() {
        SqlHelper sqlHel = new SqlHelper(this);
        SQLiteDatabase db = sqlHel.getWritableDatabase();
        if (db != null) {
            for (int i = 0; i <= 10; i++) {
                String codigo = Integer.toString(i);
                String descrip = "ABC" + i;
                String SQL = "insert into Productos (codigo,descripcion) values('" + codigo + "','" + descrip + "')";
                db.execSQL(SQL);
            }
            db.close();
        }
    }

    public void InsertarModo2() {
        SqlHelper sqlHel = new SqlHelper(this);
        SQLiteDatabase db = sqlHel.getWritableDatabase();
        if (db != null) {
            for (int i = 0; i <= 10; i++) {
                String codigo = Integer.toString(i);
                String descrip = "ABC" + i;
                ContentValues insertar = new ContentValues();
                insertar.put("codigo", codigo);
                insertar.put("descripcion", descrip);

                db.insert("Productos", null, insertar);
            }
            db.close();
        }
    }

    public void iniciarDB() {
        DbHelper dbHelper = new DbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

    }


    public void crearArchivo() {
        verificarEstado();
        try {
            InputStream lec =
                    getResources().openRawResource(R.raw.conf);
            BufferedReader bufLec =
                    new BufferedReader(new InputStreamReader(lec));
            lecturaRAW = bufLec.readLine();
            lec.close();

        } catch (Exception ex) {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
        }
    }

    private class cargarMaestro extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            limpiarProductos();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            p.dismiss();
        }

        @Override
        protected void onPreExecute() {
            p = ProgressDialog.show(MainActivity.this, "PROCESANDO SOLICITUD", "PROCESANDO", false);
        }
    }

    public void verificarEstado() {
        String estado = Environment.getExternalStorageState();

        if (estado.equals(Environment.MEDIA_MOUNTED)) {
            sdDesponible = true;
            sdAccesoEscritura = true;
        } else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            sdDesponible = true;
            sdAccesoEscritura = false;
        } else {
            sdDesponible = false;
            sdAccesoEscritura = false;
        }
    }

    public void limpiarProductos() {
        SqlHelper sqlHel = new SqlHelper(this);
        SQLiteDatabase db = sqlHel.getWritableDatabase();
        if (db != null) {
            String SQL = "DELETE FROM MAEPRODUCTOS";
            db.execSQL(SQL);
        }
        db.close();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == RESULT_OK && requestCode == 1) {
            Intent i = new Intent(MainActivity.this, CargaMaestro.class);
            startActivity(i);
            Toast.makeText(this, "Login Correcto", Toast.LENGTH_SHORT).show();
        }
        if (resultCode == RESULT_OK && requestCode == 2) {
            Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);
            Toast.makeText(this, "Login Correcto", Toast.LENGTH_SHORT).show();
        }/*
        if (resultCode == RESULT_OK && requestCode == 3) {
            Intent i = new Intent(MainActivity.this, BorrarInventario.class);
            startActivity(i);
            Toast.makeText(this, "Login Correcto", Toast.LENGTH_SHORT).show();
        }*/
        if (resultCode == RESULT_OK && requestCode == 4) {
            Toast.makeText(this, "Activacion Lista", Toast.LENGTH_SHORT).show();
            desbloquearInterfaz();
        }
        if (resultCode == RESULT_CANCELED && requestCode == 4) {
            Toast.makeText(this, "NO SE PUDO OBTENER DATO IP", Toast.LENGTH_SHORT).show();
        }
        if (resultCode == RESULT_OK && requestCode == 5) {
            Intent i = new Intent(MainActivity.this, Miscelaneos.class);
            startActivity(i);
            Toast.makeText(this, "Login Correcto", Toast.LENGTH_SHORT).show();
        }
    }

    public void bloquearApp() {
        btnNuevaUbicacion.setVisibility(View.GONE);
        btnPorLotes.setVisibility(View.GONE);
        //btnBorrarInventarioMen.setVisibility(View.GONE);
        //btnBorrarRegistro.setVisibility(View.GONE);
        btnEditarCantidad.setVisibility(View.GONE);
        //btnGenerarArchivoInventario.setVisibility(View.GONE);
        //btnPorBarrido.setVisibility(View.GONE);
        btnRevision.setVisibility(View.GONE);
        btnSalidaMercaderia.setVisibility(View.GONE);
        //cargaMaestro.setVisibility(View.GONE);
        btnMiscelaneos.setVisibility(View.GONE);
        //btnIngresarProducto.setVisibility(View.GONE);
        btnActivarLicencia.setVisibility(View.VISIBLE);
    }

    public void desbloquearInterfaz() {
        btnNuevaUbicacion.setVisibility(View.VISIBLE);
        btnPorLotes.setVisibility(View.VISIBLE);
        //btnBorrarInventarioMen.setVisibility(View.VISIBLE);
        //btnBorrarRegistro.setVisibility(View.VISIBLE);
        btnEditarCantidad.setVisibility(View.VISIBLE);
        //btnGenerarArchivoInventario.setVisibility(View.VISIBLE);
        //btnPorBarrido.setVisibility(View.VISIBLE);
        btnRevision.setVisibility(View.VISIBLE);
        btnSalidaMercaderia.setVisibility(View.VISIBLE);
        //cargaMaestro.setVisibility(View.VISIBLE);
        btnMiscelaneos.setVisibility(View.VISIBLE);
        //btnIngresarProducto.setVisibility(View.VISIBLE);
        btnActivarLicencia.setVisibility(View.GONE);
    }
}