/**
 *@author Sanya
 * The purpose of the Main class is to set up a server socket and manage client
 * connections by creating and invoking instances of the RequestProcessor class
 *  to handle client requests.
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            // Create a new ServerSocket on port 8080
            serverSocket = new ServerSocket(8080);
            
            // Continuously accept client connections
            while (true) {
                // Accept a client socket connection
                Socket clientSocket = serverSocket.accept();
                
                // Create a new RequestProcessor instance to handle the client request
                new RequestProcessor(clientSocket).run();
            }
        } catch (IOException e) {
            // Handle any IOException that occurs during the execution
            e.printStackTrace();
        }
    }
}

