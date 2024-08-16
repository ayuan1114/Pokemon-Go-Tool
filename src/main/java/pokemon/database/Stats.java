package pokemon.database;

public class Stats {
    public int attack, defense, stamina;
    public Stats() {
        attack = 0;
        defense = 0;
        stamina = 0;
    }

    public Stats(int attack, int defense, int stamina) {
        this.attack = attack;
        this.defense = defense;
        this.stamina = stamina;
    }

    public int att() {
        return attack;
    }
    public int def() {
        return defense;
    }
    public int hp() {
        return stamina;
    }
    public Stats copy() {
        return new Stats(attack, defense, stamina);
    }
}
