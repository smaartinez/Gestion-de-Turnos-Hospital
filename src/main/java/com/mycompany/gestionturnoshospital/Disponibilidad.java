package com.mycompany.gestionturnoshospital;

import java.time.LocalDate;

public final class Disponibilidad {
    private final LocalDate fecha;
    private final Bloque bloque;
    private final boolean disponible;
    private final String area;

    // Constructor corto (sin área)
    public Disponibilidad(LocalDate fecha, Bloque bloque, boolean disponible) {
        this(fecha, bloque, null, disponible);
    }

    // Constructor completo (con área antes que disponible)
    public Disponibilidad(LocalDate fecha, Bloque bloque, String area, boolean disponible) {
        if (fecha == null)  throw new IllegalArgumentException("fecha null");
        if (bloque == null) throw new IllegalArgumentException("bloque null");
        this.fecha = fecha;
        this.bloque = bloque;
        this.disponible = disponible;
        this.area = (area == null || area.isBlank()) ? null : area.trim();
    }

    public LocalDate getFecha()   { return fecha; }
    public Bloque getBloque()     { return bloque; }
    public boolean isDisponible() { return disponible; }
    public String getArea()       { return area; }

    @Override public String toString() {
        String a = (area == null ? "-" : area);
        return fecha + " " + bloque + " (" + a + ") -> " + (disponible ? "DISPONIBLE" : "NO");
    }
}
