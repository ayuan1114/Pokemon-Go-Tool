import java.util.Hashtable;

enum Type {
    BUG,
    DARK,
    DRAGON,
    ELECTRIC,
    FAIRY,
    FIGHTING,
    FIRE,
    FLYING,
    GHOST,
    GRASS,
    GROUND,
    ICE,
    NORMAL,
    POISON,
    PSYCHIC,
    ROCK,
    STEEL,
    WATER;
    Hashtable<Type, Double> multiplier = new Hashtable<Type, Double>();
    public static Type parseType(String typeString) throws IllegalArgumentException {
        typeString = typeString.toUpperCase();
        switch (typeString) {
            case "BUG":
                return Type.BUG;
            case "DARK":
                return Type.DARK;
            case "DRAGON":
                return Type.DRAGON;
            case "ELECTRIC":
                return Type.ELECTRIC;
            case "FAIRY":
                return Type.FAIRY;
            case "FIGHT":
            case "FIGHTING":
                return Type.FIGHTING;
            case "FIRE":
                return Type.FIRE;
            case "FLYING":
                return Type.FLYING;
            case "GHOST":
                return Type.GHOST;
            case "GRASS":
                return Type.GRASS;
            case "GROUND":
                return Type.GROUND;
            case "ICE":
                return Type.ICE;
            case "NORMAL":
                return Type.NORMAL;
            case "POISON":
                return Type.POISON;
            case "PSYCHIC":
                return Type.PSYCHIC;
            case "ROCK":
                return Type.ROCK;
            case "STEEL":
                return Type.STEEL;
            case "WATER":
                return Type.WATER;
        }
        throw new IllegalArgumentException("No type recognized from input");
    }
    public double dmgMult(Type otherType) {
        return multiplier.get(otherType);
    }
}
