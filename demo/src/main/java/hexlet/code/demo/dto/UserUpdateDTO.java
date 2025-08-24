package hexlet.code.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserUpdateDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
}