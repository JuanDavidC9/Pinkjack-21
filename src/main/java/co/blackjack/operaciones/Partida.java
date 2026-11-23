package co.blackjack.operaciones;

import co.blackjack.datos.Crupier;
import co.blackjack.datos.Jugador;
import co.blackjack.datos.Mazo;
import co.blackjack.datos.ResultadoRonda;

public class Partida {
    public static final int SALDO_INICIAL = 1000;
    public static final int APUESTA_MINIMA = 10;

    private int saldo = SALDO_INICIAL;
    private int apuestaActual;
    private Jugador jugador;
    private Crupier crupier;
    private Mazo mazo;
    private Ronda ronda;
    private ResultadoRonda resultado;
    private boolean rondaActiva;
    private boolean rondaFinalizada;
    private String mensaje = "Selecciona tus puntos para iniciar una ronda.";

    public void iniciarRonda(int apuesta) {
        if (rondaActiva) {
            throw new IllegalStateException("Termina la ronda actual antes de comenzar otra.");
        }

        boolean ultimaApuesta = saldo > 0 && saldo < APUESTA_MINIMA && apuesta == saldo;
        boolean apuestaNormal = apuesta >= APUESTA_MINIMA && apuesta % APUESTA_MINIMA == 0;
        if ((!apuestaNormal && !ultimaApuesta) || apuesta > saldo) {
            throw new IllegalArgumentException(
                    "Apuesta múltiplos de 10 (o todo tu saldo si te quedan menos de 10 puntos).");
        }
        apuestaActual = apuesta;
        saldo -= apuesta;
        jugador = new Jugador("Jugador");
        crupier = new Crupier();
        mazo = new Mazo();
        mazo.barajar();
        ronda = new Ronda(jugador, crupier, mazo);
        resultado = null;
        rondaActiva = true;
        rondaFinalizada = false;
        ronda.repartirCartasIniciales();
        mensaje = "Tu turno: pide una carta o plantate.";

        if (jugador.getMano().esBlackjackNatural() || crupier.getMano().esBlackjackNatural()) {
            finalizarRonda();
        }
    }

    public void pedirCarta() {
        comprobarTurno();
        ronda.jugadorPideCarta();
        if (jugador.getMano().supera21()) {
            finalizarRonda(); 
        } else if (jugador.obtenerPuntuacion() == 21) {
            ronda.jugadorSePlanta();
            finalizarRonda();
        } else {
            mensaje = "Ahora tienes " + jugador.obtenerPuntuacion() + " puntos.";
        }
    }

    public void plantarse() {
        comprobarTurno();
        ronda.jugadorSePlanta();
        finalizarRonda();
    }

    private void comprobarTurno() {
        if (!rondaActiva) {
            throw new IllegalStateException("No existe una ronda activa.");
        }
    }

    private void finalizarRonda() {
        if (!jugador.getMano().supera21()
                && !jugador.getMano().esBlackjackNatural()
                && !crupier.getMano().esBlackjackNatural()) {
            ronda.turnoCrupier();
        }
        resultado = ronda.determinarResultado();
        boolean blackjackGanador = resultado == ResultadoRonda.GANADA
                && jugador.getMano().esBlackjackNatural();
        if (resultado == ResultadoRonda.GANADA) {
            saldo += blackjackGanador ? apuestaActual * 5 / 2 : apuestaActual * 2;
            mensaje = blackjackGanador ? "¡Blackjack natural! Premio 3:2." : "¡Ganaste la ronda!";
        } else if (resultado == ResultadoRonda.EMPATE) {
            saldo += apuestaActual;
            mensaje = "Empate: recuperas los puntos de tu apuesta.";
        } else {
            mensaje = jugador.getMano().supera21()
                    ? "Superaste los 21 puntos. Pierdes esta ronda."
                    : "El crupier gana esta ronda.";
        }
        rondaActiva = false;
        rondaFinalizada = true;
    }

    public int getSaldo() { return saldo; }
    public int getApuestaActual() { return apuestaActual; }
    public Jugador getJugador() { return jugador; }
    public Crupier getCrupier() { return crupier; }
    public Mazo getMazo() { return mazo; }
    public ResultadoRonda getResultado() { return resultado; }
    public boolean isRondaActiva() { return rondaActiva; }
    public boolean isRondaFinalizada() { return rondaFinalizada; }
    public String getMensaje() { return mensaje; }
    public boolean puedeApostar() { return saldo > 0; }
}
