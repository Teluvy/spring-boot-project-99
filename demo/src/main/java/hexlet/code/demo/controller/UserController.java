package hexlet.code.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import hexlet.code.demo.model.User;
import hexlet.code.demo.dto.UserDTO;
import hexlet.code.demo.dto.UserUpdateDTO;
import hexlet.code.demo.dto.UserCreateDTO;
import hexlet.code.demo.repository.UserRepository;
import hexlet.code.demo.service.UserService;
import hexlet.code.demo.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/{id}")
    public UserDTO index(@PathVariable long id){
        return userService.getById(id);
    }

    @GetMapping(path = "")
    public List<UserDTO> show(){
        return userService.getAll();
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody UserCreateDTO userCreateDTO){
        return userService.create(userCreateDTO);
    }

    @PutMapping(path = "/{id}")
    public UserDTO update(@RequestBody UserUpdateDTO userUpdateDTO, @PathVariable long id){
        return userService.update(userUpdateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable long id){
        userService.delete(id);
    }

    private User toEntity(UserCreateDTO userCreateDTO){
        User user = new User();
        user.setEmail(userCreateDTO.getEmail());
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPassword(userCreateDTO.getPassword());
        return user;
    }

    private UserDTO toDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        return userDTO;
    }
}