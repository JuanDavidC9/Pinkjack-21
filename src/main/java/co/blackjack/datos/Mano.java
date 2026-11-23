package co.blackjack.datos;

import java.util.ArrayList;
import java.util.List;

public class Mano {
    private final List<Carta> cartas = new ArrayList<>();

    public List<Carta> getCartas() { return List.copyOf(cartas); }

    public boolean esBlackjackNatural() {
        return cartas.size() == 2 && calcularPuntuacion() == 21;
    }

    public void agregarCarta(Carta carta) {
        cartas.add(carta);
    }

    public int cantidadCartas() {
        return cartas.size();
    }

    public int calcularPuntuacion() {
        int total = 0;
        int ases = 0;
        for (Carta carta : cartas) {
            total += carta.getRango().getValor();
            if (carta.getRango() == RangoCarta.AS) {
                ases++;
            }
        }

        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }
        return total;
    }

    public boolean supera21() {
        return calcularPuntuacion() > 21;
    }
}
