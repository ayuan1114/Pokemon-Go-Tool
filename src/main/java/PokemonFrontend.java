import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pokemon.Pair;
import pokemon.database.MoveElite;
import pokemon.database.Pokemon;
import pokemon.database.PokemonData;
import pokemon.database.PokemonRanker;
import pokemon.database.Stats;
import pokemon.database.Type;
import pokemon.database.Weather;

public class PokemonFrontend {
    Scanner input;
    PokemonRanker ranker;
    PokemonData database;

    public PokemonFrontend(Scanner input, PokemonRanker ranker) {
        this.input = input;
        this.ranker = ranker;
    }

    public void start() {
        boolean exit = false;
        System.out.println("Welcome to the Pokemon Go Tool");
        System.out.println("You can use this tool to help you determine the best counters for Pokemon");
        System.out.println("Please wait while we load the Pokemon info");
        System.out.println("New features COMING SOON!!!");

        while (!exit) {
            switch (mainPrompt()) {
                case "Q":
                    System.out.println("Thanks for using the Pokemon Go Tool!");
                    System.out.println("Exiting...");
                    exit = true;
                    break;
                case "T":
                    typeRankerPrompt();
                    break;
                case "C":
                    counterRankerPrompt();
                    break;
                case "S":
                    settingsPrompt();
                    break;
                default:
                    System.out.println("Please enter a valid command.");

            }
        }
    }

    public String mainPrompt() {
        System.out.println("\nPlease choose one of the following tools:");
        System.out.println("[T] Pokemon Type Ranker (Calculate a list of the top Pokemon attackers of each type)");
        System.out.println("[C] Pokemon Counter Ranker (Calculate a list of the top counters against a Pokemon)");
        System.out.println("[S] Change Condition Settings (Weather, Include Elite Moves, etc.)");
        System.out.println("[Q] Quit");
        System.out.print("Enter command: ");
        return input.nextLine().toUpperCase();
    }

    public void typeRankerPrompt() {
        boolean valid = false;
        Type type = null;
        int num = 0, counter = 1;
        List<Pair<Pokemon, Pair<MoveElite, MoveElite>>> response;
        List<String> topPokemon = new ArrayList<>();

        System.out.print("Enter a type to rank by: ");

        while (!valid) {
            try {
                type = Type.parseType(input.nextLine().trim());
                valid = true;
            }
            catch (Exception e) {
                System.out.print("Type not found, please try again: ");
            }
        }

        valid = false;
        System.out.print("How many top counters would you like to see? ");

        while (!valid) {
            try {
                num = Integer.parseInt(input.nextLine().trim());
                valid = true;
            }
            catch (Exception e) {
                System.out.print("Invalid number, please try again: ");
            }
        }

        response = ranker.bestOfType(type, num, false, false);

        for (Pair<Pokemon, Pair<MoveElite, MoveElite>> element : response) {
            String toAdd = element.key.getNameForm() + " with moveset: [Fast: " + element.val.key.moveName();
            if (element.val.key.isElite()) {
                toAdd += "*";
            }
            toAdd += ", Charged: " + element.val.val.moveName();
            if (element.val.val.isElite()) {
                toAdd += "*";
            }
            toAdd += "]";
        }

        System.out.println("\nTop " + num + " " + type.toString().toLowerCase() + "-type attackers: ");
        if (PokemonData.includeElite) {
            System.out.println("* indicates a legacy move");
        }
        
        for (String pokemon : topPokemon) {
            System.out.print(counter + ". ");
            System.out.println(pokemon);
            counter++;
        }


    }

    public void counterRankerPrompt() {
        boolean valid = false, shadow = false;
        String searchString = "", finalPoke = "";
        Pokemon defender;
        int num = 0, counter = 1;
        List<String> topPokemon = new ArrayList<>();
        List<Pair<Pokemon, Pair<MoveElite, MoveElite>>> response;

        System.out.print("Search for Pokemon: ");

        searchString = input.nextLine().trim().toLowerCase();

        while (!valid) {
            // pokemon name entered directly
            for (String pokeName : database.pokemons.keySet()) {
                if (pokeName.equalsIgnoreCase(searchString)) {
                    finalPoke = pokeName;
                    valid = true;
                    break;
                }
            }
            if (valid) {
                break;
            }
            // search for pokemon by entered string
            counter = 1;
            for (String pokeName : database.pokemons.keySet()) { // find all pokemon names that match the searched string
                if (pokeName.toLowerCase().contains(searchString)) {
                    if (counter == 1) {
                        System.out.println("\nPokemon found that match ur search:");
                    }
                    System.out.print(counter + ". ");
                    System.out.println(pokeName);
                    topPokemon.add(pokeName);
                    counter++;
                }
            }
            if (counter == 1) { // if no matches found
                System.out.print("No Pokemon found matching your search, please try again: ");
                searchString = input.nextLine().trim().toLowerCase();
            }
            else { // ask for match or search new string
                System.out.println("Enter the number for the Pokemon you would like to select");
                System.out.print("or enter a new search for a Pokemon: ");
                searchString = input.nextLine().trim().toLowerCase();
                try { // check if the input is a valid number
                    num = Integer.parseInt(searchString) - 1;
                    finalPoke = topPokemon.get(num);
                    break;
                }
                catch (Exception e) {

                }
            }
        }
        if (database.canBeShadow(finalPoke)) {
            System.out.print("Would you like the defender to be a shadow pokemon? (Y/N) ");
            switch(input.nextLine().trim()) {
                case "Y":
                case "y":
                    shadow = true;
            }
        }

        defender = database.pokemons.get(finalPoke).createInstance(new Stats(15, 15, 15), 40, shadow);
        valid = false;
        System.out.print("How many top counters would you like to see? ");

        while (!valid) {
            try {
                num = Integer.parseInt(input.nextLine().trim());
                valid = true;
            }
            catch (Exception e) {
                System.out.print("Invalid number, please try again: ");
            }
        }

        response = ranker.bestCounters(defender, false, num);

        for (Pair<Pokemon, Pair<MoveElite, MoveElite>> element : response) {
            String toAdd = element.key.getNameForm() + " with moveset: [Fast: " + element.val.key.moveName();
            if (element.val.key.isElite()) {
                toAdd += "*";
            }
            toAdd += ", Charged: " + element.val.val.moveName();
            if (element.val.val.isElite()) {
                toAdd += "*";
            }
            toAdd += "]";
            topPokemon.add(toAdd);
        }
        System.out.println("\nTop " + num + " counters against " + finalPoke + ": ");
        if (PokemonData.includeElite) {
            System.out.println("* indicates a legacy move");
        }
        counter = 1;

        for (String pokemon : topPokemon) {
            System.out.print(counter + ". ");
            System.out.println(pokemon);
            counter++;
        }
    }
    
    public void settingsPrompt() {
        boolean exit = false;
        boolean needRecalc = false;
        System.out.print("[M] Include Mega Pokemon: ");
        if (ranker.includeMega) {
            System.out.println("On");
        }
        else {
            System.out.println("Off");
        }
        System.out.print("[S] Include Shadow Pokemon: ");
        if (ranker.includeShadow) {
            System.out.println("On");
        }
        else {
            System.out.println("Off");
        }
        System.out.print("[E] Include Elite Moves: ");
        if (PokemonData.includeElite) {
            System.out.println("On");
        }
        else {
            System.out.println("Off");
        }
        System.out.println("[W] Weather: " + PokemonData.weather);
        while (!exit) {
            System.out.print("Input the setting you would like to change: ");
            switch (input.nextLine().trim().toUpperCase()) {
                case "M":
                    ranker.toggleMega();
                    System.out.print("Include Mega Pokemon changed to ");
                    if (ranker.includeMega) {
                        System.out.println("On");
                    }
                    else {
                        System.out.println("Off");
                    }
                    break;
                case "S":
                    ranker.toggleShadow();
                    System.out.print("Include Shadow Pokemon changed to ");
                    if (ranker.includeShadow) {
                        System.out.println("On");
                    }
                    else {
                        System.out.println("Off");
                    }
                    break;
                case "E":
                    PokemonData.toggleElite();
                    System.out.print("Include Elite Moves changed to ");
                    if (PokemonData.includeElite) {
                        System.out.println("On");
                    }
                    else {
                        System.out.println("Off");
                    }
                    needRecalc = true;
                    break;
                case "W":
                    PokemonData.weather = changeWeather();
                    System.out.println("Weather changed to " + PokemonData.weather);
                    needRecalc = true;
                    break;
                case "Q":
                    exit = true;
                    if (needRecalc) {
                        System.out.println("Recalculating based on changes...");
                        ranker.calc();
                    }
                    System.out.println("Saving changes and exiting");
                    break;
                default:
                    System.out.print("Please enter a valid setting to change");
            }

        }
    }

    public Weather changeWeather() {
        Weather toReturn = null;
        String inputStr = "";
        System.out.print("Enter the weather you would like to change to: ");

        while(toReturn == null) {
            inputStr = input.nextLine().trim();
            try {
                toReturn = Weather.parseWeather(inputStr);
            }
            catch (Exception e) {
                System.out.print("Please enter a valid weather: ");
            }
        }
        return toReturn;
    }
}
