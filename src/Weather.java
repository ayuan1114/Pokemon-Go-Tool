import java.util.*;

public enum Weather {
    SUNNY,
    CLEAR,
    PARTLY_CLOUDY,
    CLOUDY,
    RAIN,
    SNOW,
    WINDY,
    FOG;
    public List<Type> getBoostedTypes() {
        List<Type> toReturn = new ArrayList<Type>();
        switch (this) {
            case SUNNY:
            case CLEAR:
                toReturn.add(Type.GRASS);
                toReturn.add(Type.GROUND);
                toReturn.add(Type.FIRE);
                return toReturn;
            case RAIN:
                toReturn.add(Type.WATER);
                toReturn.add(Type.ELECTRIC);
                toReturn.add(Type.BUG);
                return toReturn;
            case PARTLY_CLOUDY:
                toReturn.add(Type.NORMAL);
                toReturn.add(Type.ROCK);
                return toReturn;
            case CLOUDY:
                toReturn.add(Type.FAIRY);
                toReturn.add(Type.FIGHT);
                toReturn.add(Type.POISON);
                return toReturn;
            case WINDY:
                toReturn.add(Type.DRAGON);
                toReturn.add(Type.FLYING);
                toReturn.add(Type.PSYCHIC);
                return toReturn;
            case SNOW:
                toReturn.add(Type.ICE);
                toReturn.add(Type.STEEL);
                return toReturn;
            case FOG:
                toReturn.add(Type.DARK);
                toReturn.add(Type.GHOST);
                return toReturn;
        }
        return null;
    }
    public boolean boosts(Type type) {
        switch (this) {
            case SUNNY:
            case CLEAR:
                if (type == Type.GRASS || type == Type.GROUND || type == Type.FIRE) {
                    return true;
                }
                return false;
            case RAIN:
                if (type == Type.WATER || type == Type.ELECTRIC || type == Type.BUG) {
                    return true;
                }
                return false;
            case PARTLY_CLOUDY:
                if (type == Type.NORMAL || type == Type.ROCK) {
                    return true;
                }
                return false;
            case CLOUDY:
                if (type == Type.FAIRY || type == Type.FIGHT || type == Type.POISON) {
                    return true;
                }
                return false;
            case WINDY:
                if (type == Type.DRAGON || type == Type.FLYING || type == Type.PSYCHIC) {
                    return true;
                }
                return false;
            case SNOW:
                if (type == Type.ICE || type == Type.STEEL) {
                    return true;
                }
                return false;
            case FOG:
                if (type == Type.DARK || type == Type.GHOST) {
                    return true;
                }
                return false;
        }
        return false;
    }
}
