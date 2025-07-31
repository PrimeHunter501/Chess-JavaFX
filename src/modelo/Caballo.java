package modelo;

import java.util.ArrayList;
import java.util.List;

public class Caballo extends Pieza {

    public Caballo(boolean esBlanca) {
        super(esBlanca);
    }

    @Override
    public List<Posicion> movimientosValidos(Tablero tablero, int x, int y) {
        List<Posicion> movimientos = new ArrayList<>();

        int[][] saltos = {
            {1, 2}, {2, 1}, {-1, 2}, {-2, 1},
            {1, -2}, {2, -1}, {-1, -2}, {-2, -1}
        };

        for (int[] s : saltos) {
            int nx = x + s[0];
            int ny = y + s[1];
            Posicion destino = new Posicion(nx, ny);
            if (!destino.estaDentroDelTablero()) continue;

            Pieza ocupante = tablero.obtenerPieza(nx, ny);
            if (ocupante == null || ocupante.esBlanca() != this.esBlanca()) {
                movimientos.add(destino);
            }
        }

        return movimientos;
    }
}
