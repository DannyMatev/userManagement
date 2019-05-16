package com.users.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.management.controller.UserController;
import com.users.management.dto.UserDTO;
import com.users.management.exception.UserDoesNotExistException;
import com.users.management.model.User;
import com.users.management.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;

    @SpyBean
    private ModelMapper modelMapperMock;

    private ObjectMapper objectMapper;

    private User user;

    private UserDTO userDTO;

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();

        user = new User();
        user.setId("userId");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmailAddress("valid@email.address");
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));

        userDTO = new UserDTO();
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        userDTO.setEmailAddress("valid@email.address");
        userDTO.setDateOfBirth(LocalDate.of(2000, 1, 1));
    }

    @Test
    public void givenValidUser_whenCreateUser_thenReturnCreatedUser() throws Exception {
        when(userServiceMock.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    public void givenUserWithDuplicateEmail_whenCreateUser_thenReturnBadRequest() throws Exception {
        userDTO.setEmailAddress("invalidemail.address");

        when(userServiceMock.createUser(any(User.class))).thenThrow(new DuplicateKeyException("index: emailAddress dup"));

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenUserWithInvalidEmailFormat_whenCreateUser_thenReturnBadRequest() throws Exception {
        userDTO.setEmailAddress("invalidemailaddress");
        userDTO.setId("userId");

        when(userServiceMock.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenUserWithFutureBirthDate_whenCreateUser_thenReturnBadRequest() throws Exception {
        userDTO.setDateOfBirth(LocalDate.of(2100, 1, 1));

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenExistingUserId_whenFetchUser_thenReturnUser() throws Exception {
        userDTO.setId("userId");

        when(userServiceMock.fetchUserById("userId")).thenReturn(user);

        mockMvc.perform(get("/user/userId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));
    }

    @Test
    public void givenNonExistingUserId_whenFetchUser_thenReturnNotFound() throws Exception {
        when(userServiceMock.fetchUserById(anyString())).thenThrow(new UserDoesNotExistException(""));

        mockMvc.perform(get("/user/userId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenFetchAllUsers_thenReturnListOfUsers() throws Exception {
        userDTO.setId("userId");

        when(userServiceMock.fetchAllUsers()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singleton(userDTO))));
    }

    @Test
    public void givenValidUserAndUserId_whenEditUser_thenReturnUpdatedUser() throws Exception {
        userDTO.setId("userId");

        when(userServiceMock.editUser(anyString(), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/user/userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));
    }

    @Test
    public void givenValidUserAndInvalidUserId_whenEditUser_thenReturnBadRequest() throws Exception {
        when(userServiceMock.editUser(anyString(), any(User.class))).thenThrow(new UserDoesNotExistException(""));

        mockMvc.perform(put("/user/userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenInvalidEmailUserAndValidUserId_whenEditUser_thenReturnBadRequest() throws Exception {
        userDTO.setEmailAddress("invalidemailaddress");

        mockMvc.perform(put("/user/userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenFutureDateUserAndValidUserId_whenEditUser_thenReturnBadRequest() throws Exception {
        userDTO.setDateOfBirth(LocalDate.of(2100, 1, 1));

        mockMvc.perform(put("/user/userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenExistingEmailUserAndValidUserId_whenEditUser_thenReturnBadRequest() throws Exception {
        userDTO.setDateOfBirth(LocalDate.of(2100, 1, 1));

        when(userServiceMock.editUser(anyString(), any(User.class))).thenThrow(new DuplicateKeyException("index: emailAddress dup"));

        mockMvc.perform(put("/user/userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenExistingUserId_whenDeleteUser_thenReturnNoContent() throws Exception {
        userDTO.setDateOfBirth(LocalDate.of(2100, 1, 1));

        mockMvc.perform(delete("/user/userId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenNonExistingUserId_whenDeleteUser_thenReturnBadRequest() throws Exception {
        userDTO.setDateOfBirth(LocalDate.of(2100, 1, 1));

        doThrow(new UserDoesNotExistException("")).when(userServiceMock).deleteUser(anyString());

        mockMvc.perform(delete("/user/userId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
