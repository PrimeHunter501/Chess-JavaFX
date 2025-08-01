package modelo;

import java.util.ArrayList;
import java.util.List;

public class Torre extends Pieza {

    private boolean haMovido = false;



    public Torre(boolean esBlanca) {
        super(esBlanca);
    }

    @Override
    public List<Posicion> movimientosValidos(Tablero tablero, int x, int y) {
        List<Posicion> movimientos = new ArrayList<>();

        // Direcciones: arriba, abajo, izquierda, derecha
        int[][] direcciones = {
            {0, 1},   // arriba (y+)
            {0, -1},  // abajo (y-)
            {1, 0},   // derecha (x+)
            {-1, 0}   // izquierda (x-)
        };

        for (int[] dir : direcciones) {
            int dx = dir[0];
            int dy = dir[1];
            int nx = x + dx;
            int ny = y + dy;

            // Avanzar en esa dirección hasta tope o bloqueo
            while (true) {
                Posicion siguiente = new Posicion(nx, ny);
                if (!siguiente.estaDentroDelTablero()) break;

                Pieza ocupante = tablero.obtenerPieza(nx, ny);
                if (ocupante == null) {
                    movimientos.add(siguiente);
                } else {
                    // Si hay pieza, puede capturar si es del contrario
                    if (ocupante.esBlanca() != this.esBlanca()) {
                        movimientos.add(siguiente);
                    }
                    break; // se bloquea después de encontrar cualquier pieza
                }

                nx += dx;
                ny += dy;
            }
        }

        return movimientos;
    }

    public boolean haMovido() {
        return haMovido;
    }

    public void marcarMovido() {
        this.haMovido = true;
    }
}

