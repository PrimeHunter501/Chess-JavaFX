package modelo;

import java.util.ArrayList;
import java.util.List;

public class Reina extends Pieza {

    public Reina(boolean esBlanca) {
        super(esBlanca);
    }

    @Override
    public List<Posicion> movimientosValidos(Tablero tablero, int x, int y) {
        List<Posicion> movimientos = new ArrayList<>();

        // Todas las direcciones: vertical, horizontal y diagonales
        int[][] direcciones = {
            {0, 1},   // arriba
            {0, -1},  // abajo
            {1, 0},   // derecha
            {-1, 0},  // izquierda
            {1, 1},   // arriba-derecha
            {1, -1},  // abajo-derecha
            {-1, 1},  // arriba-izquierda
            {-1, -1}  // abajo-izquierda
        };

        for (int[] dir : direcciones) {
            int dx = dir[0];
            int dy = dir[1];
            int nx = x + dx;
            int ny = y + dy;

            while (true) {
                Posicion siguiente = new Posicion(nx, ny);
                if (!siguiente.estaDentroDelTablero()) break;

                Pieza ocupante = tablero.obtenerPieza(nx, ny);
                if (ocupante == null) {
                    movimientos.add(siguiente);
                } else {
                    if (ocupante.esBlanca() != this.esBlanca()) {
                        movimientos.add(siguiente);
                    }
                    break; // se bloquea tras encontrar cualquier pieza
                }

                nx += dx;
                ny += dy;
            }
        }

        return movimientos;
    }
}
