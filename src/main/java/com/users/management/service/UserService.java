package com.users.management.service;

import com.users.management.dto.UserDTO;
import com.users.management.exception.EmailAlreadyUsedException;
import com.users.management.exception.UserDoesNotExistException;
import com.users.management.model.User;
import com.users.management.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    private ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public User createUser(UserDTO userDTO) throws EmailAlreadyUsedException {
        if (emailExists(userDTO.getEmailAddress())) {
            throw new EmailAlreadyUsedException(String.format("User with email '%s' already exists.", userDTO.getEmailAddress()));
        }

        User user = modelMapper.map(userDTO, User.class);

        return userRepository.save(user);
    }

    public UserDTO fetchUserById(String id) throws UserDoesNotExistException {
        Optional<User> userResult = userRepository.findById(id);

        if (!userResult.isPresent()) {
            throw new UserDoesNotExistException(String.format("The user with id '%s' does not exist", id));
        }

        User user = userResult.get();

        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> fetchAllUsers() {
        List<User> userList = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();

        userList.forEach(user -> userDTOList.add(modelMapper.map(user, UserDTO.class)));

        return userDTOList;
    }

    public UserDTO editUser(String id, UserDTO userDTO) throws UserDoesNotExistException, EmailAlreadyUsedException {
        Optional<User> userResult = userRepository.findById(id);

        if (!userResult.isPresent()) {
            throw new UserDoesNotExistException(String.format("The user with id '%s' does not exist", id));
        }

        User user = userResult.get();

        if (!userDTO.getEmailAddress().equals(user.getEmailAddress()) && emailExists(userDTO.getEmailAddress())) {
            throw new EmailAlreadyUsedException(String.format("User with email '%s' already exists.", userDTO.getEmailAddress()));
        }

        return userDTO;
    }

    private boolean emailExists(String email) {
        Optional<User> userResult = userRepository.findOneByEmailAddress(email);

        return userResult.isPresent();
    }

    public void deleteUser(String id) throws UserDoesNotExistException {
        Optional<User> userResult = userRepository.findById(id);

        if (!userResult.isPresent()) {
            throw new UserDoesNotExistException(String.format("The user with id '%s' does not exist", id));
        }

        userRepository.deleteById(id);
    }

}
