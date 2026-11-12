package com.palestiner.pass

import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.WindowState

fun showOrNo(state: WindowState, visible: MutableState<Boolean>) {
    visible.value = !visible.value
    state.isMinimized = !state.isMinimized
    state.isMinimized = !state.isMinimized
}
