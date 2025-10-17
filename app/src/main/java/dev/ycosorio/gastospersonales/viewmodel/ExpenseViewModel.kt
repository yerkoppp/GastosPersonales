package dev.ycosorio.gastospersonales.viewmodel

import androidx.lifecycle.ViewModel
import dev.ycosorio.gastospersonales.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ExpenseViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState

    fun updateNewExpenseName(newName: String, ){
        _uiState.update { currentState ->
            currentState.copy(newExpenseName = newName)
        }
    }

    fun updateNewExpenseAmount(newAmount: String){
        _uiState.update { currentState ->
            // Remover formato (puntos de miles) para procesar
            val cleanAmount = newAmount.replace(".", "").replace(",", ".")

            // Validar que solo contenga dígitos y máximo una coma/punto
            val isValid = cleanAmount.isEmpty() ||
                    (cleanAmount.matches(Regex("^\\d*[,.]?\\d{0,2}$")))

            if (isValid) {
                val amountAsDouble = cleanAmount.toDoubleOrNull() ?: 0.0

                // Formatear con separador de miles para mostrar
                val formattedText = if (cleanAmount.isEmpty() || amountAsDouble == 0.0) {
                    cleanAmount
                } else {
                    val parts = cleanAmount.split(Regex("[,.]"))
                    val integerPart = parts[0].toLongOrNull() ?: 0L
                    val decimalPart = if (parts.size > 1) parts[1] else ""

                    val formatted = "%,d".format(java.util.Locale("es", "CL"), integerPart)
                    if (cleanAmount.contains(",") || cleanAmount.contains(".")) {
                        "$formatted,$decimalPart"
                    } else {
                        formatted
                    }
                }

                currentState.copy(
                    newExpenseAmount = amountAsDouble,
                    newExpenseAmountText = formattedText
                )
            } else {
                currentState
            }
        }
    }

    //Función para actualizar la categoría
    fun updateNewExpenseCategory(newCategory: String){
        _uiState.update { currentState ->
            currentState.copy(newExpenseCategory = newCategory)
        }
    }

    //Función para actualizar la fecha del nuevo gasto
    fun updateNewExpenseDate(newDate: String){
        _uiState.update { currentState ->
            currentState.copy(newExpenseDate = newDate)
        }
    }

    fun updateNewExpenseDate(expenseToUpdate: Expense, newDate: String){
        _uiState.update { currentState ->
            val updatedExpenses = currentState.expenses.map{ existingExpense ->
                if(existingExpense.id == expenseToUpdate.id){
                    existingExpense.copy(date = newDate)
                } else {
                    existingExpense
                }
            }
            currentState.copy(expenses = updatedExpenses)
        }
    }

    fun saveExpense(){
        _uiState.update { currentState ->
            val name = currentState.newExpenseName.trim()
            val amount = currentState.newExpenseAmount
            val category = currentState.newExpenseCategory
            val date = currentState.newExpenseDate

            if(name.isNotBlank() && amount > 0.0){
                val newExpense = Expense (
                    name = name,
                    amount = amount,
                    category = category,
                    date = date)

                val updatedExpenses = listOf(newExpense) + currentState.expenses

                currentState.copy(
                    expenses = updatedExpenses,
                    newExpenseName = "",
                    newExpenseAmount= 0.00,
                    newExpenseAmountText = "",
                    newExpenseDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    newExpenseCategory = currentState.categories.first(),
                )
            } else {
                currentState
            }

        }
    }

    fun removeExpense(expense: Expense){
        _uiState.update { currentState ->
                val updatedExpenses = currentState.expenses.filter { it.id != expense.id }
            currentState.copy(expenses = updatedExpenses)

        }
    }

    fun toggleExpenseCompletion(expense: Expense){
        _uiState.update { currentState ->
            val updatedExpenses = currentState.expenses.map {existingExpense ->
                if (existingExpense.id == expense.id){
                    existingExpense.copy(isRecurring = !existingExpense.isRecurring)
                } else {
                    existingExpense
                }
            }
            currentState.copy(expenses = updatedExpenses)
        }
    }


    data class ExpenseUiState(
        val expenses: List<Expense> = emptyList(),
        val newExpenseName: String = "",
        val newExpenseAmount: Double = 0.00,
        val newExpenseAmountText: String = "",
        val categories: List<String> = listOf(
            "🍔 Alimentación",
            "🚌 Transporte",
            "🎬 Ocio",
            "🐷 Ahorro",
            "📈 Inversiones",
            "🧾 Gastos Básicos",
            "🏷️ Otros"
        ),
        val newExpenseCategory: String = categories.first(),
        val newExpenseDate: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    ){
        val totalExpenses: Double
            get() = expenses.sumOf { it.amount }
    }
}