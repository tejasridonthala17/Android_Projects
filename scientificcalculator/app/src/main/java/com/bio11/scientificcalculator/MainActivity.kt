package com.bio11.scientificcalculator

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var displayText: TextView
    private var operand1: Double = 0.0
    private var pendingOperator: String = ""
    private var isNewOperation: Boolean = true

    private val decimalFormat = DecimalFormat("#.########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        displayText = findViewById(R.id.display_text)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rootLayout = findViewById<View>(R.id.main)
        bindButtons(rootLayout)
    }

    private fun bindButtons(view: View) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                bindButtons(view.getChildAt(i))
            }
        } else if (view is TextView) {
            val text = view.text.toString()
            if (text.isNotEmpty() && text != "SHARP EL-1801V") {
                view.setOnClickListener {
                    handleButtonClick(text)
                }
            }
        }
    }

    private fun handleButtonClick(value: String) {
        when (value) {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> {
                if (isNewOperation || displayText.text == "0") {
                    displayText.text = value
                    isNewOperation = false
                } else {
                    displayText.append(value)
                }
            }
            "00" -> {
                if (isNewOperation || displayText.text == "0") {
                    displayText.text = "0"
                    isNewOperation = false
                } else {
                    displayText.append("00")
                }
            }
            "." -> {
                if (isNewOperation) {
                    displayText.text = "0."
                    isNewOperation = false
                } else if (!displayText.text.contains(".")) {
                    displayText.append(".")
                }
            }
            "+", "-", "×", "÷", "*" -> {
                val currentVal = displayText.text.toString().toDoubleOrNull() ?: 0.0
                if (pendingOperator.isNotEmpty() && !isNewOperation) {
                    calculateResult(currentVal)
                } else {
                    operand1 = currentVal
                }
                pendingOperator = if (value == "*") "×" else value
                isNewOperation = true
            }
            "=" -> {
                val currentVal = displayText.text.toString().toDoubleOrNull() ?: 0.0
                calculateResult(currentVal)
                pendingOperator = ""
                isNewOperation = true
            }
            "C/CE" -> {
                displayText.text = "0"
                operand1 = 0.0
                pendingOperator = ""
                isNewOperation = true
            }
            "+/-" -> {
                val currentVal = displayText.text.toString().toDoubleOrNull() ?: 0.0
                if (currentVal != 0.0) {
                    val inverted = currentVal * -1
                    displayText.text = formatValue(inverted)
                }
            }
            "%" -> {
                val currentVal = displayText.text.toString().toDoubleOrNull() ?: 0.0
                val percentVal = currentVal / 100.0
                displayText.text = formatValue(percentVal)
                isNewOperation = true
            }
            "TAX+" -> {
                val currentVal = displayText.text.toString().toDoubleOrNull() ?: 0.0
                val taxRate = 0.10 // 10% Tax
                val result = currentVal * (1 + taxRate)
                displayText.text = formatValue(result)
                isNewOperation = true
            }
            "TAX-" -> {
                val currentVal = displayText.text.toString().toDoubleOrNull() ?: 0.0
                val taxRate = 0.10 // 10% Tax
                val result = currentVal / (1 + taxRate)
                displayText.text = formatValue(result)
                isNewOperation = true
            }
            "CHANGE" -> {
                Toast.makeText(this, "Mode Changed", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Button: $value", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateResult(currentVal: Double) {
        val result = when (pendingOperator) {
            "+" -> operand1 + currentVal
            "-" -> operand1 - currentVal
            "×" -> operand1 * currentVal
            "÷" -> {
                if (currentVal != 0.0) operand1 / currentVal else 0.0
            }
            else -> currentVal
        }
        operand1 = result
        displayText.text = formatValue(result)
    }

    private fun formatValue(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            decimalFormat.format(value)
        }
    }
}