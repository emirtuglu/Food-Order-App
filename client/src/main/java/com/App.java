package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            Socket socket = new Socket("localhost", 1234);
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(isr);
            BufferedWriter bufferedWriter = new BufferedWriter(osw);

            String string = "GET /restaurants HTTP/1.1\r\n\r\n";

            bufferedWriter.write(string);
            bufferedWriter.flush();

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                responseBuilder.append(line);
                responseBuilder.append("\r\n");
            }
            String request = responseBuilder.toString();

            System.out.println(request);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
