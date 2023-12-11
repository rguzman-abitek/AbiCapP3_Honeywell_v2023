package com.example.camilodesarrollo.abicap.interfaces;

import com.example.camilodesarrollo.abicap.models.Datos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DatosAPI {
    @GET("/api/getDatos")
    public Call<List<Datos>> getDatos();

    @GET("/api/getDatosPorUbicacion/{ubicacion}")
    public Call<List<Datos>> getDatosPorUbicacion(@Path("ubicacion") String ubicacion);

    @GET("/api/getDatosPorCodigo/{codigo}/{ubicacion}")
    public Call<List<Datos>> getDatosPorCodigo(@Path("codigo") String codigo, @Path("ubicacion") String ubicacion);

    @GET("/api/editardato/{codigo}/{nueva_cantidad}/{ubicacion}/{comentario}/{id_capturador}")
    public Call<String> editarDatos(@Path("codigo") String codigo,
                                    @Path("nueva_cantidad") int nueva_cantidad,
                                    @Path("ubicacion") String ubicacion,
                                    @Path("comentario") String comentario,
                                    @Path("id_capturador") String id_capturador);

    @GET("/api/editarstock/{codigo}/{nueva_cantidad}/{ubicacion}/{id_capturador}")
    public Call<String> editarCantidadDato(@Path("codigo") String codigo,
                                    @Path("nueva_cantidad") int nueva_cantidad,
                                    @Path("ubicacion") String ubicacion,
                                    @Path("id_capturador") String id_capturador);
    @GET("/api/agregardato/{codigo}/{nueva_cantidad}/{ubicacion}/{comentario}/{descripcion}/{id_capturador}/{comentario}/")
    public Call<String> agregarDato(@Path("codigo") String codigo,
                                    @Path("nueva_cantidad") int nueva_cantidad,
                                    @Path("ubicacion") String ubicacion,
                                    @Path("comentario") String comentario,
                                    @Path("descripcion") String descripcion,
                                    @Path("id_capturador") String id_capturador);
}
