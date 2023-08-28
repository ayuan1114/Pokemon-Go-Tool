import java.util.*;

public class PokemonData {
    public static Hashtable<String, Pokemon> pokemons = new Hashtable();
    public static Hashtable<String, Move> moves = new Hashtable();
    public static Hashtable<Integer, String> pokemonIDs = new Hashtable();
    public static Weather weather = Weather.NONE;
    public static boolean includeElite = true;

    public static void toggleElite() {
        includeElite = !includeElite;
    }

    public static Move getMove(String moveName) {
        return moves.get(moveName);
    }

    public static Pokemon getPokemon(String pokemonName) {
        return pokemons.get(pokemonName);
    }

    public static boolean canBeShadow(String pokemonName) {
        return pokemons.get(pokemonName).canBeShadow;
    }

    /*
    public static void main(String[] args) {
        PokemonInfoLoader test = new PokemonInfoLoader();
        loadAll(test);
        //testAttributeCalc("Mewtwo", 15, 15, 15, 40);
        //ArrayList<String> bestWater = PokemonRanker.bestOfType(Type.WATER);
        PokemonRanker.calcBestAttackersByType();
        Pokemon attacker1 = pokemons.get("Mega Blaziken").createInstance(15, 15, 15, 40, false);
        Pokemon defender1 = pokemons.get("Kartana").createInstance(15, 15, 15, 40, false);
        List<String> bestCounters = PokemonRanker.bestCounters(defender1, true, 10);
    }
    */

    public void testCPMultLoad() {
        for (double mult: Pokemon.cpMultipliers) {
            System.out.println(mult);
        }
    }

    public void testEffectivenessChart() {
        for (Type type : Type.values()) {
            for (Type type2 : Type.values()) {
                System.out.println(type.toString() + " " + type2.toString() + ": " + type.dmgMult(type2));
            }
        }
    }

    public void testPokemonLoad(PokemonInfoLoader test) {
        System.out.println("Pokemons:");
        for (String pokemon : pokemons.keySet()) {
            System.out.println(pokemons.get(pokemon));
        }
    }

    public void testMovesLoad(PokemonInfoLoader test) {
        System.out.println("Moves: ");
        for (String pokemon : moves.keySet()) {
            System.out.println(moves.get(pokemon));
        }
    }

    public void testAttributeCalc(String pokemon, int attIV, int defIV, int hpIV, double level) {
        Pokemon attacker1 = pokemons.get("Reshiram").createInstance(attIV, defIV, hpIV, level, false);
        Pokemon attacker2 = pokemons.get("Gengar").createInstance(attIV, defIV, hpIV, level, false);
        Pokemon defender = pokemons.get("Kartana").createInstance(attIV, defIV, hpIV, level, false);
        System.out.println(attacker1);
        System.out.println(attacker2);
        String[] best1 = attacker1.bestMoveset(Type.ICE);
        String[] best2 = attacker2.bestMoveset(Type.GHOST);
        System.out.println(best1[0] + " " + best1[1]);
        attacker1.movesetSummary(best1[0], best1[1], Type.ICE);
        attacker2.movesetSummary(best2[0], best2[1], Type.GHOST);
    }
}
