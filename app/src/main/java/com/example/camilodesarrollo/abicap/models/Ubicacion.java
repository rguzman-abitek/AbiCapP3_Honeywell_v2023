package com.example.camilodesarrollo.abicap.models;

public class Ubicacion {
    private int id;
    private String ubicacion1;
    private String ubicacion2;
    private String ubicacion3;
    private String ubicacion4;

    public Ubicacion(String ubicacion1, String ubicacion2, String ubicacion3, String ubicacion4) {
        this.ubicacion1 = ubicacion1;
        this.ubicacion2 = ubicacion2;
        this.ubicacion3 = ubicacion3;
        this.ubicacion4 = ubicacion4;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUbicacion1() {
        return ubicacion1;
    }

    public void setUbicacion1(String ubicacion1) {
        this.ubicacion1 = ubicacion1;
    }

    public String getUbicacion2() {
        return ubicacion2;
    }

    public void setUbicacion2(String ubicacion2) {
        this.ubicacion2 = ubicacion2;
    }

    public String getUbicacion3() {
        return ubicacion3;
    }

    public void setUbicacion3(String ubicacion3) {
        this.ubicacion3 = ubicacion3;
    }

    public String getUbicacion4() {
        return ubicacion4;
    }

    public void setUbicacion4(String ubicacion4) {
        this.ubicacion4 = ubicacion4;
    }
}
