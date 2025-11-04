package fa.training.kiotviet.service;

import fa.training.kiotviet.model.User;
import fa.training.kiotviet.enums.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User business logic operations.
 */
public interface UserService {

    /**
     * Creates a new user.
     */
    User createUser(User user);

    /**
     * Updates an existing user.
     */
    User updateUser(Long id, User user);

    /**
     * Deletes a user by ID.
     */
    void deleteUser(Long id);

    /**
     * Finds a user by ID.
     */
    Optional<User> findUserById(Long id);

    /**
     * Finds a user by username.
     */
    Optional<User> findUserByUsername(String username);

    /**
     * Finds a user by email.
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Gets all users.
     */
    List<User> getAllUsers();

    /**
     * Gets users by role.
     */
    List<User> getUsersByRole(UserRole role);

    /**
     * Gets all active users.
     */
    List<User> getActiveUsers();

    /**
     * Searches users by keyword.
     */
    List<User> searchUsers(String keyword);

    /**
     * Activates or deactivates a user.
     */
    User toggleUserStatus(Long id, boolean active);

    /**
     * Changes user password.
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * Gets user statistics.
     */
    long getActiveUserCount();

    /**
     * Gets user count by role.
     */
    long getUserCountByRole(UserRole role);

    /**
     * Checks if username exists.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if email exists.
     */
    boolean existsByEmail(String email);
}