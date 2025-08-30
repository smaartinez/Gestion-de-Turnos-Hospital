package com.mycompany.GestionTurnosHospital;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Enfermera {
    private String nombre;
    private final String rut; // identidad
    private final List<String> skills;
    private final List<Disponibilidad> disponibilidad;
    private int horasMensualMax;
    private int horasAcumuladas;

    // ------------ Constructores ------------
    public Enfermera(String nombre, String rut, List<String> skills, int horasMensualMax) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre inválido");
        if (rut == null || rut.isBlank())       throw new IllegalArgumentException("RUT inválido");
        if (horasMensualMax < 0)                throw new IllegalArgumentException("Horas máximas negativas");

        this.nombre = nombre.trim();
        this.rut = rut.trim();
        this.skills = new ArrayList<>();
        if (skills != null) for (String s : skills) addSkill(s);
        this.disponibilidad = new ArrayList<>();
        this.horasMensualMax = horasMensualMax;
        this.horasAcumuladas = 0;
    }

    public Enfermera(String nombre, String rut) {
        this(nombre, rut, new ArrayList<>(), 160);
    }

    // ------------ Getters ------------
    public String getNombre() { return nombre; }
    public String getRut()    { return rut; }
    public List<String> getSkills() { return Collections.unmodifiableList(skills); }
    public List<Disponibilidad> getDisponibilidad() { return Collections.unmodifiableList(disponibilidad); }
    public int getHorasMensualMax() { return horasMensualMax; }
    public int getHorasAcumuladas() { return horasAcumuladas; }
    public int horasRestantes() { return Math.max(0, horasMensualMax - horasAcumuladas); }

    // ------------ Setters controlados ------------
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre inválido");
        this.nombre = nombre.trim();
    }
    public void setHorasMensualMax(int max) {
        if (max < 0) throw new IllegalArgumentException("Horas máximas negativas");
        if (horasAcumuladas > max) throw new IllegalStateException("Acumuladas superan el nuevo máximo");
        this.horasMensualMax = max;
    }

    // (Opcionales para SIA1.3 estricta)
    public void setSkills(List<String> nuevas) {
        this.skills.clear();
        if (nuevas != null) for (String s : nuevas) addSkill(s);
    }
    public void setDisponibilidad(List<Disponibilidad> lista) {
        this.disponibilidad.clear();
        if (lista != null) this.disponibilidad.addAll(lista);
    }
    public void setHorasAcumuladas(int h) {
        if (h < 0) throw new IllegalArgumentException("Horas negativas");
        if (h > horasMensualMax) throw new IllegalArgumentException("Supera máximo mensual");
        this.horasAcumuladas = h;
    }

    // ------------ Skills (con sobrecarga) ------------
    public boolean addSkill(String skill) {
        String s = normalizeSkill(skill);
        if (s.isEmpty()) return false;
        if (!skills.contains(s)) { skills.add(s); return true; }
        return false;
    }
    // Sobrecarga: varargs
    public int addSkill(String... nuevas) {
        int c = 0;
        if (nuevas != null) for (String s : nuevas) if (addSkill(s)) c++;
        return c;
    }
    public boolean removeSkill(String skill) { return skills.remove(normalizeSkill(skill)); }
    public boolean tieneSkill(String skill) { return skills.contains(normalizeSkill(skill)); }
    private String normalizeSkill(String s) { return (s == null) ? "" : s.trim(); }

    // ------------ Disponibilidad (con sobrecarga) ------------
    public void setDisponibilidad(LocalDate fecha, Bloque bloque, boolean disponible) {
        if (fecha == null || bloque == null) throw new IllegalArgumentException("Fecha/bloque inválidos");
        int idx = indexDisponibilidad(fecha, bloque);
        Disponibilidad nueva = new Disponibilidad(fecha, bloque, disponible);
        if (idx >= 0) disponibilidad.set(idx, nueva); else disponibilidad.add(nueva);
    }
    // Sobrecarga: por objeto
    public void setDisponibilidad(Disponibilidad d) {
        if (d == null) throw new IllegalArgumentException("Disponibilidad nula");
        setDisponibilidad(d.getFecha(), d.getBloque(), d.isDisponible());
    }
    public boolean removeDisponibilidad(LocalDate fecha, Bloque bloque) {
        int idx = indexDisponibilidad(fecha, bloque);
        if (idx >= 0) { disponibilidad.remove(idx); return true; }
        return false;
    }
    public boolean disponiblePara(LocalDate fecha, Bloque bloque) {
        int idx = indexDisponibilidad(fecha, bloque);
        return (idx >= 0) && disponibilidad.get(idx).isDisponible();
    }
    private int indexDisponibilidad(LocalDate fecha, Bloque bloque) {
        for (int i = 0; i < disponibilidad.size(); i++) {
            Disponibilidad d = disponibilidad.get(i);
            if (d.getFecha().equals(fecha) && d.getBloque() == bloque) return i;
        }
        return -1;
    }

    // ------------ Horas ------------
    public boolean puedeTomarHoras(int horasTurno) {
        return horasTurno >= 0 && (horasAcumuladas + horasTurno) <= horasMensualMax;
    }
    public boolean registrarHoras(int horasTurno) {
        if (!puedeTomarHoras(horasTurno)) return false;
        this.horasAcumuladas += horasTurno;
        return true;
    }
    public void resetHorasAcumuladas() { this.horasAcumuladas = 0; }

 
}