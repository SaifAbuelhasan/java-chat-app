// package Server;

// import static java.nio.charset.StandardCharsets.UTF_8;

// import java.io.*;
// import java.nio.channels.*;

// public class ClientHandler implements Runnable {
// private final Server server;
// private final SocketChannel socket;
// private final BufferedReader reader;
// private final PrintWriter writer;

// public ClientHandler(Server server, SocketChannel socket) throws IOException
// {
// this.server = server;
// this.socket = socket;
// this.reader = new BufferedReader(Channels.newReader(socket, UTF_8));
// this.writer = new PrintWriter(Channels.newWriter(socket, UTF_8));
// }

// @Override
// public void run() {
// try {
// String message;
// while ((message = reader.readLine()) != null) {
// System.out.println("Recieved: " + message);
// server.broadcast(message);
// }
// } catch (Exception e) {
// System.err.println("Error handling client: " + e.getMessage());
// } finally {
// disconnect();
// }
// }

// public void sendMessage(String message) {
// writer.println(message);
// writer.flush();
// }

// private void disconnect() {
// try {
// socket.close();
// } catch (IOException e) {
// System.err.println("Error closing client socket: " + e.getMessage());
// }
// server.removeClient(this);
// }
// }
