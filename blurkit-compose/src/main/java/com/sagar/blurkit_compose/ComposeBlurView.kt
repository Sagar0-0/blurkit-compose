package com.sagar.blurkit_compose

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

@Composable
fun ComposeBlurView(
    modifier: Modifier = Modifier,
    blurRadius: Float = 20f,
    content: @Composable () -> Unit
) {
    val mBlurRadius = remember(blurRadius) {
        blurRadius.coerceIn(1f,25f)
    }
    AndroidView(
        modifier = modifier.padding(0.dp),
        factory = { context ->
            ComposeBlurView(
                context = context,
                onSetContent = { composeView ->
                    composeView.apply {
                        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                        setContent {
                            content()
                        }
                    }
                }
            ).apply {
                val decorView = (context as ComponentActivity).window.decorView
                val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
                setupWith(rootView, RenderScriptBlur(context))

                setBlurRadius(mBlurRadius)
            }
        },
        update = {
            it.setBlurRadius(mBlurRadius)
        }
    )
}

class ComposeBlurView(context: Context, onSetContent: (ComposeView) -> Unit) :
    BlurView(context) {
    init {
        val composeView = LayoutInflater.from(context)
            .inflate(R.layout.blur_view_layout, null, true) as ComposeView
        addViewInLayout(
            composeView,
            0,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        onSetContent(composeView)
    }
}