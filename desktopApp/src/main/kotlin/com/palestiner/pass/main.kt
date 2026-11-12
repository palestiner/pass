package com.palestiner.pass

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import ca.weblite.objc.Client
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.NativeHookException
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import com.palestiner.pass.component.App
import com.palestiner.pass.icon.app.AppIcon
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.SwingUtilities

private val isMac = System.getProperty("os.name").lowercase().contains("mac")

private fun setActivationPolicy(policy: Long) {
    if (!isMac) return
    try {
        val nsApp = Client.getInstance().sendProxy("NSApplication", "sharedApplication")
        nsApp.send("setActivationPolicy:", policy)
        if (policy == 0L) {
            // явно активируем приложение, иначе фокус останется у предыдущей программы
            nsApp.send("activateIgnoringOtherApps:", true)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

fun hideApp(window: ComposeWindow?) {
    setActivationPolicy(2L) // Prohibited: убрать из Dock и Cmd+Tab
    window?.isVisible = false
}

fun showApp(window: ComposeWindow?) {
    setActivationPolicy(0L) // Regular: вернуть иконку в Dock
    window?.isVisible = true
    window?.toFront()
}

fun main() = application {
    val visible = remember { mutableStateOf(true) }
    var appWindow by remember { mutableStateOf<ComposeWindow?>(null) }
    val state = rememberWindowState(
        width = 200.dp,
        height = 300.dp,
        position = WindowPosition(Alignment.Center)
    )
    var isOpen by remember { mutableStateOf(true) }
    var onTop by remember { mutableStateOf(false) }

    if (isOpen) {
        val trayState = rememberTrayState()
        Tray(
            state = trayState,
            icon = AppIcon,
            menu = { Item("Exit", onClick = { isOpen = false }) },
            onAction = { showOrNo(visible, appWindow) }
        )

        DisposableEffect(Unit) {
            Logger.getLogger(GlobalScreen::class.java.`package`.name).apply {
                level = Level.WARNING
                useParentHandlers = false
            }

            try {
                GlobalScreen.registerNativeHook()
            } catch (e: NativeHookException) {
                e.printStackTrace()
            }

            val listener = object : NativeKeyListener {
                override fun nativeKeyPressed(e: NativeKeyEvent) {
                    when (e.keyCode) {
                        NativeKeyEvent.VC_MINUS ->
                            if (e.modifiers and NativeKeyEvent.ALT_MASK != 0) {
                                showOrNo(visible, appWindow)
                            }
                        NativeKeyEvent.VC_EQUALS ->
                            if (e.modifiers and NativeKeyEvent.ALT_MASK != 0) {
                                onTop = !onTop
                                showApp(appWindow)
                            }
                        NativeKeyEvent.VC_X ->
                            if (e.modifiers and NativeKeyEvent.CTRL_MASK != 0 &&
                                e.modifiers and NativeKeyEvent.ALT_MASK != 0
                            ) {
                                isOpen = false
                            }
                    }
                }
            }
            GlobalScreen.addNativeKeyListener(listener)

            onDispose {
                GlobalScreen.removeNativeKeyListener(listener)
                try {
                    GlobalScreen.unregisterNativeHook()
                } catch (e: NativeHookException) {
                    e.printStackTrace()
                }
            }
        }

        Window(
            onCloseRequest = { showOrNo(visible, appWindow) },
            alwaysOnTop = onTop,
            icon = AppIcon,
            resizable = false,
            undecorated = true,
            state = state,
            visible = visible.value,
            focusable = true,
            title = "pass"
        ) {
            appWindow = this.window
            App(visible) { showOrNo(visible, appWindow) }
        }
    }
}

fun showOrNo(visible: MutableState<Boolean>, window: ComposeWindow?) {
    SwingUtilities.invokeLater {
        if (visible.value) {
            hideApp(window)
            visible.value = false
        } else {
            showApp(window)
            visible.value = true
        }
    }
}
