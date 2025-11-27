package com.simak;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

@Entity
@Table(name = "penilaian_kinerja")
public class PerformanceReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tanggal_penilaian", nullable = false)
    private LocalDate reviewDate;
    
    @Min(value = 1, message = "Nilai minimal adalah 1")
    @Max(value = 100, message = "Nilai maksimal adalah 100")
    @Column(name = "nilai", nullable = false)
    private Integer score;
    
    @Column(name = "catatan", columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne
    @JoinColumn(name = "karyawan_id", nullable = false)
    private Employee employee;
    
    // Konstruktor
    public PerformanceReview() {}
    
    public PerformanceReview(LocalDate reviewDate, Integer score, String notes, Employee employee) {
        this.reviewDate = reviewDate;
        this.score = score;
        this.notes = notes;
        this.employee = employee;
    }
    
    // Getter dan Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getReviewDate() {
        return reviewDate;
    }
    
    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}