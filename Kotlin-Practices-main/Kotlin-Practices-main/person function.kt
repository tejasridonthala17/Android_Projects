//PERSON FUNCTION
fun main(){
    print("Enter the name: ")
    val name = readLine()!!.toString()
    // person(name = name)
    print("Enter age:")
    val age = readLine()!!.toInt()
    /* person(age = age)
     person(age = age, name = name)
     person(name, age)*/
    print("Enter occupation: ")
    val occupation = readLine()!!.toString()
    person(occupation = occupation, name=name, age=age)


}
fun person(name: String = "john", age:Int = 21, occupation:String= "Doctor"){
    println("$name, $age, $occupation")

}