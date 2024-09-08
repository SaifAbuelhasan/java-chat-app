package Socket;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;

public class SocketEventHandler {

    // Event class to represent socket events
    public class SocketEvent {
        public String type;
        public String[] data;

        SocketEvent(String name, String[] data) {
            this.type = name;
            this.data = data;
        }
    }

    // Interface for event listeners
    public interface EventListener {
        void onEvent(SocketEvent event);
    }

    private SocketChannel socket;
    private PrintWriter out;
    private BufferedReader in;
    private Map<String, List<EventListener>> listeners = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public SocketEventHandler(SocketChannel socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(Channels.newWriter(socket, UTF_8), true);
        this.in = new BufferedReader(Channels.newReader(socket, UTF_8));
        startListening();
    }

    public void emit(String eventName, String data) {
        out.println(eventName + ":" + data);
    }

    public void on(String eventName, EventListener listener) {
        listeners.computeIfAbsent(eventName, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    private void startListening() {
        executorService.submit(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        SocketEvent event = new SocketEvent(parts[0], Arrays.copyOfRange(parts, 1, parts.length));
                        notifyListeners(event);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        });
    }

    private void disconnect() {
        try {
            if (socket != null) {
                executorService.shutdown();
                socket.close();
            }
            if (out != null)
                out.close();
            if (in != null)
                in.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    private void notifyListeners(SocketEvent event) {
        List<EventListener> eventListeners = listeners.get(event.type);
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                listener.onEvent(event);
            }
        }
    }
}
