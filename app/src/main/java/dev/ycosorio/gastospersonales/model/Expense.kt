package dev.ycosorio.gastospersonales.model

import androidx.compose.ui.platform.LocalAutofill
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Expense (
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: String = LocalDate.now().toString(),
    val amount: Double = 1.00,
    val category: String = "",
    val isRecurring: Boolean = false
)
