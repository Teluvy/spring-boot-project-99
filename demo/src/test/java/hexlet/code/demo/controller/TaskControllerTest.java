package hexlet.code.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.demo.dto.TaskRequestDTO;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.model.Task;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.LabelRepository;
import hexlet.code.demo.repository.TaskRepository;
import hexlet.code.demo.repository.TaskStatusRepository;
import hexlet.code.demo.repository.UserRepository;
import hexlet.code.demo.security.JwtTokenProvider;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private LabelRepository labelRepository;

    private Label savedLabel;
    private Task savedTask;
    private User savedUser;
    private String jwtToken;
    private TaskStatus savedStatus;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
        labelRepository.deleteAll();

        savedUser = userRepository.save(createFakeUser());
        jwtToken = jwtTokenProvider.createToken(savedUser.getId(), savedUser.getEmail());

        savedStatus = new TaskStatus();
        savedStatus.setName("ToReview");
        savedStatus.setSlug("to-review");
        savedStatus = taskStatusRepository.save(savedStatus);

        savedLabel = new Label();
        savedLabel.setName("bug");
        savedLabel = labelRepository.save(savedLabel);

        savedTask = taskRepository.save(createFakeTask());
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/tasks/" + savedTask.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("title").isEqualTo(savedTask.getTitle());
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setIndex(faker.number().numberBetween(1, 10000));
        request.setTitle(faker.lorem().sentence(3));
        request.setContent(faker.lorem().sentence(6));
        request.setAssignee_id(savedUser.getId());
        request.setStatus(savedStatus.getSlug());
        request.setTaskLabelIds(List.of(savedLabel.getId()));

        var result = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("title").isEqualTo(request.getTitle());

        assertThat(taskRepository.findAll())
                .anyMatch(t -> t.getTitle().equals(request.getTitle()));
    }

    @Test
    public void testUpdate() throws Exception {
        TaskRequestDTO updateDto = new TaskRequestDTO();
        updateDto.setIndex(savedTask.getIndex());
        updateDto.setTitle("Updated title");
        updateDto.setContent("Updated content");
        updateDto.setAssignee_id(savedUser.getId());
        updateDto.setStatus(savedStatus.getSlug());
        updateDto.setTaskLabelIds(List.of(savedLabel.getId()));

        var result = mockMvc.perform(put("/api/tasks/" + savedTask.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("title").isEqualTo("Updated title");

        assertThat(taskRepository.findById(savedTask.getId()).get().getTitle())
                .isEqualTo("Updated title");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + savedTask.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        assertThat(taskRepository.findById(savedTask.getId())).isEmpty();
    }

    private Task createFakeTask() {
        Label label = new Label();
        label.setName(faker.lorem().word());
        label = labelRepository.save(label);

        Label finalLabel = label;
        return Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getIndex), () -> faker.number().numberBetween(1, 10000))
                .supply(Select.field(Task::getAssigneeId), () -> savedUser)
                .supply(Select.field(Task::getTitle), () -> faker.lorem().sentence(3))
                .supply(Select.field(Task::getContent), () -> faker.lorem().sentence(6))
                .supply(Select.field(Task::getStatus), () -> savedStatus)
                .supply(Select.field(Task::getLabels), () -> Set.of(finalLabel))
                .create();
    }

    private User createFakeUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.internet().password())
                .create();
    }
}
