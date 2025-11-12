package com.online.school.service.impl;

import com.online.school.model.Subject;
import com.online.school.repository.SubjectRepository;
import com.online.school.service.SubjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    public List<Subject> getAllSubjects() {
        log.info("Fetching all subjects");
        return subjectRepository.findAll();
    }

    @Override
    public Subject getSubjectById(Long id) {
        log.info("Fetching subject by id: {}", id);
        return subjectRepository.findById(id).orElse(null);
    }

    @Override
    public Subject createSubject(String name, String description) {
        log.info("Creating subject: {}", name);

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name is required");
        }

        if (subjectRepository.existsByName(name.trim())) {
            throw new IllegalArgumentException("Subject already exists: " + name);
        }

        Subject subject = Subject.builder()
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .build();

        Subject savedSubject = subjectRepository.save(subject);
        log.info("Successfully created subject with id: {}", savedSubject.getId());
        return savedSubject;
    }

    @Override
    public Subject updateSubject(Long id, String name, String description) {
        log.info("Updating subject with id: {}", id);

        return subjectRepository.findById(id).map(subject -> {
            if (name != null && !name.isBlank()) {
                String trimmedName = name.trim();
                if (!subject.getName().equals(trimmedName) && subjectRepository.existsByName(trimmedName)) {
                    throw new IllegalArgumentException("Subject already exists: " + name);
                }
                subject.setName(trimmedName);
            }
            if (description != null) {
                subject.setDescription(description.trim());
            }
            return subjectRepository.save(subject);
        }).orElse(null);
    }

    @Override
    public void deleteSubject(Long id) {
        log.info("Deleting subject with id: {}", id);
        subjectRepository.deleteById(id);
    }

    @Override
    public boolean subjectExists(String name) {
        return subjectRepository.existsByName(name);
    }
}