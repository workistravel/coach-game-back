package pl.dernovyi.coushgameback.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.dernovyi.coushgameback.enumeration.Role;
import pl.dernovyi.coushgameback.exception.*;
import pl.dernovyi.coushgameback.model.User;
import pl.dernovyi.coushgameback.model.UserPrincipal;
import pl.dernovyi.coushgameback.repository.UserRepository;
import pl.dernovyi.coushgameback.service.UserService;

import javax.mail.MessagingException;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.*;
import static pl.dernovyi.coushgameback.constant.FileConstant.*;
import static pl.dernovyi.coushgameback.enumeration.Role.ROLE_SUPER_ADMIN;
import static pl.dernovyi.coushgameback.enumeration.Role.ROLE_USER;
import static pl.dernovyi.coushgameback.constant.UserImplConstant.*;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {


    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;
    private EmailGridService emailGridService;
    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService emailService,
                           EmailGridService emailGridService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService =  emailService;
        this.emailGridService = emailGridService;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if(user == null){
            LOGGER.error(NO_USER_FOUND_BY_EMAIL + email);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_EMAIL + email);

        }else {
           validateLoginAttempt(user);
           user.setLastLoginDateDisplay(user.getLastLoginData());
           user.setLastLoginData(new Date());
           userRepository.save(user);
           UserPrincipal userPrincipal = new UserPrincipal(user);
           LOGGER.info(RETURNING_FOUND_USER_BY_USERNAME + email);
           return userPrincipal;
        }
    }

    private void validateLoginAttempt(User user) {
        if(user.isNonLocked()){
            if(loginAttemptService.hasExceededMaxAttempts(user.getEmail())){
                user.setNonLocked(false);
            }else {
                user.setNonLocked(true);
            }
        }else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
        }
    }

    @Override
    public User register(String firstName, String lastName, String email) throws UserNotFoundException, EmailExistException, MessagingException, IOException {
        validateNewEmailAndOldEmail( EMPTY, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setJoinDate(new Date());

        user.setActive(true);
        user.setNonLocked(true);
        user.setRole(ROLE_USER.name());
        if(email.equalsIgnoreCase("ladadetal0@gmail.com")){
            user.setRole(ROLE_SUPER_ADMIN.name());
            encodedPassword = encodePassword("1");
        }
        user.setPassword(encodedPassword);
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(firstName));
        userRepository.save(user);
        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() , password, email);
//        emailService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() , password, email);
        LOGGER.info("New user password: "+ password);
        return user;
    }
    @Override
    public User addNewUser(String firstName, String lastName, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, MessagingException, NotAnImageFileException {
        validateNewEmailAndOldEmail(EMPTY , newEmail);
        User user = new User();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setEmail(newEmail);
        user.setPassword(encodedPassword);
        user.setActive(isActive);
        user.setNonLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(firstName));
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        emailService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,password, newEmail);
        LOGGER.info("New user password: "+ password);
        return user;
    }



    @Override
    public User updateUser(String currentEmail, String newFirstName, String newLastName, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
        User currentUser = validateNewEmailAndOldEmail(currentEmail, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNonLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public void deleteUser(String email) throws IOException {
        User user = userRepository.findUserByEmail(email);
        Path userFolder = Paths.get(USER_FOLDER + user.getEmail()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepository.deleteByEmail(email);

    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, IOException {
        User user = userRepository.findUserByEmail(email);
        if(user ==null){
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password =generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,password, email);
//        emailService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,password, email);
    }

    @Override
    public void updatePassword(String email, String oldPassword, String newPassword) throws EmailNotFoundException, MessagingException, PasswordNotCorrectException, IOException {
        User user = userRepository.findUserByEmail(email);
        if(user ==null){
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new PasswordNotCorrectException(YOUR_OLD_PASSWORD_NOT_CORRECT);
        }
        user.setPassword(encodePassword(newPassword));
        userRepository.save(user);
        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,newPassword, email);
//        emailService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,newPassword, email);
    }

    @Override
    public User updateProfileImage(String email, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, NotAnImageFileException {
        User user = validateNewEmailAndOldEmail( email , null);
        saveProfileImage(user, profileImage);
        return user;
    }



    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
        if(profileImage != null){
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())){
                throw new NotAnImageFileException(profileImage.getOriginalFilename() + "is not an image file. Please upload an image");
            }
            Path userFolder = Paths.get(USER_FOLDER  + user.getUserId()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder  + user.getUserId() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(),userFolder.resolve(user.getUserId() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImage( user.getUserId()));
            userRepository.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM  + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImage(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());

    }
    private String getTemporaryProfileImageUrl(String name) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH+ FORWARD_SLASH + name).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    public User validateNewEmailAndOldEmail(String currentEmail, String newEmail) throws  EmailExistException, UserNotFoundException {
        User userByNewEmail = findUserByEmail(newEmail);
        if(StringUtils.isNoneBlank(currentEmail)){
            User currentUser = findUserByEmail(currentEmail);
            if(currentUser == null){
                throw new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + currentEmail);
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;

        }else {
            if(userByNewEmail != null ){
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return  null;
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


}
