package techstore.patterns.singleton;

import techstore.model.User;
import techstore.patterns.observer.NotificationService; // For Observer

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManager {
    private static UserManager instance;
    private final List<User> users;
    private NotificationService notificationService; // Observer Pattern

    private UserManager() {
        users = new ArrayList<>();
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void addUser(User user) {
        users.add(user);
        if (notificationService != null) {
            notificationService.notifyObservers("New user registered: " + user.getUsername());
        }
    }
//    Rechercher un utilisateur dont le nom d’utilisateur (username) correspond à celui fourni en argument dans une collection users.
//    users.stream() : convertit la collection users (probablement une List<User>) en un flux (Stream<User>).
//
//            .filter(u -> u.getUsername().equals(username)) : garde uniquement les utilisateurs dont le nom d’utilisateur correspond exactement à celui passé en paramètre.
//
//            .findFirst() : renvoie le premier élément du flux filtré sous forme d’un Optional<User>.
//

    public Optional<User> findUserByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users); // Return a copy
    }
}