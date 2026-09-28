//Array functions
fun main(){
    // array of numbers
    var ar = intArrayOf(1,2,3,4,5)
    ar.forEach { print("$it\t") }

    println()
    // array of chars
    var arr = arrayOf('a','b','c','d','e')
    arr.forEach { print("$it\t") }
    println()
}