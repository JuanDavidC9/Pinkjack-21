package co.blackjack.operaciones;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PartidaTest {
    @Test
    void tieneSaldoInicial() {
        assertEquals(1000, new Partida().getSaldo());
    }

    @Test
    void rechazaApuestasQueSuperanSaldo() {
        Partida partida = new Partida();
        assertThrows(IllegalArgumentException.class, () -> partida.iniciarRonda(1100));
        assertThrows(IllegalArgumentException.class, () -> partida.iniciarRonda(13));
    }

    @Test
    void reparteDosCartasYDescuentaApuesta() {
        Partida partida = new Partida();
        partida.iniciarRonda(100);
        assertEquals(900, partida.getSaldo());
        assertEquals(2, partida.getJugador().getMano().cantidadCartas());
        assertEquals(2, partida.getCrupier().getMano().cantidadCartas());
        assertEquals(48, partida.getMazo().cantidadCartas());
    }

    @Test
    void permiteApostarUltimosCincoPuntosParaNoBloquearElJuego() throws Exception {
        Partida partida = new Partida();
        java.lang.reflect.Field saldo = Partida.class.getDeclaredField("saldo");
        saldo.setAccessible(true);
        saldo.setInt(partida, 5);
        assertTrue(partida.puedeApostar());
        partida.iniciarRonda(5);
        assertEquals(0, partida.getSaldo());
        assertEquals(5, partida.getApuestaActual());
    }

    @Test
    void terminaRondaYCalculaResultado() {
        Partida partida = new Partida();
        partida.iniciarRonda(100);
        if (partida.isRondaActiva()) partida.plantarse();
        assertTrue(partida.isRondaFinalizada());
        assertNotNull(partida.getResultado());
        assertTrue(partida.getSaldo() >= 0);
    }
}
