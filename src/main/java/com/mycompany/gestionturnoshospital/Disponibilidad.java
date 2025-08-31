package com.mycompany.gestionturnoshospital;

import java.time.LocalDate;

public final class Disponibilidad {
    private final LocalDate fecha;
    private final Bloque bloque;
    private final boolean disponible;

    public Disponibilidad(LocalDate fecha, Bloque bloque, boolean disponible) {
        if (fecha == null)  throw new IllegalArgumentException("fecha null");
        if (bloque == null) throw new IllegalArgumentException("bloque null");
        this.fecha = fecha;
        this.bloque = bloque;
        this.disponible = disponible;
    }
    public LocalDate getFecha()   { return fecha; }
    public Bloque getBloque()     { return bloque; }
    public boolean isDisponible() { return disponible; }

    @Override public String toString() {
        return fecha + " " + bloque + " -> " + (disponible ? "DISPONIBLE" : "NO DISPONIBLE");
    }

}
