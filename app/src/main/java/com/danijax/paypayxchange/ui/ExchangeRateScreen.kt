package com.danijax.paypayxchange.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danijax.paypayxchange.R
import com.danijax.paypayxchange.model.ExchangeResult
import com.danijax.paypayxchange.ui.ui.theme.md_theme_light_outline
import kotlinx.coroutines.delay


@Composable
fun ExchangeRateRoute(viewModel: ExchangeRateViewModel) {
    val context = LocalContext.current
    val lastUpdate by viewModel.lastUpdate.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val status by viewModel.populateJobStatus.collectAsStateWithLifecycle()
    val currencyList by viewModel.currencyList.collectAsStateWithLifecycle()
    val state by viewModel.currencies.collectAsStateWithLifecycle()
    val conversionResult by viewModel.conversionResult.collectAsStateWithLifecycle()
    val amount by viewModel.amount.collectAsStateWithLifecycle()

    ExchangeRateContent(
        lastUpdate = lastUpdate,
        selectedCurrency = selectedCurrency,
        currencyList = currencyList,
        exchangeResult = conversionResult,
        conversionAmount = amount,
        updateAmount = viewModel::updateAmount,
        onSelectCurrency = viewModel::updateSelectedCurrency,
        onGetLastUpdate = viewModel::getLastUpdate
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExchangeRateContent(
    lastUpdate: String,
    selectedCurrency: String,
    currencyList: List<String>,
    onSelectCurrency: (String) -> Unit,
    exchangeResult: List<ExchangeResult> = emptyList(),
    conversionAmount: String,
    updateAmount: (String) -> Unit,
    onGetLastUpdate: () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Scaffold(topBar = {
        DashBoardAppBar(
            lastUpdate = lastUpdate,
            onGetLastUpdate = onGetLastUpdate
        )
    }) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp)
            ) {

                Column(
                    verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxWidth()
                ) {
                    Screen(amount = conversionAmount, symbol = selectedCurrency, updateAmount = updateAmount)
                    Spacer(modifier = Modifier.size(16.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expanded.value = true
                            },
                        value = selectedCurrency,
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Start
                        ),
                        enabled = false,
                        onValueChange = { },
                        label = { },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_unfold_more_24),
                                contentDescription = ""
                            )
                        }

                    )

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }) {
                        currencyList.forEachIndexed { index, s ->
                            DropdownMenuItem(
                                text = { Text(text = s) },
                                onClick = { expanded.value = false; onSelectCurrency(s) })
                        }
                    }

                    Spacer(modifier = Modifier.size(16.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(count = 2)
                        ) {
                            items(exchangeResult) { result ->

                                OutlinedTextField(
                                    value = "${result.value} ${result.currencyRate.symbol}",
                                    onValueChange = {},
                                    enabled = false
                                )
                            }
                        }

                    }

                }

            }

        }

    }
}

@Composable
fun DashBoardAppBar(lastUpdate: String, onGetLastUpdate: () ->  Unit) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000) // Delay for one minute (60000 milliseconds)
            onGetLastUpdate()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp)
    ) {
        Text(text = "Exchange Rate Converter", style = TextStyle(
         fontSize = 28.sp, fontWeight = FontWeight.W600
        )
         )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = lastUpdate, style = TextStyle(
                fontSize = 18.sp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(amount: String, symbol: String, updateAmount: (String) -> Unit) {
    val focusRequest = remember { FocusRequester()}
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequest)
            .heightIn(min = 112.dp), value = amount, onValueChange = updateAmount, trailingIcon = {
            Text(text = symbol, style = TextStyle(
                color = md_theme_light_outline
            ))
        }, singleLine = true, maxLines = 1, textStyle = TextStyle(
            fontSize = 40.sp,
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.End
        ),keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = DecimalInputVisualTransformation(DecimalFormatter())
    )
    LaunchedEffect(Unit) {
        focusRequest.requestFocus()
    }

}
