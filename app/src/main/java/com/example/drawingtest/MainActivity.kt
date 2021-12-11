package com.example.drawingtest

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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

//                        .dragGestureFilter(object: DragObserver {
//                            override fun onDrag(dragDistance: Offset): Offset {
//                                Log.d("Track", "onActionMove ${dragDistance.x} | ${dragDistance.y}")
//                                return super.onDrag(dragDistance)
//                            }
//                            override fun onStart(downPosition: Offset) {
//                                Log.d("Track", "onActionDown ${downPosition.x} | ${downPosition.y}")
//                                super.onStart(downPosition)
//                            }
//                            override fun onStop(velocity: Offset) {
//                                Log.d("Track", "onStop ${velocity.x} | ${velocity.y}")
//                                super.onStop(velocity)
//                            }
//                        }, { true })
//                        .tapGestureFilter {
//                            Log.d("NGVL", "onActionUp ${it.x} | ${it.y}")
//                        }

                        .pointerInteropFilter(canvasDisallowInterceptTouchEvent) {
                            Log.d("TAG", "CANVAS_POINTER")
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
                    ) {

                        action.value.forEach {
//                            Log.d("TAG", "DRAW")
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
                            .height(400.dp)
                            .pointerInteropFilter(RequestDisallowInterceptTouchEvent()) {
                                Log.d("TAG", "BOX_POINTER")
                                canvasDisallowInterceptTouchEvent.invoke(true)
                                true
                            }
                    ) {

                    }

                }
            }
        }
    }
}

fun MotionEvent.getCoordinates(): Pair<Float, Float> {
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