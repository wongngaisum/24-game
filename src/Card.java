import java.io.Serializable;

public class Card implements Serializable {
    private int rank;
    private int suit;

    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    @Override
    public boolean equals(Object card) {
        if (card == null)
            return false;
        else if (this.getClass() != card.getClass())
            return false;
        else if (this.rank == ((Card) card).rank)
            return true;
        else
            return false;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSuit() {
        return suit;
    }

    public void setSuit(int suit) {
        this.suit = suit;
    }
}
