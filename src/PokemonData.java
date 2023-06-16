import java.util.*;

public class PokemonData {
    public static Hashtable<String, Boolean> possibleShadows;
    public static Hashtable<String, Pokemon> pokemons;
    public static Hashtable<String, Move> moves;
    public static Hashtable<String, Integer> pokemonIDs;

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
        testAttributeCalc("Mewtwo", 15, 15, 15, 40);
        Collections.sort(test.defenses);
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

    public static void testShadowLoad(StatLoader test) {
        System.out.println("Shadows");
        for (String pokemon : possibleShadows.keySet()) {
            System.out.println(pokemon);
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
        Pokemon attacker1 = pokemons.get("Gengar").createInstance(attIV, defIV, hpIV, level, false);
        Pokemon attacker2 = pokemons.get("Pheromosa").createInstance(attIV, defIV, hpIV, level, false);
        Pokemon defender = pokemons.get("Mewtwo").createInstance(attIV, defIV, hpIV, level, false);
        System.out.println(attacker1);
        System.out.println(attacker2);
        System.out.println(attacker1.cycleDPS(moves.get("Lick"), moves.get("Shadow Ball"), defender, false));
        System.out.println(attacker2.cycleDPS(moves.get("Bug Bite"), moves.get("Bug Buzz"), defender, false));
        System.out.println(attacker1.comprehensiveDPS(moves.get("Lick"), moves.get("Shadow Ball"), defender, false));
        System.out.println(attacker2.comprehensiveDPS(moves.get("Bug Bite"), moves.get("Bug Buzz"), defender, false));
        System.out.println(attacker1.effectiveRating(moves.get("Lick"), moves.get("Shadow Ball"), defender, false));
        System.out.println(attacker2.effectiveRating(moves.get("Bug Bite"), moves.get("Bug Buzz"), defender, false));

    }

    public static void loadAll(StatLoader test) {
        possibleShadows = new Hashtable();
        pokemons = new Hashtable();
        moves = new Hashtable();
        pokemonIDs = new Hashtable();
        test.loadCPMult();
        test.loadDmgMultipliers();
        test.loadPokemonIDs(pokemonIDs);
        test.loadMoves(moves);
        test.loadPokemon(pokemons);
        test.loadMegaPokemon(pokemons);
        test.loadPossibleShadows(possibleShadows);
    }
}
