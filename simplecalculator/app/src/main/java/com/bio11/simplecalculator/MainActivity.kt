package com.example.simplecalculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val num1 = findViewById<EditText>(R.id.editTextNumberDecimal)
        val num2 = findViewById<EditText>(R.id.editTextNumberDecimal2)
        val result = findViewById<EditText>(R.id.editTextNumber4)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnSub = findViewById<Button>(R.id.btnSubtract)
        val btnMul = findViewById<Button>(R.id.btnMultiply)
        val btnDiv = findViewById<Button>(R.id.btnDivide)

        fun calculate(operation: (Double, Double) -> Double) {
            val s1 = num1.text.toString()
            val s2 = num2.text.toString()

            val a = s1.toDoubleOrNull()
            val b = s2.toDoubleOrNull()

            if (a != null && b != null) {
                val value = operation(a, b)
                if (value.isNaN()) {
                    result.setText("")
                } else {
                    // Remove trailing .0 for cleaner output if possible
                    val textResult = if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
                    result.setText(textResult)
                }
            } else {
                Toast.makeText(this, getString(R.string.error_invalid_input), Toast.LENGTH_SHORT).show()
            }
        }

        btnAdd.setOnClickListener {
            calculate { a, b -> a + b }
        }

        btnSub.setOnClickListener {
            calculate { a, b -> a - b }
        }

        btnMul.setOnClickListener {
            calculate { a, b -> a * b }
        }

        btnDiv.setOnClickListener {
            calculate { a, b ->
                if (b != 0.0) {
                    a / b
                } else {
                    Toast.makeText(this, getString(R.string.error_divide_by_zero), Toast.LENGTH_SHORT).show()
                    Double.NaN
                }
            }
        }
    }
}