//MAPS
fun main(){
    // maps
    var map_var = mapOf(1 to 'a',2 to 'b',3 to 'c',4 to 'd')
    println(map_var)
    println(map_var.keys) // to retrieve keys
    println(map_var.values) // to retrieve values
    map_var.keys.forEach { print(map_var[it].toString()) }  // only prints values
    println()
    map_var.forEach { i,string -> print("$i->$string\t") } // to print key->value
    println()
    // adding of two maps
    var map_1: MutableMap<Int, Char> = mutableMapOf(1 to 'a',2 to 'b',3 to 'c',4 to 'd')
    var map_2: MutableMap<Int, Char> = mutableMapOf(5 to 'a',6 to 'f',7 to 'g')

    var map_3= map_1+map_2
    print(map_3)
    println()
}