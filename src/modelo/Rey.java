package modelo;

import java.util.ArrayList;
import java.util.List;

public class Rey extends Pieza {

    private boolean haMovido = false;

    public Rey(boolean esBlanca) {
        super(esBlanca);
    }

    @Override
    public List<Posicion> movimientosValidos(Tablero tablero, int x, int y) {
        List<Posicion> movimientos = new ArrayList<>();

        int[][] deltas = {
            {0, 1},   // arriba
            {0, -1},  // abajo
            {1, 0},   // derecha
            {-1, 0},  // izquierda
            {1, 1},   // arriba-derecha
            {1, -1},  // abajo-derecha
            {-1, 1},  // arriba-izquierda
            {-1, -1}  // abajo-izquierda
        };

        for (int[] d : deltas) {
            int nx = x + d[0];
            int ny = y + d[1];
            Posicion destino = new Posicion(nx, ny);
            if (!destino.estaDentroDelTablero()) continue;

            Pieza ocupante = tablero.obtenerPieza(nx, ny);
            if (ocupante == null || ocupante.esBlanca() != this.esBlanca()) {
                movimientos.add(destino);
            }
        }

        // Nota: el enroque y la verificación de si el rey quedaría en jaque se implementan después.

        return movimientos;
    }

    public boolean haMovido() {
        return haMovido;
    }

    public void marcarMovido() {
        this.haMovido = true;
    }
}
