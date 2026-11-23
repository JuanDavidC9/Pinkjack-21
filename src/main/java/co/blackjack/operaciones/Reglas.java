package co.blackjack.operaciones;

import co.blackjack.datos.Crupier;
import co.blackjack.datos.Jugador;
import co.blackjack.datos.ResultadoRonda;

public class Reglas {

    public ResultadoRonda determinarResultado(Jugador jugador, Crupier crupier) {
        if (jugador.getMano().supera21()) {
            return ResultadoRonda.PERDIDA;
        }
        if (crupier.getMano().supera21()) {
            return ResultadoRonda.GANADA;
        }

        boolean naturalJugador = jugador.getMano().esBlackjackNatural();
        boolean naturalCrupier = crupier.getMano().esBlackjackNatural();
        if (naturalJugador && !naturalCrupier) return ResultadoRonda.GANADA;
        if (naturalCrupier && !naturalJugador) return ResultadoRonda.PERDIDA;

        int puntosJugador = jugador.obtenerPuntuacion();
        int puntosCrupier = crupier.obtenerPuntuacion();

        if (puntosJugador > puntosCrupier) {
            return ResultadoRonda.GANADA;
        }
        if (puntosJugador < puntosCrupier) {
            return ResultadoRonda.PERDIDA;
        }
        return ResultadoRonda.EMPATE;
    }
}
