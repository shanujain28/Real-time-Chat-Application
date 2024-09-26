import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

// Observer Pattern: Subject and Observer interfaces
interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notify(ChatMessage message);
}

interface Observer {
    void update(ChatMessage message);
}

// Message class to encapsulate chat messages
class ChatMessage {
    private final String sender;
    private final String content;
    private final LocalDateTime timestamp;
    private final boolean isPrivate;
    private final String recipient;

    public ChatMessage(String sender, String content, boolean isPrivate, String recipient) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isPrivate = isPrivate;
        this.recipient = recipient;
    }

    // Getters
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isPrivate() { return isPrivate; }
    public String getRecipient() { return recipient; }

    @Override
    public String toString() {
        return String.format("[%s] %s%s: %s",
                timestamp,
                sender,
                isPrivate ? " (private to " + recipient + ")" : "",
                content);
    }
}

// Singleton Pattern: ChatRoomManager
class ChatRoomManager {
    private static final Logger LOGGER = Logger.getLogger(ChatRoomManager.class.getName());
    private static ChatRoomManager instance;
    private static final Lock lock = new ReentrantLock();
    private final Map<String, ChatRoom> rooms;

    private ChatRoomManager() {
        rooms = new ConcurrentHashMap<>();
    }

    public static ChatRoomManager getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new ChatRoomManager();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public ChatRoom createRoom(String roomId) {
        return rooms.computeIfAbsent(roomId, id -> {
            LOGGER.info("Created new chat room: " + id);
            return new ChatRoom(id);
        });
    }

    public ChatRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void removeRoom(String roomId) {
        rooms.remove(roomId);
        LOGGER.info("Removed chat room: " + roomId);
    }
}

// ChatRoom class implementing Subject
class ChatRoom implements Subject {
    private static final Logger LOGGER = Logger.getLogger(ChatRoom.class.getName());
    private final String roomId;
    private final Map<String, User> users;
    private final List<ChatMessage> messages;

    public ChatRoom(String roomId) {
        this.roomId = roomId;
        this.users = new ConcurrentHashMap<>();
        this.messages = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void attach(Observer observer) {
        User user = (User) observer;
        users.put(user.getUsername(), user);
        LOGGER.info("User " + user.getUsername() + " joined room " + roomId);
    }

    @Override
    public void detach(Observer observer) {
        User user = (User) observer;
        users.remove(user.getUsername());
        LOGGER.info("User " + user.getUsername() + " left room " + roomId);
    }

    @Override
    public void notify(ChatMessage message) {
        if (message.isPrivate()) {
            User recipient = users.get(message.getRecipient());
            if (recipient != null) {
                recipient.update(message);
            } else {
                LOGGER.warning("Private message recipient not found: " + message.getRecipient());
            }
        } else {
            for (User user : users.values()) {
                user.update(message);
            }
        }
    }

    public void broadcastMessage(ChatMessage message) {
        messages.add(message);
        notify(message);
        LOGGER.info("Message broadcast in room " + roomId + ": " + message);
    }

    public List<String> getActiveUsers() {
        return new ArrayList<>(users.keySet());
    }

    public List<ChatMessage> getMessageHistory() {
        return new ArrayList<>(messages);
    }

    public String getRoomId() {
        return roomId;
    }
}

// User class implementing Observer
class User implements Observer {
    private static final Logger LOGGER = Logger.getLogger(User.class.getName());
    private final String username;
    private ChatRoom currentRoom;

    public User(String username) {
        this.username = username;
    }

    @Override
    public void update(ChatMessage message) {
        // In a real application, this would send the message to the user's client
        System.out.println(message);
    }

    public void joinRoom(ChatRoom room) {
        if (currentRoom != null) {
            leaveRoom();
        }
        currentRoom = room;
        room.attach(this);
        LOGGER.info(username + " joined room " + room.getRoomId());
    }

    public void leaveRoom() {
        if (currentRoom != null) {
            currentRoom.detach(this);
            LOGGER.info(username + " left room " + currentRoom.getRoomId());
            currentRoom = null;
        }
    }

    public void sendMessage(String content) {
        sendMessage(content, false, null);
    }

    public void sendPrivateMessage(String content, String recipient) {
        sendMessage(content, true, recipient);
    }

    private void sendMessage(String content, boolean isPrivate, String recipient) {
        if (currentRoom != null) {
            ChatMessage message = new ChatMessage(username, content, isPrivate, recipient);
            currentRoom.broadcastMessage(message);
        } else {
            LOGGER.warning(username + " attempted to send a message without being in a room");
        }
    }

    public String getUsername() {
        return username;
    }
}

// Adapter Pattern: Communication Protocol Adapter
interface CommunicationAdapter {
    void sendMessage(ChatMessage message);
    ChatMessage receiveMessage();
}

class WebSocketAdapter implements CommunicationAdapter {
    private static final Logger LOGGER = Logger.getLogger(WebSocketAdapter.class.getName());

    @Override
    public void sendMessage(ChatMessage message) {
        // In a real application, this would send the message via WebSocket
        LOGGER.info("Sending via WebSocket: " + message);
    }

    @Override
    public ChatMessage receiveMessage() {
        // In a real application, this would receive a message via WebSocket
        LOGGER.info("Received message via WebSocket");
        return null; // Placeholder
    }
}

class HTTPAdapter implements CommunicationAdapter {
    private static final Logger LOGGER = Logger.getLogger(HTTPAdapter.class.getName());

    @Override
    public void sendMessage(ChatMessage message) {
        // In a real application, this would send the message via HTTP
        LOGGER.info("Sending via HTTP: " + message);
    }

    @Override
    public ChatMessage receiveMessage() {
        // In a real application, this would receive a message via HTTP
        LOGGER.info("Received message via HTTP");
        return null; // Placeholder
    }
}

// Chat Application
class ChatApplication {
    private static final Logger LOGGER = Logger.getLogger(ChatApplication.class.getName());
    private final ChatRoomManager roomManager;
    private final CommunicationAdapter communicationAdapter;

    public ChatApplication(CommunicationAdapter communicationAdapter) {
        this.roomManager = ChatRoomManager.getInstance();
        this.communicationAdapter = communicationAdapter;
    }

    public User createUser(String username) {
        User user = new User(username);
        LOGGER.info("Created new user: " + username);
        return user;
    }

    public void createOrJoinRoom(User user, String roomId) {
        ChatRoom room = roomManager.createRoom(roomId);
        user.joinRoom(room);
    }

    public void leaveRoom(User user) {
        user.leaveRoom();
    }

    public void sendMessage(User user, String content) {
        user.sendMessage(content);
        communicationAdapter.sendMessage(new ChatMessage(user.getUsername(), content, false, null));
    }

    public void sendPrivateMessage(User sender, String recipient, String content) {
        sender.sendPrivateMessage(content, recipient);
        communicationAdapter.sendMessage(new ChatMessage(sender.getUsername(), content, true, recipient));
    }

    public List<String> getActiveUsers(String roomId) {
        ChatRoom room = roomManager.getRoom(roomId);
        return room != null ? room.getActiveUsers() : Collections.emptyList();
    }

    public List<ChatMessage> getMessageHistory(String roomId) {
        ChatRoom room = roomManager.getRoom(roomId);
        return room != null ? room.getMessageHistory() : Collections.emptyList();
    }
}

// Example usage
public class Main {
    public static void main(String[] args) {
        ChatApplication chatApp = new ChatApplication(new WebSocketAdapter());

        User alice = chatApp.createUser("Alice");
        User bob = chatApp.createUser("Bob");
        User charlie = chatApp.createUser("Charlie");

        chatApp.createOrJoinRoom(alice, "Room123");
        chatApp.createOrJoinRoom(bob, "Room123");
        chatApp.createOrJoinRoom(charlie, "Room123");

        chatApp.sendMessage(alice, "Hello, everyone!");
        chatApp.sendMessage(bob, "How's it going?");
        chatApp.sendPrivateMessage(charlie, "Bob", "Hey Bob, want to grab lunch?");

        System.out.println("Active users in Room123: " + chatApp.getActiveUsers("Room123"));
        System.out.println("Message history in Room123:");
        chatApp.getMessageHistory("Room123").forEach(System.out::println);

        chatApp.leaveRoom(charlie);
        System.out.println("Active users in Room123 after Charlie left: " + chatApp.getActiveUsers("Room123"));
    }
}