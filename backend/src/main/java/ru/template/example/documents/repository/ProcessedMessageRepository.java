package ru.template.example.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.template.example.documents.entity.ProcessedMessage;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, Long> {

}