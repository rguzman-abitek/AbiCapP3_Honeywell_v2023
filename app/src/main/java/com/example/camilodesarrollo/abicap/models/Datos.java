package com.example.camilodesarrollo.abicap.models;

public class Datos {


    private String id;
    private String codigo;
    private String descripcion;

    private Integer cantidad;

    private  String comentario;

    private  String id_capturador;

    public Datos(String id, String codigo, String descripcion, Integer cantidad, String comentario, String id_capturador) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.comentario = comentario;
        this.id_capturador = id_capturador;
    }

    public Datos() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getId_capturador() {
        return id_capturador;
    }

    public void setId_capturador(String id_capturador) {
        this.id_capturador = id_capturador;
    }
}
