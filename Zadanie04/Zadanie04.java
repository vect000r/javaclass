import java.util.*;

public class CanvasFill extends Fill {
    @Override
    public void fill(Canvas canvas, List<Position2D> neighbours, Position2D start, int brightness) {
        // Sprawdzenie parametrów wejściowych
        if (canvas == null || neighbours == null || start == null) {
            throw new IllegalArgumentException("Parametry nie mogą być null");
        }

        // Jeśli punkt startowy jest poza płótnem, kończymy
        if (isOutOfBounds(canvas, start)) {
            return;
        }

        // Pobieramy oryginalną jasność punktu startowego
        int originalBrightness = canvas.getBrightness(start);

        // Jeśli jasność docelowa jest taka sama jak oryginalna, kończymy
        if (originalBrightness == brightness) {
            return;
        }

        Queue<Position2D> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Position2D current = queue.poll();

            // Sprawdzamy czy piksel ma oryginalną jasność
            if (canvas.getBrightness(current) != originalBrightness) {
                continue;
            }

            // Ustawiamy nową jasność
            canvas.setBrightness(current, brightness);

            // Sprawdzamy wszystkich sąsiadów
            for (Position2D neighbour : neighbours) {
                int newRow = current.getRow() + neighbour.getRow();
                int newCol = current.getCol() + neighbour.getCol();
                Position2D newPosition = new Position2D(newCol, newRow);

                if (!isOutOfBounds(canvas, newPosition) &&
                        canvas.getBrightness(newPosition) == originalBrightness) {
                    queue.add(newPosition);
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
