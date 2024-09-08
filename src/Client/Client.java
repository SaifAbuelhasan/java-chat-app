package Client;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import Socket.*;

public class Client {
    private SocketEventHandler handler;

    public Client(String host, int port) {
        try {
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            handler = new SocketEventHandler(socketChannel);

            handler.on("message", event -> {
                System.out.println("Received message: " + event.data[0]);
            });
        } catch (Exception e) {
            System.err.println("Error starting client: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        handler.emit("message", message);
    }

    public void subscribe(String channelName) {
        handler.emit("subscribe", channelName);
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 5000);
        String channelName = "channel2";
        client.subscribe(channelName);
        client.sendMessage(channelName + ":Hello " + channelName + "!");
    }
}