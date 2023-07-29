import java.util.*;

public class PokemonRanker {
    public static boolean includeShadow = true, includeMega = true, includeElite = true;
    public static Weather weatherBoost = null;
    public static Hashtable<Type, ArrayList<String>> bestByType;
    private static Hashtable<String, Double> pokeER;

    static class SortByDPS implements Comparator<String> {
        public int compare(String poke1, String poke2) {
            return pokeER.get(poke2).compareTo(pokeER.get(poke1));
        }
    }

    public static void calcBestAttackersByType() {

    }

    public static ArrayList<String> bestOfType(Type atkType) {
        pokeER = new Hashtable<>();
        ArrayList<String> allPokemon = new ArrayList<String>();
        for (String pokemon : PokemonData.pokemons.keySet()) {
            String curPoke;
            Double curPokeER;
            if (PokemonData.pokemons.get(pokemon).canBeShadow) {
                curPoke = pokemon + " S";
                try {
                    String[] bestMoveset = PokemonData.pokemons.get(pokemon).createInstance(15, 15, 15, 40, true).bestMoveset(atkType, includeElite);
                    curPokeER = new Double(bestMoveset[2]);
                    allPokemon.add(curPoke);
                    pokeER.put(curPoke, curPokeER);
                }
                catch (Exception e) {

                }

            }
            curPoke = pokemon + " N";
            try {
                String[] bestMoveset = PokemonData.pokemons.get(pokemon).createInstance(15, 15, 15, 40, false).bestMoveset(atkType, includeElite);
                curPokeER = new Double(bestMoveset[2]);
                allPokemon.add(pokemon + " N");
                pokeER.put(curPoke, curPokeER);
            }
            catch (Exception e) {

            }
        }
        Collections.sort(allPokemon, new SortByDPS());
        for (String poke : allPokemon) {
            System.out.print(poke + " ");
            System.out.println(pokeER.get(poke));

        }
        return allPokemon;
    }
}
