package modelo;

import java.util.ArrayList;
import java.util.List;

public class Juego {
    private Tablero tablero;
    private boolean turnoBlanco; // true = blanco, false = negro

    public Juego() {
        tablero = new Tablero();
        turnoBlanco = true;
        // aquí podrías inicializar la posición inicial si quieres
    }

    public boolean esTurnoBlanco() {
        return turnoBlanco;
    }

    public void cambiarTurno() {
        turnoBlanco = !turnoBlanco;
    }

    /**
     * Genera todos los movimientos legales (i.e., que no dejan al propio rey en jaque)
     * para el color dado.
     */
    public List<Movimiento> movimientosLegales(boolean paraBlancas) {
        List<Movimiento> legales = new ArrayList<>();

        // Recorre todas las piezas del color
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Pieza p = tablero.obtenerPieza(x, y);
                if (p != null && p.esBlanca() == paraBlancas) {
                    List<Posicion> candidatos = p.movimientosValidos(tablero, x, y);
                    for (Posicion destino : candidatos) {
                        Movimiento m = new Movimiento(x, y, destino.x, destino.y);
                        // Simular
                        Tablero copia = tablero.copiar();
                        copia.aplicarMovimiento(m);
                        // Si el rey del color sigue seguro, el movimiento es legal
                        if (!copia.estaEnJaque(paraBlancas)) {
                            legales.add(m);
                        }
                    }
                }
            }
        }

        return legales;
    }

    /**
     * Ejecuta un movimiento si es legal y cambia el turno.
     * Devuelve true si se pudo hacer.
     */
    public boolean hacerMovimiento(Movimiento m) {
        Pieza p = tablero.obtenerPieza(m.fromX, m.fromY);
        if (p == null) return false;
        if (p.esBlanca() != turnoBlanco) return false;

        List<Movimiento> legales = movimientosLegales(turnoBlanco);
        boolean permitido = false;
        for (Movimiento mv : legales) {
            if (mv.fromX == m.fromX && mv.fromY == m.fromY && mv.toX == m.toX && mv.toY == m.toY) {
                permitido = true;
                break;
            }
        }
        if (!permitido) return false;

        tablero.aplicarMovimiento(m);
        cambiarTurno();
        return true;
    }

    /**
     * Determina si el jugador del color dado está en jaque mate.
     */
    public boolean esJaqueMate(boolean blancas) {
        // Está en jaque y no tiene movimientos legales
        if (!tablero.estaEnJaque(blancas)) return false;
        List<Movimiento> legales = movimientosLegales(blancas);
        return legales.isEmpty();
    }

    /**
     * Determina si es ahogado (stalemate): no está en jaque pero no tiene movimientos legales.
     */
    public boolean esAhogado(boolean blancas) {
        if (tablero.estaEnJaque(blancas)) return false;
        return movimientosLegales(blancas).isEmpty();
    }

    public Tablero getTablero() {
        return tablero;
    }
}
