package com.simak;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "karyawan")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "NIP tidak boleh kosong")
    @Column(name = "nip", unique = true, nullable = false)
    private String employeeIdNumber;
    
    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Column(name = "nama_lengkap", nullable = false)
    private String fullName;
    
    @NotBlank(message = "Jabatan tidak boleh kosong")
    @Column(name = "jabatan", nullable = false)
    private String position;
    
    @Column(name = "tanggal_masuk", nullable = false)
    private LocalDate joinDate;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    
    // Konstruktor
    public Employee() {}
    
    public Employee(String employeeIdNumber, String fullName, String position, LocalDate joinDate) {
        this.employeeIdNumber = employeeIdNumber;
        this.fullName = fullName;
        this.position = position;
        this.joinDate = joinDate;
    }
    
    // Getter dan Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmployeeIdNumber() {
        return employeeIdNumber;
    }
    
    public void setEmployeeIdNumber(String employeeIdNumber) {
        this.employeeIdNumber = employeeIdNumber;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public LocalDate getJoinDate() {
        return joinDate;
    }
    
    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}