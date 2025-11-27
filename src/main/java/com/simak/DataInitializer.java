package com.simak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Hanya jalankan jika database benar-benar kosong
        if (employeeRepository.count() == 0 && userRepository.count() == 0) {
            System.out.println("Database kosong, membuat data awal...");

            // --- 1. MEMBUAT AKUN ADMIN ---
            // Buat objek User untuk admin
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole(Role.ROLE_ADMIN);
            
            // Buat objek Employee untuk admin
            // Kita buatkan juga data Employee untuk Admin agar konsisten
            Employee adminEmployee = new Employee();
            adminEmployee.setEmployeeIdNumber("ADM001");
            adminEmployee.setFullName("Administrator");
            adminEmployee.setPosition("System Admin");
            adminEmployee.setJoinDate(LocalDate.now());
            
            // Hubungkan keduanya
            adminEmployee.setUser(adminUser);
            
            // Cukup simpan Employee, User akan ikut tersimpan karena cascade
            employeeRepository.save(adminEmployee);

            // --- 2. MEMBUAT AKUN KARYAWAN CONTOH ---
            // Buat objek User untuk karyawan
            User karyawanUser = new User();
            karyawanUser.setUsername("karyawan01");
            karyawanUser.setPassword(passwordEncoder.encode("karyawan123"));
            karyawanUser.setRole(Role.ROLE_KARYAWAN);
            
            // Buat objek Employee untuk karyawan
            Employee karyawanEmployee = new Employee();
            karyawanEmployee.setEmployeeIdNumber("NIP001");
            karyawanEmployee.setFullName("Budi Santoso");
            karyawanEmployee.setPosition("Staff IT");
            karyawanEmployee.setJoinDate(LocalDate.of(2020, 1, 15));
            
            // Hubungkan keduanya
            karyawanEmployee.setUser(karyawanUser);
            
            // Cukup simpan Employee, User akan ikut tersimpan
            employeeRepository.save(karyawanEmployee);
            
            System.out.println("Data awal berhasil dibuat!");
            System.out.println("Login Admin -> username: admin, password: admin123");
            System.out.println("Login Karyawan -> username: karyawan01, password: karyawan123");
        } else {
            System.out.println("Database sudah berisi data, proses inisialisasi dilewati.");
        }
    }
}