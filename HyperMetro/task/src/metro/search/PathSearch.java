package metro.search;

import metro.model.Station;

import java.util.List;

public interface PathSearch {
    List<Station> shortestRoute(Station start, Station end);

    FastestPath fastestRoute(Station start, Station end);

    record StationPath(Station station, List<Station> path) {
    }

    record FastestPath(List<Station> path, Integer distance) {
    }
}
