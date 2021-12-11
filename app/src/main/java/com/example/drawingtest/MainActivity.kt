package com.example.drawingtest

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.FLAG_WINDOW_IS_OBSCURED
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.drawingtest.ui.theme.DrawingTestTheme
import kotlin.math.floor


data class Point(val x: Float, val y: Float)


@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    private val action: MutableState<MutableMap<String, Point>> =
        mutableStateOf(mutableMapOf(), neverEqualPolicy())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrawingTestTheme {
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize()
                ) {
                    val canvasDisallowInterceptTouchEvent = RequestDisallowInterceptTouchEvent()

                    Canvas(modifier = Modifier
                        .size(200.dp, 400.dp)
                        .align(Alignment.Center)
                        .onGloballyPositioned { layoutCoordinates -> layoutCoordinates.size }
                        .background(Color.White)
//                        .graphicsLayer(
//                            scaleX = 1f,
//                            scaleY = 1f,
//                            translationX = 0f,
//                            translationY = 0f
//                        )
                        .drawWithPointerInput(action)
//                        .drawWithPointerInteropFilter(action)
                    ) {

                        action.value.forEach {
                            drawRect(
                                color = Color.Blue,
                                topLeft = Offset(it.value.x, it.value.y),
                                size = Size(9f, 9f)
                            )
                        }
                    }

                    Box(
                        Modifier
                            .background(Color.Green)
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(300.dp)
                            .pointerInteropFilter {
                                Log.d("TAG", "BOX_POINTER")
//                                canvasDisallowInterceptTouchEvent.invoke(true)
                                false
                            }
                    ) { }
                    Box(
                        Modifier
                            .background(Color.Transparent)
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 300.dp)
                            .height(100.dp)
                            .pointerInteropFilter {
                                Log.d("TAG", "TRANSPARENT_BOX_POINTER")
                                false
                            }

                    ) {}
                }
            }
        }
    }
}


fun Modifier.drawWithPointerInput(action: MutableState<MutableMap<String, Point>>) = this
    .pointerInput(Unit) {
        Log.d("TAG", "detect1")
        detectDragGestures(
            onDrag = { change, dragAmount ->
                action.value =
                    action.value.apply {
                        val coordinates = change.position.getCoordinates()
                        putAll(get3x3Square(coordinates))
                    }
            }
        )
    }
    .pointerInput(Unit) {
        Log.d("TAG", "detect2")
        detectTapGestures(
            onTap = {
                action.value =
                    action.value.apply {
                        val coordinates = it.getCoordinates()
                        putAll(get3x3Square(coordinates))
                    }
            }
        )
    }


@ExperimentalComposeUiApi
fun Modifier.drawWithPointerInteropFilter(action: MutableState<MutableMap<String, Point>>) =
    this.pointerInteropFilter {
        if (it.flags and FLAG_WINDOW_IS_OBSCURED != 0) {
            Log.d("TAG", "CANVAS_POINTER_OBSCURED")
            return@pointerInteropFilter true
        }
        when (it.action) {
            MotionEvent.ACTION_DOWN -> {
                action.value =
                    action.value.apply {
                        val coordinates = it.getCoordinates()
                        putAll(get3x3Square(coordinates))
                    }
            }
            MotionEvent.ACTION_MOVE -> {
                action.value =
                    action.value.apply {
                        val coordinates = it.getCoordinates()
                        putAll(get3x3Square(coordinates))
                    }
            }
            else -> null
        }
        true
    }


fun MotionEvent.getCoordinates(): Pair<Float, Float> {
    return Pair(floor(x / 10) * 10 + 1, floor(y / 10) * 10 + 1)
}

fun Offset.getCoordinates(): Pair<Float, Float> {
    return Pair(floor(x / 10) * 10 + 1, floor(y / 10) * 10 + 1)
}

fun get3x3Square(point: Pair<Float, Float>): Map<String, Point> {
    val x = point.first
    val y = point.second
    return mapOf(
        getKey(x, y) to Point(x, y),
        getKey(x + 10, y) to Point(x + 10, y),
        getKey(x + 20, y) to Point(x + 20, y),
        getKey(x, y + 10) to Point(x, y + 10),
        getKey(x + 10, y + 10) to Point(x + 10, y + 10),
        getKey(x + 20, y + 10) to Point(x + 20, y + 10),
        getKey(x, y + 20) to Point(x, y + 20),
        getKey(x + 10, y + 20) to Point(x + 10, y + 20),
        getKey(x + 20, y + 20) to Point(x + 20, y + 20)
    )
}

fun getKey(x: Float, y: Float): String = "${x * 10000 + y}"

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DrawingTestTheme {
    }
}