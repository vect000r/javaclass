public class FastFinder implements FindSth {

    @Override
    public Position2D tryToFind(Locator locator) {
        int minRow = locator.minRow();
        int maxRow = locator.maxRow();
        int minCol = locator.minCol();
        int maxCol = locator.maxCol();

        while (minRow <= maxRow && minCol <= maxCol) {
            int midRow = (minRow + maxRow) / 2;
            int midCol = (minCol + maxCol) / 2;

            try {
                locator.here(new Position2D(midCol, midRow));
                return new Position2D(midCol, midRow);
            } catch (Locator.ColumnToHighException e) {
                maxCol = midCol - 1;
            } catch (Locator.ColumnToLowException e) {
                minCol = midCol + 1;
            } catch (Locator.RowToHighException e) {
                maxRow = midRow - 1;
            } catch (Locator.RowToLowException e) {
                minRow = midRow + 1;
            }
        }

        return null;
    }
}
