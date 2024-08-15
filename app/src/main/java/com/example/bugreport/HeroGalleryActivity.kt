package com.example.bugreport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.bugreport.ui.theme.BugReportTheme

class HeroGalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BugReportTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    HeroGallery(dogPics = dogPics)
                }
            }
        }
    }

    private companion object {
        val dogPics = listOf(
            "https://upload.wikimedia.org/wikipedia/commons/d/d9/Collage_of_Nine_Dogs.jpg",
            "https://i.ytimg.com/vi/SfLV8hD7zX4/maxresdefault.jpg",
        )
    }
}
