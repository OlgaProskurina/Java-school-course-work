package ru.course.work.documents.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.course.work.documents.persistence.entity.MessageRequest;

import java.util.Optional;

@Repository
public interface MessageRequestRepository extends JpaRepository<MessageRequest, Long> {
    
    /**
     * Получает одно не заблокированное исходящее сообщение и блокирует всю запись.
     *
     * @return исходящее сообщение, если в таблице есть записи
     */
    @Query(value = "SELECT * FROM message_request_outbox LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<MessageRequest> findUnsentMessage();
    
}