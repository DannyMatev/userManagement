package com.users.management.service;

import com.users.management.dto.UserDTO;
import com.users.management.exception.EmailAlreadyUsedException;
import com.users.management.model.User;
import com.users.management.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Optional<User> existingUser = userRepository.findOneByEmailAddress(userDTO.getEmailAddress());

        if (existingUser.isPresent()) {
            throw new EmailAlreadyUsedException("User with this email already exists.");
        }

        User user = modelMapper.map(userDTO, User.class);

        return userRepository.save(user);
    }

    public Optional<User> fetchUserById(String id) {
        return userRepository.findById(id);
    }

    public List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    public User editUser(String id, UserDTO userDTO) {
        Optional<User> user = userRepository.findById(id);

        if(!user.isPresent()) {

        }

        User updatedUser = modelMapper.map(userDTO, User.class);
        


    }

}
