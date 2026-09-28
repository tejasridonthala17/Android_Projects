//WHEN
fun main() {
    print("Enter the month number: ")
    val month = readLine()!!.toInt()
    val mnth_name = when (month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "September"
        else -> "Invalid Input"
    }
    println(mnth_name + " " + month)
}