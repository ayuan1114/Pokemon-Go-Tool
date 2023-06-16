public class Move {
    public boolean isLegacy;
    public boolean isFast;
    public String name;
    public int power;
    public double duration;
    public int energyDelta;
    public Type elementType;
    public Move (String name, int power, double duration, int energyDelta, Type elementType, boolean isFast) {
        this.name = name;
        this.power = power;
        this.duration = duration / 1000;
        this.energyDelta = Math.abs(energyDelta);
        this.elementType = elementType;
        this.isFast = isFast;
    }
    public Move() {
        this.name = "";
        this.power = -1;
        this.duration = -1.0;
        this.energyDelta = -1;
        this.elementType = null;
        this.isFast = false;
    }
    public boolean equals(Move otherType) {
        return name.equals(otherType.name);
    }
    public String toString() {
        if (isFast) {
            return "Fast Move: " + name + " (Type: " + elementType.toString() + ", Power: "
                    + power + ", Charges " + energyDelta + " energy)";
        }
        else {
            return "Charged Move: "+ name + " (Type: " + elementType.toString() + ", Power: "
                    + power + ", Uses " + energyDelta + " energy)";
        }
    }
}
