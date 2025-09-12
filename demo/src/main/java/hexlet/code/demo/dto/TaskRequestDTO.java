package hexlet.code.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TaskRequestDTO {
    private long id;
    private List<Long> labelIds;
    private Integer index;
    private LocalDateTime createdAt;
    private Long assigneeId;
    private String title;
    private String content;
    private Long status;
}
