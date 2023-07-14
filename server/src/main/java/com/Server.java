package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
            InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(isr);
            BufferedWriter bufferedWriter = new BufferedWriter(osw);

            // Read the client request
            StringBuilder requestBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                requestBuilder.append(line);
                requestBuilder.append("\r\n");
            }
            String request = requestBuilder.toString();

            // Generate response
            String response = generateResponse(request);

            // Send response to client
            bufferedWriter.write(response);
            bufferedWriter.flush();
            clientSocket.close();

        } catch (IOException e) {
            System.out.println(e);
        } 

    }

    public static String generateResponse(String request) {
        // Extract lines, HTTP method, and path
        String[] lines = request.split("/r/n");
        String method = lines[0].split(" ")[0];
        String path = lines[0].split(" ")[1];
        String body = "";

        // Extract the body of POST requests
        if (method.equals("POST")) {
            body = lines[lines.length - 1];
        }

        // Initialize gson to serialize or deseralize objects to/from json format
        Gson gson = new Gson();
        //ArrayList<User> users = gson.fromJson(json, new TypeToken<List<User>>(){}.getType());

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

        }

        // restaurant-login endpoint
        if (method.equals("POST") && path.equals("/restaurant-login")) {

        }

        // restaurants endpoint, returns the list of restaurants in the same district with the user
        if (method.equals("GET") && path.equals("/restaurants")) {

        }

        // user-orders endpoint, returns orders of the user
        if (method.equals("GET") && path.equals("/user-orders")) {

        }

        // restaurant-orders endpoint, return orders of the restaurant
        if (method.equals("GET") && path.equals("/restaurant-orders")) {

        }


        // add-food endpoint
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

        // remove-food endpoint
        if (method.equals("POST") && path.equals("/remove-food")) {
            Food food = gson.fromJson(body, Food.class);
            
            if (Database.removeFood(food)) {
                return "HTTP/1.1 200 OK\r\n\r\nFood successfully removed";
            }
            else {
                return "HTTP/1.1 401 Unauthorized\r\n\r\nFood couldn't be removed";
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
    
}
