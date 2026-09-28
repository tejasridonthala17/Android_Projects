//FACTORIAL AND RECURSION
fun main(){
    print("Enter a number: ")
    var n = readLine()!!.toInt()
    //println(factorial(n))
    println(recursiveFactorial(n))
}
// using factorial
/*fun factorial(n: Int): Int {
    var fact = 1
    for(i in 1..n){
        fact *=i
    }
    return fact
}*/

//using recursive
fun recursiveFactorial(n: Int): Int {
    if (n==1)
        return 1
    else
        return n*recursiveFactorial(n-1)
}