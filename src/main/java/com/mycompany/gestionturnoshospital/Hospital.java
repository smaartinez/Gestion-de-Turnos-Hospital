package com.mycompany.gestionturnoshospital;

import com.mycompany.gestionturnoshospital.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hospital {
    private String nombre;
    private final List<Area> areas = new ArrayList<>();

    public Hospital(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre inválido");
        this.nombre = nombre.trim();
    }

    Hospital() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre inválido");
        this.nombre = nombre.trim();
    }

    public List<Area> getAreas() { return Collections.unmodifiableList(areas); }
    public int countAreas() { return areas.size(); }
    public void clearAreas() { areas.clear(); }

    public void agregarArea(Area area) {
        if (area == null) throw new IllegalArgumentException("Área nula");
        for (Area a : areas) if (a.getId() == area.getId())
            throw new IllegalArgumentException("Ya existe un área con id=" + area.getId());
        areas.add(area);
    }

    public Area getAreaPorIndice(int idx) {
        if (idx < 0 || idx >= areas.size()) return null;
        return areas.get(idx);
    }

    public Area eliminarAreaPorIndice(int idx) {
        if (idx < 0 || idx >= areas.size()) return null;
        return areas.remove(idx);
    }

    public void editarArea(int idx, String nuevoNombre, Integer cupM, Integer cupT, Integer cupN) {
        Area a = getAreaPorIndice(idx);
        if (a == null) throw new IllegalArgumentException("Índice de área inválido");
        if (nuevoNombre != null && !nuevoNombre.isBlank()) a.setNombre(nuevoNombre);
        if (cupM != null) a.setCuposManana(cupM);
        if (cupT != null) a.setCuposTarde(cupT);
        if (cupN != null) a.setCuposNoche(cupN);
    }
}