///STAR pattern
fun main() {
    val rows = 6

    for (i in 0 until rows) {

        // Print spaces
        for (j in 0 until i) {
            print(" ")
        }

        // Print stars
        for (j in 0 until (rows - i)) {
            print("* ")
        }

        println()
    }
}