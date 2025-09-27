/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.gestionturnoshospital;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

/**
 *
 * @author simon
 */
public class DlgAsignarTurnos extends javax.swing.JDialog {
    
    private final EnfermeraService enfSvc;
    private final Hospital hospital;

    // --- Filtros (modificado: combo de fechas válidas) ---
    private final JComboBox<String> cboFecha = new JComboBox<>();
    private final JButton btnRefrescarFechas = new JButton("↻");
    private final JComboBox<String> cbBloque = new JComboBox<>(new String[]{"MANANA","TARDE","NOCHE"});
    private final JComboBox<Area> cbArea = new JComboBox<>();

    // --- Botones ---
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnAsignar = new JButton("Asignar");
    private final JButton btnCerrar = new JButton("Cerrar");

    // --- Tabla ---
    private final JTable tabla = new JTable();

    public DlgAsignarTurnos(java.awt.Frame parent, boolean modal) {
        this(parent, modal, GestionTurnosHospital.getEnfSvc(), GestionTurnosHospital.getHospital());
    }

    public DlgAsignarTurnos(Frame parent, boolean modal, EnfermeraService enfSvc, Hospital hospital) {
        super(parent, modal);
        this.enfSvc = enfSvc;
        this.hospital = hospital;
        setTitle("Asignar Turnos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(720, 420);
        setLocationRelativeTo(parent);

        initUI();
        configurarTabla();
        cargarAreas();
        cargarFechasValidas();                 // <-- llena el combo de fechas desde disponibilidades
        if (cboFecha.getItemCount() > 0) cboFecha.setSelectedIndex(0);
        cbBloque.setSelectedItem("MANANA");

        wireEvents();
    }

    // ---------------- UI ----------------

    private void initUI() {
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Fecha:"));
        cboFecha.setPrototypeDisplayValue("YYYY-MM-DD");
        filtros.add(cboFecha);
        btnRefrescarFechas.setToolTipText("Recargar fechas válidas");
        filtros.add(btnRefrescarFechas);

        filtros.add(new JLabel("Bloque:"));
        filtros.add(cbBloque);

        filtros.add(new JLabel("Área:"));
        filtros.add(cbArea);

        filtros.add(btnBuscar);

        JScrollPane sp = new JScrollPane(tabla);

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        abajo.add(btnAsignar);
        abajo.add(btnCerrar);

        getContentPane().setLayout(new BorderLayout(8,8));
        getContentPane().add(filtros, BorderLayout.NORTH);
        getContentPane().add(sp, BorderLayout.CENTER);
        getContentPane().add(abajo, BorderLayout.SOUTH);
    }

    private void configurarTabla() {
        tabla.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"RUT", "Nombre"}
        ){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void cargarAreas() {
        cbArea.setModel(new DefaultComboBoxModel<>(
                hospital.getAreas().toArray(new Area[0])
        ));
        cbArea.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Area) setText(((Area) value).getNombre());
                return this;
            }
        });
    }

    /** Llena el combo con las fechas distintas encontradas en las disponibilidades */
    private void cargarFechasValidas() {
        // TreeSet para que salgan ordenadas
        java.util.Set<LocalDate> fechas = new java.util.TreeSet<>();
        for (Enfermera e : enfSvc.listar()) {
            for (Disponibilidad d : e.getDisponibilidades()) {
                if (d != null && d.getFecha() != null) {
                    fechas.add(d.getFecha());
                }
            }
        }
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (LocalDate f : fechas) model.addElement(f.toString()); // YYYY-MM-DD
        cboFecha.setModel(model);
    }

    private void wireEvents() {
        btnRefrescarFechas.addActionListener(e -> {
            cargarFechasValidas();
            if (cboFecha.getItemCount() > 0) cboFecha.setSelectedIndex(0);
        });

        btnBuscar.addActionListener(e -> buscar());
        btnAsignar.addActionListener(e -> asignar());
        btnCerrar.addActionListener(e -> dispose());
    }

    // ---------------- Helpers ----------------

    private LocalDate parseFecha() {
        Object sel = cboFecha.getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una fecha válida.");
            return null;
        }
        try {
            return LocalDate.parse(sel.toString().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida (use yyyy-MM-dd)");
            return null;
        }
    }

    private Bloque parseBloque() {
        try {
            return Bloque.valueOf(cbBloque.getSelectedItem().toString());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Bloque inválido");
            return null;
        }
    }

    // ---------------- Lógica ----------------

    private void buscar() {
        LocalDate fecha = parseFecha();
        Bloque bloque = parseBloque();
        Area area = (Area) cbArea.getSelectedItem();
        if (fecha == null || bloque == null || area == null) return;

        DefaultTableModel m = (DefaultTableModel) tabla.getModel();
        m.setRowCount(0);

        List<Enfermera> todas = enfSvc.listar();
        for (Enfermera e : todas) {
            if (!e.disponiblePara(fecha, bloque)) continue;

            boolean cumpleSkills = true;
            for (String req : area.getSkillsRequeridas()) {
                if (!e.tieneSkill(req)) { cumpleSkills = false; break; }
            }
            if (!cumpleSkills) continue;

            m.addRow(new Object[]{ e.getRut(), e.getNombre() });
        }

        if (m.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay enfermeras que cumplan disponibilidad + skills.");
        }
    }

    private void asignar() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona una enfermera."); return; }

        LocalDate fecha = parseFecha();
        Bloque bloque = parseBloque();
        Area area = (Area) cbArea.getSelectedItem();
        if (fecha == null || bloque == null || area == null) return;

        String rut = tabla.getValueAt(row, 0).toString();
        Enfermera e = enfSvc.buscarPorRut(rut);
        if (e == null) { JOptionPane.showMessageDialog(this, "Enfermera no encontrada."); return; }

        if (!e.disponiblePara(fecha, bloque)) {
            JOptionPane.showMessageDialog(this, "Ya no está disponible para ese turno.");
            return;
        }

        int HORAS_TURNO = 8;
        if (!e.puedeTomarHoras(HORAS_TURNO)) {
            JOptionPane.showMessageDialog(this, "Supera sus horas mensuales.");
            return;
        }

        // Marcar asignación en memoria
        e.setDisponibilidad(fecha, bloque, false);
        e.registrarHoras(HORAS_TURNO);

        // Persistencia simple en archivo de texto (append)
        try {
            Path out = Paths.get("asignaciones.txt");
            try (BufferedWriter bw = Files.newBufferedWriter(out,
                    StandardCharsets.UTF_8,
                    Files.exists(out) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE)) {
                bw.write(String.format("%s\t%s\t%s\t%s\t%s",
                        fecha, bloque, area.getNombre(), e.getRut(), e.getNombre()));
                bw.newLine();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo escribir asignaciones.txt:\n" + ex.getMessage());
        }

        JOptionPane.showMessageDialog(this, "Asignado OK.");
        buscar(); // refresca la lista tras asignar
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
 
    
    
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

