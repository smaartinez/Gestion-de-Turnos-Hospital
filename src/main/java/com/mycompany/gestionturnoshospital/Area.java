package com.mycompany.gestionturnoshospital;

import java.util.ArrayList;
import java.util.List;

public class Area {
    private final int id;
    private String nombre;
    private int cuposManana, cuposTarde, cuposNoche;
    private List<String> skillsRequeridas;

    public Area(int id, String nombre, int cuposManana, int cuposTarde, int cuposNoche, List<String> skills) {
        this.id = id;
        this.nombre = nombre;
        this.cuposManana = cuposManana;
        this.cuposTarde = cuposTarde;
        this.cuposNoche = cuposNoche;
        this.skillsRequeridas = new ArrayList<>(skills);
    }

    // getters/setters básicos
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCuposManana() { return cuposManana; }
    public int getCuposTarde() { return cuposTarde; }
    public int getCuposNoche() { return cuposNoche; }
    public List<String> getSkillsRequeridas() { return skillsRequeridas; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCuposManana(int v) { this.cuposManana = v; }
    public void setCuposTarde(int v) { this.cuposTarde = v; }
    public void setCuposNoche(int v) { this.cuposNoche = v; }
    @Override
    public String toString() {
        return getNombre(); // o this.nombre
    }
    public void setSkillsRequeridas(List<String> skills) {
    if (skills == null) {
        this.skillsRequeridas = new ArrayList<>();
    } else {
        this.skillsRequeridas = new ArrayList<>(skills); 
    }
    }
}
