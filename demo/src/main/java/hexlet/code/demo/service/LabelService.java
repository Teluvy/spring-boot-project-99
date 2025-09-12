package hexlet.code.demo.service;

import hexlet.code.demo.dto.LabelRequestDTO;
import hexlet.code.demo.dto.LabelResponseDTO;
import hexlet.code.demo.exception.ResourceNotFoundException;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class LabelService {
    @Autowired
    private LabelRepository labelRepository;

    public LabelResponseDTO getById(@PathVariable long id){
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        return toDTO(label);
    }

    public List<LabelResponseDTO> getAll(){
        List<LabelResponseDTO> labels = labelRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
        return labels;
    }

    public LabelResponseDTO create(LabelRequestDTO labelRequestDTO){
        Label label = toEntity(labelRequestDTO);
        Label labelSaved = labelRepository.save(label);
        return toDTO(labelSaved);
    }

    public LabelResponseDTO update(LabelRequestDTO labelRequestDTO, long id){
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        Label labelUpdate = toEntity(labelRequestDTO);
        label.setName(labelUpdate.getName());
        return toDTO(labelRepository.save(label));
    }

    public void delete(long id){
        labelRepository.deleteById(id);
    }

    private LabelResponseDTO toDTO(Label label) {
        LabelResponseDTO labelResponseDTO = new LabelResponseDTO();
        labelResponseDTO.setId(label.getId());
        labelResponseDTO.setName(label.getName());
        labelResponseDTO.setCreatedAt(label.getCreatedAt());
        return labelResponseDTO;
    }

    private Label toEntity(LabelRequestDTO labelRequestDTO) {
        Label label = new Label();
        label.setName(labelRequestDTO.getName());
        return label;
    }
}
