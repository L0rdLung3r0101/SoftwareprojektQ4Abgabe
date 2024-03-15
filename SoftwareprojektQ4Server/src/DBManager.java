
// Autor: Yanik H.

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class DBManager {

    static String url = "jdbc:mysql://compaq-1118:3306/dettweiler";

    public DBManager() {

        System.out.println(getChatMessages("1"));

    }

    public static String getUsernameById(String id){

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://compaq-1118:3306/dettweiler";
            String username = "dettweiler";
            String pwd = "yanik";

            Connection connection = DriverManager.getConnection(url, username, pwd);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT username FROM users WHERE id = '" + id + "'");

            return result.next() ? result.getString(1) : null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String getIdByUsername(String user) {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://compaq-1118:3306/dettweiler";
            String username = "dettweiler";
            String pwd = "yanik";

            Connection connection = DriverManager.getConnection(url, username, pwd);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT id FROM users WHERE username = '" + user + "'");

            return result.next() ? result.getString(1) : null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void openConnection() {

        System.out.println(new File("data.properties").getAbsolutePath());


        /*
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File("data.properties").getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

         */

        String url = "jdbc:mysql://compaq-1118:3306/dettweiler";
        String username = "dettweiler";
        String pwd = "yanik";


        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, pwd);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int addUser(String username, String email, String password) { // noch keine Verwendung seitens der API

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, "dettweiler", "yanik");

            Statement statement = connection.createStatement();
            statement.executeQuery("INSERT INTO users(username, email, pass) values (" + username + "," + email + "," + hashPassword(password) + ")");

        }catch (SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return 0;
    }

    public static String checkUsername(String username, String password) {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, "dettweiler", "yanik");

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT username, pass FROM users WHERE username = \"" + username + "\"");

            result.next();
            String dbUsername = result.getString(1); // get username
            String dbPassword = result.getString(2); // get pwd

            String hashedPassword = hashPassword(password);

            System.out.println(dbUsername.equalsIgnoreCase(username));
            System.out.println(dbPassword.equals(hashedPassword));

            return dbUsername.equalsIgnoreCase(username) && dbPassword.equals(hashedPassword) ? dbUsername : "1";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String getChatMessages(String chatID) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, "dettweiler", "yanik");

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT content, send_at, author FROM messages WHERE chatrooms = '" + chatID + "'");

            String rtn = "chat ";

            while (result.next()) {

                Timestamp timestamp = result.getTimestamp(2);

                rtn += result.getString(1) + "|" + result.getTimestamp(2).toString() + "|" + result.getString(3) + "|";

            }
            result.close();
            statement.close();
            connection.close();

            return rtn;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null; // edge case

    }

    public static String getChats(String userID){

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, "dettweiler", "yanik");

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT chat_id, user_id FROM member_of WHERE user_id = '" + userID + "'");

            String rtn = "chats ";

            while (result.next()){

                rtn += result.getString(1) + "|" + result.getString(2) + "|";

            }

            return rtn;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static String hashPassword(String password) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));

        BigInteger number = new BigInteger(1, digest);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }

        return hexString.toString();

    }

    public static int newMessage(String chatID, String message, String userID){

        List<String> illegalStrings = Arrays.asList("|", "/", "\\", ":", "*", "?", "<", ">", "(", ")", "{", "}", "[", "]", "~", "'");

        for (String illegalString : illegalStrings){ // checking for illegal strings

            if (message.contains(illegalString)){
                return 1;
            }

        }

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, "dettweiler", "yanik");

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("INSERT INTO messages (content, author, chatrooms) VALUES (" + message + ", " + userID + ", " +  chatID + ")");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;

    }

    public static void sendMessage(String message, String chatID, ServerApp server){

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, "dettweiler", "yanik");

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT send_at FROM messages WHERE id = " + chatID);

            result.next();

            server.broadcast(message, chatID, result.getTimestamp(1).toString());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
