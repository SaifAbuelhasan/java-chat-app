package Server;

import java.util.concurrent.CopyOnWriteArraySet;

import Socket.SocketEventHandler;

public class Channel {
    private final String name;
    private final CopyOnWriteArraySet<SocketEventHandler> subscribers = new CopyOnWriteArraySet<>();

    public Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addSubscriber(SocketEventHandler subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(SocketEventHandler subscriber) {
        subscribers.remove(subscriber);
    }

    public void broadcast(String message) {
        for (SocketEventHandler subscriber : subscribers) {
            subscriber.emit("message", message);
        }
    }

}