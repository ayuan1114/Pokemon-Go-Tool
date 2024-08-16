package pokemon.server;

import pokemon.database.Move;
import pokemon.database.Type;

public record MoveRecord(
    
    String name,
    Type elementType,
    int power,
    double duration,
    int energyDelta,
    boolean isLegacy,
    boolean isFast) {
    public MoveRecord(Move move) {
        this(
            move.name,
            move.elementType,
            move.power,
            move.duration,
            move.energyDelta,
            move.isLegacy, 
            move.isFast);
    }
}
