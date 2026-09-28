//ABSTRACT and INTERFACE
abstract class Animal {
    abstract fun sound()

    fun sleep() {
        println("Animal is sleeping")
    }
}

class Dog : Animal() {
    override fun sound() {
        println("Dog barks")
    }
}

fun main() {
    val obj = Dog()
    obj.sound()
    obj.sleep()

}