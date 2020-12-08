package chat.auth;

import chat.User;

import java.awt.*;
import java.util.List;

public class BaseAuthService implements AuthService{

    private static final List<User> clients = List.of(
            new User("user1", "123", "Smith"),
            new User("user2", "321", "Pedro"),
            new User("admin", "password", "Odmen")
    );

    @Override
    public void start() {
        System.out.println("Authentication service is turn on");
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if(client.getLogin().equals(login) && client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public void close() {
        System.out.println("Authentication service is turn off");
    }
}
