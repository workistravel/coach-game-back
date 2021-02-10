package pl.dernovyi.coushgameback.service;

import com.microsoft.azure.storage.StorageException;
import org.springframework.web.multipart.MultipartFile;
import pl.dernovyi.coushgameback.exception.*;
import pl.dernovyi.coushgameback.model.User;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;

public interface UserService {

    User register (String firstName, String lastName, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException, IOException;

    List<User> getUsers();


    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, MessagingException, NotAnImageFileException, StorageException, InvalidKeyException, URISyntaxException;

    User updateUser(String currentEmail, String newFirstName, String newLastName, String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException, StorageException, InvalidKeyException, URISyntaxException;

    void deleteUser(String email) throws IOException, InvalidKeyException, StorageException, URISyntaxException;

    void resetPassword(String email) throws EmailNotFoundException, MessagingException, IOException;

    User updateProfileImage(String userId, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException, StorageException, InvalidKeyException, URISyntaxException;

    void updatePassword(String email, String oldPassword, String newPassword) throws EmailNotFoundException, MessagingException, PasswordNotCorrectException, IOException;


    User validateNewEmailAndOldEmail(String currentEmail, String newEmail ) throws EmailExistException, UserNotFoundException;
}
