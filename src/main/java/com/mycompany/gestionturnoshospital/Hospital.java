package com.mycompany.gestionturnoshospital;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Hospital {
    private String nombre;

    // Áreas
    private final List<Area> areas = new ArrayList<>();

    // NUEVO: almacenamiento de turnos asignados
    private final List<Turno> turnos = new ArrayList<>();

    public Hospital(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre inválido");
        this.nombre = nombre.trim();
    }

    // (Se mantiene igual)
    Hospital() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // ====== ÁREAS ======
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
        for (Area a : areas) {
            if (a.getId() == area.getId())
                throw new IllegalArgumentException("Ya existe un área con id=" + area.getId());
        }
        areas.add(area);
        
        GestionTurnosHospital.persistIfPossible();
    }

    public Area getAreaPorIndice(int idx) {
        if (idx < 0 || idx >= areas.size()) return null;
        return areas.get(idx);
    }

    public Area eliminarAreaPorIndice(int idx) {
        if (idx < 0 || idx >= areas.size()) return null;
        Area removed = areas.remove(idx);

        GestionTurnosHospital.persistIfPossible();
        return removed;
    }

    public void editarArea(int idx, String nuevoNombre, Integer cupM, Integer cupT, Integer cupN) {
        Area a = getAreaPorIndice(idx);
        if (a == null) throw new IllegalArgumentException("Índice de área inválido");
        if (nuevoNombre != null && !nuevoNombre.isBlank()) a.setNombre(nuevoNombre);
        if (cupM != null) a.setCuposManana(cupM);
        if (cupT != null) a.setCuposTarde(cupT);
        if (cupN != null) a.setCuposNoche(cupN);
        
        GestionTurnosHospital.persistIfPossible();
    }

    // ====== TURNOS (NUEVO) ======

    /** Registra un turno asignado (se invoca desde la asignación automática o manual). */
    public synchronized void registrarTurno(Enfermera e, Area a, LocalDate fecha, Bloque bloque) {
        if (e == null || a == null || fecha == null || bloque == null)
            throw new IllegalArgumentException("Datos de turno inválidos");
        turnos.add(new Turno(fecha, bloque, a, e));
    }

    /**
     * Devuelve los turnos dentro de [desde, hasta], con filtros opcionales por área y/o enfermera.
     * `areaNombre` y `enfermeraNombre` pueden ser null para no filtrar.
     */
    public synchronized List<Turno> getTurnos(LocalDateTime desde,
                                              LocalDateTime hasta,
                                              String areaNombre,
                                              String enfermeraNombre) {
        List<Turno> out = new ArrayList<>();
        for (Turno t : turnos) {
            LocalDateTime ini = t.inicio();
            boolean enRango = (ini.isEqual(desde) || ini.isAfter(desde)) &&
                              (ini.isBefore(hasta) || ini.isEqual(hasta));
            if (!enRango) continue;

            if (areaNombre != null && !areaNombre.equals(t.getArea().getNombre())) continue;
            if (enfermeraNombre != null && !enfermeraNombre.equals(t.getEnfermera().getNombre())) continue;

            out.add(t);
        }
        out.sort(Comparator.comparing(Turno::inicio));
        return Collections.unmodifiableList(out);
    }

    
    public synchronized void clearTurnos() { turnos.clear(); }
}