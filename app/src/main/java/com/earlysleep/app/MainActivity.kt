package com.earlysleep.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.earlysleep.app.ui.EarlySleepRoot
import com.earlysleep.app.ui.SleepViewModel
import com.earlysleep.app.ui.theme.EarlySleepTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EarlySleepTheme {
                val viewModel: SleepViewModel = viewModel()
                EarlySleepRoot(viewModel = viewModel)
            }
        }
    }
}
