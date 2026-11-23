package co.blackjack.datos;

public abstract class Participante {
    private final String nombre;
    private final Mano mano;

    public String getNombre() { return nombre; }
    public Mano getMano() { return mano; }

    public Participante(String nombre) {
        this.nombre = nombre;
        this.mano = new Mano();
    }

    public void pedirCarta(Mazo mazo) {
        mano.agregarCarta(mazo.repartirCarta());
    }

    public int obtenerPuntuacion() {
        return mano.calcularPuntuacion();
    }

    public abstract boolean debePedirCarta();
}
