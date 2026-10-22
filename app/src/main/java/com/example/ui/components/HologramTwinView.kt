package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.webkit.WebViewAssetLoader
import com.example.data.model.BiologicalTwinResult
import com.example.data.model.BiomarkerProfile
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream

private const val ASSET_HOST = WebViewAssetLoader.DEFAULT_DOMAIN
private const val TWIN_URL = "https://$ASSET_HOST/assets/twin/index.html?transparent=1"
private const val READY_TIMEOUT_MS = 4_000L

private enum class SceneState { Loading, Ready, Failed }

/**
 * Digital twin rendered as a three.js hologram inside a locked-down WebView
 * (assets/twin, served offline through WebViewAssetLoader).
 *
 * Kotlin → JS: window.twin.update(json) / setView(name) / setAutoRotate(bool)
 * JS → Kotlin: TwinBridge.onReady() / onNodeTap(id) / onError(message)
 *
 * If WebGL fails or the page doesn't report ready within 4 s, the canvas is replaced by a
 * notice; organ chips, the summary card and the deep-dive dialog keep working either way.
 */
@Composable
fun HologramTwinView(
    profile: BiomarkerProfile,
    twinResult: BiologicalTwinResult,
    modifier: Modifier = Modifier
) {
    val organNodes = remember(profile) { buildOrganNodes(profile) }
    var sceneState by remember { mutableStateOf(SceneState.Loading) }
    var isAutoRotating by remember { mutableStateOf(false) }
    var activeView by remember { mutableStateOf("front") }
    var selectedNode by remember { mutableStateOf<DigitalTwinNode?>(null) }
    var showFullDeepDiveDialog by remember { mutableStateOf(false) }
    val scene = remember { TwinScene() }

    LaunchedEffect(Unit) {
        delay(READY_TIMEOUT_MS)
        if (sceneState == SceneState.Loading) sceneState = SceneState.Failed
    }

    // Push state into the page once it's ready and whenever the inputs change.
    LaunchedEffect(sceneState, twinResult, organNodes) {
        if (sceneState == SceneState.Ready) scene.update(twinResult, organNodes)
    }
    LaunchedEffect(sceneState, isAutoRotating) {
        if (sceneState == SceneState.Ready) scene.setAutoRotate(isAutoRotating)
    }
    LaunchedEffect(sceneState, activeView) {
        if (sceneState == SceneState.Ready) scene.setView(activeView)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, CardBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. TOP HEADER & ORBIT CONTROLS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SleekPrimary)
                    )
                    Text(
                        text = "3D BIOLOGICAL TWIN",
                        color = SleekPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isAutoRotating) SleekPrimary.copy(alpha = 0.15f) else DarkSurfaceVariant)
                            .border(1.dp, if (isAutoRotating) SleekPrimary else CardBorder, RoundedCornerShape(8.dp))
                            .clickable { isAutoRotating = !isAutoRotating }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isAutoRotating) "Rotation: ON" else "Rotation: PAUSED",
                            color = if (isAutoRotating) SleekPrimary else TextMuted,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            isAutoRotating = false
                            selectedNode = null
                            activeView = "front"
                            // Re-send even if "front" was already active, to undo manual orbiting.
                            scene.setView("front")
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 2. HOLOGRAM VIEWPORT
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF0D3440), Color(0xFF061219), Color(0xFF04090E))
                        )
                    )
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                if (sceneState != SceneState.Failed) {
                    HologramWebView(
                        scene = scene,
                        onReady = { if (sceneState == SceneState.Loading) sceneState = SceneState.Ready },
                        onNodeTap = { id ->
                            organNodes.find { it.id == id }?.let { node ->
                                selectedNode = node
                                isAutoRotating = false
                                showFullDeepDiveDialog = true
                            }
                        },
                        onError = { sceneState = SceneState.Failed },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                when (sceneState) {
                    SceneState.Loading -> CircularProgressIndicator(
                        color = SleekPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(28.dp)
                    )
                    SceneState.Failed -> Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "3D hologram is not available on this device",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Use the organ systems below to open each clinical report.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    SceneState.Ready -> Unit
                }

                // HUD Overlay: LRS Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xD90F131C))
                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SleekPrimary))
                            Text(
                                text = "LRS: ${twinResult.longevityReserveScore.toInt()}/100 • PhenoAge: ${twinResult.phenoAge} yrs",
                                color = SleekPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // Camera Angle Quick Buttons (Front, Side, 3D)
                if (sceneState == SceneState.Ready) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("FRONT" to "front", "SIDE" to "side", "3D" to "3d").forEach { (label, view) ->
                            val isActive = activeView == view
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isActive) SleekPrimary.copy(alpha = 0.18f) else Color(0xB3111520))
                                    .border(1.dp, if (isActive) SleekPrimary else CardBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        activeView = view
                                        isAutoRotating = false
                                        scene.setView(view)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isActive) SleekPrimary else TextSecondary,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Inspect Callout Button
                selectedNode?.let { node ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(node.color.copy(alpha = 0.20f))
                            .border(1.dp, node.color, RoundedCornerShape(8.dp))
                            .clickable { showFullDeepDiveDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "View Clinical Report",
                                color = node.color,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.OpenInFull,
                                contentDescription = "Expand",
                                tint = node.color,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }

            // 3. ORGAN SYSTEM CHIPS
            OrganChipsRow(
                organNodes = organNodes,
                twinResult = twinResult,
                selectedNode = selectedNode,
                onSelect = { node ->
                    selectedNode = node
                    isAutoRotating = false
                }
            )

            // 4. SELECTED ORGAN COMPACT HUD CARD
            AnimatedVisibility(
                visible = selectedNode != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                selectedNode?.let { node ->
                    OrganSummaryCard(
                        node = node,
                        profile = profile,
                        twinResult = twinResult,
                        onClose = { selectedNode = null },
                        onOpenReport = { showFullDeepDiveDialog = true }
                    )
                }
            }
        }
    }

    // 5. FULL CLINICAL DEEP DIVE DIALOG
    val node = selectedNode
    if (showFullDeepDiveDialog && node != null) {
        OrganDeepDiveDialog(node = node, onDismiss = { showFullDeepDiveDialog = false })
    }
}

/** Handle on the page's `window.twin` API. Calls are dropped while no WebView is attached. */
private class TwinScene {
    var webView: WebView? = null

    fun update(result: BiologicalTwinResult, organNodes: List<DigitalTwinNode>) {
        val nodes = JSONArray()
        organNodes.forEach { node ->
            val score = node.scoreExtractor(result)
            nodes.put(
                JSONObject()
                    .put("id", node.id)
                    .put("score", score)
                    .put("status", statusFor(score))
            )
        }
        val payload = JSONObject()
            .put("lrs", result.longevityReserveScore)
            .put("phenoAge", result.phenoAge)
            .put("nodes", nodes)
        // JSONObject.toString() yields a valid JS object literal.
        call("update($payload)")
    }

    fun setView(view: String) = call("setView(${JSONObject.quote(view)})")

    fun setAutoRotate(enabled: Boolean) = call("setAutoRotate($enabled)")

    private fun call(expression: String) {
        webView?.evaluateJavascript("window.twin && window.twin.$expression", null)
    }

    private fun statusFor(score: Double) = when {
        score >= 80 -> "good"
        score >= 60 -> "warn"
        else -> "risk"
    }
}

/** Receives callbacks from the page; JavaBridge threads are hopped onto the main thread. */
private class TwinBridge(
    private val onReady: () -> Unit,
    private val onNodeTap: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private val main = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onReady() {
        main.post { onReady.invoke() }
    }

    @JavascriptInterface
    fun onNodeTap(id: String) {
        main.post { onNodeTap.invoke(id) }
    }

    @JavascriptInterface
    fun onError(message: String) {
        main.post { onError.invoke(message) }
    }
}

/** Serves only the bundled assets; every other request and navigation is refused. */
private class AssetOnlyWebViewClient(
    private val assetLoader: WebViewAssetLoader,
    private val onRendererGone: () -> Unit
) : WebViewClient() {
    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse {
        if (request.url.host == ASSET_HOST) {
            assetLoader.shouldInterceptRequest(request.url)?.let { return it }
        }
        return WebResourceResponse("text/plain", "utf-8", 404, "Not Found", emptyMap(), ByteArrayInputStream(ByteArray(0)))
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
        !isTwinPage(request.url)

    override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
        // Returning true keeps the app alive; the composable swaps in the fallback and destroys the view.
        onRendererGone()
        return true
    }

    private fun isTwinPage(url: Uri) = url.scheme == "https" && url.host == ASSET_HOST && url.path == "/assets/twin/index.html"
}

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
private fun HologramWebView(
    scene: TwinScene,
    onReady: () -> Unit,
    onNodeTap: (String) -> Unit,
    onError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnReady by rememberUpdatedState(onReady)
    val currentOnNodeTap by rememberUpdatedState(onNodeTap)
    val currentOnError by rememberUpdatedState(onError)
    val lifecycleOwner = LocalLifecycleOwner.current

    // Pausing the WebView hides the page, which stops the three.js render loop (visibilitychange).
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> scene.webView?.onPause()
                Lifecycle.Event.ON_RESUME -> scene.webView?.onResume()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AndroidView(
        modifier = modifier,
        factory = { context: Context ->
            val assetLoader = WebViewAssetLoader.Builder()
                .setDomain(ASSET_HOST)
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
                .build()
            WebView(context).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = WebView.OVER_SCROLL_NEVER
                settings.apply {
                    javaScriptEnabled = true
                    allowFileAccess = false
                    allowContentAccess = false
                    domStorageEnabled = false
                    javaScriptCanOpenWindowsAutomatically = false
                    setSupportMultipleWindows(false)
                    setGeolocationEnabled(false)
                    mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                }
                webViewClient = AssetOnlyWebViewClient(assetLoader) { currentOnError() }
                addJavascriptInterface(
                    TwinBridge(
                        onReady = { currentOnReady() },
                        onNodeTap = { currentOnNodeTap(it) },
                        onError = { currentOnError() }
                    ),
                    "TwinBridge"
                )
                scene.webView = this
                loadUrl(TWIN_URL)
            }
        },
        onRelease = { webView ->
            if (scene.webView === webView) scene.webView = null
            webView.removeJavascriptInterface("TwinBridge")
            webView.stopLoading()
            webView.destroy()
        }
    )
}
