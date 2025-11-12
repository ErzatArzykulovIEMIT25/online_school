package com.online.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Grade must be at least 1")
    @Max(value = 12, message = "Grade cannot exceed 12")
    @Column(nullable = false)
    private Integer grade;

    @Pattern(regexp = "[А-Я]", message = "Letter must be a single Cyrillic character (А-Я)")
    @Column(nullable = false, length = 1)
    private String letter;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Schedule> schedules = new ArrayList<>();

    @Transient
    public String getClassName() {
        return grade + letter;
    }
}