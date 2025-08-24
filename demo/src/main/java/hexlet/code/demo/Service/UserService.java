package hexlet.code.demo.service;

import hexlet.code.demo.dto.UserCreateDTO;
import hexlet.code.demo.dto.UserDTO;
import hexlet.code.demo.dto.UserUpdateDTO;
import hexlet.code.demo.exception.ResourceNotFoundException;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO getById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return toDTO(user);
    }

    public List<UserDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO create(UserCreateDTO userCreateDTO) {
        User user = toEntity(userCreateDTO);
        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    public UserDTO update(UserUpdateDTO userUpdateDTO, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        user.setEmail(userUpdateDTO.getEmail());
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());

        User updatedUser = userRepository.save(user);
        return toDTO(updatedUser);
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

    private User toEntity(UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setEmail(userCreateDTO.getEmail());
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPassword(userCreateDTO.getPassword());
        return user;
    }

    private UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        return userDTO;
    }
}
