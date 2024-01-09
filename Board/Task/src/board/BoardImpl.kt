package board

import java.lang.IllegalArgumentException

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(createSquareBoard(width))

class SquareBoardImpl(override val width: Int): SquareBoard {

    private val squareBoard: List<List<Cell>> = List(width) { row ->
        List(width) { column ->
            Cell(row + 1, column + 1)
        }
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return squareBoard.getOrNull(i - 1)?.getOrNull(j - 1)
    }

    override fun getCell(i: Int, j: Int): Cell {
        return squareBoard.getOrNull(i - 1)?.getOrNull(j - 1) ?: throw IllegalArgumentException()
    }

    override fun getAllCells(): Collection<Cell> {
        return squareBoard.flatten()
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        if (i > width)
            throw IllegalArgumentException()

        val shouldReverse = jRange.first > jRange.last
        val rowList: List<Cell> = squareBoard[i - 1].subList(
            fromIndex = if (shouldReverse) jRange.last - 1 else jRange.first - 1,
            toIndex = if (shouldReverse) {
                minOf(jRange.first, width)
            } else {
                minOf(jRange.last, width)
            }
        ).let { row -> if (shouldReverse) row.asReversed() else row }

        return rowList;
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        if (j > width)
            throw IllegalArgumentException()

        val shouldReverse = iRange.first > iRange.last
        val columnList: List<Cell> = squareBoard.subList(
            fromIndex = if (shouldReverse) iRange.last - 1 else iRange.first - 1,
            toIndex = if (shouldReverse) {
                minOf(iRange.first, width)
            } else {
                minOf(iRange.last, width)
            }
        ).map { it[j - 1] }.let { column -> if (shouldReverse) column.asReversed() else column }

        return columnList;
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        val (i, j) = when(direction) {
            Direction.UP -> Pair(this.i - 1, this.j)
            Direction.DOWN -> Pair(this.i + 1, this.j)
            Direction.LEFT -> Pair(this.i, this.j - 1)
            Direction.RIGHT -> Pair(this.i, this.j + 1)
        }
        return getCellOrNull(i, j)
    }
}

class GameBoardImpl<T>(private val squareBoard: SquareBoard): GameBoard<T>, SquareBoard by squareBoard {

    private val mapStore: HashMap<Cell, T?> = HashMap()

    init {
        squareBoard.getAllCells().forEach { index ->
            mapStore[index] = null
        }
    }

    override fun get(cell: Cell): T? {
        return mapStore[cell]
    }

    override fun set(cell: Cell, value: T?) {
        mapStore[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return mapStore.filter { (_, value) -> predicate(value) }.keys
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return filter(predicate).first()
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return mapStore.any { (_, value) -> predicate(value) }
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return mapStore.all { (_, value) -> predicate(value) }
    }
}