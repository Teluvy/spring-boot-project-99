package hexlet.code.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskFilter {
    private String titleCont;
    private Long assigneeId;
    private String status;
    private Long labelId;
}
