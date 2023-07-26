package com.example;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RequestManager {
    private static final String IP = "192.168.1.193";
    private static final int PORT = 1234;
    private static Socket socket;

    protected static String sendRequest(String request) {

        try {
            socket = new Socket(IP, PORT);
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(isr);
            BufferedWriter bufferedWriter = new BufferedWriter(osw);

            bufferedWriter.write(request);
            bufferedWriter.flush();
            socket.shutdownOutput();

            StringBuilder responseBuilder = new StringBuilder();
            boolean secondEmptyLine = false;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    if (secondEmptyLine) {
                        break; // Exit the loop when the second empty line is encountered
                    }
                    secondEmptyLine = true;
                    continue;
                }
                responseBuilder.append(line);
                responseBuilder.append("\r\n");
            }
            String response = responseBuilder.toString();
            bufferedReader.close();
            socket.close();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String requestBuild (String method, String path, String parameterName, String parameterValue, String body) {
        String request;
        if (parameterName != null && parameterValue != null) {
            path = path + "?" + parameterName + "=" + parameterValue;
        }
        request = method + " " + path + " HTTP/1.1\r\n";

        if (method.equals("POST")) {
            request += "Content-type: application/json\r\n";
            request += "Content-length: " + body.length() + "\r\n";
            request += "\r\n";
            request += body;
            return request;
        }
        else if (method.equals("GET")) {
            request += "\r\n";
            return request;
        }
        return null;
    }

    public static String getBody (String request) {
        if (request != null) {
            String[] lines = request.split("\r\n");
            return lines[lines.length - 1];
        }
        return null;
    }
}
