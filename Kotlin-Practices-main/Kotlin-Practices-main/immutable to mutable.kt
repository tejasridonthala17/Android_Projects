//IMMUTABLE to MUTABLE
fun main() {
    //list of function (immutable) (combination of all data types)
    var arra = mutableListOf('a', 1, "Hello", 3.0, 's') // by using this we can convert it into mutable
    arra.forEach { print("$it\t") }
    println()
    arra[1] = 'b'
    arra.add(4, "world") // to add-on a element
    arra.forEach { print("$it\t") } // changed list


}