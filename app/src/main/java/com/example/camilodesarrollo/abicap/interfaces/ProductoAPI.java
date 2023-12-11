package com.example.camilodesarrollo.abicap.interfaces;

import com.example.camilodesarrollo.abicap.models.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProductoAPI {
    @GET("/api/getProductos")
    public Call<List<Producto>> getProductos();

    @POST("/api/editarstock/{id}/{accion}")
    public Call<String> editarStock(@Path("id") int id, @Path("accion") String accion);

}
