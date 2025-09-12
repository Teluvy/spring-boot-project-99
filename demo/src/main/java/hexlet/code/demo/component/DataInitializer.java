package hexlet.code.demo.component;

import hexlet.code.demo.model.Label;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.LabelRepository;
import hexlet.code.demo.repository.UserRepository;
import hexlet.code.demo.service.UserService;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataInitializer(UserService userService,
                           UserRepository userRepository,
                           TaskStatusRepository taskStatusRepository,
                           LabelRepository labelRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.labelRepository = labelRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userRepository.findByEmail(adminEmail).orElseGet(() -> {
            User user = new User();
            user.setEmail(adminEmail);
            user.setPassword(adminPassword);
            return userRepository.save(user);
        });

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

        List<String> defaultLabels = List.of(
                "feature", "bug"
        );

        defaultLabels.forEach(name -> {
            labelRepository.findByName(name).orElseGet(() -> {
                Label label = new Label();
                label.setName(name);
                return labelRepository.save(label);
            });
        });
    }

}