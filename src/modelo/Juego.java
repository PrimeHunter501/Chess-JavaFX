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
     * Ejecuta un movimiento si es legal y cambia el turno.
     * Devuelve true si se pudo hacer.
     */
    public boolean hacerMovimiento(Movimiento m, TipoPromocion opcionPromocion) {
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

    // Aplicar movimiento normal
    tablero.aplicarMovimiento(m);

    // Promoción si fue un peón y llegó al extremo
    Pieza destino = tablero.obtenerPieza(m.toX, m.toY);
    if (destino instanceof Peon) {
        Peon peon = (Peon) destino;
        if (peon.debePromocionar(m.toY)) {
            // Se asume que el UI/modelo ya decidió opcionPromocion; si no hay, usar reina por defecto
            TipoPromocion tipo = opcionPromocion != null ? opcionPromocion : TipoPromocion.REINA;
            Pieza promocionada = peon.obtenerPiezaPromocionada(tipo);
            tablero.colocarPieza(promocionada, m.toX, m.toY);
        }
    }

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

    public boolean puedeEnrocarCorto(boolean blancas) {
        Tablero t = tablero;
        Posicion reyPos = t.obtenerPosicionRey(blancas);
        if (reyPos == null) return false;

        Rey rey = (Rey) t.obtenerPieza(reyPos.x, reyPos.y);
        if (rey.haMovido()) return false;
        int y = reyPos.y;
        // Torre en el lado rey: suposición de coordenadas estándar
        Pieza piezaTorre = t.obtenerPieza(7, y);
        if (!(piezaTorre instanceof Torre) || piezaTorre.esBlanca() != blancas) return false;
        Torre torre = (Torre) piezaTorre;
        if (torre.haMovido()) return false;

        // Verificar espacios intermedios vacíos
        if (!t.estaVacia(new Posicion(5, y)) || !t.estaVacia(new Posicion(6, y))) return false;

        // Rey no está en jaque ni pasa por casillas atacadas
        if (t.estaEnJaque(blancas)) return false;
        if (t.estaAtacada(5, y, !blancas) || t.estaAtacada(6, y, !blancas)) return false;

        return true;
    }

    public boolean puedeEnrocarLargo(boolean blancas) {
        Tablero t = tablero;
        Posicion reyPos = t.obtenerPosicionRey(blancas);
        if (reyPos == null) return false;

        Rey rey = (Rey) t.obtenerPieza(reyPos.x, reyPos.y);
        if (rey.haMovido()) return false;
        int y = reyPos.y;
        Pieza piezaTorre = t.obtenerPieza(0, y);
        if (!(piezaTorre instanceof Torre) || piezaTorre.esBlanca() != blancas) return false;
        Torre torre = (Torre) piezaTorre;
        if (torre.haMovido()) return false;

        // Verificar espacios intermedios vacíos
        if (!t.estaVacia(new Posicion(1, y)) || !t.estaVacia(new Posicion(2, y)) || !t.estaVacia(new Posicion(3, y))) return false;

        if (t.estaEnJaque(blancas)) return false;
        if (t.estaAtacada(3, y, !blancas) || t.estaAtacada(2, y, !blancas)) return false;

        return true;
    }

}
