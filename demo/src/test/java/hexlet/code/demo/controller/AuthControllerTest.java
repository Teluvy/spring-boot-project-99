package hexlet.code.demo.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.demo.dto.UserDTO;
import hexlet.code.demo.model.User;
import hexlet.code.demo.dto.UserCreateDTO;
import hexlet.code.demo.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import hexlet.code.demo.security.JwtTokenProvider;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder encoder;

    private User savedUser;

    private String jwtToken;
    private String rawPassword;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        rawPassword = faker.internet().password();
        savedUser = userRepository.save(createFakeUser());

        jwtToken = jwtTokenProvider.createToken(savedUser.getId(), savedUser.getEmail());
    }

    @Test
    void testLoginSuccess() throws Exception {
        Map<String, String> requestBody = Map.of(
                "username", savedUser.getEmail(),
                "password", rawPassword
        );

        var result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();
        assertThat(token).isNotEmpty();
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        Map<String, String> requestBody = Map.of(
                "username", savedUser.getEmail(),
                "password", faker.internet().password()
        );

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        Map<String, String> requestBody = Map.of(
                "username", faker.internet().emailAddress(),
                "password", faker.internet().password()
        );

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized());
    }

    public User createFakeUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> encoder.encode(rawPassword))
                .create();
    }
}
