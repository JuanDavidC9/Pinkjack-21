package co.blackjack.vista;

import co.blackjack.datos.Carta;
import co.blackjack.datos.Participante;
import co.blackjack.operaciones.Partida;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VentanaJuego extends JFrame {
    private static final int ANCHO = 1280;
    private static final int ALTO = 720;
    private static final int X_MAZO = 1100;
    private static final int Y_MAZO = 170;
    private static final int X_CARTAS = 420;
    private static final int Y_CRUPIER = 154;
    private static final int Y_JUGADOR = 457;
    private static final int ANCHO_CARTA = 102;
    private static final int ALTO_CARTA = 143;
    private static final Color TINTA = new Color(227, 235, 255);
    private static final Color ROSA = new Color(185, 55, 147);
    //private static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 24);
    private final Map<String, BufferedImage> imagenes = new HashMap<>();
    private Partida partida = new Partida();
    private PanelImagen panel;
    private Timer timerMovimiento;
    private boolean repartiendo = false;
    private Font fuenteBase;

    public VentanaJuego() {
        super("PinkJack 21");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        fuenteBase = cargarFuente();   // NUEVO (antes de mostrarInicio)
        mostrarInicio();
        pack();
        setLocationRelativeTo(null);
    }

    private BufferedImage leer(String ruta) {
        if (imagenes.containsKey(ruta)) return imagenes.get(ruta);
        URL url = getClass().getResource("/assets/" + ruta);
        if (url == null) return null;
        try {
            BufferedImage imagen = ImageIO.read(url);
            imagenes.put(ruta, imagen);
            return imagen;
        } catch (IOException error) {
            return null;
        }
    }

    private ImageIcon icono(String ruta, int ancho, int alto) {
        BufferedImage imagen = leer(ruta);
        return imagen == null ? null :
                new ImageIcon(imagen.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
    }

    // NUEVO
    private Font cargarFuente() {
        try (InputStream in = getClass().getResourceAsStream("/assets/fuentes/mifuente.ttf")) {
            if (in != null) {
                Font fuente = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuente);
                return fuente;
            }
        } catch (FontFormatException | IOException error) {
            // si falla, se usa la fuente por defecto
        }
        return new Font("SansSerif", Font.PLAIN, 12);
    }

    // NUEVO
    private Font fuente(boolean negrita, float tamano) {
        return fuenteBase.deriveFont(negrita ? Font.BOLD : Font.PLAIN, tamano);
    }

    private void nuevaPantalla(String fondo) {
        if (timerMovimiento != null) {
            timerMovimiento.stop();
            timerMovimiento = null;
        }
        panel = new PanelImagen(leer("pantallas/" + fondo));
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(ANCHO, ALTO));
        setContentPane(panel);
        validate();
        panel.revalidate();
        panel.repaint();
    }

    private JLabel etiqueta(String texto, int x, int y, int w, int h, int tamano, boolean negrita) {
        JLabel label = new JLabel(texto);
        label.setForeground(TINTA);
        label.setFont(fuente(negrita, tamano));
        label.setBounds(x, y, w, h);
        panel.add(label);
        return label;
    }

    private JLabel centrado(String texto, int x, int y, int w, int h, int tamano, Color color) {
        JLabel label = etiqueta(texto, x, y, w, h, tamano, true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(color);
        return label;
    }

    private JButton boton(String imagen, String textoAlternativo, int x, int y, int w, int h,
                          ActionListener accion) {
        JButton boton = new JButton(textoAlternativo);
        ImageIcon icon = icono("ui/" + imagen, w, h);
        if (icon != null) {
            boton.setIcon(icon);
            boton.setText("");
            boton.setBorderPainted(false);
            boton.setContentAreaFilled(false);
            boton.setFocusPainted(false);
        } else {
            boton.setFont(fuente(true, 22f));   // CAMBIO
        }
        boton.setBounds(x, y, w, h);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setToolTipText(textoAlternativo);
        boton.addActionListener(accion);
        panel.add(boton);
        return boton;
    }

    private void mostrarInicio() {
        repartiendo = false;
        nuevaPantalla("home.png");
        //centrado("Pink Jack", 270, 85, 740, 110, 88, ROSA);
        //centrado("Blackjack (21)", 260, 220, 760, 60, 29, TINTA);
        boton("btn_empezar.png", "Empezar", 520, 305, 360, 95, e -> {
            partida = new Partida();
            mostrarJuego();
        });
        boton("btn_creditos.png", "Creditos", 520, 420, 360, 95, e -> mostrarCreditos());

        boton("btn_reglas_inicio.png", "Reglas", 520, 535, 360, 95, e -> mostrarReglas());
    }

    private void mostrarCreditos() {
        repartiendo = false;
        nuevaPantalla("creditos.png");
        centrado("Creditos", 250, 90, 780, 90, 66, ROSA);
        centrado("Autores", 300, 225, 680, 55, 34, TINTA);
        centrado("Juan David Castaneda Alarcon", 300, 305, 680, 44, 29, TINTA);
        centrado("Shara Moreno Moreno", 300, 357, 680, 44, 29, TINTA);
        centrado("Asesor: Vinni", 300, 420, 680, 40, 23, TINTA);
        boton("btn_salir.png", "Volver", 520, 555, 240, 90, e -> mostrarInicio());
    }

    private void mostrarJuego() {
        mostrarJuego(Integer.MAX_VALUE, Integer.MAX_VALUE, partida.isRondaActiva(), false);
    }

    private void mostrarJuego(int visiblesJugador, int visiblesCrupier,
                              boolean ocultarSegunda, boolean bloqueada) {
        nuevaPantalla("juego.png");
        //centrado("PINK JACK - 21", 300, 15, 680, 74, 47, ROSA);
        if (!bloqueada) boton("btn_reglas.png", "Reglas", 1168, 25, 72, 72, e -> mostrarReglas());
        etiqueta("Crupier", 407, 100, 300, 44, 28, true);
        etiqueta("Jugador", 407, 389, 300, 44, 28, true);
        etiqueta("Saldo: " + partida.getSaldo() + " puntos", 90, 200, 355, 40, 28, true);
        JLabel instrucciones = etiqueta(bloqueada ? "Repartiendo cartas..." :
                mensajeDeMesa(), 25, 615, 710, 65, 19, false);
        instrucciones.setVerticalAlignment(SwingConstants.TOP);

        if (partida.getJugador() != null) {
            Participante jugador = partida.getJugador();
            Participante crupier = partida.getCrupier();
            etiqueta("Puntos: " + (bloqueada ? "..." : jugador.obtenerPuntuacion()),
                    1000, 389, 270, 44, 25, true);
            etiqueta("Puntos del crupier: " + (ocultarSegunda || bloqueada ? "?" :
                    crupier.obtenerPuntuacion()), 950, 100, 370, 44, 24, true);
            etiqueta("Apuesta actual: " + partida.getApuestaActual(), 90, 288, 350, 40, 28, true);
            mostrarCartas(crupier, X_CARTAS, Y_CRUPIER, ocultarSegunda, visiblesCrupier);
            mostrarCartas(jugador, X_CARTAS, Y_JUGADOR, false, visiblesJugador);
            JLabel mazo = new JLabel(icono("cartas/reverso.png", 80, 112));
            mazo.setToolTipText("Mazo de cartas");
            mazo.setBounds(X_MAZO + 11, Y_MAZO, 80, 112);
            panel.add(mazo);
        } else {
            etiqueta("La mesa esta lista para jugar", 428, 204, 650, 60, 30, true);
        }

        if (!bloqueada && partida.isRondaActiva()) {
            boton("btn_pedir.png", "Pedir", 785, 630, 195, 80, e -> ejecutarTurno(false));
            boton("btn_plantarse.png", "Plantarse", 990, 630, 245, 80,
                    e -> ejecutarTurno(true));
        } else if (!bloqueada && partida.puedeApostar()) {
            etiqueta(partida.getSaldo() < Partida.APUESTA_MINIMA
                    ? "Última apuesta:" : "Apuesta (múltiplos de 10):",
                    85, 330, 355, 40, 25, true);
            int inicial = Math.min(100, partida.getSaldo());
            int apuestaMinima = Math.min(Partida.APUESTA_MINIMA, partida.getSaldo());
            int incremento = partida.getSaldo() < Partida.APUESTA_MINIMA ? apuestaMinima : 10;
            JSpinner puntos = new JSpinner(new SpinnerNumberModel(inicial, apuestaMinima,
                    partida.getSaldo(), incremento));
            puntos.setFont(fuente(false, 24f));   // CAMBIO
            puntos.setBounds(95, 370, 210, 58);
            panel.add(puntos);
            boton("btn_apostar.png", "Apostar", 90, 445, 220, 90, e -> {
                if (repartiendo) return;
                try {
                    puntos.commitEdit();
                    partida.iniciarRonda(((Number) puntos.getValue()).intValue());
                    iniciarAnimacionInicial();
                } catch (Exception error) {
                    JOptionPane.showMessageDialog(this, error.getMessage(),
                            "Apuesta no valida", JOptionPane.WARNING_MESSAGE);
                }
            });
        } else if (!bloqueada && !partida.isRondaActiva()) {
            etiqueta("Sin puntos disponibles", 27, 360, 365, 80, 20, true);
        }
        if (!bloqueada) boton("btn_salir.png", "Salir", 1000, 28, 145, 65,
                e -> mostrarInicio());
        panel.repaint();
    }

    private String mensajeDeMesa() {

        if (partida.isRondaFinalizada()) return "";
        return partida.getMensaje();
    }

    private int pasoCartas(Participante participante) {
        return participante.getMano().cantidadCartas() > 7 ? 72 : 112;
    }

    private void mostrarCartas(Participante participante, int x, int y,
                              boolean ocultarSegunda, int visibles) {
        int indice = 0;
        for (Carta carta : participante.getMano().getCartas()) {
            if (indice >= visibles) break;
            String archivo = archivoCarta(carta, ocultarSegunda && indice == 1);
            agregarCarta(panel, archivo, x + indice * pasoCartas(participante), y);
            indice++;
        }
    }

    private String archivoCarta(Carta carta, boolean ocultar) {
        return ocultar ? "cartas/reverso.png" :
                "cartas/" + carta.getRango().name() + "_" + carta.getPalo().name() + ".png";
    }

    private void agregarCarta(PanelImagen destino, String ruta, int x, int y) {
        JLabel carta = crearEtiquetaCarta(ruta);
        carta.setBounds(x, y, ANCHO_CARTA, ALTO_CARTA);
        destino.add(carta);
        destino.setComponentZOrder(carta, 0);
        destino.repaint();
    }

    private JLabel crearEtiquetaCarta(String archivo) {
        ImageIcon icon = icono(archivo, ANCHO_CARTA, ALTO_CARTA);
        JLabel label = new JLabel(icon);
        if (icon == null) {
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setText("Carta");
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }
        return label;
    }

    private static final class Entrega {
        private final String archivo;
        private final int x;
        private final int y;
        private Entrega(String archivo, int x, int y) {
            this.archivo = archivo;
            this.x = x;
            this.y = y;
        }
    }

    private void iniciarAnimacionInicial() {
        repartiendo = true;
        mostrarJuego(0, 0, true, true);
        List<Entrega> entregas = new ArrayList<>();
        Participante jugador = partida.getJugador();
        Participante crupier = partida.getCrupier();

        for (int i = 0; i < 2; i++) {
            Carta cartaJugador = jugador.getMano().getCartas().get(i);
            Carta cartaCrupier = crupier.getMano().getCartas().get(i);
            entregas.add(new Entrega(archivoCarta(cartaJugador, false),
                    X_CARTAS + i * pasoCartas(jugador), Y_JUGADOR));
            entregas.add(new Entrega(archivoCarta(cartaCrupier, i == 1),
                    X_CARTAS + i * pasoCartas(crupier), Y_CRUPIER));
        }
        reproducirEntregas(entregas, 0, this::terminarAnimacion);
    }

    private void ejecutarTurno(boolean plantarse) {
        if (repartiendo) return;
        int antesJugador = partida.getJugador().getMano().cantidadCartas();
        int antesCrupier = partida.getCrupier().getMano().cantidadCartas();
        try {
            if (plantarse) partida.plantarse();
            else partida.pedirCarta();
            List<Entrega> entregas = new ArrayList<>();
            Participante jugador = partida.getJugador();
            Participante crupier = partida.getCrupier();
            for (int i = antesJugador; i < jugador.getMano().cantidadCartas(); i++) {
                entregas.add(new Entrega(archivoCarta(jugador.getMano().getCartas().get(i), false),
                        X_CARTAS + i * pasoCartas(jugador), Y_JUGADOR));
            }
            for (int i = antesCrupier; i < crupier.getMano().cantidadCartas(); i++) {
                entregas.add(new Entrega(archivoCarta(crupier.getMano().getCartas().get(i), false),
                        X_CARTAS + i * pasoCartas(crupier), Y_CRUPIER));
            }
            if (entregas.isEmpty()) {
                actualizarFinDeTurno();
            } else {
                repartiendo = true;
                mostrarJuego(antesJugador, antesCrupier, true, true);
                reproducirEntregas(entregas, 0, this::terminarAnimacion);
            }
        } catch (RuntimeException error) {
            JOptionPane.showMessageDialog(this, error.getMessage(),
                    "No se pudo continuar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reproducirEntregas(List<Entrega> entregas, int indice, Runnable alTerminar) {
        if (indice >= entregas.size()) {
            alTerminar.run();
            return;
        }
        Entrega entrega = entregas.get(indice);
        PanelImagen destino = panel;
        JLabel sprite = crearEtiquetaCarta(entrega.archivo);
        sprite.setBounds(X_MAZO, Y_MAZO, ANCHO_CARTA, ALTO_CARTA);
        destino.add(sprite);
        destino.setComponentZOrder(sprite, 0);
        destino.repaint();
        final long inicio = System.nanoTime();
        timerMovimiento = new Timer(16, null);
        timerMovimiento.addActionListener(e -> {
            if (panel != destino) {
                ((Timer) e.getSource()).stop();
                return;
            }
            double progreso = Math.min(1d, (System.nanoTime() - inicio) / 330_000_000d);
            double suavizado = 1d - Math.pow(1d - progreso, 3d);
            int x = X_MAZO + (int) Math.round((entrega.x - X_MAZO) * suavizado);
            int y = Y_MAZO + (int) Math.round((entrega.y - Y_MAZO) * suavizado);
            sprite.setLocation(x, y);
            if (progreso >= 1d) {
                ((Timer) e.getSource()).stop();
                sprite.setLocation(entrega.x, entrega.y);

                Timer pausa = new Timer(90, otro -> {
                    ((Timer) otro.getSource()).stop();
                    if (panel == destino) reproducirEntregas(entregas, indice + 1, alTerminar);
                });
                pausa.setRepeats(false);
                pausa.start();
            }
        });
        timerMovimiento.start();
    }

    private void terminarAnimacion() {
        repartiendo = false;
        actualizarFinDeTurno();
    }

    private void actualizarFinDeTurno() {

        if (partida.isRondaFinalizada() && partida.getSaldo() == 0) {
            mostrarDerrotaDefinitiva();
        } else {
            mostrarJuego();
        }
    }

    private void mostrarDerrotaDefinitiva() {
        repartiendo = false;
        nuevaPantalla("derrota.png");
        Color rojo = new Color(190, 50, 60);
        centrado("DERROTA", 260, 70, 760, 115, 90, rojo);
        centrado("Te has quedado sin puntos para continuar.", 190, 245, 900, 55, 28, TINTA);
        centrado("Jugador: " + partida.getJugador().obtenerPuntuacion()
                        + "    |    Crupier: " + partida.getCrupier().obtenerPuntuacion(),
                240, 325, 800, 48, 28, TINTA);
        centrado("Saldo disponible: 0 puntos", 240, 383, 800, 48, 26, TINTA);
        boton("btn_salir.png", "Salir", 520, 598, 240, 85, e -> mostrarInicio());
        panel.repaint();
    }

    private void mostrarReglas() {

        String reglas = "REGLAS DE PINKJACK\n\n"
                + "1. OBJETIVO\n"
                + "   Consigue una puntuación lo más cercana posible a 21 sin superarla y vence al crupier.\n\n"
                + "2. CARTAS\n"
                + "   Se utiliza una baraja de 52 cartas, sin comodines (Jokers). El AS sí está incluido.\n\n"
                + "3. VALOR DE LAS CARTAS\n"
                + "   Las cartas del 2 al 10 valen su número, J, Q y K valen 10 y el AS vale 1 u 11.\n\n"
                + "4. REPARTO INICIAL\n"
                + "   El jugador recibe dos cartas visibles. El crupier recibe dos, una visible y otra oculta.\n\n"
                + "5. PEDIR\n"
                + "   Puedes solicitar cartas adicionales mientras no superes 21 puntos.\n\n"
                + "6. PLANTARSE\n"
                + "   Conservas tu puntuación y terminas tu turno.\n\n"
                + "7. VICTORIA\n"
                + "   Si ganas una ronda normal, recuperas tu apuesta y recibes una ganancia igual a la apuesta (1:1).\n"
                + "   Si ganas con Blackjack natural, recuperas tu apuesta y recibes 1,5 veces la apuesta como ganancia (3:2).\n\n"
                + "8. EMPATE\n"
                + "   Si ambos terminan con la misma puntuación, recuperas la apuesta.\n\n"
                + "9. EXCESO DE PUNTOS\n"
                + "   Si superas 21 puntos, pierdes automáticamente la ronda.\n\n"
                + "10. FIN DEL JUEGO\n"
                + "    La derrota definitiva solo aparece cuando tu saldo llega a cero.";
        JTextArea contenido = new JTextArea(reglas);
        contenido.setFont(fuente(false, 19f));   // CAMBIO
        contenido.setEditable(false);
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(19, 25, 19, 25));
        contenido.setLineWrap(true);
        contenido.setWrapStyleWord(true);
        contenido.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setPreferredSize(new Dimension(820, 570));
        JOptionPane.showMessageDialog(this, scroll, "Reglas de PinkJack", JOptionPane.PLAIN_MESSAGE);
    }

    private static final class PanelImagen extends JPanel {
        private final BufferedImage fondo;
        private PanelImagen(BufferedImage fondo) { this.fondo = fondo; }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(242, 239, 246));
            g.fillRect(0, 0, getWidth(), getHeight());
            if (fondo != null) g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
