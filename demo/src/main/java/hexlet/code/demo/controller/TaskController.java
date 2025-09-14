package hexlet.code.demo.controller;

import hexlet.code.demo.dto.TaskFilter;
import hexlet.code.demo.dto.TaskRequestDTO;
import hexlet.code.demo.dto.TaskResponseDTO;
import hexlet.code.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tasks")
public class TaskController {
    @Autowired
    TaskService taskService;

    @GetMapping("/{id}")
    public TaskResponseDTO show(@PathVariable long id){
        return taskService.getById(id);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskResponseDTO>> index(
            @RequestParam(required = false) String titleCont,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long labelId
    ){
        TaskFilter filter = new TaskFilter();
        filter.setTitleCont(titleCont);
        filter.setAssigneeId(assigneeId);
        filter.setStatus(status);
        filter.setLabelId(labelId);

        List<TaskResponseDTO> taskStatuses = taskService.getAll(filter);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .header("Access-Control-Expose-Headers", "X-Total-Count")
                .body(taskStatuses);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO create(@RequestBody TaskRequestDTO taskRequestDTO){
        return taskService.create(taskRequestDTO);
    }

    @PutMapping("/{id}")
    public TaskResponseDTO update(@RequestBody TaskRequestDTO taskRequestDTO, @PathVariable long id){
        return taskService.update(taskRequestDTO, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        taskService.delete(id);
    }
}
