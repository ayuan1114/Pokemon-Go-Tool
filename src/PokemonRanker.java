import java.util.*;

public class PokemonRanker {
    public static boolean includeShadow = true, includeMega = true, includeElite = true;
    public static Weather weatherBoost = null;
    public static Hashtable<Type, ArrayList<String>> bestByType = new Hashtable<>();
    private static Hashtable<String, Double> pokeER;

    static class SortByDPS implements Comparator<String> {
        public int compare(String poke1, String poke2) {
            return pokeER.get(poke2).compareTo(pokeER.get(poke1));
        }
    }

    public static Pokemon getPokemon(String name) {
        if (name.charAt(name.length() - 1) == 'S') {
            return PokemonData.pokemons.get(name.substring(0, name.length() - 2)).createInstance(15, 15, 15, 40, true);
        }
        else {
            return PokemonData.pokemons.get(name.substring(0, name.length() - 2)).createInstance(15, 15, 15, 40, false);
        }
    }


    public static List<String> bestOfType(Type atkType, boolean print, int num) {
        ArrayList<String> allPokemon;
        ArrayList<String> toReturn = new ArrayList<String>();
        if (bestByType.containsKey(atkType)) {
            allPokemon = bestByType.get(atkType);
            for (int a = 0; a < Integer.min(num, allPokemon.size()); a++) {
                String[] moveset = getPokemon(allPokemon.get(a)).bestMoveset(atkType, includeElite);
                toReturn.add(allPokemon.get(a) + " (" + moveset[0] + ", " + moveset[1] + ")");
                if (print) {
                    System.out.print(toReturn.get(a) + " ER: ");
                    System.out.println(moveset[2]);
                }
            }
            return toReturn;
        }
        pokeER = new Hashtable<>();
        allPokemon = new ArrayList<String>();
        for (String pokemon : PokemonData.pokemons.keySet()) {
            String curPoke;
            Double curPokeER;
            if (PokemonData.pokemons.get(pokemon).canBeShadow) {
                curPoke = pokemon + " S";
                try {
                    String[] bestMoveset = getPokemon(curPoke).bestMoveset(atkType, includeElite);
                    curPokeER = new Double(bestMoveset[2]);
                    allPokemon.add(curPoke);
                    pokeER.put(curPoke, curPokeER);
                }
                catch (Exception e) {

                }

            }
            curPoke = pokemon + " N";
            try {
                String[] bestMoveset = getPokemon(curPoke).bestMoveset(atkType, includeElite);
                curPokeER = new Double(bestMoveset[2]);
                allPokemon.add(pokemon + " N");
                pokeER.put(curPoke, curPokeER);
            }
            catch (Exception e) {

            }
        }
        Collections.sort(allPokemon, new SortByDPS());
        for (int a = 0; a < Integer.min(num, allPokemon.size()); a++) {
            String[] moveset = getPokemon(allPokemon.get(a)).bestMoveset(atkType, includeElite);
            toReturn.add(allPokemon.get(a) + " (" + moveset[0] + ", " + moveset[1] + ")");
            if (print) {
                System.out.print(toReturn.get(a) + " ER: ");
                System.out.println(moveset[2]);
            }
        }
        bestByType.put(atkType, allPokemon);
        return toReturn;
    }

    public static void calcBestAttackersByType() {
        for (Type type : Type.values()) {
            bestOfType(type, false, 1000);
        }
    }

    public static class TypeEff {
        public Type type;
        public double dmgMult;
        public TypeEff(Type type, double dmgMult) {
            this.type = type;
            this.dmgMult = dmgMult;
        }
    }

    static class SortTypeCounter implements Comparator<TypeEff> {
        public int compare(TypeEff type1, TypeEff type2) {
            return Double.compare(type2.dmgMult, type1.dmgMult);
        };
    }

    /**
     * Calculates the best counters against a given defender pokemon.
     * This method uses the rankings for best Pokemon of each type.
     * First the ordering of type counters against the defending Pokemon is determined
     * From there the Effectiveness Rating against the defender for each of the best Pokemon of each type is determined
     * Once the best Pokemon is chosen and added to the list of best counters, the ER of the next best Pokemon of the
     * same type attacker will be calculated.
     * This typing will be resorted in with the other typings and the new best counter is chosen again.
     * The process repeats until the top n counters are found
     * @param defender the defending pokemon
     * @param print whether or not to print all the counters and ERs
     * @param num the number of counters to return
     * @return list of the top (num) counters against the given defender
     */

    public static List<String> bestCounters(Pokemon defender, boolean print, int num) {
        calcBestAttackersByType();
        List<String> toReturn = new ArrayList<String>();
        int count = 0, tempInc;
        List<TypeEff> typeCounter = new ArrayList<TypeEff>();
        Hashtable<String, String[]> counterDPS = new Hashtable<>();
        Hashtable<Type, Integer> typeInc = new Hashtable<>();
        String curPoke, toAdd;
        Type curType;
        TypeEff temp;
        double newPokeDPS;
        int curTypeInc;
        for (Type type : Type.values()) {
            typeInc.put(type, 0);
            if (defender.type[1] == null) {
                typeCounter.add(new TypeEff(type, type.dmgMult(defender.type[0])));
            }
            else {
                typeCounter.add(new TypeEff(type, type.dmgMult(defender.type[0]) * type.dmgMult(defender.type[1])));
            }

        }
        Collections.sort(typeCounter, new SortTypeCounter());
        for (Type type : Type.values()) {
            curPoke = bestByType.get(type).get(0);
            counterDPS.put(curPoke, getPokemon(curPoke).bestMoveset(defender, includeElite));
        }
        while (count < num) {
            curType = typeCounter.get(0).type;
            curTypeInc = typeInc.get(curType);
            curPoke = bestByType.get(curType).get(curTypeInc);
            toAdd = curPoke + " (" + counterDPS.get(curPoke)[0] + ", " + counterDPS.get(curPoke)[1] + ")";
            if (!toReturn.contains(toAdd)) {
                toReturn.add(toAdd);
                if (print) {
                    System.out.println(toAdd + " ER: " + counterDPS.get(curPoke)[2]);
                }
                count++;
            }

            tempInc = curTypeInc;
            while (counterDPS.containsKey(bestByType.get(curType).get(curTypeInc))) {
                curTypeInc++;
            }
            typeInc.replace(curType, tempInc, curTypeInc);
            curPoke = bestByType.get(curType).get(curTypeInc);
            counterDPS.put(curPoke, getPokemon(curPoke).bestMoveset(defender, includeElite));
            newPokeDPS = Double.parseDouble(counterDPS.get(curPoke)[2]);

            temp = typeCounter.get(0);
            typeCounter.remove(0);
            curTypeInc = 0; // now using variable curTypeInc to determine new position for the type based on new best attacker of that type
            curType = typeCounter.get(curTypeInc).type;
            curPoke = bestByType.get(curType).get(typeInc.get(curType));

            // increment thru types until best pokemon of type with less DPS is found
            while (newPokeDPS < Double.parseDouble(counterDPS.get(curPoke)[2])) {
                curTypeInc++;
                curType = typeCounter.get(curTypeInc).type;
                curPoke = bestByType.get(curType).get(typeInc.get(curType));
            }
            typeCounter.add(curTypeInc, temp);
        }
        return toReturn;
    }
}
