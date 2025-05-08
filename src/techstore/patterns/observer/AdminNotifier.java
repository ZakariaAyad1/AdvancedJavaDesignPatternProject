package techstore.patterns.observer;

public class AdminNotifier implements Observer {
    private String adminName;

    public AdminNotifier(String adminName) {
        this.adminName = adminName;
    }

    @Override
    public void update(String message) {
        System.out.println("ADMIN NOTIFICATION (" + adminName + "): " + message);
    }
}