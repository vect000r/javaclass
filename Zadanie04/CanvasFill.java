import java.util.*;

public class CanvasFill extends Fill {
    @Override
    public void fill(Canvas canvas, List<Position2D> neighbours, Position2D start, int brightness) {
        // Walidacja parametrów wejściowych
        if (canvas == null || neighbours == null || start == null) {
            throw new IllegalArgumentException("Parameters cannot be null.");
        }
        if (neighbours.isEmpty()) {
            throw new IllegalArgumentException("The neighbors list cannot be empty.");
        }

        // Sprawdzanie czy punkt startowy znajduje się na płótnie
        if (isOutOfBounds(canvas, start)) {
            throw new IllegalArgumentException("The starting point is out of bounds.");
        }

        // Kolejka do przetwarzania pikseli
        Queue<Position2D> queue = new LinkedList<>();
        Set<Position2D> visited = new HashSet<>(); // Śledzenie odwiedzonych pikseli
        queue.add(start);

        // Algorytm BFS
        while (!queue.isEmpty()) {
            Position2D current = queue.poll();

            // Ustawiamy nową jasność piksela
            if (canvas.getBrightness(current) < brightness) {
                canvas.setBrightness(current, brightness);
            }

            // Przetwarzamy sąsiadów
            for (Position2D neighbour : neighbours) {
                int newRow = current.getRow() + neighbour.getRow();
                int newCol = current.getCol() + neighbour.getCol();
                Position2D newPosition = new Position2D(newCol, newRow);

                // Sprawdzamy warunki dla nowej pozycji
                if (!isOutOfBounds(canvas, newPosition) &&
                        canvas.getBrightness(newPosition) < brightness && // Warunek jasności
                        !visited.contains(newPosition)) {
                    queue.add(newPosition);
                    visited.add(newPosition); // Dodajemy do odwiedzonych
                }
            }
        }
    }

    private boolean isOutOfBounds(Canvas canvas, Position2D point) {
        return point.getCol() < 0 ||
                point.getRow() < 0 ||
                point.getCol() > canvas.getMaxPosition().getCol() ||
                point.getRow() > canvas.getMaxPosition().getRow();
    }
}