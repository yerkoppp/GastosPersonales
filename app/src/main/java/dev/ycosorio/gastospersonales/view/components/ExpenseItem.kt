package dev.ycosorio.gastospersonales.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.ycosorio.gastospersonales.model.Expense
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpenseItem(
    modifier: Modifier = Modifier,
    expense: Expense,
    onToggleCompletion: (Expense) -> Unit,
    onDateChange: (Expense, String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
        // Color de fondo coherente con el tema
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (expense.isRecurring) {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        )
    ) {
        Row(
            // 2. MODIFICADO: Padding vertical para mejor espaciado interno
            modifier = Modifier.padding(vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // La barra lateral de estado se mantiene igual
            if (expense.isRecurring) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .padding(5.dp) // Este padding parece un error, lo corregiré
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium
                        )
                )
            }


            IconButton(onClick = { onToggleCompletion(expense) }) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Marcar como Gasto Recurrente",
                    tint = if (expense.isRecurring) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    }
                )
            }

            // Columna para Nombre y Categoría (sin cambios)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Columna para Monto y Fecha
            Column(
                modifier = Modifier.padding(end = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = currencyFormatter.format(expense.amount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    // 3. MODIFICADO: Color del monto para mayor énfasis
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                )
                TextButton(
                    onClick = { showDatePicker = true },
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    Text(
                        text = expense.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        CustomDatePickerDialog(
            onDateSelected = { newDate ->
                onDateChange(expense, newDate)
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
}