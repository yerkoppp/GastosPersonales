package dev.ycosorio.gastospersonales.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.ycosorio.gastospersonales.model.Expense
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseItem (
    modifier: Modifier = Modifier,
    expense: Expense = Expense(
        id = "",
        name = "",
        date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        amount = 0.00,
        category = "",
        isRecurring = false
    ),
    onToggleCompletion: (Expense) -> Unit = {}
) {

    // Estado para controlar si el diálogo de fecha está visible
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Estado para la fecha que se muestra en la UI. Inicialmente es la fecha del gasto.
    var selectedDateText by remember { mutableStateOf(expense.date) }

    val datePickerState = rememberDatePickerState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (expense.isRecurring) {
                MaterialTheme.colorScheme.secondary
            }
            else MaterialTheme.colorScheme.inversePrimary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Switch(
                checked = expense.isRecurring,
                onCheckedChange = {
                    onToggleCompletion(expense)
                },
                modifier = Modifier.padding(start = 8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = expense.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.bodySmall
                )
            }

                // El texto de la fecha es un botón para abrir el diálogo
                TextButton(onClick = { showDatePickerDialog = true }) {
                    Text(
                        text = selectedDateText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
    }    // El diálogo del DatePicker ahora se define aquí
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false // Cierra el diálogo

                        val milisegundos = datePickerState.selectedDateMillis
                        if (milisegundos != null) {
                            val formateador = DateTimeFormatter
                                .ofPattern("dd/MM/yyyy", Locale("es", "CL"))

                            val nuevaFecha = Instant.ofEpochMilli(milisegundos)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .format(formateador)

                            selectedDateText = nuevaFecha
                            // Aquí deberías llamar a una función del ViewModel para guardar la nueva fecha
                            // ej: onDateChange(expense, nuevaFecha)
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}