package com.example.calclab3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Знаходження всіх потрібних елементів
        val P_C: EditText = findViewById(R.id.p_c)
        val B: EditText = findViewById(R.id.b)
        val SIGMA: EditText = findViewById(R.id.sigma)

        val calculate: Button = findViewById(R.id.submit_button)
        val result: TextView = findViewById(R.id.result)

        calculate.setOnClickListener{
            val p_c = P_C.getText().toString();
            val b = B.getText().toString();
            val sigma = SIGMA.getText().toString();

            var errorMessage = ""

            // Функція що повертає число якщо воно конвертується і дійсне а якщо ні то додає повідомлення про помилку
            fun checkAndConvert(value: String, fieldName: String): Double? {
                return if (value.isEmpty()) {

                    errorMessage += "$fieldName is empty.\n "
                    null
                } else {
                    try {
                        value.toDouble()
                    } catch (e: NumberFormatException) {
                        errorMessage += "$fieldName is not a valid number.\n "
                        null
                    }
                }
            }

            // Перевірка всіх чисел
            val p_c_val = checkAndConvert(p_c, "P_C")
            val b_val = checkAndConvert(b, "B")
            val sigma_val = checkAndConvert(sigma, "Sigma")

            if (errorMessage.isNotEmpty()) {
                // Якщо є помилка то спровіщуємо про це
                result.text = errorMessage
            } else {
                // Обраховуємо значення
                val upperbor = p_c_val!! + p_c_val * 0.05
                val lowerbor = p_c_val - p_c_val * 0.05
                // Обрахунки
                val percent = integrate(lowerbor, upperbor, 5000, sigma_val!!, p_c_val)
                val income = p_c_val * 24 * percent * b_val!!
                val penalty = p_c_val * 24 * (1-percent) * b_val
                val difference = income - penalty


                val result_text = """
                    Income = ${"%.5f".format(income)}
                    Penalty = ${"%.5f".format(penalty)}
                    Difference = ${"%.5f".format(difference)}
                    """.trimIndent()

                // Виводимо результат
                result.text = result_text
            }
        }

    }
    // Нормальний закон розподілу потужності
    fun pd_calc(p: Double, p_c: Double, sigma: Double): Double {
        return 1 / (sigma * sqrt(2 * PI)) * exp(-(p - p_c).pow(2) / (2 * sigma.pow(2)))
    }
    // Інтеграл
    fun integrate(
        a: Double,  // нижня межа інтегрування
        b: Double,  // верхня межа інтегрування
        n: Int,
        sigma: Double,
        p_c: Double
    ): Double {
        val h = (b - a) / n  // довжина кроку
        var result = 0.5 * (pd_calc(a, p_c, sigma) + pd_calc(b, p_c, sigma))  // враховуємо значення на межах

        for (i in 1 until n) {
            result += pd_calc(a + i * h, p_c, sigma)  // додаємо значення функції у внутрішніх точках
        }

        result *= h  // множимо на довжину кроку
        return result
    }

}