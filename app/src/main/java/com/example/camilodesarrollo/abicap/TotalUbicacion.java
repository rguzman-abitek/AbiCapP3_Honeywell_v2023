package com.example.camilodesarrollo.abicap;

import static com.example.camilodesarrollo.abicap.MainActivity.configuracion;
import static com.example.camilodesarrollo.abicap.MainActivity.ubicacionActual;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilodesarrollo.abicap.interfaces.DatosAPI;
import com.example.camilodesarrollo.abicap.interfaces.ProductoAPI;
import com.example.camilodesarrollo.abicap.models.Datos;
import com.example.camilodesarrollo.abicap.models.Producto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TotalUbicacion extends Activity {
    private GridView gvConteo;
    private Button btnMenuPrincipalCantidadUbi;

    private TextView txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_ubicacion);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN );

        //DECLARACION COMPONENTES GRAFICOS
        //--------------------------------------------------------------------------------------
        gvConteo = (GridView) findViewById(R.id.gvConteo);

        btnMenuPrincipalCantidadUbi = (Button)findViewById(R.id.btnMenuPrincipalCantidadUbi);
        //--------------------------------------------------------------------------------------
        String ubi = getIntent().getExtras().getString("codigoUbicacion");
        List<String> listadoFinal = obtenerListadoCantidad(ubi);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
          //      android.R.layout.simple_list_item_1, listadoFinal);

        //gvConteo.setAdapter(adapter);

        btnMenuPrincipalCantidadUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txt = findViewById(R.id.mostrar);
        //getProductos();
        getDatosPorubicacion(ubicacionActual.getActual1()+ubicacionActual.getActual2()+ubicacionActual.getActual3()+ubicacionActual.getActual4());

    }

    @SuppressLint("Range")
    public List<String> obtenerListadoCantidad(String ubic){
        List<String> listado = new ArrayList<String>();
        try{
            SqlHelper sqlHel = new SqlHelper(TotalUbicacion.this);
            SQLiteDatabase db = sqlHel.getReadableDatabase();

            String sql = "SELECT codigo, cantidad FROM MAEDATOS WHERE UBICACION = '"+ubic+"' ORDER BY codigo ASC";
            Cursor c = db.rawQuery(sql,null);
            Integer contador = 0;
            while (c.moveToNext()){
                listado.add(c.getString(c.getColumnIndex("codigo")));
                listado.add(c.getString(c.getColumnIndex("cantidad")));
            }
            c.close();
            db.close();
        }catch (Exception ex) {
            Log.e("Fichero sd", "Error");
        }
        return listado;
    }

    private void getProductos(){
        //.baseUrl("http://10.0.2.2:5000/")

        Retrofit retrofit = new Retrofit.Builder()  .baseUrl(configuracion.getIp()+":"+configuracion.getPuerto())
                                                    .addConverterFactory(GsonConverterFactory.create())
                                                    .build();
        ProductoAPI productoAPI = retrofit.create(ProductoAPI.class);
        Call<List<Producto>> call = productoAPI.getProductos();
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {

                if(!response.isSuccessful()){
                    Toast toast = Toast.makeText(getApplicationContext(), "Servidor no encontrado", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                String content = "";
                List<Producto> p = response.body();

                for (Producto producto: p) {
                    content += "Id: " + producto.getId() + "\n";
                    content += "Código: " + producto.getCodigo() + "\n";
                    content += "Descripción: " + producto.getDescripcion() + "\n\n";
                    txt.append(content);
                    content = "";
                }

            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), "Servidor no encontrado", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    private void getDatosPorubicacion(String ubicacion){
        //.baseUrl("http://10.0.2.2:5000/")
        Log.i("TAG", "http://"+ configuracion.getIp() + ":" + configuracion.getPuerto() +"/");
        Retrofit retrofit = new Retrofit.Builder()  .baseUrl("http://"+ configuracion.getIp() + ":" + configuracion.getPuerto() +"/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DatosAPI datosAPI = retrofit.create(DatosAPI.class);
        Call<List<Datos>> call = datosAPI.getDatosPorUbicacion(ubicacion);
        call.enqueue(new Callback<List<Datos>>() {
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
                    try{
                        sa.add(datos.getDescripcion().substring(0,20));
                    }catch (Exception e){
                        sa.add(datos.getDescripcion());
                    }
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
                gvConteo.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, sa));

            }

            @Override
            public void onFailure(Call<List<Datos>> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), "Servidor no encontrado", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        return false;
    }

}
