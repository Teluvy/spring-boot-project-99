package hexlet.code.demo.component;

import hexlet.code.demo.Service.TaskStatusService;
import hexlet.code.demo.Service.UserService;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import hexlet.code.demo.dto.UserCreateDTO;
import lombok.AllArgsConstructor;

import java.util.Map;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final UserService userService;

    @Autowired
    private final TaskStatusRepository taskStatusRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail(email);
        userCreateDTO.setPassword("qwerty");
        userService.create(userCreateDTO);

        Map<String, String> defaultTaskStatuses = Map.of(
                "Draft", "draft",
                "ToReview", "to_review",
                "ToBeFixed", "to_be_fixed",
                "ToPublish", "to_publish",
                "Published", "published"
        );

        defaultTaskStatuses.forEach((name, slug) -> {
            taskStatusRepository.findBySlug(slug).orElseGet(() -> {
                TaskStatus status = new TaskStatus();
                status.setName(name);
                status.setSlug(slug);
                return taskStatusRepository.save(status);
            });
        });
    }

}