import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RegularCard implements Card {

    private CardColor cardColor;
    private int number;

    public RegularCard(CardColor cardColor, int number) {
        this.cardColor = cardColor;
        this.number = number;
    }

    @Override
    public int getValue() {
        throw new NotImplementedException();
    }

    @Override
    public CardColor getColor() {
        return cardColor;
    }

    @Override
    public int getNumber() {
        return number;
    }
}
