package hexlet.code.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserCreateDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}