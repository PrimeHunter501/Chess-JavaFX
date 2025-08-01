package modelo;

import java.util.List;

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

    public void moverPieza(int fromX, int fromY, int toX, int toY) {
        Pieza p = obtenerPieza(fromX, fromY);
        tablero[toX][toY] = p;
        tablero[fromX][fromY] = null;
    }

    /**
     * Encuentra la posición del rey del color dado. Devuelve null si no se encuentra.
     */
    public Posicion obtenerPosicionRey(boolean esBlanca) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Pieza p = obtenerPieza(x, y);
                if (p instanceof Rey && p.esBlanca() == esBlanca) {
                    return new Posicion(x, y);
                }
            }
        }
        return null;
    }

    /**
     * Determina si el rey del color dado está en jaque.
     */
    public boolean estaEnJaque(boolean reyEsBlanca) {
        Posicion posRey = obtenerPosicionRey(reyEsBlanca);
        if (posRey == null) {
            // Opcional: lanzar excepción o considerar que no hay rey (estado inválido)
            return false;
        }

        // Recorremos todas las piezas enemigas y vemos si alguna puede atacar la casilla del rey
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Pieza p = obtenerPieza(x, y);
                if (p != null && p.esBlanca() != reyEsBlanca) {
                    List<Posicion> ataques = p.movimientosValidos(this, x, y);
                    for (Posicion atacando : ataques) {
                        if (atacando.equals(posRey)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Tablero copiar() {
        Tablero copia = new Tablero();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Pieza p = this.obtenerPieza(x, y);
                if (p != null) {
                    // Asume que las piezas no tienen estado mutable complejo; se puede clonar por tipo básico.
                    if (p instanceof Rey) copia.colocarPieza(new Rey(p.esBlanca()), x, y);
                    else if (p instanceof Reina) copia.colocarPieza(new Reina(p.esBlanca()), x, y);
                    else if (p instanceof Torre) copia.colocarPieza(new Torre(p.esBlanca()), x, y);
                    else if (p instanceof Alfil) copia.colocarPieza(new Alfil(p.esBlanca()), x, y);
                    else if (p instanceof Caballo) copia.colocarPieza(new Caballo(p.esBlanca()), x, y);
                    else if (p instanceof Peon) copia.colocarPieza(new Peon(p.esBlanca()), x, y);
                    else {
                        // por si hay otras piezas futuras
                        copia.colocarPieza(p, x, y);
                    }
                }
            }
        }
        return copia;
    }

    public void aplicarMovimiento(Movimiento m) {
        Pieza p = obtenerPieza(m.fromX, m.fromY);
        tablero[m.toX][m.toY] = p;
        tablero[m.fromX][m.fromY] = null;
    }
    public boolean estaAtacada(int x, int y, boolean porBlancas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = obtenerPieza(i, j);
                if (p != null && p.esBlanca() == porBlancas) {
                    List<Posicion> ataques = p.movimientosValidos(this, i, j);
                    for (Posicion pos : ataques) {
                        if (pos.x == x && pos.y == y) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}

