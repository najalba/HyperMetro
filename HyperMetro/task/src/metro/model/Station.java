package metro.model;

import java.util.List;
import java.util.stream.Collectors;

public record Station(String line,
                      String name,
                      List<String> prev,
                      List<String> next,
                      List<Transfer> transfer,
                      Integer time) {

    @Override
    public String toString() {
        return transfer.isEmpty() ? name : String.format("%s - %s", name, transfer.stream().map(Transfer::toString).collect(Collectors.joining(" ")));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;
        return line.equals(station.line) && name.equals(station.name);
    }

    @Override
    public int hashCode() {
        int result = line.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
