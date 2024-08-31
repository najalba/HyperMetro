package metro;

import com.google.gson.JsonSyntaxException;
import metro.map.MetroMap;
import metro.map.impl.MetroMapJson;
import metro.model.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var metroMap = new MetroMapJson(args[0]);
        if (loadMetroMap(metroMap)) {
            var scanner = new Scanner(System.in);
            boolean run = true;
            do {
                var rowCommand = scanner.nextLine();
                var command = parseCommand(rowCommand);
                switch (command.get(0)) {
                    case "/exit" -> run = false;
                    case "/output" -> metroMap.output(command.get(1));
                    case "/append" -> metroMap.append(command.get(1), command.get(2));
                    case "/add-head" -> metroMap.addHead(command.get(1), command.get(2));
                    case "/remove" -> metroMap.remove(command.get(1), command.get(2));
                    case "/connect" -> {
                        metroMap.connect(command.get(1), command.get(2), command.get(3), command.get(4));
                        metroMap.connect(command.get(3), command.get(4), command.get(1), command.get(2));
                    }
                    case "/route" -> metroMap.showRoute(command.get(1), command.get(2), command.get(3), command.get(4));
                    case "/fastest-route" ->
                            metroMap.showFastestRoute(command.get(1), command.get(2), command.get(3), command.get(4));
                    default -> System.out.println("Invalid command");
                }
            } while (run);
        }
    }

    private static boolean loadMetroMap(MetroMap metroMap) {
        try {
            metroMap.load();
            return true;
        } catch (IOException e) {
            System.out.println("Error! Such a file doesn't exist!");
            return false;
        } catch (JsonSyntaxException e) {
            System.out.println("Incorrect file");
            return false;
        }
    }

    private static List<String> parseCommand(String rowCommand) {
        var commandArgumentsSplit = rowCommand.split(" ", 2);
        var command = new ArrayList<String>();
        int commandArgumentsNumber = 0;
        if (Command.OUTPUT.getName().equals(commandArgumentsSplit[0])) {
            command.add(commandArgumentsSplit[0]);
            commandArgumentsNumber = 1;
        } else if (List.of(Command.APPEND.getName(), Command.ADD_HEAD.getName(), Command.REMOVE.getName()).contains(commandArgumentsSplit[0])) {
            command.add(commandArgumentsSplit[0]);
            commandArgumentsNumber = 2;
        } else if (List.of(Command.CONNECT.getName(), Command.ROUTE.getName(), Command.FASTEST_ROUTE.getName()).contains(commandArgumentsSplit[0])) {
            command.add(commandArgumentsSplit[0]);
            commandArgumentsNumber = 4;
        } else if (Command.EXIT.getName().equals(commandArgumentsSplit[0])) {
            command.add(Command.EXIT.getName());
        } else {
            command.add(Command.UNKNOWN.getName());
        }
        if (commandArgumentsNumber > 0) {
            extractCommandArguments(commandArgumentsSplit[1], command, commandArgumentsNumber);
        }
        return command;
    }

    private static void extractCommandArguments(String rawCommandArguments, List<String> commandArguments, int length) {
        int endIndex;
        for (int i = 1; i <= length; i++) {
            if (rawCommandArguments.charAt(0) == '"') {
                endIndex = rawCommandArguments.indexOf("\"", 1);
                commandArguments.add(rawCommandArguments.substring(1, endIndex == -1 ? rawCommandArguments.length() : endIndex));
                if (i < length) {
                    rawCommandArguments = rawCommandArguments.substring(endIndex + 2);
                }
            } else {
                endIndex = rawCommandArguments.indexOf(" ", 1);
                commandArguments.add(rawCommandArguments.substring(0, endIndex == -1 ? rawCommandArguments.length() : endIndex));
                if (i < length) {
                    rawCommandArguments = rawCommandArguments.substring(endIndex + 1);
                }
            }
        }
    }
}
