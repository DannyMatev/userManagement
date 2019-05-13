package com.users.management.controller;

import com.users.management.dto.UserDTO;
import com.users.management.exception.EmailAlreadyUsedException;
import com.users.management.exception.UserDoesNotExistException;
import com.users.management.model.User;
import com.users.management.service.UserService;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) throws EmailAlreadyUsedException {
        User user = userService.createUser(userDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("user/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping(value = "/user/{id}", produces = "application/json")
    public  ResponseEntity<UserDTO> fetchUser(@PathVariable String id) throws UserDoesNotExistException {
        UserDTO user = userService.fetchUserById(id);

        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "/user", produces = "application/json")
    public  ResponseEntity<List<UserDTO>> fetchAllUsers() {
        List<UserDTO> userList = userService.fetchAllUsers();

        return ResponseEntity.ok(userList);
    }

    @PutMapping(value = "/user/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> editUser(@PathVariable String id, @RequestBody @Valid UserDTO userDTO) throws UserDoesNotExistException, EmailAlreadyUsedException {
        return ResponseEntity.ok(userService.editUser(id, userDTO));
    }

    @DeleteMapping(value = "/user/{id}", produces = "application/json")
    public  ResponseEntity<Object> deleteUser(@PathVariable String id) throws UserDoesNotExistException {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

}
