package pokemon.database;

public record MoveElite(String moveName, boolean isElite) {
    @Override
    public String toString() {
        return isElite ? moveName + "*" : moveName;
    }
}