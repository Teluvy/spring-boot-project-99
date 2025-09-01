package hexlet.code.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusRequestDTO {
    private String name;
    private String slug;
}
