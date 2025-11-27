package com.simak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/karyawan")
public class KaryawanController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private PerformanceService performanceService;
    
    // Lihat profil pribadi
    @GetMapping("/my-profile")
    public ResponseEntity<?> lihatProfilPribadi(Authentication authentication) {
        String username = authentication.getName();
        
        return employeeService.cariKaryawanByUsername(username)
            .map(karyawan -> {
                Map<String, Object> profil = new HashMap<>();
                profil.put("id", karyawan.getId());
                profil.put("nip", karyawan.getEmployeeIdNumber());
                profil.put("namaLengkap", karyawan.getFullName());
                profil.put("jabatan", karyawan.getPosition());
                profil.put("tanggalMasuk", karyawan.getJoinDate());
                profil.put("username", karyawan.getUser().getUsername());
                
                return ResponseEntity.ok(profil);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Lihat riwayat penilaian kinerja pribadi
    @GetMapping("/my-performance")
    public ResponseEntity<?> lihatPenilaianPribadi(Authentication authentication) {
        String username = authentication.getName();
        
        return employeeService.cariKaryawanByUsername(username)
            .map(karyawan -> {
                List<PerformanceReview> riwayatPenilaian = 
                    performanceService.lihatRiwayatPenilaianByKaryawan(karyawan);
                
                Map<String, Object> response = new HashMap<>();
                response.put("namaKaryawan", karyawan.getFullName());
                response.put("nip", karyawan.getEmployeeIdNumber());
                response.put("jumlahPenilaian", riwayatPenilaian.size());
                response.put("riwayatPenilaian", riwayatPenilaian);
                
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}