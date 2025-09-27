package com.mycompany.gestionturnoshospital;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Turno {
    private final LocalDate fecha;
    private final Bloque bloque;
    private final Area area;
    private final Enfermera enfermera;

    public Turno(LocalDate fecha, Bloque bloque, Area area, Enfermera enfermera) {
        this.fecha = fecha;
        this.bloque = bloque;
        this.area = area;
        this.enfermera = enfermera;
    }

    public LocalDate getFecha() { return fecha; }
    public Bloque getBloque() { return bloque; }
    public Area getArea() { return area; }
    public Enfermera getEnfermera() { return enfermera; }

    // 08:00 / 16:00 / 00:00 (noche del mismo día) — ajusta si usas otros horarios
    public LocalDateTime inicio() {
        switch (bloque) {
            case MANANA: return fecha.atTime(8,0);
            case TARDE:  return fecha.atTime(16,0);
            case NOCHE:  return fecha.atTime(0,0);
            default:     return fecha.atStartOfDay();
        }
    }
    public LocalDateTime fin() { return inicio().plusHours(8); }
}
