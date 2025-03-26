package org.romanzhula.journal_service.controllers;

import lombok.RequiredArgsConstructor;
import org.romanzhula.journal_service.responses.JournalResponse;
import org.romanzhula.journal_service.services.JournalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/journal")
public class JournalController {

    private final JournalService journalService;

    @GetMapping("/all")
    public ResponseEntity<List<JournalResponse>> getAllEntries() {
        return ResponseEntity.ok(journalService.getAllEntries());
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<List<JournalResponse>> getAllUserJournalEntries(
            @PathVariable("user-id") String userId
    ) {
        return ResponseEntity.ok(journalService.getAllUserJournalEntries(userId));
    }

}
