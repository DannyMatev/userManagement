package com.users.management.service;

import com.users.management.exception.UserDoesNotExistException;
import com.users.management.model.User;
import com.users.management.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepositoryMock;

    private User user;

    @Before
    public void setup() {
        user = new User();

        user.setId("userId");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmailAddress("valid@email.address");
        user.setDateOfBirth(LocalDate.of(2000, 1, 1));
    }

    @Test
    public void givenUser_whenCreateUser_thenReturnCreatedUser() {
        User userDomainObject = new User();

        userDomainObject.setFirstName("firstName");
        userDomainObject.setLastName("lastName");
        userDomainObject.setEmailAddress("valid@email.address");
        userDomainObject.setDateOfBirth(LocalDate.of(2000, 1, 1));

        given(userRepositoryMock.save(userDomainObject)).willReturn(user);

        User actualOutput = userService.createUser(userDomainObject);

        verify(userRepositoryMock, times(1)).save(any());
        assertEquals(user, actualOutput);
    }

    @Test
    public void givenExistingUserId_whenFetchUserById_thenReturnFoundUser() throws UserDoesNotExistException {
        given(userRepositoryMock.findById("userId")).willReturn(Optional.of(user));

        User actualOutput = userService.fetchUserById("userId");

        verify(userRepositoryMock, times(1)).findById("userId");
        assertEquals(user, actualOutput);
    }

    @Test(expected = UserDoesNotExistException.class)
    public void givenNonExistingUserId_whenFetchUserById_thenThrowException() throws UserDoesNotExistException {
        given(userRepositoryMock.findById("userId")).willReturn(Optional.empty());

        userService.fetchUserById("userId");

        verify(userRepositoryMock, times(1)).findById("userId");
    }

    @Test
    public void whenFetchAllUsers_thenReturnUsersList() {
        List<User> expectedUserList = Collections.singletonList(user);

        given(userRepositoryMock.findAll()).willReturn(Collections.singletonList(user));

        List<User> actualUserList = userService.fetchAllUsers();

        verify(userRepositoryMock, times(1)).findAll();
        assertEquals(expectedUserList, actualUserList);
    }

    @Test
    public void givenUserAndExistingUserId_whenEditUser_thenReturnUpdatedUser() throws UserDoesNotExistException {
        User updatedUser = new User();

        updatedUser.setId("userId");
        updatedUser.setFirstName("updatedFirstName");
        updatedUser.setLastName("updatedLastName");
        updatedUser.setEmailAddress("valid@email.address");
        updatedUser.setDateOfBirth(LocalDate.of(2000, 1, 1));

        given(userRepositoryMock.findById("userId")).willReturn(Optional.of(user));
        given(userRepositoryMock.save(updatedUser)).willReturn(updatedUser);

        User actualOutput = userService.editUser("userId", updatedUser);

        verify(userRepositoryMock, times(1)).findById("userId");
        assertEquals(updatedUser, actualOutput);
    }

    @Test(expected = UserDoesNotExistException.class)
    public void givenUserAndNonExistingUserId_whenEditUser_thenReturnUpdatedUser() throws UserDoesNotExistException {
        User updatedUser = new User();

        updatedUser.setId("userId");
        updatedUser.setFirstName("updatedFirstName");
        updatedUser.setLastName("updatedLastName");
        updatedUser.setEmailAddress("valid@email.address");
        updatedUser.setDateOfBirth(LocalDate.of(2000, 1, 1));

        given(userRepositoryMock.findById("userId")).willReturn(Optional.empty());

        userService.editUser("userId", updatedUser);

        verify(userRepositoryMock, times(0)).save(user);
    }

    @Test
    public void givenExistingUserId_whenDeleteUser_thenReturnUpdatedUser() throws UserDoesNotExistException {
        given(userRepositoryMock.findById("userId")).willReturn(Optional.of(user));

        userService.deleteUser("userId");

        verify(userRepositoryMock, times(1)).delete(user);
    }

    @Test(expected = UserDoesNotExistException.class)
    public void givenNonExistingUserId_whenDeleteUser_thenReturnUpdatedUser() throws UserDoesNotExistException {
        given(userRepositoryMock.findById("userId")).willReturn(Optional.empty());

        userService.deleteUser("userId");

        verify(userRepositoryMock, times(0)).delete(user);
    }

}
