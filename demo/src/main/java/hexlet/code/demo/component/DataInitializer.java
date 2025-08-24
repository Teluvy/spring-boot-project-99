package hexlet.code.demo.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import hexlet.code.demo.service.UserService;
import hexlet.code.demo.model.User;
import hexlet.code.demo.dto.UserCreateDTO;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail(email);
        userCreateDTO.setPassword("qwerty");
        userService.create(userCreateDTO);
    }

}