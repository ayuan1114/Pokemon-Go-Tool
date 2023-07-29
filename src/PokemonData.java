import java.util.*;

public class PokemonData {
    public static Hashtable<String, Boolean> possibleShadows;
    public static Hashtable<String, Pokemon> pokemons;
    public static Hashtable<String, Move> moves;
    public static Hashtable<Integer, String> pokemonIDs;

    public static boolean weatherBoost = false;

    public static Move getMove(String moveName) {
        return moves.get(moveName);
    }

    public static Pokemon getPokemon(String pokemonName) {
        return pokemons.get(pokemonName);
    }

    public static boolean canBeShadow(String pokemonName) {
        return possibleShadows.containsKey(pokemonName);
    }

    public static void main(String[] args) {
        StatLoader test = new StatLoader();
        loadAll(test);
        //testAttributeCalc("Mewtwo", 15, 15, 15, 40);
        PokemonRanker.bestOfType(Type.WATER);
    }

    public static void loadAll(StatLoader test) {
        pokemons = new Hashtable();
        moves = new Hashtable();
        pokemonIDs = new Hashtable();
        test.loadCPMult();
        test.loadDmgMultipliers();
        test.loadPokemonIDs(pokemonIDs);
        test.loadMoves(moves);
        test.loadPokemonInfo(pokemons);
        test.loadMegaPokemon(pokemons);
        test.loadPossibleShadows(pokemons);
    }

    public static void testCPMultLoad() {
        for (double mult: Pokemon.cpMultipliers) {
            System.out.println(mult);
        }
    }

    public static void testEffectivenessChart() {
        for (Type type : Type.values()) {
            for (Type type2 : Type.values()) {
                System.out.println(type.toString() + " " + type2.toString() + ": " + type.dmgMult(type2));
            }
        }
    }

    public static void testPokemonLoad(StatLoader test) {
        System.out.println("Pokemons:");
        for (String pokemon : pokemons.keySet()) {
            System.out.println(pokemons.get(pokemon));
        }
    }

    public static void testMovesLoad(StatLoader test) {
        System.out.println("Moves: ");
        for (String pokemon : moves.keySet()) {
            System.out.println(moves.get(pokemon));
        }
    }

    public static void testAttributeCalc(String pokemon, int attIV, int defIV, int hpIV, double level) {
        Pokemon attacker1 = pokemons.get("Mewtwo").createInstance(attIV, defIV, hpIV, level, false);
        Pokemon attacker2 = pokemons.get("Gengar").createInstance(attIV, defIV, hpIV, level, false);
        Pokemon defender = pokemons.get("Mewtwo").createInstance(attIV, defIV, hpIV, level, false);
        System.out.println(attacker1);
        System.out.println(attacker2);
        String[] best1 = attacker1.bestMoveset(Type.ICE, false);
        String[] best2 = attacker2.bestMoveset(Type.GHOST, true);
        System.out.println(best1[0] + " " + best1[1]);
        attacker1.movesetSummary(best1[0], best1[1], Type.ICE);
        attacker2.movesetSummary(best2[0], best2[1], Type.GHOST);
    }

    public static void topTypeAttacker(Type type, boolean includeElite, boolean includeMega, boolean includeShadow) {

    }
}
