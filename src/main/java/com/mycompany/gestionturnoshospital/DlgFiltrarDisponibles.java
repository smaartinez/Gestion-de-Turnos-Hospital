package com.mycompany.gestionturnoshospital;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


/**
 *
 * @author simon
 */
public class DlgFiltrarDisponibles extends javax.swing.JDialog {

    private final EnfermeraService enfSvc;
    
    public DlgFiltrarDisponibles(java.awt.Frame parent, boolean modal,EnfermeraService enfSvc) {
        super(parent, modal);
        this.enfSvc = enfSvc;
        initComponents();
        setLocationRelativeTo(parent);
        setTitle("Enfermeras disponibles por fecha y bloque");
        jTextField1.setText(java.time.LocalDate.now().toString()); // fecha por defecto
        configurarTabla();
        cargarBloques();
        conectarEventos();
        
    }
    public DlgFiltrarDisponibles(java.awt.Frame parent, boolean modal){
        this(parent, modal, GestionTurnosHospital.getEnfSvc());
    }
    private void configurarTabla(){
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{"RUT", "Nombre", "Área"}
        ){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }
    private void cargarBloques(){
        javax.swing.DefaultComboBoxModel<String> model = new javax.swing.DefaultComboBoxModel<>();
        for (Bloque b : Bloque.values()) model.addElement(b.name());
        jComboBox1.setModel(model);
    }
    private void conectarEventos(){
        jButton1.addActionListener(e -> buscar());
        jButton2.addActionListener(e -> exportar());
    }
    private static Bloque parseBloqueFlexible(String s) {
    return Bloque.valueOf(s.trim().toUpperCase().replace('Ñ','N')); // MANANA | TARDE | NOCHE
    }


    private static LocalDate parseFechaISO(String s) {
        return java.time.LocalDate.parse(s.trim()); // yyyy-MM-dd
    }

    private void buscar() {
     try {
        LocalDate fechaSel = java.time.LocalDate.parse(jTextField1.getText().trim());
        Bloque bloqueSel = parseBloqueFlexible((String) jComboBox1.getSelectedItem());

        java.util.List<Enfermera> lista = enfSvc.filtrarDisponibles(fechaSel, bloqueSel);

        javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) jTable1.getModel();
        m.setRowCount(0);

        for (Enfermera e : lista) {
            String area = e.areaPara(fechaSel, bloqueSel); 
            m.addRow(new Object[]{ e.getRut(), e.getNombre(), area == null ? "-" : area });
        }

        if (m.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Sin resultados para esa fecha/bloque.");
        }
    } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Filtrar", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    private void exportar(){
    javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) jTable1.getModel();
    int cols = m.getColumnCount();
    if (m.getRowCount() == 0){
        javax.swing.JOptionPane.showMessageDialog(this, "No hay resultados para exportar.");
        return;
    }
    javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
    fc.setDialogTitle("Guardar reporte .txt");
    fc.setSelectedFile(new java.io.File("reporte_disponibles.txt"));
    if (fc.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION)return;

    java.nio.file.Path destino = fc.getSelectedFile().toPath();
    try(java.io.BufferedWriter bw = java.nio.file.Files.newBufferedWriter(destino, java.nio.charset.StandardCharsets.UTF_8)){
        // Encabezado dinámico
        if (cols == 3) {
            bw.write("RUT\tNombre\tÁrea");
        } else {
            bw.write("RUT\tNombre");
        }
        bw.newLine();

        for(int i = 0; i < m.getRowCount(); i++){
            if (cols == 3) {
                bw.write(m.getValueAt(i,0) + "\t" + m.getValueAt(i,1) + "\t" + m.getValueAt(i,2));
            } else {
                bw.write(m.getValueAt(i,0) + "\t" + m.getValueAt(i,1));
            }
            bw.newLine();
        }
    } catch (java.io.IOException ex){
        javax.swing.JOptionPane.showMessageDialog(this, "No se pudo escribir:\n" + ex.getMessage(),
                "Exportar", javax.swing.JOptionPane.ERROR_MESSAGE);
        return;
    }
    javax.swing.JOptionPane.showMessageDialog(this, "Reporte guardado.");
    }
    
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setText("Buscar");

        jButton2.setText("Exportar");

        jTextField1.setText("Fecha(yyyy-MM-aaaa)");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(92, 92, 92)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addGap(0, 213, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DlgFiltrarDisponibles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgFiltrarDisponibles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgFiltrarDisponibles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgFiltrarDisponibles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgFiltrarDisponibles dialog = new DlgFiltrarDisponibles(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}