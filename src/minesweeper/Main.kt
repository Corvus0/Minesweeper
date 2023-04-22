package minesweeper

fun main() {
    Minefield(9).mainLoop()
}

class Minefield(private val size: Int) {
    private val minefield: Array<MutableList<Boolean>>
    private val minefieldDisplay: Array<MutableList<String>>
    private val mineLocations = mutableSetOf<Pair<Int, Int>>()
    private val guessLocations = mutableSetOf<Pair<Int, Int>>()
    private val mask = arrayOf(
        Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
        Pair(0, -1), Pair(0, 1),
        Pair(1, -1), Pair(1, 0), Pair(1, 1)
    )

    init {
        print("How many mines do you want on the field? ")
        var mines: Int
        while (true) {
            try {
                mines = readln().toInt()
                if (mines in 1..(size * size)) break
            } catch (_: Exception) { }
            println("Invalid number of mines")
        }
        minefield = Array(size) { MutableList(size) { false } }
        minefieldDisplay = Array(size) { MutableList(size) { "." } }
        var minesToPlace = mines
        while (minesToPlace > 0) {
            val r = (0 until size).random()
            val c = (0 until size).random()
            if (minefield[r][c]) continue
            else {
                minefield[r][c] = true
                mineLocations.add(Pair(r, c))
                minesToPlace--
            }
        }
        print()
    }

    fun mainLoop() {
        var gameOver = false
        while (!gameOver) {
            print("Set/unset mines marks or claim a cell as free: ")
            try {
                val (x, y, action) = readln().trim().split(" ")
                val r = y.toInt() - 1
                val c = x.toInt() - 1
                gameOver = when (action) {
                    "free" -> markFree(r, c)
                    "mine" -> markMine(r, c)
                    else -> {
                        println("Invalid action (enter mine or free)")
                        false
                    }
                }
            } catch (e: Exception) {
                println("Invalid input")
            }
        }
    }

    private fun getValidCoords(r: Int, c: Int): List<Pair<Int, Int>> {
        val search = mutableListOf<Pair<Int, Int>>()
        for ((mr, mc) in mask) search.add(Pair(mr + r, mc + c))
        return search.filter { it.first in 0 until size && it.second in 0 until size }
    }

    private fun markMine(r: Int, c: Int): Boolean {
        val displayed = minefieldDisplay[r][c]
        if (displayed == "*") {
            minefieldDisplay[r][c] = "."
            guessLocations.remove(Pair(r, c))
        } else if (displayed != ".") {
            println("There is a number here!")
            return false
        } else {
            minefieldDisplay[r][c] = "*"
            guessLocations.add(Pair(r, c))
        }
        print()
        if (guessLocations == mineLocations) println("Congratulations! You found all the mines!")
        return guessLocations == mineLocations
    }

    private fun markFree(mr: Int, mc: Int): Boolean {
        if (minefield[mr][mc]) {
            print(true)
            println("You stepped on a mine and failed!")
            return true
        } else {
            val toMark = mutableSetOf<Pair<Int, Int>>(Pair(mr, mc))
            do {
                val point = toMark.first()
                toMark.remove(point)
                val (r, c) = point
                val valid = getValidCoords(r, c)
                val mines = valid.sumOf { if (minefield[it.first][it.second]) 1 as Int else 0 }
                if (mines != 0) {
                    minefieldDisplay[r][c] = mines.toString()
                    continue
                }
                minefieldDisplay[r][c] = "/"
                for ((vr, vc) in valid) {
                    if (minefieldDisplay[vr][vc] != "/") toMark.add(Pair(vr, vc))
                }
            } while (toMark.isNotEmpty())
        }
        print()
        return false
    }

    private fun print(reveal: Boolean = false) {
        if (reveal) {
            for (r in 0 until size) {
                for (c in 0 until size) {
                    if (!minefield[r][c] && minefieldDisplay[r][c] == ".") minefieldDisplay[r][c] = "/"
                    else if (minefield[r][c]) minefieldDisplay[r][c] = "X"
                }
            }
        }
        val header = "\n |${(1..size).joinToString(separator = "") { it.toString() }}|"
        val separator = "—│—————————│"
        var r = 1
        println(header)
        println(separator)
        for (row in minefieldDisplay) {
            println(
                row.joinToString(
                    separator = "",
                    prefix = "${r++}|",
                    postfix = "|"
                )
            )
        }
        println(separator)
    }
}
