//FIBONACCI SERIES
fun main() {
    var first = 0
    var second = 1
    val n = 10

    println("Fibonacci Series:")

    for (i in 1..n) {
        print("$first ")

        val next = first + second
        first = second
        second = next
    }
}