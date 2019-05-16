package com.users.management.controller;

import com.users.management.dto.UserDTO;
import com.users.management.exception.UserDoesNotExistException;
import com.users.management.model.User;
import com.users.management.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    private UserService userService;

    private ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        User createdUser = userService.createUser(user);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("user/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping(value = "/user/{id}", produces = "application/json")
    public  ResponseEntity<UserDTO> fetchUser(@PathVariable String id) throws UserDoesNotExistException {
        User user = userService.fetchUserById(id);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping(value = "/user", produces = "application/json")
    public  ResponseEntity<List<UserDTO>> fetchAllUsers() {
        List<User> userList = userService.fetchAllUsers();
        List<UserDTO> userDTOList = new ArrayList<>();

        userList.forEach(user -> userDTOList.add(modelMapper.map(user, UserDTO.class)));

        return ResponseEntity.ok(userDTOList);
    }

    @PutMapping(value = "/user/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> editUser(@PathVariable String id, @RequestBody @Valid UserDTO userDTO) throws UserDoesNotExistException {
        User user = modelMapper.map(userDTO, User.class);
        User editedUser = userService.editUser(id, user);
        UserDTO editedUserDTO = modelMapper.map(editedUser, UserDTO.class);

        return ResponseEntity.ok(editedUserDTO);
    }

    @DeleteMapping(value = "/user/{id}", produces = "application/json")
    public  ResponseEntity<Object> deleteUser(@PathVariable String id) throws UserDoesNotExistException {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }



}
