package modelo;

public class Tablero {
    private Pieza[][] tablero;

    public Tablero() {
        tablero = new Pieza[8][8];
        // Inicializar si quieres piezas en posiciones concretas
    }

    public Pieza obtenerPieza(int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) return null;
        return tablero[x][y];
    }

    public boolean estaVacia(Posicion pos) {
        return obtenerPieza(pos.x, pos.y) == null;
    }

    public void colocarPieza(Pieza pieza, int x, int y) {
        tablero[x][y] = pieza;
    }
}

