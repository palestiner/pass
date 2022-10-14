package com.palestiner.pass

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.palestiner.pass.component.App
import com.palestiner.pass.icon.app.AppIcon
import com.tulskiy.keymaster.common.Provider
import com.tulskiy.keymaster.windows.WindowsProvider
import javax.swing.KeyStroke

fun main() = application {
    val visible = remember { mutableStateOf(true) }
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
            onAction = { showOrNo(state, visible) }
        )
        Window(
            onCloseRequest = {
                visible.value = !visible.value
                state.isMinimized = !state.isMinimized
            },
            alwaysOnTop = onTop,
            icon = AppIcon,
            resizable = false,
            undecorated = true,
            state = state,
            visible = visible.value,
            focusable = true,
            title = "pass"
        ) {
            val provider: WindowsProvider = Provider.getCurrentProvider(false) as WindowsProvider
            provider.register(KeyStroke.getKeyStroke("alt MINUS")) {
                showOrNo(state, visible)
            }
            provider.register(KeyStroke.getKeyStroke("alt PLUS")) {
                onTop = !onTop
                onTop = !onTop
            }
            provider.register(KeyStroke.getKeyStroke("ctrl alt X")) { isOpen = false }
            App(state, visible)
        }
    }
}

fun showOrNo(state: WindowState, visible: MutableState<Boolean>) {
    visible.value = !visible.value
    state.isMinimized = !state.isMinimized
    state.isMinimized = !state.isMinimized
}
