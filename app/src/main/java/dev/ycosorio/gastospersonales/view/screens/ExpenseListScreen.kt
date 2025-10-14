package dev.ycosorio.gastospersonales.view.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.ycosorio.gastospersonales.R
import dev.ycosorio.gastospersonales.view.components.AddExpenseBar
import dev.ycosorio.gastospersonales.view.components.ExpenseItem
import dev.ycosorio.gastospersonales.viewmodel.ExpenseViewModel
import kotlinx.coroutines.delay

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    expenseViewModel: ExpenseViewModel = viewModel()
) {

    val uiState by expenseViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Gastos Personales")
                }
            )
        }) { innerPadding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            AddExpenseBar(expenseViewModel, uiState)
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(
                    items = uiState.expenses,
                    key = { expense -> expense.id }
                ) { expense ->
                    SwipeToDeleteContainer(
                        item = expense,
                        onDelete = { expenseToDelete -> expenseViewModel.removeExpense(expenseToDelete) }
                    ) { item ->
                        ExpenseItem(expense = item,
                            onToggleCompletion = { expenseToToggle ->
                                expenseViewModel.toggleExpenseCompletion(expenseToToggle)
                            },
                            onDateChange = { expenseToUpdate, newDate ->
                                expenseViewModel.updateNewExpenseDate(expenseToUpdate,newDate)
                            }
                        )
                    }
                }
            }
            // Muestra el mensaje solo si la lista está vacía
            if (uiState.expenses.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.lc_add_expense),
                        Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }

    val dismissState =
        rememberSwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
        )

    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }

    LaunchedEffect(key1 = dismissState.targetValue) {
        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
            isRemoved = true
        }

    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                DeleteBackground(swipeDismissState = dismissState)
            },
            content = { content(item) },
            enableDismissFromStartToEnd = false,
        )
    }
}

@Composable
fun DeleteBackground(
    swipeDismissState: SwipeToDismissBoxState
) {
    val color by animateColorAsState(
        when (swipeDismissState.targetValue) {
            SwipeToDismissBoxValue.Settled -> Color.White
            SwipeToDismissBoxValue.StartToEnd -> Color.Green
            SwipeToDismissBoxValue.EndToStart -> Color.Red
        }, label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}



