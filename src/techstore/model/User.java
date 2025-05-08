package techstore.model;

public abstract class User {
    private static int nextId = 1;
    protected int id;
    protected String username;
    protected String password; // In a real app, hash this!
    protected String email;

    public User(String username, String password, String email) {
        this.id = nextId++;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
    public abstract String getRole();

    @Override
    public String toString() {
        return "User ID: " + id + ", Username: " + username + ", Role: " + getRole();
    }
}