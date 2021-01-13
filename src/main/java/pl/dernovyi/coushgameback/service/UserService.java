package pl.dernovyi.coushgameback.service;

import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.coushgameback.exception.*;
import pl.dernovyi.coushgameback.model.User;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User register (String firstName, String lastName, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

    List<User> getUsers();


    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, MessagingException, NotAnImageFileException;

    User updateUser(String currentEmail, String newFirstName, String newLastName, String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    void deleteUser(String email) throws IOException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String userId, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    void updatePassword(String email, String oldPassword, String newPassword) throws EmailNotFoundException, MessagingException, PasswordNotCorrectException;


    User validateNewEmailAndOldEmail(String currentEmail, String newEmail ) throws EmailExistException, UserNotFoundException;
}
