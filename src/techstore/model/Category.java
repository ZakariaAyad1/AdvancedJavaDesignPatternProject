package techstore.model;

public class Category {
    private static int nextId = 1;
    private final int id;
    private String name;

    public Category(String name) {
        this.id = nextId++;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}