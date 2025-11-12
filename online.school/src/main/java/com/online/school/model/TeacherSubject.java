package com.online.school.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "teacher_subjects",
        uniqueConstraints = @UniqueConstraint(columnNames = {"teacher_id", "subject_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}