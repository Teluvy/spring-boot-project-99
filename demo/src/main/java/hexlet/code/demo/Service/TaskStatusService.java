package hexlet.code.demo.Service;

import hexlet.code.demo.dto.TaskStatusRequestDTO;
import hexlet.code.demo.dto.TaskStatusResponseDTO;
import hexlet.code.demo.exception.ResourceNotFoundException;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.repository.TaskStatusRepository;
import hexlet.code.demo.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    TaskStatusRepository taskStatusRepository;

    public TaskStatusResponseDTO getById(@PathVariable long id){
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + " not found"));
        return toDTO(taskStatus);
    }

    public List<TaskStatusResponseDTO> getAll(){
        List<TaskStatusResponseDTO> taskStatuses = taskStatusRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
        return taskStatuses;
    }

    public TaskStatusResponseDTO create(TaskStatusRequestDTO taskStatusRequestDTO){
        TaskStatus taskStatus = toEntity(taskStatusRequestDTO);
        TaskStatus taskStatusSaved = taskStatusRepository.save(taskStatus);
        return toDTO(taskStatusSaved);
    }

    public TaskStatusResponseDTO update(TaskStatusRequestDTO taskStatusRequestDTO, long id){
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + " not found"));
        TaskStatus taskStatusUpdate = toEntity(taskStatusRequestDTO);
        taskStatus.setName(taskStatusUpdate.getName());
        taskStatus.setSlug(taskStatusUpdate.getSlug());
        return toDTO(taskStatusRepository.save(taskStatus));
    }

    public void delete(long id){
        taskStatusRepository.deleteById(id);
    }

    private TaskStatusResponseDTO toDTO(TaskStatus taskStatus) {
        TaskStatusResponseDTO taskStatusDTO = new TaskStatusResponseDTO();
        taskStatusDTO.setId(taskStatus.getId());
        taskStatusDTO.setName(taskStatus.getName());
        taskStatusDTO.setSlug(taskStatus.getSlug());
        taskStatusDTO.setCreatedAt(taskStatus.getCreatedAt());
        return taskStatusDTO;
    }

    private TaskStatus toEntity(TaskStatusRequestDTO taskStatusRequestDTO) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusRequestDTO.getName());
        taskStatus.setSlug(taskStatusRequestDTO.getSlug());
        return taskStatus;
    }
}
