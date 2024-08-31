package metro.search.impl;

import metro.map.MetroMap;
import metro.model.Station;
import metro.search.PathSearch;

import java.util.*;

public class PathSearchImpl implements PathSearch {
    private final MetroMap metroMap;

    public PathSearchImpl(MetroMap metroMap) {
        this.metroMap = metroMap;
    }

    @Override
    public List<Station> shortestRoute(Station start, Station goal) {
        if (start.equals(goal)) {
            return List.of(start);
        }
        Set<Station> visited = new HashSet<>();
        visited.add(start);

        Queue<StationPath> queue = new LinkedList<>();
        queue.add(new StationPath(start, List.of(start)));

        while (!queue.isEmpty()) {
            var stationPath = queue.poll();
            for (Station next : this.metroMap.neighbours(stationPath.station())) {
                if (!visited.contains(next)) {
                    if (next.equals(goal)) {
                        var newPath = new ArrayList<>(stationPath.path());
                        newPath.add(next);
                        return newPath;
                    }
                    visited.add(next);
                    var newPath = new ArrayList<>(stationPath.path());
                    newPath.add(next);
                    queue.add(new StationPath(next, newPath));
                }
            }
        }
        return List.of();
    }

    @Override
    public FastestPath fastestRoute(Station start, Station end) {
        var distances = new HashMap<Station, FastestPath>();
        distances.put(start, new FastestPath(List.of(start), 0));
        this.metroMap.allStations().forEach(station -> {
            if (!station.equals(start)) {
                distances.put(station, new FastestPath(List.of(), Integer.MAX_VALUE));
            }
        });

        var priorityQueue = new PriorityQueue<FastestPath>((fp1, fp2) -> fp2.distance().compareTo(fp1.distance()));
        priorityQueue.add(distances.get(start));
        while (!priorityQueue.isEmpty()) {
            var headFastestPath = priorityQueue.remove();
            var headStation = headFastestPath.path().get(headFastestPath.path().size() - 1);
            for (var stationHeadNeighbour : this.metroMap.neighbours(headStation)) {
                var fastestPathToNeighbour = distances.get(stationHeadNeighbour);
                int newDistance;
                if (headStation.line().equals(stationHeadNeighbour.line())) {
                    var line = this.metroMap.getLineStations(stationHeadNeighbour.line());
                    var lineIterator = line.listIterator(line.indexOf(stationHeadNeighbour));
                    if (lineIterator.hasPrevious()) {
                        var previous = lineIterator.previous();
                        if (previous.equals(headStation)) {
                            newDistance = headFastestPath.distance() + previous.time();
                        } else {
                            newDistance = headFastestPath.distance() + stationHeadNeighbour.time();
                        }
                    } else {
                        newDistance = headFastestPath.distance() + stationHeadNeighbour.time();
                    }
                } else {
                    newDistance = headFastestPath.distance() + 5;
                }
                if (newDistance < fastestPathToNeighbour.distance()) {
                    var newPath = new ArrayList<>(headFastestPath.path());
                    newPath.add(stationHeadNeighbour);
                    var newFastestPath = new FastestPath(newPath, newDistance);
                    distances.put(stationHeadNeighbour, newFastestPath);
                    priorityQueue.add(newFastestPath);
                }
            }
        }
        return distances.get(end);
    }
}
