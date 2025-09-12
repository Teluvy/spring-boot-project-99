package hexlet.code.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LabelResponseDTO {
    private long id;
    private String name;
    private LocalDateTime createdAt;
}
