package com.mycompany.gestionturnoshospital;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public final class AppConfig {
    private static final String DIR_APP = System.getProperty("user.home") + File.separator + ".gestionturnos";
    private static final String FILE_CFG = DIR_APP + File.separator + "config.properties";
    private static final String KEY_FOLDER = "carpetaDatos";

    public static void saveLastFolder(Path folder) {
        try {
            Files.createDirectories(Paths.get(DIR_APP));
            Properties p = new Properties();
            p.setProperty(KEY_FOLDER, folder.toAbsolutePath().toString());
            try (OutputStream os = Files.newOutputStream(Paths.get(FILE_CFG))) {
                p.store(os, "Configuración GestionTurnosHospital");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Path loadLastFolder() {
        try {
            Path cfg = Paths.get(FILE_CFG);
            if (!Files.exists(cfg)) return null;
            Properties p = new Properties();
            try (InputStream is = Files.newInputStream(cfg)) {
                p.load(is);
            }
            String v = p.getProperty(KEY_FOLDER);
            if (v == null || v.isBlank()) return null;
            Path f = Paths.get(v);
            return Files.isDirectory(f) ? f : null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}