package com.simak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private PerformanceService performanceService;
    
    // Tambah karyawan baru
    @PostMapping("/employees")
    public ResponseEntity<?> tambahKaryawan(@Valid @RequestBody Map<String, Object> request) {
        try {
            Employee karyawanBaru = new Employee();
            karyawanBaru.setEmployeeIdNumber((String) request.get("nip"));
            karyawanBaru.setFullName((String) request.get("namaLengkap"));
            karyawanBaru.setPosition((String) request.get("jabatan"));
            karyawanBaru.setJoinDate(java.time.LocalDate.parse((String) request.get("tanggalMasuk")));
            
            String username = (String) request.get("username");
            String password = (String) request.get("password");
            
            Employee karyawanTersimpan = employeeService.tambahKaryawan(karyawanBaru, username, password);
            
            Map<String, Object> response = new HashMap<>();
            response.put("pesan", "Karyawan berhasil ditambahkan");
            response.put("data", karyawanTersimpan);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Lihat semua karyawan
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> lihatSemuaKaryawan() {
        List<Employee> daftarKaryawan = employeeService.lihatSemuaKaryawan();
        return ResponseEntity.ok(daftarKaryawan);
    }
    
    // Lihat detail karyawan
    @GetMapping("/employees/{id}")
    public ResponseEntity<?> lihatDetailKaryawan(@PathVariable Long id) {
        return employeeService.lihatDetailKaryawan(id)
            .map(karyawan -> ResponseEntity.ok(karyawan))
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Ubah data karyawan
    @PutMapping("/employees/{id}")
    public ResponseEntity<?> ubahKaryawan(@PathVariable Long id, @Valid @RequestBody Employee karyawan) {
        try {
            Employee karyawanDiperbarui = employeeService.ubahKaryawan(id, karyawan);
            
            Map<String, Object> response = new HashMap<>();
            response.put("pesan", "Data karyawan berhasil diperbarui");
            response.put("data", karyawanDiperbarui);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Tambah penilaian kinerja
    @PostMapping("/performance")
    public ResponseEntity<?> tambahPenilaianKinerja(@RequestBody Map<String, Object> request) {
        try {
            Long idKaryawan = Long.parseLong(request.get("idKaryawan").toString());
            
            PerformanceReview penilaianBaru = new PerformanceReview();
            penilaianBaru.setReviewDate(java.time.LocalDate.parse((String) request.get("tanggalPenilaian")));
            penilaianBaru.setScore(Integer.parseInt(request.get("nilai").toString()));
            penilaianBaru.setNotes((String) request.get("catatan"));
            
            PerformanceReview penilaianTersimpan = performanceService.tambahPenilaian(idKaryawan, penilaianBaru);
            
            Map<String, Object> response = new HashMap<>();
            response.put("pesan", "Penilaian kinerja berhasil ditambahkan");
            response.put("data", penilaianTersimpan);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}