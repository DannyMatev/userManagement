package com.users.management.service;

import com.users.management.exception.UserDoesNotExistException;
import com.users.management.model.User;
import com.users.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User fetchUserById(String id) throws UserDoesNotExistException {
        Optional<User> userResult = userRepository.findById(id);

        if (!userResult.isPresent()) {
            throw new UserDoesNotExistException(String.format("The user with id '%s' does not exist", id));
        }

        return userResult.get();
    }

    public List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    public User editUser(String id, User updatedUser) throws UserDoesNotExistException {
        User existingUser = fetchUserById(id);

        updatedUser.setId(existingUser.getId());

        return userRepository.save(updatedUser);
    }

    public void deleteUser(String id) throws UserDoesNotExistException {
        fetchUserById(id);

        userRepository.deleteById(id);
    }

}
