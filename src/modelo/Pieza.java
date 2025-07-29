package modelo;

import java.util.List;


public abstract class Pieza {
    protected boolean esBlanca;

    public Pieza(boolean esBlanca) {
        this.esBlanca = esBlanca;
    }

    public boolean esBlanca() {
        return esBlanca;
    }

    public abstract List<Posicion> movimientosValidos(Tablero tablero, int x, int y);
}

