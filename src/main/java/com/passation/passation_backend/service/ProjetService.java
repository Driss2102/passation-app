package com.passation.passation_backend.service;

import com.passation.passation_backend.dto.CreateProjetRequest;
import com.passation.passation_backend.dto.ProjetDTO;
import com.passation.passation_backend.model.Projet;
import com.passation.passation_backend.model.StatutProjet;
import com.passation.passation_backend.model.User;
import com.passation.passation_backend.repository.ProjetRepository;
import com.passation.passation_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<ProjetDTO> findAll() {
        return projetRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProjetDTO findById(Long id) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet not found with id: " + id));
        return toDTO(projet);
    }

    public List<ProjetDTO> findByStatut(StatutProjet statut) {
        return projetRepository.findByStatut(statut).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjetDTO create(CreateProjetRequest request) {
        User responsable = null;
        if (request.getResponsableId() != null) {
            responsable = userRepository.findById(request.getResponsableId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getResponsableId()));
        }
        Projet projet = Projet.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .statut(request.getStatut())
                .pourcentageAvancement(request.getPourcentageAvancement())
                .dateDebut(request.getDateDebut())
                .dateFinPrevue(request.getDateFinPrevue())
                .responsable(responsable)
                .build();
        return toDTO(projetRepository.save(projet));
    }

    @Transactional
    public ProjetDTO update(Long id, CreateProjetRequest request) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet not found with id: " + id));
        User responsable = null;
        if (request.getResponsableId() != null) {
            responsable = userRepository.findById(request.getResponsableId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getResponsableId()));
        }
        projet.setNom(request.getNom());
        projet.setDescription(request.getDescription());
        projet.setStatut(request.getStatut());
        projet.setPourcentageAvancement(request.getPourcentageAvancement());
        projet.setDateDebut(request.getDateDebut());
        projet.setDateFinPrevue(request.getDateFinPrevue());
        projet.setResponsable(responsable);
        return toDTO(projetRepository.save(projet));
    }

    @Transactional
    public void delete(Long id) {
        if (!projetRepository.existsById(id)) {
            throw new RuntimeException("Projet not found with id: " + id);
        }
        projetRepository.deleteById(id);
    }

    public ProjetDTO toDTO(Projet projet) {
        if (projet == null) return null;
        return ProjetDTO.builder()
                .id(projet.getId())
                .nom(projet.getNom())
                .description(projet.getDescription())
                .statut(projet.getStatut())
                .pourcentageAvancement(projet.getPourcentageAvancement())
                .dateDebut(projet.getDateDebut())
                .dateFinPrevue(projet.getDateFinPrevue())
                .responsable(userService.toDTO(projet.getResponsable()))
                .dateCreation(projet.getDateCreation())
                .build();
    }
}
