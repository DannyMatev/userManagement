package com.users.management.controller;

import com.users.management.dto.UserDTO;
import com.users.management.exception.EmailAlreadyUsedException;
import com.users.management.model.User;
import com.users.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Validated UserDTO userDTO) throws EmailAlreadyUsedException {
        User user = userService.createUser(userDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("user/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping(value = "/user/{id}", produces = "application/json")
    public  ResponseEntity<User> fetchUser(@PathVariable String id) {
        Optional<User> user = userService.fetchUserById(id);

        if(user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/user", produces = "application/json")
    public  ResponseEntity<List<User>> fetchAllUsers() {
        List<User> user = userService.fetchAllUsers();

        return ResponseEntity.ok(user);
    }

    @PutMapping(value = "/user/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> editUser() {

    }

}
