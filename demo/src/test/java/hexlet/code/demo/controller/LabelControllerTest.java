package hexlet.code.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.demo.dto.LabelRequestDTO;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.LabelRepository;
import hexlet.code.demo.repository.TaskRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TaskRepository taskRepository;

    private User savedUser;
    private String jwtToken;
    private Label savedLabel;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();

        savedUser = userRepository.save(createFakeUser());
        jwtToken = jwtTokenProvider.createToken(savedUser.getId(), savedUser.getEmail());

        savedLabel = labelRepository.save(createFakeLabel());
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/labels/" + savedLabel.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                json -> json.node("name").isEqualTo(savedLabel.getName())
        );
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/labels")
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
        LabelRequestDTO request = Instancio.of(LabelRequestDTO.class)
                .supply(Select.field(LabelRequestDTO::getName), () -> faker.lorem().word())
                .create();

        var result = mockMvc.perform(post("/api/labels")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo(request.getName());

        assertThat(labelRepository.findByName(request.getName())).isPresent();
    }

    @Test
    public void testUpdate() throws Exception {
        LabelRequestDTO updateDto = new LabelRequestDTO();
        updateDto.setName("UpdatedLabel");

        var result = mockMvc.perform(put("/api/labels/" + savedLabel.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo("UpdatedLabel");

        assertThat(labelRepository.findById(savedLabel.getId()).get().getName())
                .isEqualTo("UpdatedLabel");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/labels/" + savedLabel.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(labelRepository.findById(savedLabel.getId())).isEmpty();
    }

    @Test
    public void testCreateWithoutAuthShouldFail() throws Exception {
        LabelRequestDTO request = new LabelRequestDTO();
        request.setName("NoAuthLabel");

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteWithoutAuthShouldFail() throws Exception {
        mockMvc.perform(delete("/api/labels/" + savedLabel.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private Label createFakeLabel() {
        Label label = new Label();
        label.setName(faker.lorem().word());
        return label;
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
