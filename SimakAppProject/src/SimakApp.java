import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class SimakApp extends JFrame {

    private JTextField txtNip, txtNama, txtJabatan;
    private JButton btnTambah, btnUpdate, btnHapus, btnClear, btnCetak;
    private JTable tableKaryawan;
    private DefaultTableModel tableModel;
    private int selectedId = -1; // Untuk menyimpan ID yang dipilih

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/simak_db?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public SimakApp() {
        setTitle("Sistem Informasi Manajemen Karyawan (SIMAK)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        // Panel Form
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Form Data Karyawan"));

        panelForm.add(new JLabel("NIP:"));
        txtNip = new JTextField();
        panelForm.add(txtNip);

        panelForm.add(new JLabel("Nama Lengkap:"));
        txtNama = new JTextField();
        panelForm.add(txtNama);

        panelForm.add(new JLabel("Jabatan:"));
        txtJabatan = new JTextField();
        panelForm.add(txtJabatan);

        // Panel Tombol
        JPanel panelTombol = new JPanel();
        btnTambah = new JButton("Tambah");
        btnUpdate = new JButton("Update");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Clear");
        btnCetak = new JButton("Cetak Laporan");

        panelTombol.add(btnTambah);
        panelTombol.add(btnUpdate);
        panelTombol.add(btnHapus);
        panelTombol.add(btnClear);
        panelTombol.add(btnCetak);

        // Tabel
        String[] columnNames = {"ID", "NIP", "Nama Lengkap", "Jabatan"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Membuat tabel tidak bisa diedit langsung
            }
        };
        tableKaryawan = new JTable(tableModel);
        tableKaryawan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Layout
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.add(panelForm, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(tableKaryawan), BorderLayout.CENTER);
        contentPane.add(panelTombol, BorderLayout.SOUTH);

        // EVENT LISTENERS

        // Tombol TAMBAH
        btnTambah.addActionListener((ActionEvent e) -> {
            tambahKaryawan();
        });

        // Tombol UPDATE
        btnUpdate.addActionListener((ActionEvent e) -> {
            updateKaryawan();
        });

        // Tombol HAPUS
        btnHapus.addActionListener((ActionEvent e) -> {
            hapusKaryawan();
        });

        // Tombol CLEAR
        btnClear.addActionListener(e -> clearForm());

        // Tombol CETAK
        btnCetak.addActionListener(e -> cetakLaporan());

        // Table Selection Listener
        tableKaryawan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableKaryawan.getSelectedRow();
                if (selectedRow != -1) {
                    selectedId = (int) tableModel.getValueAt(selectedRow, 0);
                    String nip = tableModel.getValueAt(selectedRow, 1).toString();
                    String nama = tableModel.getValueAt(selectedRow, 2).toString();
                    String jabatan = tableModel.getValueAt(selectedRow, 3).toString();

                    txtNip.setText(nip);
                    txtNama.setText(nama);
                    txtJabatan.setText(jabatan);
                }
            }
        });
    }

    // Method untuk menambah karyawan
    private void tambahKaryawan() {
        String nip = txtNip.getText().trim();
        String nama = txtNama.getText().trim();
        String jabatan = txtJabatan.getText().trim();

        if (nip.isEmpty() || nama.isEmpty() || jabatan.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Semua field harus diisi!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Cek apakah NIP sudah ada
            String checkSql = "SELECT COUNT(*) FROM karyawan WHERE nip = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, nip);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "NIP sudah terdaftar!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert data baru
            String sql = "INSERT INTO karyawan (nip, nama_lengkap, jabatan, tanggal_masuk) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nip);
            stmt.setString(2, nama);
            stmt.setString(3, jabatan);
            stmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Karyawan berhasil ditambahkan!");
            loadDataToTable();
            clearForm();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk update karyawan
    private void updateKaryawan() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data yang akan diupdate!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nip = txtNip.getText().trim();
        String nama = txtNama.getText().trim();
        String jabatan = txtJabatan.getText().trim();

        if (nip.isEmpty() || nama.isEmpty() || jabatan.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Semua field harus diisi!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin mengupdate data ini?",
                "Konfirmasi Update",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Cek apakah NIP baru sudah digunakan oleh karyawan lain
            String checkSql = "SELECT COUNT(*) FROM karyawan WHERE nip = ? AND id != ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, nip);
            checkStmt.setInt(2, selectedId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "NIP sudah digunakan karyawan lain!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update data
            String sql = "UPDATE karyawan SET nip = ?, nama_lengkap = ?, jabatan = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nip);
            stmt.setString(2, nama);
            stmt.setString(3, jabatan);
            stmt.setInt(4, selectedId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal mengupdate data!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk hapus karyawan
    private void hapusKaryawan() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data yang akan dihapus!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM karyawan WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus data!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, nip, nama_lengkap, jabatan FROM karyawan ORDER BY id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String nip = rs.getString("nip");
                String nama = rs.getString("nama_lengkap");
                String jabatan = rs.getString("jabatan");
                tableModel.addRow(new Object[]{id, nip, nama, jabatan});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtNip.setText("");
        txtNama.setText("");
        txtJabatan.setText("");
        tableKaryawan.clearSelection();
        selectedId = -1; // Reset ID yang terpilih
    }

    private void cetakLaporan() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String baseDir = System.getProperty("user.dir");

            String jrxmlPath = baseDir + File.separator + "src" + File.separator + "report" + File.separator + "report_SimakAPP.jrxml";
            String jasperPath = baseDir + File.separator + "src" + File.separator + "report" + File.separator + "report_SimakAPP.jasper";

            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);

            Map<String, Object> params = new HashMap<>();
            JasperPrint jp = JasperFillManager.fillReport(jasperPath, params, conn);

            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Laporan Data Karyawan");
            viewer.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mencetak laporan: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SimakApp().setVisible(true);
        });
    }
}