//IF ELSE
fun main() {
    print("Enter the month number: ")
    val month = readLine()!!.toInt()
    if (month == 1){
        print("jan")
    }else if (month == 2){
        print("feb")
    }else if (month == 3){
        print("mar")
    }else if (month == 4){
        print("april")
    }else if (month == 5){
        print("may")
    }else if (month == 6){
        print("jun")
    }else if (month == 7){
        print("jul")
    }else if (month == 8){
        print("aug")
    }else if (month == 9){
        print("sep")
    }else if (month == 10){
        print("oct")
    }else if (month == 11){
        print("nov")
    }else if (month == 12) {
        print("Dec")
    }else{
        print("Wrong number entered")
    }
}