//string function
fun main(){
    var some_str = "Hello World"
     println("The string is $some_str")
     println("length is ${some_str.length}")
     for(index in 0..(some_str.length-1)){
         println("some_str[$index] = ${some_str[index]}")
     }
     // to print each char of a string
     var char_array = some_str.toCharArray()
     char_array.forEach{print(it + "\t")}
     println()
}