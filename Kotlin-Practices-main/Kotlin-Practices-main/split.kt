//SPLIT
fun main(){

    print("Enter a string: ")
    val str = readln()
    val tokens = str.split(","," ")

    for (i in tokens.indices) {
        println("${i + 1}, ${tokens[i]}")
    }
}