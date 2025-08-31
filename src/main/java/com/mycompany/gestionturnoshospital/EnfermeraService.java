package com.mycompany.gestionturnoshospital;

import java.util.*;

public class EnfermeraService {
    private final List<Enfermera> store = new ArrayList<>();
    private final Map<String, Enfermera> indexByRut = new HashMap<>();

    private String key(String rut) {
        if (rut == null) return "";
        return rut.replace(".", "").replace("-", "").replace(" ", "").toUpperCase();
    }

    // ---- CRUD ----
    public void agregar(String nombre, String rut, List<String> skills, int horasMax) {
        String k = key(rut);
        if (indexByRut.containsKey(k)) throw new IllegalArgumentException("Ya existe enfermera con RUT " + rut);
        Enfermera e = new Enfermera(nombre, rut, skills, horasMax);
        store.add(e);
        indexByRut.put(k, e);
    }

    public void add(Enfermera e) {
        if (e == null) return;
        String k = key(e.getRut());
        if (indexByRut.containsKey(k)) throw new IllegalArgumentException("Ya existe enfermera con RUT " + e.getRut());
        store.add(e);
        indexByRut.put(k, e);
    }

    public void editar(int idx, String nuevoNombre, Integer nuevoMax) {
        Enfermera e = store.get(idx);
        if (nuevoNombre != null && !nuevoNombre.isBlank()) e.setNombre(nuevoNombre);
        if (nuevoMax != null) e.setHorasMensualMax(nuevoMax);
        // RUT es final → índice no cambia
    }

    public Enfermera eliminar(int idx) {
        Enfermera e = store.remove(idx);
        if (e != null) indexByRut.remove(key(e.getRut()));
        return e;
    }

    // ---- Consultas ----
    public Enfermera buscarPorRut(String rut) { return indexByRut.get(key(rut)); }
    public boolean existeRut(String rut)      { return indexByRut.containsKey(key(rut)); }
    public List<Enfermera> listar()           { return Collections.unmodifiableList(store); }
    public int count()                        { return store.size(); }
    public boolean indiceValido(int idx)      { return idx >= 0 && idx < store.size(); }

    public void clear() { store.clear(); indexByRut.clear(); }
}