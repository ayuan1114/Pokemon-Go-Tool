package pokemon.database;
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

    public boolean canBeShadow;
    public String name;
    public String form;
    public List<MoveElite> charMoves;
    public List<MoveElite> fastMoves;
    public Stats baseStats;
    public Stats iv;
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
            this.baseStats = new Stats(181, 156, 100);
            this.iv = new Stats();
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
            this.baseStats = new Stats();
            this.iv = new Stats();
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
        this.baseStats = new Stats();
        this.iv = new Stats();
        type = new Type[2];
        this.isShadow = false;
        this.isMega = false;
        canBeShadow = false;
        ID = 0;
    }

    public String[] getFastMoves() {
        String[] toReturn = new String[fastMoves.size()];
        for (int a = 0; a < fastMoves.size(); a++) {
            toReturn[a] = fastMoves.get(a).moveName();
        }
        return toReturn;
    }

    public String[] getCharMoves() {
        String[] toReturn = new String[charMoves.size()];
        for (int a = 0; a < charMoves.size(); a++) {
            toReturn[a] = charMoves.get(a).moveName();
        }
        return toReturn;
    }

    public boolean isElite(String moveName) throws IllegalArgumentException {
        for (MoveElite move : fastMoves) {
            if (move.moveName().equalsIgnoreCase(moveName)) {
                return move.isElite();
            }
        }
        for (MoveElite move : charMoves) {
            if (move.moveName().equalsIgnoreCase(moveName)) {
                return move.isElite();
            }
        }
        throw new IllegalArgumentException("Pokemon " + this.name + " does not have the move " + moveName);
    }

    public int calcCP() {
        return (int) (((this.baseStats.att() + this.iv.att()) * 
            Math.sqrt(this.baseStats.def() + this.iv.def()) * 
            Math.sqrt(this.baseStats.hp() + this.iv.hp()) * 
            getCPMult(level) * getCPMult(level)) / 10);
    }

    public static String getNameForm(String nameTemp, String formTemp) {
        if (formTemp.equals("Normal") || formTemp.isEmpty()) {
            return nameTemp;
        }
        return nameTemp + " (" + formTemp + ")";
    }

    public String getNameForm() {
        String toReturn = name;
        if (!form.equals("Normal") && !form.isEmpty()) {
            toReturn += " (" + form + ")";
        }
        if (isShadow) {
            toReturn += " (Shadow)";
        }
        return toReturn;
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
                "IVs: (attack: " + this.iv.att() + ", defense: " + this.iv.def() + ", stamina: " + this.iv.hp() + ")";
    }

    public boolean equals(Pokemon otherPoke) {
        return this.iv.att() == otherPoke.iv.att() &&
                this.iv.def()  == otherPoke.iv.def() &&
                this.iv.hp() == otherPoke.iv.hp() &&
                this.ID == otherPoke.ID &&
                this.type[0] == otherPoke.type[0] &&
                this.type[1] == otherPoke.type[1] &&
                this.isShadow == otherPoke.isShadow;
    }

    public Pokemon asRaidBoss(int tier) {
        Pokemon raid = new Pokemon(this.name, this.ID);
        if (tier == 1) {
            raid.iv.stamina = 600 - this.baseStats.hp();
        }
        if (tier == 3) {
            raid.iv.stamina = 3600- this.baseStats.hp();
        }
        if (tier == 4) { // mega
            raid.iv.stamina = 9000- this.baseStats.hp();
        }
        if (tier == 5) {
            raid.iv.stamina = 15000- this.baseStats.hp();
        }
        if (tier == 6) {
            raid.iv.stamina = 22500- this.baseStats.hp();
        }
        raid.form = this.form;
        raid.baseStats = this.baseStats.copy();
        raid.iv.attack = 15;
        raid.iv.defense = 15;
        raid.charMoves = this.charMoves;
        raid.fastMoves = this.fastMoves;
        raid.type = this.type;
        raid.level = -1;
        return raid;
    }

    public Pokemon createInstance(Stats iv, double level, boolean isShadow) {
        Pokemon instance = new Pokemon(this.name, this.ID);
        if (level < 1 || level > 51) {
            instance.level = -1;
        }
        else {
            instance.level = (int) (level * 2);
        }
        
        instance.form = this.form;
        instance.charMoves = this.charMoves;
        instance.fastMoves = this.fastMoves;
        instance.isShadow = isShadow;
        instance.iv = iv.copy();
        instance.baseStats = this.baseStats.copy();
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
        double effectAtt = baseStats.att() + iv.att();
        double enemyEffDef = otherPokemon.baseStats.def() + otherPokemon.iv.def();
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
        if (PokemonData.weather.boosts(move.elementType)) {
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
        double modifier = 1.0;
        double effectAtt = this.baseStats.att() + this.iv.att();
        double enemyEffDef = (Pokemon.DUMMY.baseStats.def() + Pokemon.DUMMY.iv.def()) * getCPMult(Pokemon.DUMMY.level);
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
        if (PokemonData.weather.boosts(move.elementType)) {
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
        double effectDef = this.baseStats.def() + this.iv.def();
        double enemyEffAtt = otherPokemon.baseStats.att() + otherPokemon.iv.att();
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
        return simpleDPS + (((charDPS - fastDPS) / (charEPS + fastEPS)) * 
            (0.5 - (expectedEnergyWasted / ((this.baseStats.hp() + this.iv.hp()) * getCPMult(level)))) * expectedEnemyDPS);
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
        double effectDef = this.baseStats.def() + this.iv.def();
        double enemyEffAtt = (Pokemon.DUMMY.baseStats.att() + Pokemon.DUMMY.iv.att()) * getCPMult(Pokemon.DUMMY.level);
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
        return simpleDPS + (((charDPS - fastDPS) / (charEPS + fastEPS)) * 
            (0.5 - (expectedEnergyWasted / ((this.baseStats.hp() + this.iv.hp()) * getCPMult(level)))) * expectedEnemyDPS);
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
        double effectDef = this.baseStats.def() + this.iv.def();
        double enemyEffAtt = otherPokemon.baseStats.att() + otherPokemon.iv.att();
        if (isShadow) {
            effectDef = effectDef * (5.0 / 6.0);
        }
        if (otherPokemon.isShadow) {
            enemyEffAtt = enemyEffAtt * 1.2;
        }
        effectDef *= getCPMult(level);
        enemyEffAtt *= getCPMult(otherPokemon.level);

        double timeOnField = ((this.baseStats.hp() + this.iv.hp()) * getCPMult(level) * effectDef) / (6 * enemyEffAtt);
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
        double effectDef = this.baseStats.def() + this.iv.def();
        double enemyEffAtt = (Pokemon.DUMMY.baseStats.att() + Pokemon.DUMMY.iv.att()) * getCPMult(Pokemon.DUMMY.level);
        if (isShadow) {
            effectDef = effectDef * (5.0 / 6.0);
        }
        effectDef *= getCPMult(level);

        double timeOnField = ((this.baseStats.hp() + this.iv.hp()) * getCPMult(level) * effectDef) / (6 * enemyEffAtt);
        return comprehensiveDPS(fast, charged, atkType) * Math.pow(timeOnField, 0.25);
    }

    /**
     * returns the best moveset against this Pokemon has against the defending Pokemon
     * @param otherPokemon Pokemon being attacked
     * @return a String array that contains the name of the fast and charged move
     */
    public String[] bestMoveset(Pokemon otherPokemon) {
        String[] moveset = new String[3];
        double bestRating = 0;
        Move fastMove, chargedMove;

        for (MoveElite fast : fastMoves) {
            if (!fast.isElite() || PokemonData.includeElite) {
                fastMove = PokemonData.getMove(fast.moveName());
                for (MoveElite charged : charMoves) {
                    if (!charged.isElite() || PokemonData.includeElite) {
                        chargedMove = PokemonData.getMove(charged.moveName());
                        double curRating = effectiveRating(fastMove, chargedMove, otherPokemon);
                        if (bestRating < curRating) {
                            bestRating = curRating;
                            moveset[0] = fast.moveName();
                            moveset[1] = charged.moveName();
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
     * @return a String array that contains the name of the fast and charged move
     */
    public String[] bestMoveset(Type atkType) {
        String[] moveset = new String[3];
        double bestRating = 0;
        Move fastMove, chargedMove;

        for (MoveElite fast : fastMoves) {
            if (!fast.isElite() || PokemonData.includeElite) {
                fastMove = PokemonData.getMove(fast.moveName());
                for (MoveElite charged : charMoves) {
                    if (!charged.isElite() || PokemonData.includeElite) {
                        chargedMove = PokemonData.getMove(charged.moveName());

                        if (chargedMove.elementType != atkType) {
                            continue;
                        }

                        double curRating = effectiveRating(fastMove, chargedMove, atkType);
                        if (bestRating < curRating) {
                            bestRating = curRating;
                            moveset[0] = fast.moveName();
                            moveset[1] = charged.moveName();
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
