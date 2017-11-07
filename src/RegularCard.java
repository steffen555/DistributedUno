import java.math.BigInteger;

public class RegularCard extends Card {

    private int number;

    public RegularCard(CardColor color, int number) {
        super(color);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String toString() {
        String color = colorsAsString.get(getColor().ordinal());
        return color + " " + getNumber();
    }
}
