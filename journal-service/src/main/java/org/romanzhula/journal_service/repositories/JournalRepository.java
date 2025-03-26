package org.romanzhula.journal_service.repositories;

import org.romanzhula.journal_service.models.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findAllByUserId(UUID userId);

}
