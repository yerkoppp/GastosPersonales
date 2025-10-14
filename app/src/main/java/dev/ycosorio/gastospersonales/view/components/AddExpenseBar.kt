package dev.ycosorio.gastospersonales.view.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ycosorio.gastospersonales.R
import dev.ycosorio.gastospersonales.ui.theme.shape
import dev.ycosorio.gastospersonales.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseBar(expenseViewModel: ExpenseViewModel, uiState: ExpenseViewModel.ExpenseUiState) {

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    if (showDatePickerDialog) {
        CustomDatePickerDialog(
            onDateSelected = { newDate ->
                expenseViewModel.updateNewExpenseDate(newDate)
            },
            onDismiss = {
                showDatePickerDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Añade espacio vertical entre filas
        ) {
            // --- PRIMERA FILA: Nombre y Total ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Añade espacio entre campos
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = uiState.newExpenseName,
                    onValueChange = { expenseViewModel.updateNewExpenseName(it) },
                    label = { Text("Nombre del gasto") },
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = if (uiState.newExpenseAmount == 0.0) "" else uiState.newExpenseAmount.toString(),
                    onValueChange = { expenseViewModel.updateNewExpenseAmount(it) },
                    label = { Text("Total") },
                    singleLine = true
                )
            }

            // --- SEGUNDA FILA: Categoría y Fecha ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Añade espacio entre campos
            ) {
                // Contenedor del Menú Desplegable
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            ),
                            readOnly = true,
                            value = uiState.newExpenseCategory,
                            onValueChange = {},
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            uiState.categories.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        expenseViewModel.updateNewExpenseCategory(selectionOption)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // --- CAMPO DE FECHA CON BORDE (LA SOLUCIÓN) ---
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed: Boolean by interactionSource.collectIsPressedAsState()
                if (isPressed) {
                    showDatePickerDialog = true
                }
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    value = uiState.newExpenseDate,
                    onValueChange = {},
                    label = { Text("Fecha") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar Fecha"
                        )
                    },
                    interactionSource = interactionSource
                )
            }

            // --- BOTÓN DE AÑADIR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { expenseViewModel.saveExpense() },
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = shape.medium
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.btn_add_expense)
                    )
                }
            }
        }
    }
}