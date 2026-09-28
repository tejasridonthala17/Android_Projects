///star circle
fun main() {
    val r = 5

    for (i in -r..r) {
        for (j in -r..r) {

            val dis = i * i + j * j

            if (dis >= r * r - 2 && dis <= r * r + 2) {
                print("* ")
            } else {
                print("  ")
            }
        }
        println()
    }
}