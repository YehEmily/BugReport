package com.example.bugreport

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bugreport.ui.theme.BugReportTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BugReportTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    val context = LocalContext.current

                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(context, HeroGalleryActivity::class.java)
                            )
                        }
                    ) {
                        Text(text = "Show me dogs")
                    }
                }
            }
        }
    }
}
