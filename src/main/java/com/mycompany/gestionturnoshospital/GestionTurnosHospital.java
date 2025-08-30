package com.mycompany.GestionTurnosHospital;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class GestionTurnosHospital {

    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static final EnfermeraService enfSvc = new EnfermeraService();   // catálogo de enfermeras
    private static final Hospital hospital = new Hospital("Hospital Demo");  // dueño de las áreas

    public static void main(String[] args) throws IOException {
        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Opcion: ");
            System.out.println();
            switch (opcion) {
                case 1: importarDatosDemo(); pause(); break;
                case 2: menuGestionDatos(); break;
                case 3: menuAsignarTurnos(); break;
                case 4: visualizarCalendario(); pause(); break;
                case 5: menuConflictos(); pause(); break;
                case 0: System.out.println("Saliendo..."); break;
                default: System.out.println("Opcion invalida"); pause();
            }
        } while (opcion != 0);
    }

    // ===================== MENUS =====================

    private static void mostrarMenuPrincipal() {
        System.out.println("\n=== Menu de Sistema de Gestion de turnos Hospitalario ===\n");
        System.out.println("1) Importar datos");
        System.out.println("2) Gestionar datos");
        System.out.println("3) Asignar turnos");
        System.out.println("4) Visualizar Calendario");
        System.out.println("5) Conflictos");
        System.out.println("0) Salir");
    }

    private static void menuGestionDatos() throws IOException {
        int op;
        do {
            System.out.println("\n--- GESTIONAR DATOS ---");
            System.out.println("1) Gestionar Enfermeras");
            System.out.println("2) Gestionar Areas");
            System.out.println("0) Volver al Menu Principal");
            op = leerEntero("Opcion: ");
            switch (op) {
                case 1: menuGestionEnfermeras(); break;
                case 2: menuGestionAreas(); break;
                case 0: break;
                default: System.out.println("Opcion invalida"); pause();
            }
        } while (op != 0);
    }

    // --------- Enfermeras (usa EnfermeraService) ---------
    private static void menuGestionEnfermeras() throws IOException {
        int op;
        do {
            System.out.println("\n--- Gestionar Enfermeras ---");
            System.out.println("Total: " + enfSvc.count());
            System.out.println("1) Agregar");
            System.out.println("2) Editar");
            System.out.println("3) Eliminar");
            System.out.println("4) Listar");
            System.out.println("5) Gestionar Disponibilidad");
            System.out.println("0) Volver");
            op = leerEntero("Opcion: ");
            switch (op) {
                case 1: uiAgregarEnfermera(); pause(); break;
                case 2: uiEditarEnfermera();  pause(); break;
                case 3: uiEliminarEnfermera(); pause(); break;
                case 4: uiListarEnfermeras();  pause(); break;
                case 5: uiDisponibilidad();    break;
                case 0: break;
                default: System.out.println("Opcion invalida"); pause();
            }
        } while (op != 0);
    }

    private static void uiAgregarEnfermera() throws IOException {
        System.out.println("\n[Agregar Enfermera]");
        String nombre = leerLinea("Nombre: ");
        String rut    = leerLinea("RUT: ");
        int horasMax  = leerEntero("Horas máximas mensuales (ej. 160): ");
        List<String> skills = toListaSkills(leerLinea("Skills (coma): "));
        enfSvc.agregar(nombre, rut, skills, horasMax);
        System.out.println("OK: Enfermera agregada.");
    }

    private static void uiEditarEnfermera() throws IOException {
        if (enfSvc.count() == 0) { System.out.println("No hay enfermeras."); return; }
        uiListarEnfermeras();
        int idx = leerEntero("Seleccione # a editar: ") - 1;
        if (!enfSvc.indiceValido(idx)) { System.out.println("Indice invalido."); return; }
        String nuevoNombre = leerLineaOpcional("Nuevo nombre (Enter mantiene): ");
        Integer nuevoMax = leerEnteroOpcional("Nuevas horas max mensuales (Enter mantiene): ");
        enfSvc.editar(idx, nuevoNombre, nuevoMax);
        System.out.println("OK: Actualizada.");
    }

    private static void uiEliminarEnfermera() throws IOException {
        if (enfSvc.count() == 0) { System.out.println("No hay enfermeras."); return; }
        uiListarEnfermeras();
        int idx = leerEntero("Seleccione # a eliminar: ") - 1;
        if (!enfSvc.indiceValido(idx)) { System.out.println("Indice invalido."); return; }
        System.out.println("Eliminada: " + enfSvc.eliminar(idx).getNombre());
    }

    private static void uiListarEnfermeras() {
        System.out.println("\n#  Nombre (RUT)");
        List<Enfermera> list = enfSvc.listar();
        for (int i = 0; i < list.size(); i++) {
            Enfermera e = list.get(i);
            System.out.println((i + 1) + ") " + e.getNombre() + " (" + e.getRut() + ")");
        }
    }

    // --- Submenú Disponibilidad ---
    private static void uiDisponibilidad() throws IOException {
        if (enfSvc.count() == 0) { System.out.println("No hay enfermeras."); pause(); return; }
        uiListarEnfermeras();
        int idx = leerEntero("Seleccione # para gestionar disponibilidad: ") - 1;
        if (!enfSvc.indiceValido(idx)) { System.out.println("Indice invalido."); pause(); return; }
        Enfermera e = enfSvc.listar().get(idx);

        int op;
        do {
            System.out.println("\n[Disponibilidad de " + e.getNombre() + "]");
            System.out.println("1) Agregar/Actualizar disponibilidad");
            System.out.println("2) Listar disponibilidad");
            System.out.println("0) Volver");
            op = leerEntero("Opcion: ");
            switch (op) {
                case 1:
                    LocalDate fecha = leerFecha("Fecha (YYYY-MM-DD): ");
                    Bloque bloque   = leerBloque("Bloque [1=MANANA, 2=TARDE, 3=NOCHE]: ");
                    boolean disp    = leerSiNo("¿Disponible? (S/N): ");
                    e.setDisponibilidad(fecha, bloque, disp);
                    System.out.println("OK: disponibilidad actualizada.");
                    pause();
                    break;
                case 2:
                    System.out.println("\nFecha        Bloque   Estado");
                    for (Disponibilidad d : e.getDisponibilidad()) {
                        System.out.println(d.getFecha() + "   " + d.getBloque() + "   " + (d.isDisponible()?"DISP":"NO"));
                    }
                    pause();
                    break;
                case 0: break;
                default: System.out.println("Opcion invalida"); pause();
            }
        } while (op != 0);
    }

    // --------- Áreas (usa Hospital) ---------
    private static void menuGestionAreas() throws IOException {
        int op;
        do {
            System.out.println("\n--- Gestionar Áreas (Total: " + hospital.countAreas() + ") ---");
            System.out.println("1) Agregar");
            System.out.println("2) Editar");
            System.out.println("3) Eliminar");
            System.out.println("4) Listar");
            System.out.println("0) Volver");
            op = leerEntero("Opcion: ");
            switch (op) {
                case 1: agregarArea();  pause(); break;
                case 2: editarArea();   pause(); break;
                case 3: eliminarArea(); pause(); break;
                case 4: listarAreas();  pause(); break;
                case 0: break;
                default: System.out.println("Opcion invalida"); pause();
            }
        } while (op != 0);
    }

    private static void agregarArea() throws IOException {
        System.out.println("\n[Agregar Área]");
        String nombre = leerLinea("Nombre de área: ");
        int id        = leerEntero("ID (int): ");
        int cupM      = leerEntero("Cupos MANANA: ");
        int cupT      = leerEntero("Cupos TARDE: ");
        int cupN      = leerEntero("Cupos NOCHE: ");
        List<String> skills = toListaSkills(leerLinea("Skills requeridas (coma): "));
        hospital.agregarArea(new Area(id, nombre, cupM, cupT, cupN, skills));
        System.out.println("OK: Área agregada.");
    }

    private static void editarArea() throws IOException {
        if (hospital.countAreas() == 0) { System.out.println("No hay áreas."); return; }
        listarAreas();
        int idx = leerEntero("Seleccione # a editar: ") - 1;
        try {
            String nuevoNombre = leerLineaOpcional("Nuevo nombre (Enter mantiene): ");
            Integer nuevoM = leerEnteroOpcional("Nuevo cupo MANANA (Enter mantiene): ");
            Integer nuevoT = leerEnteroOpcional("Nuevo cupo TARDE (Enter mantiene): ");
            Integer nuevoN = leerEnteroOpcional("Nuevo cupo NOCHE (Enter mantiene): ");
            hospital.editarArea(idx, nuevoNombre, nuevoM, nuevoT, nuevoN);
            System.out.println("OK: Área actualizada.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void eliminarArea() throws IOException {
        if (hospital.countAreas() == 0) { System.out.println("No hay áreas."); return; }
        listarAreas();
        int idx = leerEntero("Seleccione # a eliminar: ") - 1;
        Area a = hospital.eliminarAreaPorIndice(idx);
        if (a == null) { System.out.println("Indice invalido."); return; }
        System.out.println("Eliminada: " + a.getNombre());
    }

    private static void listarAreas() {
        System.out.println("\n#  Área (ID)  Cupos[M/T/N]  Skills");
        List<Area> lista = hospital.getAreas();
        for (int i = 0; i < lista.size(); i++) {
            Area a = lista.get(i);
            System.out.println((i + 1) + ") " + a.getNombre() + " (" + a.getId() + ")  "
                    + a.getCuposManana() + "/" + a.getCuposTarde() + "/" + a.getCuposNoche()
                    + "  " + a.getSkillsRequeridas());
        }
    }

    // --------- Asignación / Calendario / Conflictos (stubs) ---------
    private static void menuAsignarTurnos() throws IOException {
        int op;
        do {
            System.out.println("\n--- Asignar turnos ---");
            System.out.println("1) Asignación automática (MVP)");
            System.out.println("2) Gestión manual");
            System.out.println("0) Volver");
            op = leerEntero("Opcion: ");
            switch (op) {
                case 1: System.out.println("(Pendiente) Motor automático."); pause(); break;
                case 2: System.out.println("(Pendiente) Gestión manual.");   pause(); break;
                case 0: break;
                default: System.out.println("Opcion invalida"); pause();
            }
        } while (op != 0);
    }

    private static void visualizarCalendario() {
        System.out.println("\n--- Visualización de Calendario ---");
        System.out.println("(Pendiente) Mostrar calendario por día/semana/mes, filtros por área/enfermera.");
    }

    private static void menuConflictos() {
        System.out.println("\n--- Conflictos ---");
        System.out.println("(Pendiente) Mostrar conflictos detectados y opciones de resolución.");
    }

    // -------------------- UTILIDADES --------------------
    private static void importarDatosDemo() {
        enfSvc.clear();
        enfSvc.agregar("Ana", "12.345.678-9", Arrays.asList("UCI","Ventilacion"), 160);
        enfSvc.agregar("Bea", "10.111.222-3", Arrays.asList("Pediatria"), 160);

        hospital.clearAreas();
        hospital.agregarArea(new Area(1, "UCI", 2, 2, 2, Arrays.asList("UCI","Ventilacion")));
        hospital.agregarArea(new Area(2, "Pediatria", 3, 3, 2, Arrays.asList("Pediatria")));

        System.out.println("Datos de ejemplo importados.");
    }

    private static int leerEntero(String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String s = br.readLine();
            try { return Integer.parseInt(s.trim()); }
            catch (NumberFormatException ex) { System.out.println("Numero invalido, reintente."); }
        }
    }
    private static String leerLinea(String prompt) throws IOException {
        System.out.print(prompt); return br.readLine().trim();
    }
    private static String leerLineaOpcional(String prompt) throws IOException {
        System.out.print(prompt); String s = br.readLine(); return (s==null)?"":s.trim();
    }
    private static Integer leerEnteroOpcional(String prompt) throws IOException {
        System.out.print(prompt); String s = br.readLine().trim();
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException ex) { return null; }
    }
    private static List<String> toListaSkills(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new ArrayList<>();
        List<String> out = new ArrayList<>();
        String[] parts = csv.split(",");
        for (String p : parts) { String v = p.trim(); if (!v.isEmpty()) out.add(v); }
        return out;
    }
    private static LocalDate leerFecha(String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String s = br.readLine();
            try { return LocalDate.parse(s.trim()); }
            catch (DateTimeParseException ex) { System.out.println("Formato inválido (use YYYY-MM-DD)."); }
        }
    }
    private static Bloque leerBloque(String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String s = br.readLine().trim();
            if (s.equals("1")) return Bloque.MANANA;
            if (s.equals("2")) return Bloque.TARDE;
            if (s.equals("3")) return Bloque.NOCHE;
            System.out.println("Opcion inválida.");
        }
    }
    private static boolean leerSiNo(String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String s = br.readLine().trim().toUpperCase();
            if (s.equals("S")) return true;
            if (s.equals("N")) return false;
            System.out.println("Responda S o N.");
        }
    }
    private static void pause() throws IOException {
        System.out.print("\n[Enter] para continuar...");
        br.readLine();
    }
}