package hexlet.code.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TaskRequestDTO {
    private long id;
    private List<Long> taskLabelIds;
    private Integer index;
    private LocalDateTime createdAt;
    private Long assignee_id;
    private String title;
    private String content;
    private String status;
}
