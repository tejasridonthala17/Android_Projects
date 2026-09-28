//3.for loop
fun main(){
    var charRange = 'a'..'z'
    // to print alphabets
    for (i in charRange) {
        print("$i\t")
    }
    print("\n")
    // to print numbers with 5 gap
    val numRange = 1..100 step 5
    for (i in numRange) {
        print("$i\t")
    }
}