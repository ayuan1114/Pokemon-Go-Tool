import java.util.*;

public class Pokemon {
    public static Pokemon DUMMY = new Pokemon("dummy", -1);

    public static double[] cpMultipliers;

    /**
     * gets the CP multiplier at the given pokemon level
     * @param level double the level of the pokemon and -1 indicates a raid boss
     * @return a double representing the CP multiplier for a pokemon at the inputted level or 1 for raid boss
     */

    public static double getCPMult(int level) {
        if (level == -1) {
            return 1;
        }
        else {
            return cpMultipliers[level];
        }
    }

    static class MoveElite {
        public String moveName;
        public boolean isElite;
        public MoveElite(String moveName, boolean isElite) {
            this.moveName = moveName;
            this.isElite = isElite;
        }
    }

    public boolean canBeShadow;
    public String name;
    public String form;
    public List<MoveElite> charMoves;
    public List<MoveElite> fastMoves;
    public int stamina, defense, attack;
    public Type type[];
    public int level; // level of -1 indicates raid boss (which has CP Mult 1)
    public boolean isShadow, isMega;
    public int ID;

    /**
     * Constructor with string parameter allows the name of the Pokemon to be set
     * Is also used to define a dummy Pokemon that serves as a typeless Pokemon for calculating general dps
     * @param name
     */
    public Pokemon(String name, int ID) {
        if (name.equals("dummy")) {
            this.name = "DUMMY";
            attack = 181;
            defense = 156;
            level = 40;
            type = null;
            form = "";
            ID = -1;
        }
        else {
            this.name = name;
            this.ID = ID;
            charMoves = new ArrayList<MoveElite>();
            fastMoves = new ArrayList<MoveElite>();
            stamina = 0;
            defense = 0;
            attack = 0;
            type = new Type[2];
            this.isShadow = false;
            this.isMega = false;
        }

    }
    public Pokemon() {
        name = "";
        form = "";
        charMoves = new ArrayList<MoveElite>();
        fastMoves = new ArrayList<MoveElite>();
        stamina = 0;
        defense = 0;
        attack = 0;
        type = new Type[2];
        this.isShadow = false;
        this.isMega = false;
        canBeShadow = false;
        ID = 0;
    }

    public String[] getFastMoves() {
        String[] toReturn = new String[fastMoves.size()];
        for (int a = 0; a < fastMoves.size(); a++) {
            toReturn[a] = fastMoves.get(a).moveName;
        }
        return toReturn;
    }

    public String[] getCharMoves() {
        String[] toReturn = new String[charMoves.size()];
        for (int a = 0; a < charMoves.size(); a++) {
            toReturn[a] = charMoves.get(a).moveName;
        }
        return toReturn;
    }

    public int calcCP() {
        return (int) ((attack * Math.sqrt(defense) * Math.sqrt(stamina) * getCPMult(level) * getCPMult(level)) / 10);
    }

    public static String getNameForm(String nameTemp, String formTemp) {
        if (formTemp.equals("Normal") || formTemp.isEmpty()) {
            return nameTemp;
        }
        return nameTemp + " (" + formTemp + ")";
    }

    public String getNameForm() {
        if (form.equals("Normal") || form.isEmpty()) {
            return name;
        }
        return name + " (" + form + ")";
    }

    public String toString() {
        String typeString = "";
        String shadow = "";
        if (type[1] == null) {
            typeString = type[0].toString();
        }
        else {
            typeString = type[0].toString() + ", " + type[1].toString();
        }
        if (isShadow) {
            shadow = " (Shadow)";
        }
        return getNameForm() + shadow + " (Type: " + typeString + ", CP: " + calcCP() + ", " + fastMoves.size() + " fast moves, " + charMoves.size() + " charged moves) \n" +
                "Stats: (attack: " + attack + ", defense: " + defense + ", stamina: " + stamina + ")";
    }

    public boolean equals(Pokemon otherPoke) {
        return this.attack == otherPoke.attack &&
                this.defense == otherPoke.defense &&
                this.stamina == otherPoke.stamina &&
                this.name.equals(otherPoke.name) &&
                this.isShadow == otherPoke.isShadow;
    }

    public Pokemon asRaidBoss(int tier) {
        Pokemon raid = new Pokemon(this.name, this.ID);
        if (tier == 1) {
            raid.stamina = 600;
        }
        if (tier == 3) {
            raid.stamina = 3600;
        }
        if (tier == 4) { // mega
            raid.stamina = 9000;
        }
        if (tier == 5) {
            raid.stamina = 15000;
        }
        if (tier == 6) {
            raid.stamina = 22500;
        }
        raid.form = this.form;
        raid.attack = this.attack + 15;
        raid.defense = this.defense + 15;
        raid.charMoves = this.charMoves;
        raid.fastMoves = this.fastMoves;
        raid.type = this.type;
        raid.level = -1;
        return raid;
    }

    public Pokemon createInstance(int attIV, int defIV, int hpIV, double level, boolean isShadow) {
        Pokemon instance = new Pokemon(this.name, this.ID);
        instance.level = (int) (level * 2);
        instance.form = this.form;
        instance.charMoves = this.charMoves;
        instance.fastMoves = this.fastMoves;
        instance.isShadow = isShadow;
        instance.stamina = this.stamina + hpIV;
        instance.defense = this.defense + defIV;
        instance.attack = this.attack + attIV;
        instance.type = this.type;
        instance.isMega = this.isMega;
        return instance;
    }

    /**
     * Calculates the actual damage output of a move used against another Pokemon
     * @param move the move to be used
     * @param otherPokemon the Pokemon being attacked
     * @return the amount of damage one use of the move would do
     */
    public double calcMoveDmg(Move move, Pokemon otherPokemon) {
        double modifier = 1;
        double effectAtt = attack;
        double enemyEffDef = otherPokemon.defense;
        if (isShadow) {
            effectAtt = effectAtt * 1.2;
        }
        if (otherPokemon.isShadow) {
            enemyEffDef = enemyEffDef * (5.0 / 6.0);
        }
        effectAtt *= getCPMult(level);
        enemyEffDef *= getCPMult(otherPokemon.level);


        if (move.elementType == type[0]) {
            modifier *= 1.2;
        }
        else if (type[1] != null && move.elementType == type[1]) {
            modifier *= 1.2;
        }
        if (PokemonData.weatherBoost) {
            modifier *= 1.2;
        }
        else if (otherPokemon.type != null) {
            modifier = move.elementType.dmgMult(otherPokemon.type[0]);
            if (otherPokemon.type[1] != null) {
                modifier *= move.elementType.dmgMult(otherPokemon.type[1]);
            }
        }
        return Math.round(0.5 * move.power * (effectAtt / enemyEffDef) * modifier) + 1;
    }

    /**
     * Calculates the actual damage output of a move used against another Pokemon
     * @param move the move to be used
     * @param atkType typing the move is being analyzed for
     * @return the amount of damage one use of the move would do
     */
    public double calcMoveDmg(Move move, Type atkType) {
        if (move.elementType != atkType) {
            return 0;
        }
        double modifier = 1;
        double effectAtt = attack;
        double enemyEffDef = Pokemon.DUMMY.defense * getCPMult(Pokemon.DUMMY.level);
        if (isShadow) {
            effectAtt = effectAtt * 1.2;
        }
        effectAtt *= getCPMult(level);

        if (move.elementType == type[0]) {
            modifier *= 1.2;
        }
        else if (type[1] != null && move.elementType == type[1]) {
            modifier *= 1.2;
        }
        if (PokemonData.weatherBoost) {
            modifier *= 1.2;
        }
        return Math.round(0.5 * move.power * (effectAtt / enemyEffDef) * modifier) + 1;
    }

    /**
     * Calculates simple dps when a pokemon is able to complete a damage cycle
     * (using fast moves until charged move is charged and using charged move can be considered one cycle)
     * (calculation is: (fast move dps * ratio of time fast move is being performed) + (charged move dps * ratio of time charged move is being performed)
     * @param fast Move representing the fast move to be used for DPS calculation
     * @param charged Move representing the charged move to be used for DPS calculation
     * @param otherPokemon Pokemon representing the enemy pokemon that is being attacked
     * @return the cycle DPS of this pokemon with the given move set against the enemy pokemon
     */
    public double cycleDPS(Move fast, Move charged, Pokemon otherPokemon) {
        double fastDPS = calcMoveDmg(fast, otherPokemon) / fast.duration;
        double fastEPS = fast.energyDelta / fast.duration;
        double charDPS = calcMoveDmg(charged, otherPokemon) / charged.duration;
        double charEPS = charged.energyDelta / charged.duration;
        if (charEPS == 0) {
            return charDPS;
        }
        return ((fastDPS * charEPS) + (charDPS * fastEPS)) / (charEPS + fastEPS);
    }

    /**
     * Calculates simple dps when a pokemon is able to complete a damage cycle
     * (using fast moves until charged move is charged and using charged move can be considered one cycle)
     * (calculation is: (fast move dps * ratio of time fast move is being performed) + (charged move dps * ratio of time charged move is being performed)
     * @param fast Move representing the fast move to be used for DPS calculation
     * @param charged Move representing the charged move to be used for DPS calculation
     * @param atkType typing the moveset is being analyzed for
     * @return the cycle DPS of this pokemon with the given move set against the enemy pokemon
     */
    public double cycleDPS(Move fast, Move charged, Type atkType) {
        double fastDPS = calcMoveDmg(fast, atkType) / fast.duration;
        double fastEPS = fast.energyDelta / fast.duration;
        double charDPS = calcMoveDmg(charged, atkType) / charged.duration;
        double charEPS = charged.energyDelta / charged.duration;
        if (charEPS == 0) {
            return charDPS;
        }
        return ((fastDPS * charEPS) + (charDPS * fastEPS)) / (charEPS + fastEPS);
    }

    /**
     * Calculates comprehensive DPS which takes into account the energy remaining when pokemon dies and the dps of the enemy pokemon
     * Reference and further details: https://gamepress.gg/pokemongo/how-calculate-comprehensive-dps
     * @param fast Move representing the fast move to be used for DPS calculation
     * @param charged Move representing the charged move to be used for DPS calculation
     * @param otherPokemon Pokemon representing the enemy pokemon that is being attacked
     * @return the comprehensive DPS of this pokemon with the given move set against the enemy pokemon
     */
    public double comprehensiveDPS(Move fast, Move charged, Pokemon otherPokemon) {
        double effectDef = defense;
        double enemyEffAtt = otherPokemon.attack;
        if (isShadow) {
            effectDef = effectDef * (5.0 / 6.0);
        }
        if (otherPokemon.isShadow) {
            enemyEffAtt = enemyEffAtt * 1.2;
        }
        effectDef *= getCPMult(level);
        enemyEffAtt *= getCPMult(otherPokemon.level);

        double simpleDPS = cycleDPS(fast, charged, otherPokemon);
        double expectedEnergyWasted = 0.5 * fast.energyDelta + 0.5 * charged.energyDelta;
        double expectedEnemyDPS = (6 * enemyEffAtt) / effectDef;
        double fastDPS = calcMoveDmg(fast, otherPokemon) / fast.duration;
        double charDPS = calcMoveDmg(charged, otherPokemon) / charged.duration;
        double fastEPS = fast.energyDelta / fast.duration;
        double charEPS = charged.energyDelta / charged.duration;
        if (charEPS == 0) {
            return charDPS;
        }
        return simpleDPS + (((charDPS - fastDPS) / (charEPS + fastEPS)) * (0.5 - (expectedEnergyWasted / (stamina * getCPMult(level)))) * expectedEnemyDPS);
    }

    /**
     * Calculates comprehensive DPS which takes into account the energy remaining when pokemon dies and the dps of the enemy pokemon
     * Reference and further details: https://gamepress.gg/pokemongo/how-calculate-comprehensive-dps
     * @param fast Move representing the fast move to be used for DPS calculation
     * @param charged Move representing the charged move to be used for DPS calculation
     * @param atkType typing the moveset is being analyzed for
     * @return the comprehensive DPS of this pokemon with the given move set against the enemy pokemon
     */
    public double comprehensiveDPS(Move fast, Move charged, Type atkType) {
        double effectDef = defense;
        double enemyEffAtt = Pokemon.DUMMY.attack * getCPMult(Pokemon.DUMMY.level);
        if (isShadow) {
            effectDef = effectDef * (5.0 / 6.0);
        }
        effectDef *= getCPMult(level);

        double simpleDPS = cycleDPS(fast, charged, atkType);
        double expectedEnergyWasted = 0.5 * fast.energyDelta + 0.5 * charged.energyDelta;
        double expectedEnemyDPS = (6 * enemyEffAtt) / effectDef;
        double fastDPS = calcMoveDmg(fast, atkType) / fast.duration;
        double charDPS = calcMoveDmg(charged, atkType) / charged.duration;
        double fastEPS = fast.energyDelta / fast.duration;
        double charEPS = charged.energyDelta / charged.duration;
        if (charEPS == 0) {
            return charDPS;
        }
        return simpleDPS + (((charDPS - fastDPS) / (charEPS + fastEPS)) * (0.5 - (expectedEnergyWasted / (stamina * getCPMult(level)))) * expectedEnemyDPS);
    }

    /**
     * calculates the effective rating of a Pokemon that is proportional to DPS * (TOF ^ 0.25)
     * This measurement will be used to assess a Pokemon's moveset's viability as an attacker
     * @param fast the fast move of the pokemon
     * @param charged the charged move os the Pokemon
     * @param otherPokemon the Pokemon being attacked
     * @return double that is the effectiveness rating of the moveset against the defender
     */
    public double effectiveRating(Move fast, Move charged, Pokemon otherPokemon) {
        double effectDef = defense;
        double enemyEffAtt = otherPokemon.attack;
        if (isShadow) {
            effectDef = effectDef * (5.0 / 6.0);
        }
        if (otherPokemon.isShadow) {
            enemyEffAtt = enemyEffAtt * 1.2;
        }
        effectDef *= getCPMult(level);
        enemyEffAtt *= getCPMult(otherPokemon.level);

        double timeOnField = (stamina * getCPMult(level) * effectDef) / (6 * enemyEffAtt);
        return comprehensiveDPS(fast, charged, otherPokemon) * Math.pow(timeOnField, 0.25);
    }

    /**
     * calculates the effective rating of a Pokemon that is proportional to DPS * (TOF ^ 0.25)
     * This measurement will be used to assess a Pokemon's moveset's viability as an attacker
     * @param fast the fast move of the pokemon
     * @param charged the charged move os the Pokemon
     * @param atkType typing the moveset is being analyzed for
     * @return double that is the effectiveness rating of the moveset against the defender
     */
    public double effectiveRating(Move fast, Move charged, Type atkType) {
        double effectDef = defense;
        double enemyEffAtt = Pokemon.DUMMY.attack * getCPMult(Pokemon.DUMMY.level);
        if (isShadow) {
            effectDef = effectDef * (5.0 / 6.0);
        }
        effectDef *= getCPMult(level);

        double timeOnField = (stamina * getCPMult(level) * effectDef) / (6 * enemyEffAtt);
        return comprehensiveDPS(fast, charged, atkType) * Math.pow(timeOnField, 0.25);
    }

    /**
     * returns the best moveset against this Pokemon has against the defending Pokemon
     * @param otherPokemon Pokemon being attacked
     * @param includeElite if true, include legacy moves, if false do not
     * @return a String array that contains the name of the fast and charged move
     */
    public String[] bestMoveset(Pokemon otherPokemon, boolean includeElite) {
        String[] moveset = new String[3];
        double bestRating = 0;
        Move fastMove, chargedMove;

        for (MoveElite fast : fastMoves) {
            if (!fast.isElite || includeElite) {
                fastMove = PokemonData.getMove(fast.moveName);
                for (MoveElite charged : charMoves) {
                    if (!charged.isElite || includeElite) {
                        chargedMove = PokemonData.getMove(fast.moveName);
                        double curRating = effectiveRating(fastMove, chargedMove, otherPokemon);
                        if (bestRating < curRating) {
                            bestRating = curRating;
                            moveset[0] = fast.moveName;
                            moveset[1] = charged.moveName;
                            moveset[2] = Double.toString(bestRating);
                        }
                    }
                }
            }
        }
        return moveset;
    }

    /**
     * returns the best moveset against this Pokemon has against the defending Pokemon
     * @param atkType the damage typing the moveset is being assessed for
     * @param includeElite if true, include legacy moves, if false do not
     * @return a String array that contains the name of the fast and charged move
     */
    public String[] bestMoveset(Type atkType, boolean includeElite) {
        String[] moveset = new String[3];
        double bestRating = 0;
        Move fastMove, chargedMove;

        for (MoveElite fast : fastMoves) {
            if (!fast.isElite || includeElite) {
                fastMove = PokemonData.getMove(fast.moveName);
                for (MoveElite charged : charMoves) {
                    if (!charged.isElite || includeElite) {
                        chargedMove = PokemonData.getMove(charged.moveName);

                        if (chargedMove.elementType != atkType) {
                            continue;
                        }

                        double curRating = effectiveRating(fastMove, chargedMove, atkType);
                        if (bestRating < curRating) {
                            bestRating = curRating;
                            moveset[0] = fast.moveName;
                            moveset[1] = charged.moveName;
                            moveset[2] = Double.toString(bestRating);
                        }
                    }
                }
            }
        }
        return moveset;
    }

    /**
     * Prints out details about the given moveset against a certain pokemon
     * @param fast the name of the fast move
     * @param charged the name of the charged move
     * @param otherPokemon the name of the pokemon being attacked
     */
    public void movesetSummary(String fast, String charged, Pokemon otherPokemon) {
        double movesetER = effectiveRating(PokemonData.getMove(fast), PokemonData.getMove(charged), otherPokemon);
        double movesetDPS = comprehensiveDPS(PokemonData.getMove(fast), PokemonData.getMove(charged), otherPokemon);
        System.out.println(this.getNameForm() + " with: ");
        System.out.println("Fast: " + fast + ", Charged: " + charged + " (ER: " + movesetER + ", DPS: " + movesetDPS + ") against Pokemon: " + otherPokemon.getNameForm());
    }

    /**
     * Prints out details about the given moveset for a certain type
     * @param fast the name of the fast move
     * @param charged the name of the charged move
     * @param atkType the type the pokemon's moveset is being analyzed for
     */
    public void movesetSummary(String fast, String charged, Type atkType) {
        double movesetER = effectiveRating(PokemonData.getMove(fast), PokemonData.getMove(charged), atkType);
        double movesetDPS = comprehensiveDPS(PokemonData.getMove(fast), PokemonData.getMove(charged), atkType);
        System.out.println(this.getNameForm() + " with: ");
        System.out.println("Fast: " + fast + ", Charged: " + charged + " (ER: " + movesetER + ", DPS: " + movesetDPS + ") as a(n) " + atkType.toString() + " type attacker");
    }
}
