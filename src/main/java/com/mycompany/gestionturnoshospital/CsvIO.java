package com.mycompany.gestionturnoshospital;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import excepciones.DatosInconsistentesException;

public final class CsvIO {
    private static final String ENF_CSV  = "enfermeras.csv";
    private static final String AREA_CSV = "areas.csv";
    private static final String DISP_CSV = "disponibilidades.csv";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    // ---------- SAVE ----------
    public static void saveAll(Path folder, EnfermeraService enfSvc, Hospital hospital) {
        try {
            Files.createDirectories(folder);
            saveEnfermeras(folder, enfSvc);
            saveAreas(folder, hospital);
            saveDisponibilidades(folder, enfSvc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveEnfermeras(Path folder, EnfermeraService enfSvc) throws IOException {
        Path f = folder.resolve(ENF_CSV);
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(f, StandardCharsets.UTF_8))) {
            pw.println("rut,nombre,horasMensualMax,skills");
            for (Enfermera e : enfSvc.listar()) {
                String skills = String.join(";", e.getSkills()); // usa ; para no chocar con comas
                pw.printf("%s,%s,%d,%s%n",
                        sanitize(e.getRut()),
                        sanitize(e.getNombre()),
                        e.getHorasMensualMax(),
                        sanitize(skills));
            }
        }
    }

    public static void saveAreas(Path folder, Hospital hospital) throws IOException {
        Path f = folder.resolve(AREA_CSV);
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(f, StandardCharsets.UTF_8))) {
            pw.println("id,nombre,cuposManana,cuposTarde,cuposNoche,skillsRequeridas");
            for (Area a : hospital.getAreas()) {
                String skills = String.join(";", a.getSkillsRequeridas());
                pw.printf("%d,%s,%d,%d,%d,%s%n",
                        a.getId(),
                        sanitize(a.getNombre()),
                        a.getCuposManana(),
                        a.getCuposTarde(),
                        a.getCuposNoche(),
                        sanitize(skills));
            }
        }
    }

    public static void saveDisponibilidades(Path folder, EnfermeraService enfSvc) throws IOException {
        Path f = folder.resolve(DISP_CSV);
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(f, StandardCharsets.UTF_8))) {
            pw.println("rut,fecha,bloque,disponible");
            for (Enfermera e : enfSvc.listar()) {
                for (Disponibilidad d : e.getDisponibilidad()) {
                    pw.printf("%s,%s,%s,%s%n",
                            sanitize(e.getRut()),
                            d.getFecha().format(FMT),
                            d.getBloque().name(),          // MANANA|TARDE|NOCHE
                            d.isDisponible() ? "1" : "0");
                }
            }
        }
    }

    // ---------- LOAD ----------
    public static void loadAll(Path folder, EnfermeraService enfSvc, Hospital hospital) throws IOException {
        // limpiar actuales
        enfSvc.clear();
        hospital.clearAreas();

        // ENFERMERAS
        Path enf = findByHeader(folder, "rut,nombre,horasmensualmax,skills");
        if (enf != null) {
            try (BufferedReader br = Files.newBufferedReader(enf, StandardCharsets.UTF_8)) {
                String hdr = sanitizeHeader(br.readLine());
                String line;
                while ((line = br.readLine()) != null) {
                    String[] t = splitCsv(line);
                    try {
                        if (t.length < 4) {
                            throw new DatosInconsistentesException("ENF: columnas insuficientes: " + Arrays.toString(t));
                        }
                        String rut = t[0];
                        String nombre = t[1];
                        int max = Integer.parseInt(t[2].trim());
                        List<String> skills = parseSkills(t[3]);
                        enfSvc.agregar(nombre, rut, skills, max);
                    } catch (NumberFormatException e) {
                        try {
                            throw new DatosInconsistentesException(
                                    "ENF: error numérico en fila: " + Arrays.toString(t), e);
                        } catch (DatosInconsistentesException die) {
                            System.err.println(die.getMessage());
                            // continuar con siguiente fila
                        }
                    } catch (DatosInconsistentesException die) {
                        System.err.println(die.getMessage());
                        // continuar con siguiente fila
                    }
                }
            }
        }

        // AREAS
        Path ar = findByHeader(folder, "id,nombre,cuposmanana,cupostarde,cuposnoche,skillsrequeridas");
        if (ar != null) {
            try (BufferedReader br = Files.newBufferedReader(ar, StandardCharsets.UTF_8)) {
                String hdr = sanitizeHeader(br.readLine());
                String line;
                while ((line = br.readLine()) != null) {
                    String[] t = splitCsv(line);
                    if (t.length < 6) continue;
                    int id = Integer.parseInt(t[0].trim());
                    String nombre = t[1];
                    int cm = Integer.parseInt(t[2].trim());
                    int ct = Integer.parseInt(t[3].trim());
                    int cn = Integer.parseInt(t[4].trim());
                    List<String> skills = parseSkills(t[5]);
                    hospital.agregarArea(new Area(id, nombre, cm, ct, cn, skills));
                }
            }
        }

        // DISPONIBILIDADES (acepta con/sin columna area; usamos la simple)
        Path disp = findByHeader(folder, "rut,fecha,bloque,disponible");
        if (disp == null) disp = findByHeader(folder, "rut,fecha,bloque,area,disponible");
        if (disp != null) {
            try (BufferedReader br = Files.newBufferedReader(disp, StandardCharsets.UTF_8)) {
                String hdr = sanitizeHeader(br.readLine());
                boolean conArea = hdr.equalsIgnoreCase("rut,fecha,bloque,area,disponible");
                String line;
                while ((line = br.readLine()) != null) {
                    String[] t = splitCsv(line);
                    try {
                        if ((!conArea && t.length < 4) || (conArea && t.length < 5)) {
                            throw new DatosInconsistentesException("DISP: columnas insuficientes: " + Arrays.toString(t));
                        }
                        String rut = t[0];
                        Enfermera e = enfSvc.buscarPorRut(rut);
                        if (e == null) {
                            throw new DatosInconsistentesException("DISP: RUT no encontrado: " + rut);
                        }
                        var fecha = java.time.LocalDate.parse(t[1].trim());
                        var bloque = Bloque.valueOf(t[2].trim().toUpperCase());
                        String dispStr = conArea ? t[4] : t[3];
                        boolean disponible = dispStr.trim().equals("1") || dispStr.trim().equalsIgnoreCase("true");
                        e.setDisponibilidad(fecha, bloque, disponible);
                    } catch (Exception any) { // DateTimeParseException, IllegalArgumentException (enum), etc.
                        try {
                            throw new DatosInconsistentesException(
                                    "DISP: error parseando fila: " + Arrays.toString(t), any);
                        } catch (DatosInconsistentesException die) {
                            System.err.println(die.getMessage());
                            // continuar con siguiente fila
                        }
                    }
                }
            }
        }
    }

    // ---------- helpers ----------
    private static String sanitize(String s){ return s==null? "" : s.replace(",", " "); }
    private static String sanitizeHeader(String h){ return (h==null)? "" : h.replace("\uFEFF","").trim().toLowerCase(); }
    private static String[] splitCsv(String line){
        // simple split; si necesitas comillas, implementa parser más robusto
        return line.split(",", -1);
    }
    private static List<String> parseSkills(String s){
        if (s==null || s.isBlank()) return Collections.emptyList();
        // aceptamos ; o , como separadores
        return Arrays.stream(s.split("[;,]"))
                .map(String::trim)
                .filter(x->!x.isEmpty())
                .collect(Collectors.toList());
    }
    private static Path findByHeader(Path folder, String headerLowercase) throws IOException {
        try (var st = Files.list(folder)) {
            for (Path p : st.collect(Collectors.toList())) {
                String name = p.getFileName().toString().toLowerCase();
                if (!name.endsWith(".csv")) continue;
                try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                    String hdr = sanitizeHeader(br.readLine());
                    if (hdr.equals(headerLowercase.toLowerCase())) return p;
                } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
