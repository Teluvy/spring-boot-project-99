package hexlet.code.demo.controller;

import hexlet.code.demo.dto.LabelRequestDTO;
import hexlet.code.demo.dto.LabelResponseDTO;
import hexlet.code.demo.dto.TaskRequestDTO;
import hexlet.code.demo.dto.TaskResponseDTO;
import hexlet.code.demo.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/labels")
public class LabelController {
    @Autowired
    LabelService labelService;

    @GetMapping("/{id}")
    public LabelResponseDTO show(@PathVariable long id){
        return labelService.getById(id);
    }

    @GetMapping("")
    public ResponseEntity<List<LabelResponseDTO>> index(){
        List<LabelResponseDTO> labels = labelService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .header("Access-Control-Expose-Headers", "X-Total-Count")
                .body(labels);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDTO create(@RequestBody LabelRequestDTO labelRequestDTO){
        return labelService.create(labelRequestDTO);
    }

    @PutMapping("/{id}")
    public LabelResponseDTO update(@RequestBody LabelRequestDTO labelRequestDTO, @PathVariable long id){
        return labelService.update(labelRequestDTO, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        labelService.delete(id);
    }
}
