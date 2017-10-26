import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RegularCard extends Card {

    private Card.Color cardColor;
    private int number;

    public RegularCard(int i) {
        super(i);
    }

    @Override
    public int getValue() {
        throw new NotImplementedException();
    }

    @Override
    public Card.Color getColor() {
        return cardColor;
    }

    @Override
    public int asInt() {
        return number;
    }
}
