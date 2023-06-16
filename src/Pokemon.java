import java.util.*;

public class Pokemon {

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

    public String name;
    public String form;
    public List<MoveElite> charMoves;
    public List<MoveElite> fastMoves;
    public int stamina, defense, attack;
    public Type type[];
    public int level; // level of -1 indicates raid boss (which has CP Mult 1)
    public boolean isShadow, isMega;

    public Pokemon(String name) {
        if (name.equals("dummy")) {
            attack = 181;
            defense = 156;
            level = 40;
            type = null;
        }
        else {
            this.name = name;
            form = "";
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
        Pokemon raid = new Pokemon(name);
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
        Pokemon instance = new Pokemon(this.name);
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

    public double calcMoveDmg(Move move, Pokemon otherPokemon, boolean weatherBoost) {
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
        if (weatherBoost) {
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
     * Calculates simple dps when a pokemon is able to complete a damage cycle
     * (using fast moves until charged move is charged and using charged move can be considered one cycle)
     * (calculation is: (fast move dps * ratio of time fast move is being performed) + (charged move dps * ratio of time charged move is being performed)
     * @param fast Move representing the fast move to be used for DPS calculation
     * @param charged Move representing the charged move to be used for DPS calculation
     * @param otherPokemon Pokemon representing the enemy pokemon that is being attacked
     * @param weatherBoost whether weather boost multiplier should be applied
     * @return the cycle DPS of this pokemon with the given move set against the enemy pokemon
     */

    public double cycleDPS(Move fast, Move charged, Pokemon otherPokemon, boolean weatherBoost) {
        double fastDPS = calcMoveDmg(fast, otherPokemon, weatherBoost) / fast.duration;
        double fastEPS = fast.energyDelta / fast.duration;
        double charDPS = calcMoveDmg(charged, otherPokemon, weatherBoost) / charged.duration;
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
     * @param weatherBoost whether weather boost multiplier should be applied
     * @return the comprehensive DPS of this pokemon with the given move set against the enemy pokemon
     */

    public double comprehensiveDPS(Move fast, Move charged, Pokemon otherPokemon, boolean weatherBoost) {
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

        double simpleDPS = cycleDPS(fast, charged, otherPokemon, weatherBoost);
        double expectedEnergyWasted = 0.5 * fast.energyDelta + 0.5 * charged.energyDelta;
        double expectedEnemyDPS = (5 * enemyEffAtt) / (effectDef);
        double fastDPS = calcMoveDmg(fast, otherPokemon, weatherBoost) / fast.duration;
        double charDPS = calcMoveDmg(charged, otherPokemon, weatherBoost) / charged.duration;
        double fastEPS = fast.energyDelta / fast.duration;
        double charEPS = charged.energyDelta / charged.duration;
        if (charEPS == 0) {
            return charDPS;
        }
        return simpleDPS + (((charDPS - fastDPS) / (charEPS + fastEPS)) * (0.5 - (expectedEnergyWasted / stamina * getCPMult(level))) * expectedEnemyDPS);
    }

    public double effectiveRating(Move fast, Move charged, Pokemon otherPokemon, boolean weatherBoost) {
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

        double timeOnField = (stamina * getCPMult(level) * effectDef) / (5 * enemyEffAtt);
        System.out.println(timeOnField);
        return comprehensiveDPS(fast, charged, otherPokemon, weatherBoost) * Math.pow(timeOnField, 0.25);
    }

    public Move[] bestMoveset(Pokemon otherPokemon, boolean includeElite) {
        Move[] moveset = new Move[2];
        double bestRating = 0;
        for (MoveElite fast : fastMoves) {
            if (!fast.isElite || includeElite) {
                for (MoveElite charged : charMoves) {
                    if (!charged.isElite || includeElite) {
                        bestRating = effectiveRating(PokemonGoTool.moves.get(fast.moveName), charged.moveName, otherPokemon, false);
                    }
                }
            }
        }
        return moveset;
    }
}
