package modelo;

public class Posicion {
    public final int x;
    public final int y;

    public Posicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean estaDentroDelTablero() {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Posicion)) return false;
        Posicion p = (Posicion) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }
}

