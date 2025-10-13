package dev.ycosorio.gastospersonales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.ycosorio.gastospersonales.ui.theme.GastosPersonalesTheme
import dev.ycosorio.gastospersonales.view.screens.ExpenseListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GastosPersonalesTheme {
                ExpenseListScreen()
            }
        }
    }
}
