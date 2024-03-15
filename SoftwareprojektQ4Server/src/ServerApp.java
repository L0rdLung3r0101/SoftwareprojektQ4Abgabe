
// Autor: Yanik H.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerApp {

    private int port = 1985;

    private ServerSocket server;

    private boolean running;

    List<PreClientHandler> preClients;
    HashMap<ClientHandler, String> clients = new HashMap<ClientHandler, String>(); // for storing
    HashMap<ClientHandler, String> clientCurrentlyIn = new HashMap<ClientHandler, String>();


    public ServerApp() {
        // Constructor


        try {

            server = new ServerSocket(port);

            running = true;

            System.out.println("[INFO] Listening on port " + port + " for client to connect...");

            while (running) {

                Socket client = server.accept(); // new client connected
                // TODO: check Security Manager on .accept() method

                System.out.println("[INFO] New client connected: " + client.getInetAddress().getHostAddress() + "/" + client.getPort());

                PreClientHandler handler = new PreClientHandler(client, this);

                preClients.add(handler);

            }

        } catch (IOException e) {
            shutdown();
        }


    }

    public void createNewClient(PreClientHandler preClientHandler, Socket client, String userID) {

        preClients.remove(preClientHandler);

        ClientHandler clientHandler = new ClientHandler(this, client);

        clients.put(clientHandler, userID);

    }

    private void shutdown() {

        System.out.println("[INFO] Shutting server down...");

        long startTime = System.nanoTime();

        running = false;

        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long stopTime = System.nanoTime();

        System.out.println("[INFO] Finished shutting the server down (" + (stopTime - startTime) + ").");

    }

    public void removeClient(ClientHandler clientHandler) {

        clients.remove(clientHandler);

    }

    public void broadcast(String message, String chatID, String timestamp) {

        for (ClientHandler handler : clients.keySet()) {

            if (handler != null) {

                if (clientCurrentlyIn.get(handler) == chatID){

                    handler.sendMessage(chatID, message, timestamp, DBManager.getUsernameById(clients.get(handler)));

                }

            }

        }

    }

    public String getUserIdByClientHandler(ClientHandler clientHandler){ return clients.get(clientHandler); }

    public void setUserActivity(ClientHandler client, String chatID){
        clientCurrentlyIn.put(client, chatID);
    }

    public static void main(String[] args) {

        new ServerApp();

    }

}


/*

CREATE TABLE member_of(

chat_id int NOT NULL,
user_id int NOT NULL,

foreign key(chat_id) references chatrooms(id),
foreign key(user_id) references users(id)

);


 */
