package hexlet.code.demo.service;

import hexlet.code.demo.dto.TaskRequestDTO;
import hexlet.code.demo.dto.TaskResponseDTO;
import hexlet.code.demo.exception.ResourceNotFoundException;
import hexlet.code.demo.model.Task;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.TaskRepository;
import hexlet.code.demo.repository.TaskStatusRepository;
import hexlet.code.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskStatusRepository taskStatusRepository;

    public TaskResponseDTO getById(@PathVariable long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return toDTO(task);
    }

    public List<TaskResponseDTO> getAll(){
        List<TaskResponseDTO> tasks = taskRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
        return tasks;
    }

    public TaskResponseDTO create(TaskRequestDTO taskRequestDTO){
        Task task = toEntity(taskRequestDTO);
        Task taskSaved = taskRepository.save(task);
        return toDTO(taskSaved);
    }

    public TaskResponseDTO update(TaskRequestDTO taskStatusRequestDTO, long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        Task taskUpdate = toEntity(taskStatusRequestDTO);
        task.setIndex(taskUpdate.getIndex());
        task.setAssigneeId(taskUpdate.getAssigneeId());
        task.setTitle(taskUpdate.getTitle());
        task.setContent(taskUpdate.getContent());
        task.setStatus(taskUpdate.getStatus());
        return toDTO(taskRepository.save(task));
    }

    public void delete(long id){
        taskRepository.deleteById(id);
    }

    private TaskResponseDTO toDTO(Task task) {
        TaskResponseDTO taskDTO = new TaskResponseDTO();
        taskDTO.setId(task.getId());
        taskDTO.setIndex(task.getIndex());
        taskDTO.setCreatedAt(task.getCreatedAt());

        if (task.getAssigneeId() != null) {
            taskDTO.setAssigneeId(task.getAssigneeId().getId());
        }

        taskDTO.setTitle(task.getTitle());
        taskDTO.setContent(task.getContent());

        if (task.getStatus() != null) {
            taskDTO.setStatus(task.getStatus().getSlug());
        }

        return taskDTO;
    }

    private Task toEntity(TaskRequestDTO taskRequestDTO) {
        Task task = new Task();
        task.setIndex(taskRequestDTO.getIndex());
        task.setTitle(taskRequestDTO.getTitle());
        task.setContent(taskRequestDTO.getContent());

        if (taskRequestDTO.getAssigneeId() != null) {
            User assignee = userRepository.findById(taskRequestDTO.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssigneeId(assignee);
        }

        if (taskRequestDTO.getStatus() != null) {
            TaskStatus status = taskStatusRepository.findById(taskRequestDTO.getStatus())
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            task.setStatus(status);
        }

        return task;
    }
}
