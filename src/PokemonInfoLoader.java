import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.net.http.*;
import java.util.*;

/**
 * contains all the methods needed to obtain and parse information from the PokemonGo API
 */
public class PokemonInfoLoader {
    PokemonData database;
    public static void loadIntoDatabase() {
        loadCPMult();
        loadDmgMultipliers();
        loadPokemonIDs(PokemonData.pokemonIDs);
        loadMoves(PokemonData.moves);
        loadPokemonInfo(PokemonData.pokemons);
        loadMegaPokemon(PokemonData.pokemons);
        loadPossibleShadows(PokemonData.pokemons);
    }
    public ArrayList<Integer> defenses = new ArrayList<Integer>();
    private static String removeQuotes(String input, int startIndex) {
        int firstIndex = input.indexOf("\"", startIndex);
        return input.substring(firstIndex + 1, input.indexOf("\"", firstIndex + 1));
    }

    /**
     * parses the cp_multiplier.json file to obtaian the CP multipliers at each pokemon level in the form of an array
     * @return double array where the cp multiplier at level x is stored in index x/2 of the array
     */
    public static void loadCPMult() {
        Pokemon.cpMultipliers = new double[110];
        try {
            System.out.println("Loading CP Multipliers...");
            Scanner input = new Scanner(new File("CPMultipliers.in"));
            while (input.hasNextDouble()) {
                double level = input.nextDouble();
                double cpMult = input.nextDouble();
                Pokemon.cpMultipliers[(int) (level * 2)] = cpMult;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Something went wrong, please try again");
        }
    }

    /**
     * parses the pokemon_names.json file and stores the pokemon names inside a string array an the index that is its id
     * @return string array of all pokemon with the pokemon name stored in the location of its id
     */

    public static void loadPokemonIDs(Hashtable<Integer, String> pokemonIDs) {
        try {
            System.out.println("Loading all Pokedex IDs...");
            URL url = new URL("https://pogoapi.net/api/v1/released_pokemon.json");
            HttpURLConnection httpConnect = (HttpURLConnection) url.openConnection();
            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-Type", "application/json");

            int responseCode = httpConnect.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                StringTokenizer pokemonSplitter = new StringTokenizer(response.toString().substring(1, response.toString().length() - 1), "}");

                while (pokemonSplitter.hasMoreTokens()) {
                    String cur = pokemonSplitter.nextToken();
                    cur = cur.substring(cur.indexOf("{") + 1).trim();
                    StringTokenizer pokemonID = new StringTokenizer(cur, ",");

                    int ID = 0;
                    String pokemonName = "";

                    while (pokemonID.hasMoreTokens()) {
                        String temp = pokemonID.nextToken().trim();
                        String[] attribute = temp.split(":");
                        if (attribute[0].contains("id")) {
                            ID = Integer.parseInt(attribute[1].trim());
                        }
                        else if (attribute[0].contains("name")) {
                            pokemonName = attribute[1].trim();
                            pokemonName = pokemonName.substring(1, pokemonName.length() - 1);
                        }
                    }
                    pokemonIDs.put(ID, pokemonName);
                }

                in.close();
            }
            else {
                System.out.println("Something went wrong, please try again");
            }
        }
        catch (Exception e) {
            System.out.println("Something went wrong, please try again");
        }
    }

    /**
     *
     * @return
     */

    public static void loadMoves(Hashtable<String, Move> moves) {
        try {
            System.out.println("Loading fast moves data...");

            // define separate URL links to fast and charged move files;
            URL fastMoveFile = new URL("https://pogoapi.net/api/v1/fast_moves.json");
            URL charMoveFile = new URL ("https://pogoapi.net/api/v1/charged_moves.json");

            // load all fast moves
            HttpURLConnection httpConnect = (HttpURLConnection) fastMoveFile.openConnection();
            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-Type", "application/json");
            int responseCode = httpConnect.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                StringTokenizer moveID = new StringTokenizer(response.toString().substring(1, response.toString().length() - 1), "}");

                while (moveID.hasMoreTokens()) {
                    String temp = moveID.nextToken();
                    StringTokenizer curMoveAttr = new StringTokenizer(temp.substring(temp.indexOf("{")).trim(), ",");
                    Move newMove = new Move();
                    newMove.isFast = true;
                    while (curMoveAttr.hasMoreTokens()) {
                        temp = curMoveAttr.nextToken();
                        String[] attribute = temp.trim().split(":");
                        if (attribute[0].contains("name")) {
                            newMove.name = attribute[1].trim();
                            newMove.name = removeQuotes(newMove.name, 0);
                        }
                        else if (attribute[0].contains("power")) {
                            newMove.power = Integer.parseInt(attribute[1].trim());
                        }
                        else if (attribute[0].contains("duration")) {
                            newMove.duration = Double.parseDouble(attribute[1].trim()) / 1000;
                        }
                        else if (attribute[0].contains("energy_delta")) {
                            newMove.energyDelta = Integer.parseInt(attribute[1].trim());
                        }
                        else if (attribute[0].contains("type")) {
                            String typeTemp = attribute[1].trim();
                            newMove.elementType = Type.parseType(removeQuotes(typeTemp, 0));
                        }
                    }
                    if (newMove.name != null) {
                        moves.put(newMove.name, newMove);
                    }
                    else {
                        System.out.println("Something went wrong, please try again");
                    }

                }
            }

            System.out.println("Loading charged moves data...");

            // load all charged moves
            httpConnect = (HttpURLConnection) charMoveFile.openConnection();
            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-Type", "application/json");
            responseCode = httpConnect.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                StringTokenizer moveID = new StringTokenizer(response.toString().substring(1, response.toString().length() - 1), "}");

                while (moveID.hasMoreTokens()) {
                    String temp = moveID.nextToken();
                    StringTokenizer curMoveAttr = new StringTokenizer(temp.substring(temp.indexOf("{")).trim(), ",");
                    Move newMove = new Move();
                    newMove.isFast = false;
                    while (curMoveAttr.hasMoreTokens()) {
                        temp = curMoveAttr.nextToken();
                        String[] attribute = temp.trim().split(":");
                        if (attribute[0].contains("name")) {
                            newMove.name = attribute[1].trim();
                            newMove.name = removeQuotes(newMove.name, 0);
                        }
                        else if (attribute[0].contains("power")) {
                            newMove.power = Integer.parseInt(attribute[1].trim());
                        }
                        else if (attribute[0].contains("duration")) {
                            newMove.duration = Double.parseDouble(attribute[1].trim()) / 1000;
                        }
                        else if (attribute[0].contains("energy_delta")) {
                            newMove.energyDelta = Math.abs(Integer.parseInt(attribute[1].trim()));
                        }
                        else if (attribute[0].contains("type")) {
                            String typeTemp = attribute[1].trim();
                            newMove.elementType = Type.parseType(removeQuotes(typeTemp, 0));
                        }
                    }
                    if (newMove.name != null) {
                        moves.put(newMove.name, newMove);
                    }
                    else {
                        return;
                    }

                }
            }

            //return moves;
        }
        catch (Exception e) {
            //return null;
        }
    }
    public static void loadPokemonInfo(Hashtable<String, Pokemon> pokemon) {
        try {

            // define file URLS
            URL statsFile = new URL("https://pogoapi.net/api/v1/pokemon_stats.json");
            URL typingsFile = new URL("https://pogoapi.net/api/v1/pokemon_types.json");
            URL movesFile = new URL("https://pogoapi.net/api/v1/current_pokemon_moves.json");

            System.out.println("Loading each Pokemon's stats...");

            boolean pokemonExists;

            // load pokemon stats
            HttpURLConnection httpConnect = (HttpURLConnection) statsFile.openConnection();
            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-Type", "application/json");
            int responseCode = httpConnect.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    Pokemon newPokemon = new Pokemon();
                    pokemonExists = true;
                    if (inputLine.contains("{")) {
                        while ((inputLine = in.readLine()) != null && !inputLine.contains("}")) {
                            inputLine = inputLine.trim();
                            String attributeName = inputLine.substring(0, inputLine.indexOf(":"));
                            String attributeValue = inputLine.substring(inputLine.indexOf(":") + 1);
                            attributeValue = attributeValue.trim();
                            if (attributeName.contains("base_attack")) {
                                newPokemon.attack = Integer.parseInt(attributeValue.substring(0, attributeValue.indexOf(",")));
                            }
                            else if (attributeName.contains("base_defense")) {
                                newPokemon.defense = Integer.parseInt(attributeValue.substring(0, attributeValue.indexOf(",")));

                            }
                            else if (attributeName.contains("base_stamina")) {
                                newPokemon.stamina = Integer.parseInt(attributeValue.substring(0, attributeValue.indexOf(",")));
                            }
                            else if (attributeName.contains("\"form\"")) {
                                newPokemon.form = removeQuotes(attributeValue, 0);
                            }
                            else if (attributeName.contains("pokemon_name")) {
                                newPokemon.name = removeQuotes(attributeValue, 0);
                            }
                            else if (attributeName.contains("pokemon_id")) {
                                newPokemon.ID = Integer.parseInt(attributeValue.substring(0, attributeValue.indexOf(",")));
                                if (!PokemonData.pokemonIDs.containsKey(newPokemon.ID)) {
                                    pokemonExists = false;
                                    break;
                                }
                            }
                        }
                        if (pokemonExists) {
                            pokemon.put(newPokemon.getNameForm(), newPokemon);
                        }
                    }
                }

            }
            else {
                System.out.println("Something went wrong, please try again");
            }

            System.out.println("Loading each Pokemon's types...");

            // load pokemon types
            httpConnect = (HttpURLConnection) typingsFile.openConnection();
            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-Type", "application/json");
            responseCode = httpConnect.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("{")) {
                        String pokeName = "";
                        String form = "";
                        Type[] type = new Type[2];
                        while ((inputLine = in.readLine()) != null && !inputLine.contains("}")) {
                            inputLine = inputLine.trim();
                            int firstIndex;
                            if (inputLine.contains("pokemon_name")) {
                                pokeName = removeQuotes(inputLine, inputLine.indexOf(":"));
                            }
                            else if (inputLine.contains("\"form\"")) {
                                form = removeQuotes(inputLine, inputLine.indexOf(":"));
                            }
                            else if (inputLine.contains("\"type\"")) {
                                int typeCounter = 0;
                                while ((inputLine = in.readLine()) != null && !inputLine.contains("]")) {
                                    type[typeCounter] = Type.parseType(removeQuotes(inputLine, 0));
                                    typeCounter++;
                                }
                            }
                        }
                        if (pokemon.containsKey(Pokemon.getNameForm(pokeName, form))) {
                            pokemon.get(Pokemon.getNameForm(pokeName, form)).type = type;
                        }
                    }
                }
            }
            else {
                System.out.println("Something went wrong, please try again");
            }

            System.out.println("Loading each Pokemon's moves...");

            // load each pokemons moves
            httpConnect = (HttpURLConnection) movesFile.openConnection();
            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-Type", "application/json");
            responseCode = httpConnect.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("{")) {
                        String pokeName = "";
                        String form = "";
                        int ID;
                        List<Pokemon.MoveElite> fastMoves = new ArrayList<Pokemon.MoveElite>();
                        List<Pokemon.MoveElite> charMoves = new ArrayList<Pokemon.MoveElite>();
                        while ((inputLine = in.readLine()) != null && !inputLine.contains("}")) {
                            inputLine = inputLine.trim();
                            int firstIndex;
                            if (inputLine.contains("pokemon_name")) {
                                pokeName = removeQuotes(inputLine, inputLine.indexOf(":"));
                            }
                            else if (inputLine.contains("\"form\"")) {
                                form = removeQuotes(inputLine, inputLine.indexOf(":"));
                            }
                            else if (inputLine.contains("charged_moves")) {
                                if (inputLine.contains("]")) {
                                    continue;
                                }
                                boolean elite = inputLine.contains("elite");
                                while ((inputLine = in.readLine()) != null && !inputLine.contains("]")) {
                                    charMoves.add(new Pokemon.MoveElite(removeQuotes(inputLine, 0), elite));
                                }
                            }
                            else if (inputLine.contains("fast_moves")) {
                                if (inputLine.contains("]")) {
                                    continue;
                                }
                                boolean elite = inputLine.contains("elite");
                                while ((inputLine = in.readLine()) != null && !inputLine.contains("]")) {
                                    fastMoves.add(new Pokemon.MoveElite(removeQuotes(inputLine, 0), elite));
                                }
                            }
                        }
                        if (pokemon.containsKey(Pokemon.getNameForm(pokeName, form))) {
                            pokemon.get(Pokemon.getNameForm(pokeName, form)).fastMoves = fastMoves;
                            pokemon.get(Pokemon.getNameForm(pokeName, form)).charMoves = charMoves;
                        }
                    }
                }
            }
            else {
                System.out.println("Something went wrong, please try again");
            }
        }
        catch (Exception e) {
            System.out.println("Something went wrong, please try again");
        }
    }
    public static void loadDmgMultipliers() {
        try {
            System.out.println("Loading type effectiveness chart...");

            // define file URLS
            URL statsFile = new URL("https://pogoapi.net/api/v1/type_effectiveness.json");

            HttpURLConnection httpConnect = (HttpURLConnection) statsFile.openConnection();
            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-Type", "application/json");
            int responseCode = httpConnect.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                String inputLine;
                in.readLine();
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("{")) {
                        Type curType = Type.parseType(removeQuotes(inputLine, 0));
                        while ((inputLine = in.readLine()) != null && !inputLine.contains("}")) {
                            String[] typeMult = inputLine.split(":");
                            typeMult[0] = removeQuotes(typeMult[0], 0);
                            typeMult[1] = typeMult[1].trim();
                            if (typeMult[1].contains(",")) {
                                typeMult[1] = typeMult[1].substring(0, typeMult[1].indexOf(","));
                            }
                            curType.multiplier.put(Type.parseType(typeMult[0]), Double.parseDouble(typeMult[1]));
                        }
                    }
                }
            }
            else {
                System.out.println("Something went wrong, please try again");
            }
        }
        catch (Exception e) {
            System.out.println("Something went wrong, please try again");
        }
    }

    public static void loadPossibleShadows(Hashtable<String, Pokemon> pokemon) {
        try {
            System.out.println("Loading shadow Pokemon...");

            // define file URLS
            URL statsFile = new URL("https://pogoapi.net/api/v1/shadow_pokemon.json");

            HttpURLConnection httpConnect = (HttpURLConnection) statsFile.openConnection();
            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-Type", "application/json");
            int responseCode = httpConnect.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                String inputLine;
                in.readLine();
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("{")) {
                        while ((inputLine = in.readLine()) != null && !inputLine.contains("}")) {
                            String[] attribute = inputLine.trim().split(":");
                            if (attribute[0].contains("name")) {
                                String pokeName = removeQuotes(attribute[1], 0);
                                pokemon.get(pokeName).canBeShadow = true;
                            }
                        }
                    }
                }
            }
            else {
                System.out.println("Something went wrong, please try again");
            }
        }
        catch (Exception e) {
            System.out.println("Something went wrong, please try again");
        }
    }

    public static void loadMegaPokemon(Hashtable<String, Pokemon> pokemon) {
        try {
            System.out.println("Loading mega Pokemon...");

            // define file URLS
            URL statsFile = new URL("https://pogoapi.net/api/v1/mega_pokemon.json");

            HttpURLConnection httpConnect = (HttpURLConnection) statsFile.openConnection();
            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-Type", "application/json");
            int responseCode = httpConnect.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                String inputLine;
                in.readLine();
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("{")) {
                        Pokemon newMega = new Pokemon();
                        newMega.isMega = true;
                        String basePokemon = "";
                        while ((inputLine = in.readLine()) != null && !inputLine.contains("}")) {
                            String[] attribute = inputLine.split(":");
                            if (attribute[0].contains("mega_name")) {
                                newMega.name = removeQuotes(attribute[1], 0);
                            }
                            if (attribute[0].contains("pokemon_name")) {
                                basePokemon = removeQuotes(attribute[1], 0);
                            }
                            if (attribute[0].contains("\"stats\"")) {
                                while ((inputLine = in.readLine()) != null && !inputLine.contains("}")) {
                                    String[] stat = inputLine.split(":");
                                    stat[1] = stat[1].trim();
                                    if (stat[0].contains("attack")) {
                                        newMega.attack = Integer.parseInt(stat[1].substring(0, stat[1].indexOf(",")));
                                    }
                                    if (stat[0].contains("defense")) {
                                        newMega.defense = Integer.parseInt(stat[1].substring(0, stat[1].indexOf(",")));
                                    }
                                    if (stat[0].contains("stamina")) {
                                        newMega.stamina = Integer.parseInt(stat[1]);
                                    }
                                }
                            }
                            if (attribute[0].contains("\"type\"")) {
                                int typeNum = 0;
                                while ((inputLine = in.readLine()) != null && !inputLine.contains("]")) {
                                    newMega.type[typeNum] = Type.parseType(removeQuotes(inputLine, 0));
                                    typeNum++;
                                }
                            }
                        }
                        newMega.fastMoves = pokemon.get(basePokemon).fastMoves;
                        newMega.charMoves = pokemon.get(basePokemon).charMoves;
                        pokemon.put(newMega.name, newMega);
                    }
                }
            }
            else {
                System.out.println("Something went wrong, please try again");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Something went wrong, please try again");
        }
    }
}
