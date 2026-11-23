package co.blackjack.datos;

public class Carta {
    private final RangoCarta rango;
    private final Palo palo;

    public RangoCarta getRango() { return rango; }
    public Palo getPalo() { return palo; }

    public Carta(RangoCarta rango, Palo palo) {
        this.rango = rango;
        this.palo = palo;
    }
}

