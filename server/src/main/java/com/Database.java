package com;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost/foodAppDatabase";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static Connection conn = setConnection();

    /**
     * The method that sets database connection.
     */
    private static Connection setConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Saves the user object to database after encrypting the password with SHA-512.
     * @param user
     */
    public static void saveUser (User user) {
        // Hash the password
        byte[] hashedPassword = null;
        byte[] salt = null;

        try {
            // Create salt
            SecureRandom random = new SecureRandom();
            salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);

            hashedPassword = md.digest(user.getPassword().getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Save user to database
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (name, surname, phone_number, mail, password, salt) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getSurname());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.setString(4, user.getMail());
            pstmt.setBytes(5, hashedPassword);
            pstmt.setBytes(6, salt);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Saves address object to database, sets the relation between the user and address in database
     * @param user
     * @param address
     */
    public static void saveUserAddress (User user, Address address) {
        try {
            // Insert into addresses table
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO addresses (city, district, full_address) VALUES (?, ?, ?)");
            pstmt.setString(1, address.getCity());
            pstmt.setString(2, address.getDistrict());
            pstmt.setString(3, address.getFullAddress());
            pstmt.executeUpdate();

            // Get id of the inserted address
            PreparedStatement pstmt2 = conn.prepareStatement("SELECT id FROM addresses WHERE city = ? AND district = ? AND full_address = ? ORDER BY id DESC LIMIT 1");
            pstmt2.setString(1, address.getCity());
            pstmt2.setString(2, address.getDistrict());
            pstmt2.setString(3, address.getFullAddress());
            ResultSet rs = pstmt2.executeQuery();
            int addressId = rs.getInt("id");

            // Insert into user_addresses table which holds address-user relations
            PreparedStatement pstmt3 = conn.prepareStatement("INSERT INTO user_addresses (user_id, address_id) VALUES (?, ?)");
            pstmt3.setInt(1, user.getId());
            pstmt3.setInt(2, addressId);
            pstmt3.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Check whether the entered mail and password matches with a user in database
     * @param mail Mail entered by user 
     * @param password Password entered by user
     * @return true if matches with a user, false if not
     */
    public static boolean checkUserPassword (String mail, String password) {
        byte[] hashedPassword = null;
        byte[] salt = null;
        // Hash the password entered by the user in order to compare with the hash in the database
        try {
            // Create salt
            SecureRandom random = new SecureRandom();
            salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);

            hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            return false;
        }

        // Check for a match in database
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE mail = ?");
            pstmt.setString(1, mail);
            ResultSet rs = pstmt.executeQuery();
            return Arrays.equals(hashedPassword, rs.getBytes("password"));
        }
        catch (SQLException e) {
            return false;
        }
    }

    /**
     * Retrieve user which has the specified mail from the database
     * @param mail
     * @return User object
     */
    public static User getUser (String mail) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, name, surname, phone_number FROM users WHERE mail = ?");
            pstmt.setString(1, mail);
            ResultSet rs = pstmt.executeQuery();

            ArrayList<Address> addresses = getAddressesOfUser(rs.getInt("id"));
            HashMap<Food, Integer> cart = getCartOfUser(rs.getInt("id"));
            User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("surname"), rs.getString("phone_number"), mail, addresses, cart);
            return user;
            
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Retrieve addresses associated with the user from the database
     * @param id
     * @return ArrayList containing addresses of user
     */
    private static ArrayList<Address> getAddressesOfUser (int id) {
        ArrayList<Address> addresses = new ArrayList<Address>();
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT address_id FROM user_addresses WHERE user_id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                addresses.add(getAddress(rs.getInt("address_id")));
            }
            return addresses;

        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Retrieve foods in user's cart from the database
     * @param id
     * @return Map containing foods and their quantities
     */
    private static HashMap<Food, Integer> getCartOfUser (int id) {
        HashMap<Food, Integer> cart = new HashMap<Food, Integer>();
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT food_id, quantity FROM cart_foods WHERE user_id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Food food = getFood(rs.getInt("food_id"));
                cart.put(food, rs.getInt("quantity"));
            }
            return cart;
            
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Retrieve the address which has the specified id from the database
     * @param id
     * @return address object
     */
    public static Address getAddress (int id) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT city, district, full address FROM addresses WHERE id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            Address address = new Address(id, rs.getString("city"), rs.getString("district"), rs.getString("full_address"));
            return address;
            
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Retrieve the food which has the specified id from the database
     * @param id
     * @return food object
     */
    public static Food getFood (int id) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT restaurant_id, name, price, enabled FROM foods WHERE id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            Restaurant restaurant = getRestaurant(rs.getInt("restaurant_id"));
            Food food = new Food(id, restaurant, rs.getString("name"), rs.getDouble("price"), rs.getBoolean("enabled"));
            return food;
            
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Retrieve the restaurant which has the specified id from the database
     * @param id
     * @return restaurant object
     */
    public static Restaurant getRestaurant (int id) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT address_id, name, phone_number, mail FROM restaurants WHERE id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            Address address = getAddress(rs.getInt("address_id"));
            Restaurant restaurant = new Restaurant(id, address, rs.getString("name"), rs.getString("phone_number"), rs.getString("mail"));
            return restaurant;
            
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }
}
