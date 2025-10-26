package edu.pe.cibertec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.pe.cibertec.navigatiion.NavGraph
import edu.pe.cibertec.ui.screens.LoginScreen
import edu.pe.cibertec.ui.theme.SharedlocationappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedlocationappTheme {
                Surface(color= MaterialTheme.colorScheme.background){
                    NavGraph()
                }
            }
        }
    }
}