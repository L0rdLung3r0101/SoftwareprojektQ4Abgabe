
// Autor: Jeff D.

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.util.List;

public class PreClientHandler implements Runnable {
    private Socket client;
    private ServerApp server;

    private BufferedReader reader;
    private PrintWriter writer;

    private boolean running;


    public PreClientHandler(Socket client, ServerApp server) {
        this.client = client;
        this.server = server;

        new Thread(this).start();

    }

    @Override
    public void run() {

        this.running = true;

        try {
            this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String message;

        while (running) {

            try {

                message = reader.readLine();

                String[] result = message.split("\\|");

                String username = result[0];
                String password = result[1];


                String checked = DBManager.checkUsername(username, password);

                if (checked != "1"){ // login successful
                    String userID = DBManager.getIdByUsername(username);
                    running = false;
                    server.createNewClient(this, client, userID);
                }

                writer.println(checked);

            } catch (IOException e) {
                shutdown();
            }

        }

    }

    private void shutdown() {

        running = false;
        try {
            reader.close();
        } catch (IOException e) {}

        writer.close();

        try {
            client.close();
        } catch (IOException e) {}

    }


}
