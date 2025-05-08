package techstore.patterns.factory;

import techstore.model.Admin;
import techstore.model.Client;
import techstore.model.User;

public class UserFactory {
    public enum UserType { ADMIN, CLIENT }

    public User createUser(UserType type, String username, String password, String email) {
        switch (type) {
            case ADMIN:
                return new Admin(username, password, email);
            case CLIENT:
                return new Client(username, password, email);
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }
}