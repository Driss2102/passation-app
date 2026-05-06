package com.passation.passation_backend.controller;

import com.passation.passation_backend.dto.TimelineDTO;
import com.passation.passation_backend.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping("/api/passations/{id}/timeline")
    public ResponseEntity<List<TimelineDTO>> getTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(timelineService.findByPassation(id));
    }

    @PostMapping("/api/passations/{id}/timeline")
    public ResponseEntity<TimelineDTO> createEtape(@PathVariable Long id,
                                                    @RequestBody TimelineDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timelineService.create(id, dto));
    }

    @PutMapping("/api/timeline/{id}")
    public ResponseEntity<TimelineDTO> updateEtape(@PathVariable Long id,
                                                    @RequestBody TimelineDTO dto) {
        return ResponseEntity.ok(timelineService.update(id, dto));
    }

    @DeleteMapping("/api/timeline/{id}")
    public ResponseEntity<Void> deleteEtape(@PathVariable Long id) {
        timelineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
