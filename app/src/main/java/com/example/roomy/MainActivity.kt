package com.example.roomy

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.roomy.db.Supabase.UserSessionManager
import com.example.roomy.ui.RoomyApp
import com.example.roomy.ui.Screens
import com.example.roomy.ui.theme.RoomyTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            RoomyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RoomyApp(
                        modifier = Modifier.padding(innerPadding),

                        )
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            RoomyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RoomyApp(
                        modifier = Modifier.padding(innerPadding),

                        )
                }
            }
        }
    }
}