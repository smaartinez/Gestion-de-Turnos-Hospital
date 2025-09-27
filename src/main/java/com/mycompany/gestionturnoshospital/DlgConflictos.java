/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.gestionturnoshospital;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
/**
 *
 * @author samuelastudillo
 */
public class DlgConflictos extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgConflictos.class.getName());

    /**
     * Creates new form DlgConflictos
     */
    private final Hospital hospital;
    private final EnfermeraService enfSvc;

    // Top filters
    private JTextField txtFecha;
    private JComboBox<Bloque> cboBloque;
    private JCheckBox chkTodasAreas;
    private JList<Area> lstAreas;
    private JCheckBox chkRespetarSkills;
    private JCheckBox chkRespetarHoras;
    private JButton btnAnalizar;
    private JButton btnLimpiar;

    // Tabs
    private JTabbedPane tabs;

    // Tab 1: Cobertura por área
    private JTable tblCobertura;
    private JLabel lblContexto;
    private JLabel lblCuposTotales;
    private JLabel lblElegiblesTotales;
    private JCheckBox chkDedupe;
    private JLabel lblUnicas;

    // Tab 2: Alertas de horas
    private JTable tblHoras;
    private JCheckBox chkSoloExcedidas;

    // Tab 3: Detalle de elegibles
    private JList<Area> lstAreasDetalle;
    private JCheckBox chkFiltrarSkillsArea;
    private JCheckBox chkFiltrarHorasArea;
    private JTextField txtBuscarNombre;
    private JTable tblElegiblesArea;

    // Bottom buttons
    private JButton btnExportar;
    private JButton btnImprimir;
    private JButton btnCerrar;

    // Constantes
    private static final int HORAS_POR_TURNO = 8;

    public DlgConflictos(Frame owner, Hospital hospital, EnfermeraService enfSvc) {
        super(owner, "Conflictos de Cobertura", true);
        this.hospital = hospital;
        this.enfSvc = enfSvc;
        buildUI();
        pack();
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(900, 600));
    }

    private void buildUI() {
        setLayout(new BorderLayout(8,8));
        add(buildTopFilters(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
        hookEvents();
    }

    private JPanel buildTopFilters() {
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.gridy = 0; c.gridx = 0; c.anchor = GridBagConstraints.WEST;

        p.add(new JLabel("Fecha (YYYY-MM-DD):"), c);
        c.gridx++;
        txtFecha = new JTextField(10);
        p.add(txtFecha, c);

        c.gridx++;
        p.add(new JLabel("Bloque:"), c);
        c.gridx++;
        cboBloque = new JComboBox<>(Bloque.values());
        p.add(cboBloque, c);

        c.gridx++;
        p.add(new JLabel("Áreas:"), c);
        c.gridx++;
        chkTodasAreas = new JCheckBox("Todas");
        chkTodasAreas.setSelected(true);
        p.add(chkTodasAreas, c);

        c.gridx = 0; c.gridy = 1;
        JScrollPane spAreas = new JScrollPane();
        lstAreas = new JList<>(hospital.getAreas().toArray(new Area[0]));
        lstAreas.setVisibleRowCount(3);
        lstAreas.setEnabled(false);
        spAreas.setViewportView(lstAreas);
        spAreas.setPreferredSize(new Dimension(300, 70));
        spAreas.setBorder(new TitledBorder("Seleccionar áreas"));
        c.gridwidth = 3;
        p.add(spAreas, c);
        c.gridwidth = 1;

        c.gridx = 3;
        JPanel opts = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        opts.setBorder(new TitledBorder("Criterios"));
        chkRespetarSkills = new JCheckBox("Requerir skills del área", true);
        chkRespetarHoras = new JCheckBox("No superar horas máx.", true);
        opts.add(chkRespetarSkills);
        opts.add(chkRespetarHoras);
        p.add(opts, c);

        c.gridx = 5;
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnAnalizar = new JButton("Analizar");
        btnLimpiar  = new JButton("Limpiar");
        actions.add(btnAnalizar);
        actions.add(btnLimpiar);
        p.add(actions, c);

        return p;
    }

    private JComponent buildTabs() {
        tabs = new JTabbedPane();
        tabs.addTab("Cobertura por área", buildTabCobertura());
        tabs.addTab("Alertas de horas", buildTabHoras());
        tabs.addTab("Detalle de elegibles", buildTabDetalle());
        return tabs;
    }

    private JComponent buildTabCobertura() {
        JPanel root = new JPanel(new BorderLayout(8,8));

        lblContexto = new JLabel("—");
        root.add(lblContexto, BorderLayout.NORTH);

        tblCobertura = new JTable(new DefaultTableModel(new Object[]{
                "Área", "Cupos", "Elegibles", "Déficit", "Observación"
        }, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        root.add(new JScrollPane(tblCobertura), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        lblCuposTotales = new JLabel("Cupos totales: 0");
        lblElegiblesTotales = new JLabel("Elegibles (sin dedupe): 0");
        chkDedupe = new JCheckBox("Contar únicas");
        lblUnicas = new JLabel("Únicas: 0");
        south.add(lblCuposTotales);
        south.add(new JSeparator(SwingConstants.VERTICAL));
        south.add(lblElegiblesTotales);
        south.add(chkDedupe);
        south.add(lblUnicas);
        root.add(south, BorderLayout.SOUTH);

        return root;
    }

    private JComponent buildTabHoras() {
        JPanel root = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        chkSoloExcedidas = new JCheckBox("Solo excedidas");
        top.add(chkSoloExcedidas);
        root.add(top, BorderLayout.NORTH);

        tblHoras = new JTable(new DefaultTableModel(new Object[]{
                "Enfermera", "Horas acumuladas", "Máximo mensual", "Estado"
        }, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        root.add(new JScrollPane(tblHoras), BorderLayout.CENTER);
        return root;
    }

    private JComponent buildTabDetalle() {
        JPanel root = new JPanel(new BorderLayout(8,8));

        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(new TitledBorder("Áreas"));
        lstAreasDetalle = new JList<>(hospital.getAreas().toArray(new Area[0]));
        left.add(new JScrollPane(lstAreasDetalle), BorderLayout.CENTER);
        root.add(left, BorderLayout.WEST);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        chkFiltrarSkillsArea = new JCheckBox("Requerir skills del área", true);
        chkFiltrarHorasArea  = new JCheckBox("No superar horas", true);
        txtBuscarNombre = new JTextField(18);
        txtBuscarNombre.setToolTipText("Buscar por nombre...");
        top.add(chkFiltrarSkillsArea);
        top.add(chkFiltrarHorasArea);
        top.add(new JLabel("Buscar:"));
        top.add(txtBuscarNombre);
        root.add(top, BorderLayout.NORTH);

        tblElegiblesArea = new JTable(new DefaultTableModel(new Object[]{
                "#", "Nombre", "Skills", "Horas (acum/max)", "Disponible"
        }, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        root.add(new JScrollPane(tblElegiblesArea), BorderLayout.CENTER);

        return root;
    }

    private JPanel buildBottomBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnExportar = new JButton("Exportar CSV…");
        btnImprimir = new JButton("Imprimir…");
        btnCerrar   = new JButton("Cerrar");
        p.add(btnExportar);
        p.add(btnImprimir);
        p.add(btnCerrar);
        return p;
    }

    private void hookEvents() {
        chkTodasAreas.addActionListener(e -> lstAreas.setEnabled(!chkTodasAreas.isSelected()));
        btnLimpiar.addActionListener(e -> clearFilters());
        btnCerrar.addActionListener(e -> dispose());

        btnAnalizar.addActionListener(e -> analyseAll());
        chkDedupe.addActionListener(e -> refreshUnicasLabel());

        chkSoloExcedidas.addActionListener(e -> analyseHoras());

        lstAreasDetalle.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) analyseDetalle();
        });
        chkFiltrarSkillsArea.addActionListener(e -> analyseDetalle());
        chkFiltrarHorasArea.addActionListener(e -> analyseDetalle());
        txtBuscarNombre.addCaretListener(e -> analyseDetalle());
    }

    private void clearFilters() {
        txtFecha.setText("");
        cboBloque.setSelectedIndex(0);
        chkTodasAreas.setSelected(true);
        lstAreas.clearSelection();
        lstAreas.setEnabled(false);
        chkRespetarSkills.setSelected(true);
        chkRespetarHoras.setSelected(true);
        chkSoloExcedidas.setSelected(false);
        chkDedupe.setSelected(false);
        lblUnicas.setText("Únicas: 0");

        // limpiar tablas
        ((DefaultTableModel)tblCobertura.getModel()).setRowCount(0);
        ((DefaultTableModel)tblHoras.getModel()).setRowCount(0);
        ((DefaultTableModel)tblElegiblesArea.getModel()).setRowCount(0);
        lblContexto.setText("—");
        lblCuposTotales.setText("Cupos totales: 0");
        lblElegiblesTotales.setText("Elegibles (sin dedupe): 0");
    }

    private LocalDate readFechaOrWarn() {
        try {
            return LocalDate.parse(txtFecha.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // === Lógica de análisis ===

    private void analyseAll() {
        LocalDate fecha = readFechaOrWarn();
        if (fecha == null) return;
        Bloque bloque = (Bloque) cboBloque.getSelectedItem();
        lblContexto.setText("Fecha: " + fecha + " — Bloque: " + bloque);

        // Áreas a considerar
        List<Area> areas = new ArrayList<>();
        if (chkTodasAreas.isSelected()) {
            areas.addAll(hospital.getAreas());
        } else {
            areas.addAll(lstAreas.getSelectedValuesList());
        }
        if (areas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un área.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Disponibles base para el bloque
        List<Enfermera> baseDisponibles = enfSvc.filtrarDisponibles(fecha, bloque);

        // Tabla cobertura
        DefaultTableModel m = (DefaultTableModel) tblCobertura.getModel();
        m.setRowCount(0);
        int totalCupos = 0;
        int totalElegiblesNoDedupe = 0;
        Set<Enfermera> setUnicas = new HashSet<>();

        for (Area a : areas) {
            int cupos = cuposDe(a, bloque);
            totalCupos += cupos;
            List<Enfermera> candidatas = filtrarCandidatas(baseDisponibles, a);
            totalElegiblesNoDedupe += candidatas.size();
            setUnicas.addAll(candidatas);

            int deficit = Math.max(0, cupos - candidatas.size());
            String obs = (deficit > 0) ? ("Faltan " + deficit) : "OK";
            m.addRow(new Object[]{ a.getNombre(), cupos, candidatas.size(), deficit, obs });
        }

        lblCuposTotales.setText("Cupos totales: " + totalCupos);
        lblElegiblesTotales.setText("Elegibles (sin dedupe): " + totalElegiblesNoDedupe);
        lblUnicas.setText("Únicas: " + setUnicas.size());

        // Tab Horas
        analyseHoras();

        // Tab Detalle: seleccionar primera área por defecto
        if (!areas.isEmpty()) {
            lstAreasDetalle.setListData(areas.toArray(new Area[0]));
            lstAreasDetalle.setSelectedIndex(0);
            analyseDetalle();
        }
    }

    private List<Enfermera> filtrarCandidatas(List<Enfermera> disponibles, Area area) {
        List<Enfermera> out = new ArrayList<>();
        List<String> req = area.getSkillsRequeridas();
        boolean exigirSkills = chkRespetarSkills.isSelected() && req != null && !req.isEmpty();
        boolean exigirHoras  = chkRespetarHoras.isSelected();
        for (Enfermera e : disponibles) {
            if (exigirSkills) {
                boolean ok = true;
                for (String s : req) {
                    if (!e.tieneSkill(s)) { ok = false; break; }
                }
                if (!ok) continue;
            }
            if (exigirHoras && !e.puedeTomarHoras(HORAS_POR_TURNO)) continue;
            out.add(e);
        }
        return out;
    }

    private void refreshUnicasLabel() {
        // Re-ejecutar el análisis para consistencia
        DefaultTableModel m = (DefaultTableModel) tblCobertura.getModel();
        if (m.getRowCount() == 0) return;
        analyseAll();
    }

    private void analyseHoras() {
        DefaultTableModel mh = (DefaultTableModel) tblHoras.getModel();
        mh.setRowCount(0);
        boolean soloExcedidas = chkSoloExcedidas.isSelected();
        for (Enfermera e : enfSvc.listar()) {
            int acum = e.getHorasAcumuladas();
            int max  = e.getHorasMensualMax();
            boolean excedida = acum > max;
            if (soloExcedidas && !excedida) continue;
            mh.addRow(new Object[]{
                    e.getNombre(),
                    acum,
                    max,
                    excedida ? "EXCEDIDA" : "OK"
            });
        }
    }

    private void analyseDetalle() {
        DefaultTableModel md = (DefaultTableModel) tblElegiblesArea.getModel();
        md.setRowCount(0);

        Area area = lstAreasDetalle.getSelectedValue();
        if (area == null) return;

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim());
        } catch (DateTimeParseException ex) {
            return;
        }
        Bloque bloque = (Bloque) cboBloque.getSelectedItem();

        List<Enfermera> disponibles = enfSvc.filtrarDisponibles(fecha, bloque);
        List<Enfermera> cand = filtrarCandidatas(disponibles, area);

        // filtros adicionales de esta pestaña
        boolean exigirSkills = chkFiltrarSkillsArea.isSelected();
        boolean exigirHoras  = chkFiltrarHorasArea.isSelected();
        String filtroNombre = txtBuscarNombre.getText().trim().toLowerCase(Locale.ROOT);

        List<String> req = area.getSkillsRequeridas();
        int i = 1;
        for (Enfermera e : cand) {
            if (exigirSkills && req != null && !req.isEmpty()) {
                boolean ok = true;
                for (String s : req) if (!e.tieneSkill(s)) { ok = false; break; }
                if (!ok) continue;
            }
            if (exigirHoras && !e.puedeTomarHoras(HORAS_POR_TURNO)) continue;
            if (!filtroNombre.isEmpty() && !e.getNombre().toLowerCase(Locale.ROOT).contains(filtroNombre)) continue;

            String skills = String.valueOf(e.getSkills());
            String horas  = e.getHorasAcumuladas() + "/" + e.getHorasMensualMax();
            boolean disponible = e.puedeTomarHoras(HORAS_POR_TURNO); // aproximación
            md.addRow(new Object[]{ i++, e.getNombre(), skills, horas, disponible ? "Sí" : "No" });
        }
    }

    // Helper: cupos por bloque
    private int cuposDe(Area a, Bloque b) {
        switch (b) {
            case MANANA: return a.getCuposManana();
            case TARDE:  return a.getCuposTarde();
            case NOCHE:  return a.getCuposNoche();
            default:     return 0;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    /* Set the Nimbus look and feel */
    try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception ex) {
        java.util.logging.Logger.getLogger(DlgConflictos.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
    }

    /* Create and display the dialog */
    java.awt.EventQueue.invokeLater(() -> {
        // Instancias mínimas para probar el diálogo
        Hospital hospital = new Hospital();            // usa tu clase real
        EnfermeraService enfSvc = new EnfermeraService(); // usa tu servicio real
        // Si quieres, aquí puedes precargar áreas/enfermeras de prueba

        DlgConflictos dialog = new DlgConflictos(new javax.swing.JFrame(), hospital, enfSvc);
        dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        dialog.setVisible(true);
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
