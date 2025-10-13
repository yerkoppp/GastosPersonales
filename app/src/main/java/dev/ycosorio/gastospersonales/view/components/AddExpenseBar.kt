package dev.ycosorio.gastospersonales.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ycosorio.gastospersonales.R
import dev.ycosorio.gastospersonales.ui.theme.shape
import dev.ycosorio.gastospersonales.viewmodel.ExpenseViewModel
import kotlin.math.exp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseBar(expenseViewModel: ExpenseViewModel, uiState: ExpenseViewModel.ExpenseUiState){

    //Estado para controlar si el menú está expandido o no
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(8.dp)){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){

            OutlinedTextField(
                modifier = Modifier.weight(0.5f)
                    .padding(end= 8.dp),
                onValueChange = { expenseViewModel.updateNewExpenseName(it) },
                value = uiState.newExpenseName,
                textStyle = MaterialTheme.typography.bodySmall,
                label = {Text("Nombre del gasto",
                    style = MaterialTheme.typography.labelLarge)},
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.weight(0.5f)
                    .padding(end= 8.dp),
                onValueChange = { expenseViewModel.updateNewExpenseAmount(it) },
                value = if(uiState.newExpenseAmount == 0.0) "" else uiState.newExpenseAmount.toString(),
                textStyle = MaterialTheme.typography.bodySmall,
                label = {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                singleLine = true
            )

            IconButton(onClick = {expenseViewModel.saveExpense()},
                modifier = Modifier.size(50.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = shape.medium
            ){
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.btn_add_expense)
                )
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ){
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                        enabled = true
                    ),
                    readOnly = true,
                    value = uiState.newExpenseCategory,
                    onValueChange = {},
                    label = { Text ("Categoría")},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded = false }
                ){
                    uiState.categories.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = {Text (selectionOption)},
                            onClick = {
                                expenseViewModel.updateNewExpenseCategory(selectionOption)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }


            }
        }
    }}