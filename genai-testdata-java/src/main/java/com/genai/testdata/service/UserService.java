package com.genai.testdata.service;

import com.genai.testdata.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// A simple service class to manage User objects.
// This class will be the target for our tests using GenAI-generated data.
public class UserService {

    // In a real application, this would interact with a database.
    // For this example, we use an in-memory map.
    private final Map<String, User> userDatabase = new HashMap<>();

    /**
     * Registers a new user.
     * @param user The user object to register.
     * @return The registered user, or null if a user with the same ID already exists.
     */
    public User registerUser(User user) {
        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            throw new IllegalArgumentException("User and User ID cannot be null or empty.");
        }
        if (userDatabase.containsKey(user.getId())) {
            System.out.println("User with ID " + user.getId() + " already exists.");
            return null; // User already exists
        }
        userDatabase.put(user.getId(), user);
        System.out.println("User registered: " + user.getFirstName() + " " + user.getLastName());
        return user;
    }

    /**
     * Finds a user by their ID.
     * @param id The ID of the user to find.
     * @return An Optional containing the User if found, or an empty Optional otherwise.
     */
    public Optional<User> findUserById(String id) {
        return Optional.ofNullable(userDatabase.get(id));
    }

    /**
     * Retrieves all registered users.
     * @return A list of all users.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userDatabase.values());
    }

    /**
     * Updates an existing user's information.
     * @param user The user object with updated information.
     * @return The updated user, or null if the user does not exist.
     */
    public User updateUser(User user) {
        if (user == null || user.getId() == null || user.getId().isEmpty()) {
            throw new IllegalArgumentException("User and User ID cannot be null or empty.");
        }
        if (!userDatabase.containsKey(user.getId())) {
            System.out.println("User with ID " + user.getId() + " not found for update.");
            return null; // User does not exist
        }
        userDatabase.put(user.getId(), user); // Overwrites existing user
        System.out.println("User updated: " + user.getFirstName() + " " + user.getLastName());
        return user;
    }

    /**
     * Deletes a user by their ID.
     * @param id The ID of the user to delete.
     * @return True if the user was deleted, false otherwise.
     */
    public boolean deleteUser(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (userDatabase.containsKey(id)) {
            userDatabase.remove(id);
            System.out.println("User with ID " + id + " deleted.");
            return true;
        }
        System.out.println("User with ID " + id + " not found for deletion.");
        return false;
    }

    /**
     * Clears all users from the database. Useful for test setup/teardown.
     */
    public void clearUsers() {
        userDatabase.clear();
        System.out.println("All users cleared from database.");
    }
}