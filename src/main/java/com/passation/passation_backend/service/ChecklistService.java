package com.passation.passation_backend.service;

import com.passation.passation_backend.dto.ChecklistDTO;
import com.passation.passation_backend.dto.ChecklistItemDTO;
import com.passation.passation_backend.model.ChecklistTemplate;
import com.passation.passation_backend.model.ChecklistTemplateItem;
import com.passation.passation_backend.repository.ChecklistTemplateItemRepository;
import com.passation.passation_backend.repository.ChecklistTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistTemplateRepository templateRepository;
    private final ChecklistTemplateItemRepository itemRepository;

    public List<ChecklistDTO> findAllTemplates() {
        return templateRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ChecklistDTO findTemplateById(Long id) {
        ChecklistTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ChecklistTemplate not found with id: " + id));
        return toDTO(template);
    }

    @Transactional
    public ChecklistDTO createTemplate(ChecklistDTO dto) {
        ChecklistTemplate template = ChecklistTemplate.builder()
                .nom(dto.getNom())
                .typePoste(dto.getTypePoste())
                .description(dto.getDescription())
                .build();
        ChecklistTemplate saved = templateRepository.save(template);
        return toDTO(saved);
    }

    @Transactional
    public ChecklistDTO updateTemplate(Long id, ChecklistDTO dto) {
        ChecklistTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ChecklistTemplate not found with id: " + id));
        template.setNom(dto.getNom());
        template.setTypePoste(dto.getTypePoste());
        template.setDescription(dto.getDescription());
        return toDTO(templateRepository.save(template));
    }

    @Transactional
    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("ChecklistTemplate not found with id: " + id);
        }
        templateRepository.deleteById(id);
    }

    public List<ChecklistItemDTO> findItemsByTemplate(Long templateId) {
        return itemRepository.findByTemplateIdOrderByOrdreAsc(templateId).stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChecklistItemDTO addItem(Long templateId, ChecklistItemDTO dto) {
        ChecklistTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("ChecklistTemplate not found with id: " + templateId));
        ChecklistTemplateItem item = ChecklistTemplateItem.builder()
                .template(template)
                .libelle(dto.getLibelle())
                .obligatoire(dto.getObligatoire())
                .ordre(dto.getOrdre())
                .build();
        return toItemDTO(itemRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new RuntimeException("ChecklistTemplateItem not found with id: " + itemId);
        }
        itemRepository.deleteById(itemId);
    }

    private ChecklistDTO toDTO(ChecklistTemplate template) {
        List<ChecklistItemDTO> items = itemRepository.findByTemplateIdOrderByOrdreAsc(template.getId())
                .stream().map(this::toItemDTO).collect(Collectors.toList());
        return ChecklistDTO.builder()
                .id(template.getId())
                .nom(template.getNom())
                .typePoste(template.getTypePoste())
                .description(template.getDescription())
                .items(items)
                .build();
    }

    private ChecklistItemDTO toItemDTO(ChecklistTemplateItem item) {
        return ChecklistItemDTO.builder()
                .id(item.getId())
                .libelle(item.getLibelle())
                .obligatoire(item.getObligatoire())
                .ordre(item.getOrdre())
                .build();
    }
}
