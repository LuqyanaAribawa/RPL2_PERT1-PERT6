package com.simak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PerformanceService {
    
    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    // Tambah penilaian kinerja baru
    public PerformanceReview tambahPenilaian(Long idKaryawan, PerformanceReview penilaian) {
        Employee karyawan = employeeRepository.findById(idKaryawan)
            .orElseThrow(() -> new RuntimeException("Karyawan tidak ditemukan"));
        
        penilaian.setEmployee(karyawan);
        return performanceReviewRepository.save(penilaian);
    }
    
    // Lihat riwayat penilaian karyawan
    public List<PerformanceReview> lihatRiwayatPenilaian(Long idKaryawan) {
        return performanceReviewRepository.findByEmployeeId(idKaryawan);
    }
    
    // Lihat riwayat penilaian berdasarkan objek karyawan
    public List<PerformanceReview> lihatRiwayatPenilaianByKaryawan(Employee karyawan) {
        return performanceReviewRepository.findByEmployeeOrderByReviewDateDesc(karyawan);
    }
}