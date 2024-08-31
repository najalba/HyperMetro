package metro.model;

public record Transfer(String line,
                       String station) {

    @Override
    public String toString() {
        return String.format("%s (%s line)", station, line);
    }
}
