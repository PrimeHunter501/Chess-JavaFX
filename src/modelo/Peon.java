package modelo;

import java.util.ArrayList;
import java.util.List;

public class Peon extends Pieza {
    
    public Peon(boolean esBlanca) {
        super(esBlanca);
    }

    @Override
    public List<Posicion> movimientosValidos(Tablero tablero, int x, int y) {
        List<Posicion> movimientos = new ArrayList<>();

        int direccion = esBlanca ? -1 : 1; // Blancas suben, negras bajan

        // Movimiento normal hacia adelante
        Posicion adelante = new Posicion(x, y + direccion);
        if (adelante.estaDentroDelTablero() && tablero.estaVacia(adelante)) {
            movimientos.add(adelante);

            // Movimiento doble desde la fila inicial
            boolean enFilaInicial = (esBlanca && y == 6) || (!esBlanca && y == 1);
            Posicion dobleAdelante = new Posicion(x, y + 2 * direccion);
            if (enFilaInicial && tablero.estaVacia(dobleAdelante)) {
                movimientos.add(dobleAdelante);
            }
        }

        // Captura en diagonal
        int[] dx = {-1, 1};  // izquierda y derecha
        for (int deltaX : dx) {
            int nx = x + deltaX;
            int ny = y + direccion;
            Posicion diagonal = new Posicion(nx, ny);
            if (diagonal.estaDentroDelTablero()) {
                Pieza oponente = tablero.obtenerPieza(nx, ny);
                if (oponente != null && oponente.esBlanca() != this.esBlanca()) {
                    movimientos.add(diagonal);
                }
            }
        }

        return movimientos;
    }

    public enum TipoPromocion {
        REINA,
        TORRE,
        ALFIL,
        CABALLO
    }

    public Pieza obtenerPiezaPromocionada(TipoPromocion tipo) {
        switch (tipo) {
            case REINA:
                return new Reina(this.esBlanca());
            case TORRE:
                return new Torre(this.esBlanca());
            case ALFIL:
                return new Alfil(this.esBlanca());
            case CABALLO:
                return new Caballo(this.esBlanca());
            default:
                // por seguridad, devolver reina
                return new Reina(this.esBlanca());
        }
    }

    
}
