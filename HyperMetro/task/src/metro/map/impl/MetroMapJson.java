package metro.map.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import metro.map.MetroMap;
import metro.model.Station;
import metro.model.Transfer;
import metro.search.impl.PathSearchImpl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MetroMapJson implements MetroMap {
    private static final String DEPOT_NAME = "depot";
    private final Map<String, LinkedList<Station>> MAP = new HashMap<>();
    private final String jsonPath;

    public MetroMapJson(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Override
    public void load() throws IOException {
        Type mapType = new TypeToken<Map<String, LinkedList<Station>>>() {
        }.getType();
        var model = (Map<String, LinkedList<Station>>) new Gson().fromJson(Files.readString(Paths.get(this.jsonPath)), mapType);
        model.forEach((line, stationsRaw) -> {
            var stations = new LinkedList<Station>();
            stationsRaw.forEach(station -> stations.add(new Station(line, station.name(), station.prev(), station.next(), station.transfer(), station.time())));
            MAP.put(line, stations);
        });
    }

    @Override
    public void output(String line) {
        if (MAP.containsKey(line)) {
            var metroLine = MAP.get(line);
            var tmpLine = new ArrayList<Station>();
            tmpLine.add(new Station(line, DEPOT_NAME, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0));
            tmpLine.addAll(metroLine);
            tmpLine.add(new Station(line, DEPOT_NAME, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0));
            tmpLine.forEach(station -> System.out.println(station.toString()));
        } else {
            System.out.println("Line not found");
        }
    }

    @Override
    public void append(String line, String station) {
        if (MAP.containsKey(line)) {
            var metroLine = MAP.get(line);
            metroLine.addLast(new Station(line, station, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0));
        } else {
            System.out.println("Line not found");
        }
    }

    @Override
    public void addHead(String line, String station) {
        if (MAP.containsKey(line)) {
            var metroLine = MAP.get(line);
            metroLine.addFirst(new Station(line, station, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0));
        } else {
            System.out.println("Line not found");
        }
    }

    @Override
    public void remove(String line, String station) {
        if (MAP.containsKey(line)) {
            var metroLine = MAP.get(line);
            metroLine.removeIf(s -> s.name().equals(station));
        } else {
            System.out.println("Line not found");
        }
    }

    @Override
    public void connect(String line, String station, String toLine, String toStation) {
        if (MAP.containsKey(line)) {
            var metroLine = MAP.get(line);
            metroLine.stream()
                    .filter(s -> s.name().equals(station))
                    .findFirst()
                    .ifPresent(s -> s.transfer().add(new Transfer(toLine, toStation)));
        } else {
            System.out.println("Line not found");
        }
    }

    @Override
    public LinkedList<Station> getLineStations(String line) {
        return this.MAP.get(line);
    }

    @Override
    public Optional<Station> findStation(String line, String name) {
        if (!MAP.containsKey(line)) {
            return Optional.empty();
        }
        return MAP.get(line)
                .stream()
                .filter(s -> s.line().equals(line) && s.name().equals(name))
                .findFirst();
    }

    @Override
    public List<Station> allStations() {
        return this.MAP.values().stream().flatMap(Collection::stream).toList();
    }

    @Override
    public List<Station> neighbours(Station station) {
        var neighbours = new ArrayList<Station>();
        station.transfer().forEach(transfer -> findStation(transfer.line(), transfer.station()).ifPresent(neighbours::add));
        station.prev().forEach(stationName -> findStation(station.line(), stationName).ifPresent(neighbours::add));
        station.next().forEach(stationName -> findStation(station.line(), stationName).ifPresent(neighbours::add));
        return neighbours;
    }

    @Override
    public void showRoute(String fromLine, String fromStation, String toLine, String toStation) {
        var start = findStation(fromLine, fromStation);
        var end = findStation(toLine, toStation);
        if (start.isPresent() && end.isPresent()) {
            var path = new PathSearchImpl(this).shortestRoute(start.get(), end.get());
            if (path.isEmpty()) {
                System.out.println("No route found");
            } else {
                for (int i = 0; i < path.size(); i++) {
                    var currStation = path.get(i);
                    if (i > 0) {
                        var prevStation = path.get(i - 1);
                        if (!currStation.line().equals(prevStation.line())) {
                            System.out.printf("Transition to %s%n", currStation.line());
                        }
                    }
                    System.out.println(currStation.name());
                }
            }
        } else {
            System.out.println("Line not found");
        }
    }

    @Override
    public void showFastestRoute(String fromLine, String fromStation, String toLine, String toStation) {
        var start = findStation(fromLine, fromStation);
        var end = findStation(toLine, toStation);
        if (start.isPresent() && end.isPresent()) {
            var fastestPath = new PathSearchImpl(this).fastestRoute(start.get(), end.get());
            for (int i = 0; i < fastestPath.path().size(); i++) {
                var currStation = fastestPath.path().get(i);
                if (i > 0) {
                    var prevStation = fastestPath.path().get(i - 1);
                    if (!currStation.line().equals(prevStation.line())) {
                        System.out.printf("Transition to line %s%n", currStation.line());
                    }
                }
                System.out.println(currStation.name());
            }
            System.out.printf("Total: %d minutes in the way%n", fastestPath.distance());
        } else {
            System.out.println("Line not found");
        }
    }
}
