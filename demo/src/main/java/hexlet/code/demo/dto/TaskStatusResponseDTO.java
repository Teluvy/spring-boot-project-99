package hexlet.code.demo.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskStatusResponseDTO {
    private long id;
    private String name;
    private String slug;
    private LocalDateTime createdAt;
}
