package ru.course.work.documents.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.course.work.documents.persistence.entity.Document;

import java.util.Set;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    void deleteAllByIdIn(Set<Long> ids);
    
}
