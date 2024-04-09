package com.danijax.paypayxchange.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.danijax.paypayxchange.api.ExchangeRateService
import com.danijax.paypayxchange.data.datasource.RemoteDataSource
import com.danijax.paypayxchange.ui.ui.theme.PayPayXchangeTheme
import com.danijax.paypayxchange.worker.WorkScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExchangeActivity : ComponentActivity() {
    @Inject
    lateinit var exchangeRateService: ExchangeRateService

    val viewModel: ExchangeRateViewModel by viewModels()

    @Inject
    lateinit var  dataSource: RemoteDataSource
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PayPayXchangeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExchangeRateRoute( viewModel =  viewModel)
                }
            }
        }
        //Schedule Background jobs
        WorkScheduler.invoke(applicationContext)
    }

}


