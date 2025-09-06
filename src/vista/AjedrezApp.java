package vista;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import modelo.*;

import java.util.List;

public class AjedrezApp extends Application {

    private static final int TAM_CASILLA = 80;
    private Juego juego;
    private StackPane[][] casillas = new StackPane[8][8];
    private Label estadoLabel = new Label();

    private Posicion seleccionada = null;

    @Override
    public void start(Stage stage) {
        juego = new Juego();
        inicializarPosicionInicial(); // puedes crear una función que coloque piezas iniciales

        BorderPane root = new BorderPane();
        GridPane tableroUI = new GridPane();
        tableroUI.setAlignment(Pos.CENTER);

        // Crear 8x8
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                StackPane celda = crearCelda(x, y);
                casillas[x][y] = celda;
                tableroUI.add(celda, x, y);
            }
        }

        estadoLabel.setFont(Font.font(16));
        actualizarEstado();

        root.setCenter(tableroUI);
        root.setBottom(estadoLabel);
        BorderPane.setAlignment(estadoLabel, Pos.CENTER);

        actualizarVista();

        Scene scene = new Scene(root);
        stage.setTitle("Ajedrez - JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    private StackPane crearCelda(int x, int y) {
        Rectangle fondo = new Rectangle(TAM_CASILLA, TAM_CASILLA);
        boolean claro = (x + y) % 2 == 0;
        fondo.setFill(claro ? Color.BEIGE : Color.SADDLEBROWN);

        StackPane stack = new StackPane();
        stack.getChildren().add(fondo);
        stack.setOnMouseClicked(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            manejarClick(x, y);
        });
        stack.setPrefSize(TAM_CASILLA, TAM_CASILLA);
        return stack;
    }

    private void manejarClick(int x, int y) {
        Tablero tablero = juego.getTablero();
        Pieza pieza = tablero.obtenerPieza(x, y);
        boolean turnoBlanco = juego.esTurnoBlanco();

        if (seleccionada == null) {
            // seleccionar si hay pieza del turno
            if (pieza != null && pieza.esBlanca() == turnoBlanco) {
                seleccionada = new Posicion(x, y);
                resaltarMovimientosLegales(seleccionada);
            }
        } else {
            // intentar mover
            Movimiento mov = new Movimiento(seleccionada.x, seleccionada.y, x, y);
            boolean exito = juego.hacerMovimiento(mov); // aquí puedes ampliar para promoción si necesario
            if (!exito) {
                // si clicaste en otra pieza del mismo color, cambiar selección
                if (pieza != null && pieza.esBlanca() == turnoBlanco) {
                    seleccionada = new Posicion(x, y);
                    limpiarResaltados();
                    resaltarMovimientosLegales(seleccionada);
                    return;
                }
                // movimiento ilegal, opcional mostrar aviso
            } else {
                // detectar promoción simple: si hay un peón que debe promocionar, mostrar diálogo
                verificarPromocion(mov);
            }
            seleccionada = null;
            limpiarResaltados();
        }

        actualizarVista();
        actualizarEstado();
        if (juego.esJaqueMate(!juego.esTurnoBlanco())) {
            mostrarAlerta("Jaque mate", (juego.esTurnoBlanco() ? "Blancas" : "Negras") + " ganan.");
        } else if (juego.getTablero().estaEnJaque(juego.esTurnoBlanco())) {
            mostrarAlerta("Jaque", "El rey de " + (juego.esTurnoBlanco() ? "blanco" : "negro") + " está en jaque.");
        }
    }

    private void resaltarMovimientosLegales(Posicion desde) {
        limpiarResaltados();
        Pieza p = juego.getTablero().obtenerPieza(desde.x, desde.y);
        if (p == null) return;
        List<Movimiento> legales = juego.movimientosLegales(p.esBlanca());
        for (Movimiento m : legales) {
            if (m.fromX == desde.x && m.fromY == desde.y) {
                StackPane destino = casillas[m.toX][m.toY];
                Rectangle overlay = new Rectangle(TAM_CASILLA - 10, TAM_CASILLA - 10);
                overlay.setFill(Color.color(0, 1, 0, 0.3));
                overlay.setMouseTransparent(true);
                destino.getChildren().add(overlay);
            }
        }
        // resaltar seleccionado
        StackPane sel = casillas[desde.x][desde.y];
        Rectangle borde = new Rectangle(TAM_CASILLA - 4, TAM_CASILLA - 4);
        borde.setStroke(Color.BLUE);
        borde.setStrokeWidth(3);
        borde.setFill(Color.TRANSPARENT);
        borde.setMouseTransparent(true);
        sel.getChildren().add(borde);
    }


    private void limpiarResaltados() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                StackPane s = casillas[x][y];
                var children = s.getChildren();

                // Recorremos al revés para eliminar sin desordenar índices
                for (int i = children.size() - 1; i >= 0; i--) {
                    var n = children.get(i);

                    // Mantén solo el fondo (índice 0) y la etiqueta de pieza
                    if (i == 0) continue;           // fondo
                    if (n instanceof Label) continue; // pieza
                    children.remove(i);             // elimina overlays (verde, azul, etc.)
                }
            }
        }
    }

    private void actualizarVista() {
        Tablero tablero = juego.getTablero();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                StackPane celda = casillas[x][y];
                // quitar etiquetas anteriores de pieza
                celda.getChildren().removeIf(node -> node instanceof Label && node != estadoLabel);

                Pieza pieza = tablero.obtenerPieza(x, y);
                if (pieza != null) {
                    Label label = new Label(simboloUnicode(pieza));
                    label.setFont(Font.font(36));
                    celda.getChildren().add(label);
                }
            }
        }
    }

    private String simboloUnicode(Pieza pieza) {
        // Usa unicode para piezas como fallback: blancas mayúsculas, negras minúsculas
        if (pieza instanceof Rey) return pieza.esBlanca() ? "\u2654" : "\u265A";
        if (pieza instanceof Reina) return pieza.esBlanca() ? "\u2655" : "\u265B";
        if (pieza instanceof Torre) return pieza.esBlanca() ? "\u2656" : "\u265C";
        if (pieza instanceof Alfil) return pieza.esBlanca() ? "\u2657" : "\u265D";
        if (pieza instanceof Caballo) return pieza.esBlanca() ? "\u2658" : "\u265E";
        if (pieza instanceof Peon) return pieza.esBlanca() ? "\u2659" : "\u265F";
        return "?";
    }

    private void actualizarEstado() {
        String turno = juego.esTurnoBlanco() ? "Blancas" : "Negras";
        estadoLabel.setText("Turno: " + turno);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.show();
    }

    private void verificarPromocion(Movimiento m) {
        Pieza p = juego.getTablero().obtenerPieza(m.toX, m.toY);
        if (p instanceof Peon) {
            Peon peon = (Peon) p;
            if (peon.debePromocionar(m.toY)) {
                // Mostrar desplegable
                ChoiceDialog<TipoPromocion> dialog = new ChoiceDialog<>(TipoPromocion.REINA,
                        TipoPromocion.values());
                dialog.setTitle("Promoción de Peón");
                dialog.setHeaderText("Elige la pieza para promocionar:");
                dialog.setContentText("Pieza:");

                dialog.showAndWait().ifPresent(tipo -> {
                    Pieza nueva = peon.obtenerPiezaPromocionada(tipo);
                    juego.getTablero().colocarPieza(nueva, m.toX, m.toY);
                    actualizarVista(); // refrescar el tablero
                });
            }
        }
    }


    private void inicializarPosicionInicial() {
        Tablero t = juego.getTablero();
        
        t.colocarPieza(new Torre(true), 0, 7);
        t.colocarPieza(new Caballo(true), 1, 7);
        t.colocarPieza(new Alfil(true), 2, 7);
        t.colocarPieza(new Reina(true), 3, 7);
        t.colocarPieza(new Rey(true), 4, 7);
        t.colocarPieza(new Alfil(true), 5, 7);
        t.colocarPieza(new Caballo(true), 6, 7);
        t.colocarPieza(new Torre(true), 7, 7);
        for (int i = 0; i < 8; i++) {
            t.colocarPieza(new Peon(true), i, 6);
            t.colocarPieza(new Peon(false), i, 1);
        }
        t.colocarPieza(new Torre(false), 0, 0);
        t.colocarPieza(new Caballo(false), 1, 0);
        t.colocarPieza(new Alfil(false), 2, 0);
        t.colocarPieza(new Reina(false), 3, 0);
        t.colocarPieza(new Rey(false), 4, 0);
        t.colocarPieza(new Alfil(false), 5, 0);
        t.colocarPieza(new Caballo(false), 6, 0);
        t.colocarPieza(new Torre(false), 7, 0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}