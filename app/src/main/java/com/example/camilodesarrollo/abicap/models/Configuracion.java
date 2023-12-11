package com.example.camilodesarrollo.abicap.models;

public class Configuracion {
    private String ip;
    private String puerto;
    private String id_capturador;

    public Configuracion(String ip, String puerto, String id_capturador) {
        this.ip = ip;
        this.puerto = puerto;
        this.id_capturador = id_capturador;
    }

    public Configuracion() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getId_capturador() {
        return id_capturador;
    }

    public void setId_capturador(String id_capturador) {
        this.id_capturador = id_capturador;
    }
}
