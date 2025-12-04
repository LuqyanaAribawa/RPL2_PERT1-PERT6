package com.simak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Tambah karyawan baru
    public Employee tambahKaryawan(Employee karyawan, String username, String password) {
        // Buat user baru untuk karyawan
        User userBaru = new User();
        userBaru.setUsername(username);
        userBaru.setPassword(passwordEncoder.encode(password));
        userBaru.setRole(Role.ROLE_KARYAWAN);
        
        User userTersimpan = userRepository.save(userBaru);
        karyawan.setUser(userTersimpan);
        
        return employeeRepository.save(karyawan);
    }
    
    // Lihat semua karyawan
    public List<Employee> lihatSemuaKaryawan() {
        return employeeRepository.findAll();
    }
    
    // Lihat detail karyawan
    public Optional<Employee> lihatDetailKaryawan(Long id) {
        return employeeRepository.findById(id);
    }
    
    // Ubah data karyawan
    public Employee ubahKaryawan(Long id, Employee karyawanBaru) {
        Employee karyawanLama = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Karyawan tidak ditemukan"));
        
        karyawanLama.setEmployeeIdNumber(karyawanBaru.getEmployeeIdNumber());
        karyawanLama.setFullName(karyawanBaru.getFullName());
        karyawanLama.setPosition(karyawanBaru.getPosition());
        karyawanLama.setJoinDate(karyawanBaru.getJoinDate());
        
        return employeeRepository.save(karyawanLama);
    }
    
    // Hapus karyawan
    public void hapusKaryawan(Long id) {
        employeeRepository.deleteById(id);
    }
    
    // Cari karyawan berdasarkan username
    public Optional<Employee> cariKaryawanByUsername(String username) {
        return employeeRepository.findByUserUsername(username);
    }
}