package com.passation.passation_backend.service;

import com.passation.passation_backend.dto.TimelineDTO;
import com.passation.passation_backend.model.Passation;
import com.passation.passation_backend.model.TimelineEtape;
import com.passation.passation_backend.repository.PassationRepository;
import com.passation.passation_backend.repository.TimelineEtapeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TimelineEtapeRepository timelineEtapeRepository;
    private final PassationRepository passationRepository;

    public List<TimelineDTO> findByPassation(Long passationId) {
        return timelineEtapeRepository.findByPassationIdOrderByOrdreAsc(passationId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TimelineDTO create(Long passationId, TimelineDTO dto) {
        Passation passation = passationRepository.findById(passationId)
                .orElseThrow(() -> new RuntimeException("Passation not found with id: " + passationId));
        TimelineEtape etape = TimelineEtape.builder()
                .passation(passation)
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .datePrevu(dto.getDatePrevu())
                .dateReelle(dto.getDateReelle())
                .statut(dto.getStatut())
                .ordre(dto.getOrdre())
                .build();
        return toDTO(timelineEtapeRepository.save(etape));
    }

    @Transactional
    public TimelineDTO update(Long id, TimelineDTO dto) {
        TimelineEtape etape = timelineEtapeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TimelineEtape not found with id: " + id));
        etape.setTitre(dto.getTitre());
        etape.setDescription(dto.getDescription());
        etape.setDatePrevu(dto.getDatePrevu());
        etape.setDateReelle(dto.getDateReelle());
        etape.setStatut(dto.getStatut());
        etape.setOrdre(dto.getOrdre());
        return toDTO(timelineEtapeRepository.save(etape));
    }

    @Transactional
    public void delete(Long id) {
        if (!timelineEtapeRepository.existsById(id)) {
            throw new RuntimeException("TimelineEtape not found with id: " + id);
        }
        timelineEtapeRepository.deleteById(id);
    }

    private TimelineDTO toDTO(TimelineEtape etape) {
        return TimelineDTO.builder()
                .id(etape.getId())
                .passationId(etape.getPassation() != null ? etape.getPassation().getId() : null)
                .titre(etape.getTitre())
                .description(etape.getDescription())
                .datePrevu(etape.getDatePrevu())
                .dateReelle(etape.getDateReelle())
                .statut(etape.getStatut())
                .ordre(etape.getOrdre())
                .build();
    }
}
