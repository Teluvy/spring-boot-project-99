package hexlet.code.demo.controller;

import hexlet.code.demo.service.TaskStatusService;
import hexlet.code.demo.dto.TaskStatusRequestDTO;
import hexlet.code.demo.dto.TaskStatusResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/task_statuses")
public class TaskStatusController {

    @Autowired
    TaskStatusService taskStatusService;

    @GetMapping("/{id}")
    public TaskStatusResponseDTO show(@PathVariable long id){
        return taskStatusService.getById(id);
    }

    @GetMapping("")
    public ResponseEntity<List<TaskStatusResponseDTO>> index(){
        List<TaskStatusResponseDTO> taskStatuses = taskStatusService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .header("Access-Control-Expose-Headers", "X-Total-Count")
                .body(taskStatuses);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusResponseDTO create(@RequestBody TaskStatusRequestDTO taskStatusRequestDTO){
        return taskStatusService.create(taskStatusRequestDTO);
    }

    @PutMapping("/{id}")
    public TaskStatusResponseDTO update(@RequestBody TaskStatusRequestDTO taskStatusRequestDTO, @PathVariable long id){
        return taskStatusService.update(taskStatusRequestDTO, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        taskStatusService.delete(id);
    }
}
