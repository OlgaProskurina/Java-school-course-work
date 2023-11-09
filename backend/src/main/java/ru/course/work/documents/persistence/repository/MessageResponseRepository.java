package ru.course.work.documents.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.course.work.documents.persistence.entity.MessageResponse;

@Repository
public interface MessageResponseRepository extends JpaRepository<MessageResponse, Long> {

}