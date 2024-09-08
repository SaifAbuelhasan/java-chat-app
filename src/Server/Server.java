package Server;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import Socket.*;

public class Server {
    private int port;
    private List<Channel> channels = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress("127.0.0.1", this.port));
            while (serverChannel.isOpen()) {
                SocketChannel clientSocket = serverChannel.accept();

                SocketEventHandler client = new SocketEventHandler(clientSocket);

                client.on("subscribe", event -> {
                    String channelName = event.data[0];
                    for (Channel channel : channels) {
                        if (channel.getName().equals(channelName)) {
                            channel.addSubscriber(client);
                        }
                    }
                });

                client.on("message", event -> {
                    System.out.println(event.data);
                    String channelName = event.data[0];
                    String message = event.data[1];
                    for (Channel channel : channels) {
                        if (channel.getName().equals(channelName)) {
                            channel.broadcast(message);
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        server.channels.add(new Channel("channel1"));
        server.channels.add(new Channel("channel2"));
        server.start();
    }
}