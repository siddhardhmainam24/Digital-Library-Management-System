package learning.outcome.Service;

import learning.outcome.DTO.*;
import learning.outcome.Entity.appUser;
import learning.outcome.Repository.UserRepository;
import learning.outcome.config.Jwtutil;
import learning.outcome.role;
import learning.outcome.utils.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static learning.outcome.role.user;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final Jwtutil jwtutil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, Jwtutil jwtutil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.jwtutil = jwtutil;
    }


    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public appUser createuser(appUser user) {
        return userRepository.save(user);
    }

    public List<appUser> getAllUsers() {
        return userRepository.findAll();

    }

    public appUser getuserbyid(long userid) {
        return userRepository.findById(userid).orElseThrow(() -> new RuntimeException("User Not Found"));

    }


    public appUser getbyemail(String email) {
        return userRepository.findByEmail(email);
    }


    public appUser updateuser(appUser user1) {
        return userRepository.save(user1);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public List<appUser> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }


    public ApiResponse<UserResponse> register(UserRegistrationRequest userRegistrationRequest) {
        try {
            if (emailExists(userRegistrationRequest.getEmail())) {
                return ApiResponse.error("email already exist");

            }
            appUser user = new appUser();
            user.setEmail(userRegistrationRequest.getEmail());
            user.setFirstName(userRegistrationRequest.getFirstName());
            user.setLastName(userRegistrationRequest.getLastName());
            user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
            user.setUserrole(role.user);
            user = userRepository.save(user);
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            return ApiResponse.success("resgistration sucessfull", userResponse);


        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }

    }

    public Boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public ApiResponse<LoginResponse> loginUser(LoginRequest loginRequest) {
        try {
            Optional<appUser> userOptional = userRepository.getUserByEmail(loginRequest.getEmail());
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found");
            }
            appUser user = userOptional.get();
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ApiResponse.error("Wrong password");
            }
            String token = jwtutil.generateToken(user);
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            LoginResponse loginResponse = new LoginResponse(token, userResponse, jwtutil.getExpirationTime());
            return ApiResponse.success("LOGIN SUCCESS", loginResponse);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<UserResponse> loginUser(Long userid, UpdateUSerRequest updateUserRequest) {
        try {
            Optional<appUser> userOptional = userRepository.getUserById(userid);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found");
            }
            appUser user = userOptional.get();
            if (updateUserRequest.getFirstName() != null && !updateUserRequest.getFirstName().trim().isEmpty()) {
                user.setFirstName(updateUserRequest.getFirstName());
            }

            if (updateUserRequest.getLastName() != null && !updateUserRequest.getLastName().trim().isEmpty()) {
                user.setLastName(updateUserRequest.getLastName());
            }
            if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().trim().isEmpty()) {
                String email = updateUserRequest.getEmail().toLowerCase().trim();
                if (!email.equals(user.getEmail().toLowerCase().trim())) {
                    return ApiResponse.error("Email Already Use");
                }
                user.setEmail(email);
            }
            if (updateUserRequest.getNewPassword() != null && !updateUserRequest.getNewPassword().isEmpty()) {
                if (updateUserRequest.getNewPassword() == null || updateUserRequest.getNewPassword().trim().isEmpty()) {
                    return ApiResponse.error("New Password Required");
                }

            }
            if (!passwordEncoder.matches(updateUserRequest.getCurrentPassword(), user.getPassword())) {
                return ApiResponse.error("Wrong password");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            appUser updateUser = userRepository.save(user);
            return ApiResponse.success("profile updated sucessfully", modelMapper.map(updateUser, UserResponse.class));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }


    }

    public ApiResponse<String> changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        try {
            if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
                return ApiResponse.error("New Password Does Not Match");
            }
            Optional<appUser> userOptional = userRepository.getUserById(userId);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found");
            }
            appUser user = userOptional.get();
            if (!passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
                return ApiResponse.error("New Password Does Not Match");
            }
            if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
                return ApiResponse.error("New Password muste be different from current password");

            }
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);
            return ApiResponse.success("profile updated successfully");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<String> deleteUserAccount(Long userId, String currentPassword) {
        try {
            Optional<appUser> userOptional = userRepository.getUserById(userId);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found");
            }
            appUser user = userOptional.get();
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ApiResponse.error("Wrong password");
            }
            userRepository.deleteById(userId);
            return ApiResponse.success("profile deleted successfully");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<UserResponse> getCurrentUserProfile(String token) {
        try {
            String email = jwtutil.extractUsername(token);
            Optional<appUser> userOptional = userRepository.getUserByEmail(email);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found");
            }
            return ApiResponse.success("profile found", modelMapper.map(userOptional.get(), UserResponse.class));

        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<List<UserResponse>> getAllUser()
    {
        List<appUser> userList = userRepository.findAll();
        List<UserResponse> userResponseList =userList.stream().map()


    }
}





