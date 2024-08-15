package com.example.bugreport

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

private const val CAROUSEL_CROSSFADE_ANIMATION_DURATION = 1_000
private const val SLIDING_IMAGE_ANIMATION_DURATION = 3_000L
private const val SLIDING_IMAGE_START_OFFSET = 0.25F
private const val SLIDING_IMAGE_END_OFFSET = 0.75F
private const val SLIDING_IMAGE_MIN_WIDTH_BUFFER = 200

@Composable
internal fun HeroGallery(
    dogPics: List<String> = emptyList()
) {
    val areAnimationsDisabled = !LocalContext.current.areAnimationsEnabled()
    var currentPhotoIndex by rememberSaveable(dogPics) { mutableIntStateOf(0) }
    val isAnimating by rememberSaveable(dogPics) { mutableStateOf(!areAnimationsDisabled) }

    Box {
        Crossfade(
            targetState = currentPhotoIndex,
            animationSpec = tween(
                durationMillis = CAROUSEL_CROSSFADE_ANIMATION_DURATION,
                easing = LinearEasing
            ),
            label = "crossfade animation"
        ) { position ->
            SlidingImage(
                imageUrl = dogPics[position],
                isAnimating = isAnimating,
                scrollFinished = {
                    Log.d("bugreportDebug", "scroll finished, photo index: $currentPhotoIndex")

                    currentPhotoIndex = dogPics.getUpdatedListPosition(
                        current = currentPhotoIndex,
                        desiredChange = 1
                    )
                },
                flipForward = {
                    Log.d("bugreportDebug", "flip forward, photo index: $currentPhotoIndex")

                    currentPhotoIndex = dogPics.getUpdatedListPosition(
                        current = currentPhotoIndex,
                        desiredChange = 1
                    )
                },
                flipBackward = {
                    currentPhotoIndex = dogPics.getUpdatedListPosition(
                        current = currentPhotoIndex,
                        desiredChange = -1
                    )
                }
            )
        }
    }
}

private fun <T> List<T>.getUpdatedListPosition(current: Int, desiredChange: Int): Int {
    return Math.floorMod(current + desiredChange, this.size)
}

@Composable
private fun SlidingImage(
    imageUrl: String,
    isAnimating: Boolean,
    scrollFinished: () -> Unit,
    flipForward: () -> Unit,
    flipBackward: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var loaded by remember { mutableStateOf(false) }

    val shouldScroll = loaded && isAnimating
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = shouldScroll) {
        val value = scrollState.value
        val maxValue = scrollState.maxValue
        val offsetMaxValue = (maxValue * SLIDING_IMAGE_END_OFFSET).toInt()
        val offsetMinValue = (maxValue * SLIDING_IMAGE_START_OFFSET).toInt()

        val rightPosition =
            if (value == 0 && maxValue > 0) {
                offsetMaxValue
            } else {
                value
            }

        val leftPosition =
            if (value <= offsetMinValue) {
                0
            } else {
                offsetMinValue
            }

        val percentageDurationRemaining =
            if (rightPosition >= offsetMaxValue || maxValue - leftPosition <= 0) {
                1f
            } else {
                (rightPosition - leftPosition).toFloat() / (maxValue - leftPosition)
            }

        val animationDuration =
            (SLIDING_IMAGE_ANIMATION_DURATION * percentageDurationRemaining).toInt()

        with(scrollState) {
            scrollTo(rightPosition)
            if (shouldScroll) {
                Log.d("bugreportDebug", "START scrollState.value: ${scrollState.value}  rightPosition: $rightPosition  animationDuration: $animationDuration")

                animateScrollTo(
                    value = leftPosition,
                    animationSpec = TweenSpec(
                        animationDuration,
                        0,
                        LinearEasing
                    )
                )
                Log.d("bugreportDebug", "END scrollState.value: ${scrollState.value}  rightPosition: $rightPosition  animationDuration: $animationDuration")
                if (scrollState.value != rightPosition) {
                    scrollFinished()
                }
            }
        }
    }

    var currentChangeAmount by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .height(490.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (currentChangeAmount < 0) {
                                flipForward()
                            } else if (currentChangeAmount > 0) {
                                flipBackward()
                            }
                            currentChangeAmount = 0 // consume the change
                        }
                    },
                    onHorizontalDrag = { _, changeAmount ->
                        scope.launch {
                            currentChangeAmount = changeAmount.toInt()
                        }
                    }
                )
            }
            .horizontalScroll(state = scrollState, enabled = false)
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        AsyncImage(
            model = imageUrl,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .requiredWidth(screenWidth + SLIDING_IMAGE_MIN_WIDTH_BUFFER.dp)
                .fillMaxHeight(),
            onSuccess = { loaded = true }
        )
    }
}

private fun Context.areAnimationsEnabled(): Boolean {
    val animationScale = Settings.Global.getFloat(
        this.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1.0F
    )
    return animationScale != 0.0F
}
