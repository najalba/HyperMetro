package metro.map;

import metro.model.Station;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface MetroMap {
    void load() throws IOException;

    void output(String line);

    void append(String line, String station);

    void addHead(String line, String station);

    void remove(String line, String station);

    void connect(String line, String station, String toLine, String toStation);

    LinkedList<Station> getLineStations(String line);

    Optional<Station> findStation(String line, String name);

    List<Station> allStations();

    List<Station> neighbours(Station station);

    void showRoute(String fromLine, String fromStation, String toLine, String toStation);

    void showFastestRoute(String fromLine, String fromStation, String toLine, String toStation);
}
