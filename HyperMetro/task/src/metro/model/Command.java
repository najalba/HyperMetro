package metro.model;

public enum Command {
    OUTPUT("/output"),
    APPEND("/append"),
    ADD_HEAD("/add-head"),
    REMOVE("/remove"),
    CONNECT("/connect"),
    ROUTE("/route"),
    FASTEST_ROUTE("/fastest-route"),
    EXIT("/exit"),
    UNKNOWN("/unknown");

    private final String name;

    Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
