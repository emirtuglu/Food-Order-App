package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);

            while (true) {
                final Socket clientSocket = serverSocket.accept();
                Thread thread = new Thread( new Runnable() {
                    public void run() {
                        handleRequest(clientSocket);
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void handleRequest(Socket clientSocket) {
        try {
            boolean secondEmptyLine = false;
            InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(isr);
            BufferedWriter bufferedWriter = new BufferedWriter(osw);

            System.out.println("someone connected");

            // Read the client request
            StringBuilder requestBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    if (secondEmptyLine) {
                        break; // Exit the loop when the second empty line is encountered
                    }
                    secondEmptyLine = true;
                    continue;
                }
                requestBuilder.append(line);
                requestBuilder.append("\r\n");
            }

            String request = requestBuilder.toString();

            // Generate response
            String response = generateResponse(request);

            // Send response to client
            bufferedWriter.write(response);
            bufferedWriter.flush();
            bufferedWriter.close();
            clientSocket.close();

        } catch (IOException e) {
            System.out.println(e);
        } 

    }

    public static String generateResponse(String request) {
        // Extract lines, HTTP method, and path
        String[] lines = request.split("\r\n");
        String method = lines[0].split(" ")[0];
        String path = lines[0].split(" ")[1];
        String parameters = "";
        String body = "";

        // Extract parameters from path if any
        if (path.contains("?")) {
            parameters = path.split("\\?")[1];
            path = path.split("\\?")[0];

            // If parameters isn't valid, return
            if (!isValidParameter(parameters)) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid request";
            }
        }

        // Extract the body of POST requests
        if (method.equals("POST")) {
            body = lines[lines.length - 1];
        }

        // Initialize gson to serialize or deseralize objects to/from json format
        Gson gson = new Gson();

        // user-register endpoint
        if (method.equals("POST") && path.equals("/user-register")) {
            User user = gson.fromJson(body, User.class);

            // Perform checks
            if (!isValidName(user.getName(), 30)) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid name";
            }
            if (!isValidName(user.getSurname(), 30)) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid surname";
            }
            if (!isValidPhoneNumber(user.getPhoneNumber())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid phone number";
            }
            if (!isValidMail(user.getMail())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid mail";
            }
            if (!isValidPassword(user.getPassword())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid password";
            }

            if (Database.saveUser(user)) {
                return "HTTP/1.1 201 Created\r\n\r\nRegistration successful";
            }
            else {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nRegistration error";
            }
        }

        // restaurant-register endpoint
        if (method.equals("POST") && path.equals("/restaurant-register")) {
            Restaurant restaurant = gson.fromJson(body, Restaurant.class);

            // Perform checks
            if (!isValidName(restaurant.getName(), 50)) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid name";
            }
            if (!isValidPhoneNumber(restaurant.getPhoneNumber())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid phone number";
            }
            if (!isValidMail(restaurant.getMail())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid mail";
            }
            if (!isValidPassword(restaurant.getPassword())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid password";
            }
            if (!isValidAddress(restaurant.getAddress())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid address";
            }

            int addressId = Database.saveRestaurantAddress(restaurant, restaurant.getAddress());
            if (addressId == -1) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nRegistration error";
            }
            else if (Database.saveRestaurant(restaurant, addressId)) {
                return "HTTP/1.1 201 Created\r\n\r\nRegistration successful";
            }
            else {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nRegistration error";
            }
        }

        // user-login endpoint
        if (method.equals("POST") && path.equals("/user-login")) {
            User input = gson.fromJson(body, User.class);

            // Perform checks
            if (!isValidMail(input.getMail())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid mail address";
            }
            if (!isValidPassword(input.getPassword())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid password";
            }

            if (Database.checkUserPassword(input.getMail(), input.getPassword())) {
                User user = Database.getUser(input.getMail());
                String json = gson.toJson(user, User.class);

                String response = "HTTP/1.1 200 OK\r\n"
                + "Content-type: application/json\r\n"
                + "Content-Length: " + json.length()
                +"\r\n"
                + json;
                return response;
            }
            else {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nWrong mail address or password";
            }
        }

        // restaurant-login endpoint
        if (method.equals("POST") && path.equals("/restaurant-login")) {
            Restaurant input = gson.fromJson(body, Restaurant.class);

            // Perform checks
            if (!isValidMail(input.getMail())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid mail address";
            }
            if (!isValidPassword(input.getPassword())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid password";
            }

            if (Database.checkRestaurantPassword(input.getMail(), input.getPassword())) {
                Restaurant restaurant = Database.getRestaurant(input.getMail());
                String json = gson.toJson(restaurant, Restaurant.class);

                String response = "HTTP/1.1 200 OK\r\n"
                + "Content-type: application/json\r\n"
                + "Content-Length: " + json.length()
                +"\r\n"
                + json;
                return response;
            }
            else {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nLogin error";
            }
        }

        // restaurants endpoint, returns the list of restaurants in the same district with the user
        if (method.equals("GET") && path.equals("/restaurants")) {

            // Check parameters
            if (!parameters.split("=")[0].equals("addressId")) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid parameters";
            }
            int addressId = Integer.parseInt(parameters.split("=")[1]);
            Address address = Database.getAddress(addressId);
            
            // Perform checks
            if (!isValidAddress(address)) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid address error";
            }

            try {
                ArrayList<Restaurant> restaurants;
                restaurants = Database.getRestaurantsInDistrict(address);
                String json = gson.toJson(restaurants, new TypeToken<List<Restaurant>>(){}.getType());

                String response = "HTTP/1.1 200 OK\r\n"
                    + "Content-type: application/json\r\n"
                    + "Content-Length: " + json.length()
                    +"\r\n"
                    + json;
                return response;

            } catch (SQLException e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't get restaurants";
            }
        }

        // user-orders endpoint, returns orders of the user
        if (method.equals("GET") && path.equals("/user-orders")) {

            // Check parameters
            if (!parameters.split("=")[0].equals("userId")) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid parameters";
            }
            int userId = Integer.parseInt(parameters.split("=")[1]);

            try {
                ArrayList<Order> orders = Database.getOrdersOfUser(userId);
                String json = gson.toJson(orders, new TypeToken<List<Order>>(){}.getType());

                String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + json.length()
                +"\r\n"
                + json;
                return response;
            } catch (SQLException e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't get orders";
            }
        }

        // user-cart endpoint, returns cart of the user
        if (method.equals("GET") && path.equals("/user-cart")) {

            // Check parameters
            if (!parameters.split("=")[0].equals("userId")) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid parameters";
            }
            int userId = Integer.parseInt(parameters.split("=")[1]);

            try {
                ArrayList<Food> cart = Database.getCartOfUser(userId);
                String json = gson.toJson(cart, new TypeToken<List<Food>>(){}.getType());

                String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + json.length()
                +"\r\n"
                + json;
                return response;
            } catch (Exception e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't get cart";
            }
        }

        // user-addresses endpoint, returns addresses of the user
        if (method.equals("GET") && path.equals("/user-addresses")) {

            // Check parameters
            if (!parameters.split("=")[0].equals("userId")) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid parameters";
            }
            int userId = Integer.parseInt(parameters.split("=")[1]);

            try {
                ArrayList<Address> addresses = Database.getAddressesOfUser(userId);
                String json = gson.toJson(addresses, new TypeToken<List<Address>>(){}.getType());

                String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + json.length()
                +"\r\n"
                + json;
                return response;
            } catch (Exception e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't get addresses";
            }
        }

        // restaurant-orders endpoint, returns orders of the restaurant
        if (method.equals("GET") && path.equals("/restaurant-orders")) {

            // Check parameters
            if (!parameters.split("=")[0].equals("restaurantId")) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid parameters";
            }
            int restaurantId = Integer.parseInt(parameters.split("=")[1]);

            try {
                ArrayList<Order> orders = Database.getOrdersOfRestaurant(restaurantId);
                String json = gson.toJson(orders, new TypeToken<List<Order>>(){}.getType());

                String response = "HTTP/1.1 200 OK\r\n"
                + "Content-type: application/json\r\n"
                +"\r\n"
                + json;
                return response;
            } catch (Exception e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't get orders";
            }
        }

        // restaurant-menu endpoint, returns menu of the restaurant
        if (method.equals("GET") && path.equals("/restaurant-menu")) {

            // Check parameters
            if (!parameters.split("=")[0].equals("restaurantId")) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid parameters";
            }
            int restaurantId = Integer.parseInt(parameters.split("=")[1]);

            try {
                ArrayList<Food> foods = Database.getMenuOfRestaurant(restaurantId);
                String json = gson.toJson(foods, new TypeToken<List<Food>>(){}.getType());

                String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + json.length()
                +"\r\n"
                + json;
                return response;
            } catch (Exception e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't get menu";
            }
        }

        // update-cart endpoint, updates the cart of the user
        if (method.equals("POST") && path.equals("/update-cart")) {
            User user = gson.fromJson(body, User.class);

            // Perform checks
            if (!isValidCart(user.getCart())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCannot add food from different restaurants to cart";
            }

            try {
                Database.updateCart(user);
                return "HTTP/1.1 200 OK\r\n\r\nCart updated";
            } catch (SQLException e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't update cart";
            }

        }

        // send-order endpoint, user sends an order
        if (method.equals("POST") && path.equals("/send-order")) {
            Order order = gson.fromJson(body, Order.class);
            order.setStatus(Status.ACTIVE);
            try {
                Database.saveOrder(order);
                return "HTTP/1.1 200 OK\r\n\r\nOrder received";
            } catch (SQLException e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't send order";
            }
        }

        // update-order endpoint, updates status of the order
        if (method.equals("POST") && path.equals("/update-order")) {
            Order order = gson.fromJson(body, Order.class);
            try {
                Database.updateStatusOfOrder(order);
            } catch (SQLException e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't update order";
            }
        }

        // update-food endpoint, restaurant changes the availibility of food
        if (method.equals("POST") && path.equals("/update-food")) {
            Food food = gson.fromJson(body, Food.class);
            try {
                Database.updateStatusOfFood(food);
            } catch (SQLException e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nCouldn't update food";
            }
        }

        // user-add-address endpoint, adds address to user
        if (method.equals("POST") && path.equals("/user-add-address")) {
            Address address = gson.fromJson(body, Address.class);

            // Check parameters
            if (!parameters.split("=")[0].equals("userId")) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid parameters";
            }
            int userId = Integer.parseInt(parameters.split("=")[1]);

            // Perform checks
            if (!isValidAddress(address)) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid address";
            }

            try {
                Database.saveUserAddress(address, userId);
                return "HTTP/1.1 201 Created\r\n\r\nAddress successfully added";
            } catch (SQLException e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nAddress couldn't be added";
            }
        }

        // user-delete-address endpoint, deletes address from user
        if (method.equals("POST") && path.equals("/user-delete-address")) {
            Address address = gson.fromJson(body, Address.class);

            // Perform checks
            if (!isValidAddress(address)) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid address";
            }

            try {
                Database.deleteUserAddress(address);
                return "HTTP/1.1 200 OK\r\n\r\nAddress successfully deleted";
            } catch (SQLException e) {
                System.out.println(e);
                return "HTTP/1.1 401 Unauthorized\r\n\r\nAddress couldn't be deleted";
            }
        }

        // add-food endpoint, adds food to the menu of the restaurant
        if (method.equals("POST") && path.equals("/add-food")) {
            Food food = gson.fromJson(body, Food.class);

            // Perform checks
            if (!isValidName(food.getName(), 50)) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid food name";
            }
            if (!isValidPrice(food.getPrice())) {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nInvalid food price";
            }

            if (Database.saveFood(food)) {
                return "HTTP/1.1 201 Created\r\n\r\nFood successfully added";
            } 
            else {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nFood couldn't be added";
            }
        }

        // delete-food endpoint, deletes food from the menu of the restaurant
        if (method.equals("POST") && path.equals("/delete-food")) {
            Food food = gson.fromJson(body, Food.class);
            
            if (Database.deleteFood(food)) {
                return "HTTP/1.1 200 OK\r\n\r\nFood successfully deleted";
            }
            else {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nFood couldn't be deleted";
            }
        }

        return "HTTP/1.1 404 Not Found\r\n\r\nPage not found";
    }

    public static boolean isValidName (String name, int length) {
        return name != null && name.length() < length && name.matches("[a-zA-Z-]+");
    }

    public static boolean isValidPassword (String password) {
        return password.length() > 5 && password.length() < 16;
    }

    public static boolean isValidPhoneNumber (String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
    }

    public static boolean isValidMail (String mail) {
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return mail != null && mail.matches(regex);
    }

    public static boolean isValidPrice (Double price) {
        return price >= 0;
    }

    public static boolean isValidAddress (Address address) {
        if (address == null) {
            return false;
        }
        String city = address.getCity();
        String district = address.getDistrict();
        String fullAddress = address.getFullAddress();

        return city != null && city.length() > 1 && city.length() < 30 &&
            district != null && district.length() > 1 && district.length() < 30 &&
            fullAddress != null && fullAddress.length() > 9 && fullAddress.length() < 256;
    }

    public static boolean isValidParameter (String parameter) {
        if (parameter.split("&").length == 1) {
            String[] params = parameter.split("=");
            if (params.length == 2) {
                if (params[0].matches("[a-zA-Z]+") && params[0].length() < 15 && params[1].matches("[0-9]+") && params[1].length() < 5) {
                    return true;
                }
            }
        } 
        return false;
    }

    public static boolean isValidCart (ArrayList<Food> cart) {
        // Check if all foods has same restaurant id
        if (cart.isEmpty()) {
            return true;
        }
        int restaurantId = cart.get(0).getRestaurantId();
        for (Food food : cart) {
            if (food.getRestaurantId() != restaurantId) {
                return false;
            }
        }
        return true;
    }
    
}
