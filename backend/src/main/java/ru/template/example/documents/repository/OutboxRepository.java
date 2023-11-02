package ru.template.example.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.template.example.documents.entity.OutboxMessage;

import java.util.Optional;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    
    /**
     * Получает не заблокированное исходящее сообщение и блокирует всю запись.
     *
     * @return исходящее сообщение, если в таблице есть записи
     */
    @Query(value = "SELECT * FROM outbox LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<OutboxMessage> getMessage();
    
}