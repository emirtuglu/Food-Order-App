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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost/foodAppDatabase";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static Connection conn = setConnection();

    /**
     * Sets database connection.
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
     * @return True if successfully registered
     */
    public static boolean saveUser (User user) {
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
            return true;

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * Saves the restaurant object to database after encrypting the password with SHA-512.
     * @param restaurant
     * @param addressId address of the restaurant
     * @return True if successfully registered
     */
    public static boolean saveRestaurant (Restaurant restaurant, int addressId) {
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

            hashedPassword = md.digest(restaurant.getPassword().getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Save restaurant to database
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO restaurants (address_id, name, phone_number, mail, password, salt) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, addressId);
            pstmt.setString(2, restaurant.getName());
            pstmt.setString(3, restaurant.getPhoneNumber());
            pstmt.setString(4, restaurant.getMail());
            pstmt.setBytes(5, hashedPassword);
            pstmt.setBytes(6, salt);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * Saves address object to database, sets the relation between the user and address in database
     * @param address
     * @param userId
     */
    public static void saveUserAddress (Address address, int userId) throws SQLException {
        // Insert into addresses table
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO addresses (title, city, district, full_address) VALUES (?, ?, ?, ?)");
        pstmt.setString(1, address.getTitle());
        pstmt.setString(2, address.getCity());
        pstmt.setString(3, address.getDistrict().toLowerCase());
        pstmt.setString(4, address.getFullAddress());
        pstmt.executeUpdate();

        // Get id of the inserted address
        int addressId = -1;
        PreparedStatement pstmt2 = conn.prepareStatement("SELECT id FROM addresses WHERE city = ? AND district = ? AND full_address = ? ORDER BY id DESC LIMIT 1");
        pstmt2.setString(1, address.getCity());
        pstmt2.setString(2, address.getDistrict().toLowerCase());
        pstmt2.setString(3, address.getFullAddress());
        ResultSet rs = pstmt2.executeQuery();
        if (rs.next()) {
            addressId = rs.getInt("id");
        }

        // Insert into user_addresses table which holds address-user relations
        PreparedStatement pstmt3 = conn.prepareStatement("INSERT INTO user_addresses (user_id, address_id) VALUES (?, ?)");
        pstmt3.setInt(1, userId);
        pstmt3.setInt(2, addressId);
        pstmt3.executeUpdate();
    }


    /**
     * Deletes address and address-user relation from the database
     * @param address
     * @param userId
     */
    public static void deleteUserAddress (Address address) throws SQLException {

        // Delete user-address relation 
        PreparedStatement pstmt = conn.prepareStatement(("DELETE FROM user_addresses WHERE address_id = ?"));
        pstmt.setInt(1, address.getId());
        pstmt.executeUpdate();

        // Delete address itself
        PreparedStatement pstmt2 = conn.prepareStatement("DELETE FROM addresses WHERE id = ?");
        pstmt2.setInt(1, address.getId());
        pstmt2.executeUpdate();
    }

    /**
     * Saves address object to database
     * @param restaurant
     * @param address
     * @return id of the address in the database
     */
    public static int saveRestaurantAddress (Restaurant restaurant, Address address) throws SQLException {
        // Insert into addresses table
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO addresses (city, district, full_address) VALUES (?, ?, ?)");
        pstmt.setString(1, address.getCity());
        pstmt.setString(2, address.getDistrict().toLowerCase());
        pstmt.setString(3, address.getFullAddress());
        pstmt.executeUpdate();

        // Get id of the inserted address
        PreparedStatement pstmt2 = conn.prepareStatement("SELECT id FROM addresses WHERE city = ? AND district = ? AND full_address = ? ORDER BY id DESC LIMIT 1");
        pstmt2.setString(1, address.getCity());
        pstmt2.setString(2, address.getDistrict().toLowerCase());
        pstmt2.setString(3, address.getFullAddress());
        ResultSet rs = pstmt2.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        return -1;
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
            // Get salt
            PreparedStatement pstmt = conn.prepareStatement("SELECT salt FROM users WHERE mail = ?");
            pstmt.setString(1, mail);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                salt = rs.getBytes("salt");
            }

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);

            hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

        // Check for a match in database
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM users WHERE mail = ?");
            pstmt.setString(1, mail);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Arrays.equals(hashedPassword, rs.getBytes("password"));
            }
            return false;
        }
        catch (SQLException e) {
            System.out.println("chcekUserPassword error");
            System.out.println(e);
            return false;
        }
    }

    /**
     * Check whether the entered mail and password matches with a restaurant in database
     * @param mail Mail entered by user
     * @param password Password entered by user
     * @return true if matches with a restaurant, false if not
     */
    public static boolean checkRestaurantPassword (String mail, String password) {
        byte[] hashedPassword = null;
        byte[] salt = null;
        // Hash the password entered by the user in order to compare with the hash in the database
        try {
            // Get salt
            PreparedStatement pstmt = conn.prepareStatement("SELECT salt FROM restaurants WHERE mail = ?");
            pstmt.setString(1, mail);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                salt = rs.getBytes("salt");
            }

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);

            hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println();
            return false;
        }

        // Check for a match in database
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM restaurants WHERE mail = ?");
            pstmt.setString(1, mail);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Arrays.equals(hashedPassword, rs.getBytes("password"));
            }
            return false;
        }
        catch (SQLException e) {
            System.out.println(e);
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

            if (rs.next()) {
                int id = rs.getInt("id");
                ArrayList<Address> addresses = getAddressesOfUser(id);
                ArrayList<Order> orders = getOrdersOfUser(id);
                ArrayList<Food> cart = getCartOfUser(id);
                User user = new User(id, rs.getString("name"), rs.getString("surname"), rs.getString("phone_number"), mail, addresses, orders, cart);
                return user;
            }
            return null;
            
        } catch (SQLException e) {
            System.out.println("getUser error");
            System.out.println(e);
            return null;
        }
    }

    /**
     * Retrieve user which has the specified id from the database
     * @param id
     * @return User object
     */
    public static User getUser (int id) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT name, surname, phone_number, mail FROM users WHERE id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ArrayList<Address> addresses = getAddressesOfUser(id);
                ArrayList<Order> orders = getOrdersOfUser(id);
                ArrayList<Food> cart = getCartOfUser(id);
                User user = new User(id, rs.getString("name"), rs.getString("surname"), rs.getString("phone_number"), rs.getString("mail"), addresses, orders, cart);
                return user;
            }
            return null;
            
        } catch (SQLException e) {
            System.out.println("getUser error");
            System.out.println(e);
            return null;
        }
    }

    /**
     * Retrieve addresses associated with the user from the database
     * @param id
     * @return ArrayList containing addresses of user
     */
    public static ArrayList<Address> getAddressesOfUser (int id) {
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
            System.out.println("getAddressesOfUser error");
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
            PreparedStatement pstmt = conn.prepareStatement("SELECT title, city, district, full_address FROM addresses WHERE id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Address(id, rs.getString("title"), rs.getString("city"), rs.getString("district"), rs.getString("full_address"));
            }
            return null;
            
        } catch (SQLException e) {
            System.out.println("getAddress error");
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
            PreparedStatement pstmt = conn.prepareStatement("SELECT restaurant_id, restaurant_name, name, description, price, enabled FROM foods WHERE id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Food(id, rs.getInt("restaurant_id"), rs.getString("restaurant_name"), rs.getString("name"), rs.getString("description"), rs.getDouble("price"), rs.getBoolean("enabled"));
            }
            return null;

        } catch (SQLException e) {
            System.out.println("getFood error");
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
            
            if (rs.next()) {
                Address address = getAddress(rs.getInt("address_id"));
                ArrayList<Food> menu = getMenuOfRestaurant(id);
                ArrayList<Order> orders = getOrdersOfRestaurant(id);
                return new Restaurant(id, address, rs.getString("name"), rs.getString("phone_number"), rs.getString("mail"), menu, orders);
            }
            return null;
            
        } catch (SQLException e) {
            System.out.println("getRestaurant error");
            System.out.println(e);
            return null;
        }
    }

    /**
     * Retrieve the restaurant which has the specified mail from the database
     * @param mail
     * @return restaurant object
     */
    public static Restaurant getRestaurant (String mail) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, address_id, name, phone_number FROM restaurants WHERE mail = ?");
            pstmt.setString(1, mail);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Address address = getAddress(rs.getInt("address_id"));
                int id = rs.getInt("id");
                ArrayList<Food> menu = getMenuOfRestaurant(id);
                ArrayList<Order> orders = getOrdersOfRestaurant(id);
                return new Restaurant(id, address, rs.getString("name"), rs.getString("phone_number"), mail, menu, orders);
            }
            return null;
            
        } catch (SQLException e) {
            System.out.println("getRestaurant error");
            System.out.println(e);
            return null;
        }
    }

    /**
     * Returns the arraylist of restaurants in the same district with the given address
     * @param address
     * @return
     */
    public static ArrayList<Restaurant> getRestaurantsInDistrict (Address address) throws SQLException {
        ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();

        String query = "SELECT r.id, r.address_id, r.name, r.phone_number, r.mail " +
        "FROM restaurants r " +
        "JOIN addresses a ON r.address_id = a.id " +
        "WHERE a.district = ?";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, address.getDistrict().toLowerCase());
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            Address restaurantAddress = getAddress(rs.getInt("address_id"));
            String name = rs.getString("name");
            String phoneNumber = rs.getString("phone_number");
            String mail = rs.getString("mail");
            ArrayList<Food> menu = getMenuOfRestaurant(id);
            Restaurant restaurant = new Restaurant(id, restaurantAddress, name, phoneNumber, mail, menu, null);
            restaurants.add(restaurant);
        }
        return restaurants;
    }

    /**
     * Returns the arraylist of foods in the given restaurant
     * @param restaurantId
     * @return
     */
    public static ArrayList<Food> getMenuOfRestaurant (int restaurantId) {
        ArrayList<Food> foods = new ArrayList<Food>();
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, name, restaurant_name, description, price, enabled FROM foods WHERE restaurant_id = ?");
            pstmt.setInt(1, restaurantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String restaurantName = rs.getString("restaurant_name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                boolean enabled = rs.getBoolean("enabled");
                
                Food food = new Food(id, restaurantId, restaurantName, name, description, price, enabled);
                foods.add(food);
            }
            return foods;
        } catch (SQLException e) {
            System.out.println(e);
            return foods;
        }
    }

    /**
     * Returns the arraylist of orders of the specified restaurant
     * @param restaurant
     * @return
     * @throws SQLException
     */
    public static ArrayList<Order> getOrdersOfRestaurant (int restaurantId) throws SQLException {
        ArrayList<Order> orders = new ArrayList<Order>();

        PreparedStatement pstmt = conn.prepareStatement("SELECT id, user_id, restaurant_name, user_address_id, time, price, status FROM orders WHERE restaurant_id = ?");
        pstmt.setInt(1, restaurantId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            int userId = rs.getInt("user_id");
            String restaurantName = rs.getString("restaurant_name");
            int userAddressId = rs.getInt("user_address_id");
            String time = rs.getString("time");
            double price = rs.getDouble("price");
            Status status = Status.valueOf(rs.getString("status"));

            ArrayList<Food> foods = getFoodsOfOrder(id);
            Address userAddress = Database.getAddress(userAddressId);
            PreparedStatement pstmt2 = conn.prepareStatement("SELECT name, surname, phone_number FROM users WHERE id = ?");
            pstmt2.setInt(1, userId);
            ResultSet rs2 = pstmt2.executeQuery();
            String fullName = "", phoneNumber = "";
            if (rs2.next()) {
                fullName = rs2.getString("name") + " " + rs2.getString("surname");
                phoneNumber = rs2.getString("phone_number");
            }

            Order order = new Order(id, userId, restaurantId, restaurantName, userAddress, fullName, phoneNumber, time, price, status, foods);
            orders.add(order);
        }
        return orders;
    }


    /**
     * Returns the arraylist of orders of the specified user
     * @param restaurant
     * @return
     */
    public static ArrayList<Order> getOrdersOfUser (int userId) throws SQLException {
        ArrayList<Order> orders = new ArrayList<Order>();

        PreparedStatement pstmt = conn.prepareStatement("SELECT id, restaurant_id, restaurant_name, user_address_id, time, price, status FROM orders WHERE user_id = ?");
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            int restaurantId = rs.getInt("restaurant_id");
            String restaurantName = rs.getString("restaurant_name");
            int userAddressId = rs.getInt("user_address_id");
            String time = rs.getString("time");
            double price = rs.getDouble("price");
            Status status = Status.valueOf(rs.getString("status"));

            ArrayList<Food> foods = getFoodsOfOrder(id);
            Address userAddress = Database.getAddress(userAddressId);
            PreparedStatement pstmt2 = conn.prepareStatement("SELECT name, surname, phone_number FROM users WHERE id = ?");
            pstmt2.setInt(1, userId);
            ResultSet rs2 = pstmt2.executeQuery();
            String fullName = "", phoneNumber = "";
            if (rs2.next()) {
                fullName = rs2.getString("name") + " " + rs2.getString("surname");
                phoneNumber = rs2.getString("phone_number");
            }

            Order order = new Order(id, userId, restaurantId, restaurantName, userAddress, fullName, phoneNumber, time, price, status, foods);
            orders.add(order);
        }
        return orders;
    }

    /**
     * Returns foods and their quantities of the specified order
     * @param orderId
     * @return
     */
    public static ArrayList<Food> getFoodsOfOrder (int orderId) {
        ArrayList<Food> foods = new ArrayList<Food>();

        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT food_id, quantity FROM ordered_foods WHERE order_id = ?");
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int foodId = rs.getInt("food_id");
                int quantity = rs.getInt("quantity");
                Food food = getFood(foodId);
                food.setQuantity(quantity);
                foods.add(food);
            }
            return foods;
        } catch (SQLException e) {
            System.out.println("getFoodsOfOrder error");
            System.out.println(e);
            return foods;
        }
    }

    
    /**
     * Save the given order to database
     * @param order
     */
    public static void saveOrder (Order order) throws SQLException {
        // Get time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        order.setTime(dtf.format(now));

        // Save order
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO orders (restaurant_id, restaurant_name, user_id, user_address_id, user_full_name, user_phone_number, time, price, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        pstmt.setInt(1, order.getRestaurantId());
        pstmt.setString(2, order.getRestaurantName());
        pstmt.setInt(3, order.getUserId());
        pstmt.setInt(4, order.getUserAddress().getId());
        pstmt.setString(5, order.getUserFullName());
        pstmt.setString(6, order.getUserPhoneNumber());
        pstmt.setString(7, dtf.format(now));
        pstmt.setDouble(8, order.getPrice());
        pstmt.setString(9, order.getStatus().toString());
        pstmt.executeUpdate();

        // Get id of order
        int id = -1;
        PreparedStatement pstmt2 = conn.prepareStatement("SELECT id FROM orders WHERE user_id = ? AND time = ? ORDER BY id DESC LIMIT 1");
        pstmt2.setInt(1, order.getUserId());
        pstmt2.setString(2, order.getTime());
        ResultSet rs = pstmt2.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }

        // Save ordered foods
        for (Food food : order.getFoods()) {
            PreparedStatement pstmt3 = conn.prepareStatement("INSERT INTO ordered_foods (order_id, food_id, quantity) VALUES (?, ?, ?)");
            pstmt3.setInt(1, id);
            pstmt3.setInt(2, food.getId());
            pstmt3.setInt(3, food.getQuantity());
            pstmt3.executeUpdate();
        }

        // Clear user's cart
        User user = Database.getUser(order.getUserId());
        user.setCart(new ArrayList<Food>());
        updateCart(user);
    }

    /**
     * Updates the status of the given order in database
     * @param order
     */
    public static void updateStatusOfOrder (Order order) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("UPDATE orders SET status = ? WHERE id = ?");
        pstmt.setString(1, order.getStatus().toString());
        pstmt.setInt(2, order.getId());
        pstmt.executeUpdate();
    }

    /**
     * Retrieve foods in user's cart from the database
     * @param id
     * @return Map containing foods and their quantities
     */
    public static ArrayList<Food> getCartOfUser (int id) {
        ArrayList<Food> cart = new ArrayList<Food>();
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT food_id, quantity FROM cart_foods WHERE user_id = ?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Food food = getFood(rs.getInt("food_id"));
                food.setQuantity(rs.getInt("quantity"));
                cart.add(food);
            }
            return cart;
            
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Updates foods in the cart of the user in database
     * @param user
     * @throws SQLException
     */
    public static void updateCart (User user) throws SQLException {
        // Delete foods that belong to that user from database
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM cart_foods WHERE user_id = ?");
        pstmt.setInt(1, user.getId());
        pstmt.executeUpdate();

        // Add foods in the cart of the user to database
        for (Food food : user.getCart()) {
            PreparedStatement pstmt2 = conn.prepareStatement("INSERT INTO cart_foods (user_id, food_id, quantity) VALUES (?, ?, ?)");
            pstmt2.setInt(1, user.getId());
            pstmt2.setInt(2, food.getId());
            pstmt2.setInt(3, food.getQuantity());
            pstmt2.executeUpdate();
        }
    }

    /**
     * Add a food to database and relates it to the specified restaurant
     * @param restaurant
     * @param food
     */
    public static boolean saveFood (Food food) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO foods (restaurant_id, restaurant_name, name, description, price, enabled) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, food.getRestaurantId());
            pstmt.setString(2, food.getRestaurantName());
            pstmt.setString(3, food.getName());
            pstmt.setString(4, food.getDescription());
            pstmt.setDouble(5, food.getPrice());
            pstmt.setBoolean(6, food.isEnabled());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * deletes the specified food from the restaurant menu
     * @param food
     */
    public static boolean deleteFoodFromMenu (Food food) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE foods SET restaurant_id = null WHERE id = ?");
            pstmt.setInt(1, food.getId());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * Updates whether the food is enabled or not
     * @param food
     */
    public static void updateFood (Food food) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("UPDATE foods SET name = ?, description = ?, price = ?, enabled = ? WHERE id = ?");
        pstmt.setString(1, food.getName());
        pstmt.setString(2, food.getDescription());
        pstmt.setDouble(3, food.getPrice());
        pstmt.setBoolean(4, food.isEnabled());
        pstmt.setInt(5, food.getId());
        pstmt.executeUpdate();
    }

}


