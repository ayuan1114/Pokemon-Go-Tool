import java.util.Scanner;

import pokemon.database.PokemonInfoLoader;
import pokemon.database.PokemonRanker;

public class PokemonGoTool {
    public static void main(String[] args) {
        PokemonInfoLoader.loadIntoDatabase();
        PokemonFrontend frontendUI = new PokemonFrontend(new Scanner(System.in), new PokemonRanker());
        frontendUI.start();
    }
}
