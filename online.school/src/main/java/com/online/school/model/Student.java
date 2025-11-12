package com.online.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Grade> grades = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Attendance> attendances = new ArrayList<>();

    @Transient
    public String getFullName() {
        return user != null ? user.getFullName() : "";
    }

    @Transient
    public String getEmail() {
        return user != null ? user.getEmail() : "";
    }
}