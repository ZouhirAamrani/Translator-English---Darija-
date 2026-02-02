package com.translator.security;

import com.translator.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    
    private static final UserRepository INSTANCE = new UserRepository();
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
    private UserRepository() {
        loadUsers();
    }
    
    public static UserRepository getInstance() {
        return INSTANCE;
    }
    
    /**
     * Load users from properties file
     */
    private void loadUsers() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("users.properties")) {
            
            if (input == null) {
                System.err.println("users.properties not found, creating default users");
                createDefaultUsers();
                return;
            }
            
            Properties prop = new Properties();
            prop.load(input);
            
            // Parse users from properties
            Set<String> usernames = new HashSet<>();
            for (String key : prop.stringPropertyNames()) {
                if (key.endsWith(".password")) {
                    String username = key.substring(0, key.indexOf(".password"));
                    usernames.add(username);
                }
            }
            
            // Create User objects
            for (String username : usernames) {
                String password = prop.getProperty(username + ".password");
                String rolesStr = prop.getProperty(username + ".roles", "USER");
                String enabledStr = prop.getProperty(username + ".enabled", "true");
                
                Set<String> roles = new HashSet<>();
                for (String role : rolesStr.split(",")) {
                    roles.add(role.trim());
                }
                
                User user = new User(username, password, roles);
                user.setEnabled(Boolean.parseBoolean(enabledStr));
                
                users.put(username, user);
            }
            
            System.out.println("Loaded " + users.size() + " users");
            
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            createDefaultUsers();
        }
    }
    
    /**
     * Create default users for testing
     */
    private void createDefaultUsers() {
        // Admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(PasswordEncoder.encode("admin123"));
        admin.addRole("ADMIN");
        admin.addRole("USER");
        users.put("admin", admin);
        
        // Regular user
        User user = new User();
        user.setUsername("user");
        user.setPassword(PasswordEncoder.encode("user123"));
        user.addRole("USER");
        users.put("user", user);
        
        System.out.println("Created default users: admin/admin123, user/user123");
    }
    
    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        return users.get(username);
    }
    
    /**
     * Authenticate user
     */
    public User authenticate(String username, String password) {
        User user = findByUsername(username);
        
        if (user == null || !user.isEnabled()) {
            return null;
        }
        
        if (PasswordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }
    
    /**
     * Add new user (for future use)
     */
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }
    
    /**
     * Get all users
     */
    public Collection<User> getAllUsers() {
        return users.values();
    }
}