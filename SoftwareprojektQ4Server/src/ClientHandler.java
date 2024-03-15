
// Autor: Yanik H.

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    ServerApp server;
    Socket client;

    private BufferedReader reader;
    private PrintWriter writer;

    private boolean running;

    public ClientHandler(ServerApp server, Socket client) {

        this.server = server;
        this.client = client;

        running = true;

        new Thread(this).start();

    }

    private void shutdown() {

        System.out.println("[CLIENT] Shutting client connection down. (" + client.getInetAddress().getHostAddress() + "/" + client.getPort() + ")");

        long startTime = System.nanoTime();

        running = false;

        if (client != null) {

            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (writer != null) {
            writer.close();
        }

        long stopTime = System.nanoTime();

        System.out.println("[CLIENT] Finished shutting the client down (" + (stopTime - startTime) + ").");

    }

    @Override
    public void run() {

        try {

            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

            while (running) {

                String message = reader.readLine();

                if (message.startsWith("chat")){ // chat command

                    String chatID = message.split(" ")[1]; // holt die chatID aus dem eingabestring "chat <chatID>" : siehe api

                    server.setUserActivity(this, chatID);

                    String messages = DBManager.getChatMessages(chatID);

                    writer.println(messages);

                }else if (message.startsWith("chats")){ // chats command

                    String userID = server.getUserIdByClientHandler(this);

                    String chats = DBManager.getChats(userID);

                    writer.println(chats);

                }else if (message.startsWith("send")){ // message send

                    String[] splitMessage = message.split(" ");

                    String[] splitContent = splitMessage[1].split("\\|");

                    DBManager.newMessage(splitContent[0], splitContent[1], server.getUserIdByClientHandler(this));
                    DBManager.sendMessage(splitContent[1], splitContent[0], server);

                }else if (message.startsWith("logout")){

                    shutdown();

                }

            }

        } catch (IOException e) {

            System.out.println("[CLIENT] Client disconnected (" + client.getInetAddress().getHostAddress() + "/" + client.getPort() + ").");

            server.removeClient(this);

            shutdown();
        }

    }

    public void sendMessage(String chatID, String message, String timestamp, String authorName) {

        writer.println("msg " + chatID + "|" + message + "|" + timestamp + "|" + authorName);

    }
}
