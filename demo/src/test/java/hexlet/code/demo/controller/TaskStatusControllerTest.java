package hexlet.code.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.demo.dto.TaskStatusRequestDTO;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.model.User;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private TaskStatus savedStatus;

    private User savedUser;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        savedUser = userRepository.save(createFakeUser());

        jwtToken = jwtTokenProvider.createToken(savedUser.getId(), savedUser.getEmail());

        taskStatusRepository.deleteAll();
        savedStatus = taskStatusRepository.save(createFakeStatus());
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses/" + savedStatus.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                json -> json.node("name").isEqualTo(savedStatus.getName())
        );
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        TaskStatusRequestDTO request = Instancio.of(TaskStatusRequestDTO.class)
                .supply(Select.field(TaskStatusRequestDTO::getName), () -> faker.lorem().word())
                .supply(Select.field(TaskStatusRequestDTO::getSlug), () -> faker.internet().slug())
                .create();

        var result = mockMvc.perform(post("/api/task_statuses")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo(request.getName());

        assertThat(taskStatusRepository.findBySlug(request.getSlug())).isPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        TaskStatusRequestDTO updateDto = new TaskStatusRequestDTO();
        updateDto.setName("UpdatedName");
        updateDto.setSlug("updated-slug");

        var result = mockMvc.perform(put("/api/task_statuses/" + savedStatus.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo("UpdatedName");

        assertThat(taskStatusRepository.findById(savedStatus.getId()).get().getName())
                .isEqualTo("UpdatedName");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/" + savedStatus.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(taskStatusRepository.findById(savedStatus.getId())).isEmpty();
    }

    @Test
    public void testCreateWithoutAuthShouldFail() throws Exception {
        TaskStatusRequestDTO request = new TaskStatusRequestDTO();
        request.setName("NoAuth");
        request.setSlug("no-auth");

        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteWithoutAuthShouldFail() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/" + savedStatus.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private TaskStatus createFakeStatus() {
        TaskStatus status = new TaskStatus();
        status.setName(faker.lorem().word());
        status.setSlug(faker.internet().slug());
        return status;
    }

    public User createFakeUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.internet().password())
                .create();
    }
}
