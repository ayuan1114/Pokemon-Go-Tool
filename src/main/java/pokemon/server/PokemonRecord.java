package pokemon.server;

import java.util.Arrays;
import java.util.List;

import pokemon.database.MoveElite;
import pokemon.database.Pokemon;
import pokemon.database.Stats;
import pokemon.database.Type;

public record PokemonRecord(
    String name,
    String species,
    int CP,
    List<Type> type,
    List<MoveElite> fastMoves,
    List<MoveElite> charMoves,
    Stats iv,
    Stats baseStats,
    double level,
    boolean isShadow, 
    boolean isMega,
    int ID
) {
    public PokemonRecord(Pokemon pokemon) {
        this(
            pokemon.getNameForm(), 
            pokemon.getNameForm(),
            -1,
            (pokemon.type[1] == null) ? Arrays.asList(pokemon.type[0]) : Arrays.asList(pokemon.type),
            pokemon.fastMoves,
            pokemon.charMoves,
            pokemon.iv,
            pokemon.baseStats,
            (pokemon.level == -1) ? -1 : (double) pokemon.level / 2,
            pokemon.isShadow,
            pokemon.isMega,
            pokemon.ID
        );
    }

    public PokemonRecord(Pokemon pokemon, String name) {
        this(
            name,
            pokemon.getNameForm(),
            pokemon.calcCP(),
            (pokemon.type[1] == null) ? Arrays.asList(pokemon.type[0]) : Arrays.asList(pokemon.type),
            pokemon.fastMoves,
            pokemon.charMoves,
            pokemon.iv,
            pokemon.baseStats,
            (pokemon.level == -1) ? -1 : (double) pokemon.level / 2,
            pokemon.isShadow,
            pokemon.isMega,
            pokemon.ID
        );
    }

    public PokemonRecord(Pokemon pokemon, int rank, MoveElite fast, MoveElite charged) {
        this (
            "#" + Integer.toString(rank) + " Counter", 
            pokemon.getNameForm(),
            pokemon.calcCP(),
            (pokemon.type[1] == null) ? Arrays.asList(pokemon.type[0]) : Arrays.asList(pokemon.type),
            Arrays.asList(fast),
            Arrays.asList(charged),
            pokemon.iv,
            pokemon.baseStats,
            (pokemon.level == -1) ? -1 : (double) pokemon.level / 2,
            pokemon.isShadow,
            pokemon.isMega,
            pokemon.ID
        );
    }
}
