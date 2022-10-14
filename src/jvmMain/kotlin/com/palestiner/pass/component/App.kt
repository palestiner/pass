package com.palestiner.pass.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import com.palestiner.pass.clipboard.StringTransferable
import com.palestiner.pass.component.dialog.CreateDialog
import com.palestiner.pass.model.KeyValue
import com.palestiner.pass.showOrNo
import com.palestiner.pass.style.columnModifier
import com.palestiner.pass.style.fontColor
import com.palestiner.pass.style.mainColor
import kotlinx.coroutines.launch
import java.awt.Toolkit


@Preview
@Composable
@OptIn(
    ExperimentalUnitApi::class,
    ExperimentalComposeUiApi::class,
)
fun App(
    windowState: WindowState,
    mainWindowVisible: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    val pairs = refresh()
    val mainInputFocusRequester = remember { FocusRequester() }
    var showCreateButton by remember { mutableStateOf(pairs.isEmpty()) }
    val lazyListState = rememberLazyListState()

    MaterialTheme(darkColors(primary = Color(fontColor))) {
        Column(
            modifier = columnModifier.onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Escape) {
                    text = ""
                    showCreateButton = pairs.none { it.name.contains(text) }
                    mainInputFocusRequester.requestFocus()
                    coroutineScope.launch {
                        lazyListState.scrollToItem(0)
                    }
                }
                false
            }
        ) {
            OutlinedTextField(
                value = text,
                singleLine = true,
                textStyle = TextStyle(
                    Color(fontColor),
                    fontSize = TextUnit(20F, TextUnitType.Sp)
                ),
                onValueChange = { inputText ->
                    text = inputText
                    showCreateButton = pairs.none { it.name.contains(inputText) }
                },
                modifier = Modifier.background(Color(mainColor))
                    .focusRequester(mainInputFocusRequester)
                    .fillMaxWidth()
                    .height(60.dp)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                lazyListState.scrollToItem(0)
                            }
                        }
                    }
            )

            Box {
                val filteredPairs = mutableStateListOf<KeyValue>().apply {
                    addAll(pairs.filter { it.name.contains(text) }.toMutableList())
                }
                LazyColumn(state = lazyListState) {
                    if (text.isEmpty()) {
                        coroutineScope.launch {
                            lazyListState.scrollToItem(0)
                        }
                    }
                    items(
                        items = filteredPairs,
                        key = { it.name }
                    ) {
                        val index = filteredPairs.indexOf(it)
                        TextButton(
                            onClick = {
                                copyClipboard(it.value)
                                showOrNo(windowState, mainWindowVisible)
                            },
                            modifier = Modifier.fillMaxWidth()
                                .focusOrder {
                                    if (index == filteredPairs.size - 1) {
                                        next = mainInputFocusRequester
                                    }
                                }
                                .onFocusEvent { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            lazyListState.animateScrollToItem(index)
                                        }
                                    }
                                }
                                .padding(start = 10.dp, end = 10.dp)
                                .onKeyEvent { keyEvent ->
                                    if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Delete) {
                                        pairs.remove(it)
                                        filteredPairs.remove(it)
                                        coroutineScope.launch {
                                            KeyValue.saveState(pairs)
                                            mainInputFocusRequester.requestFocus()
                                        }
                                        return@onKeyEvent true
                                    }
                                    false
                                }
                        ) {
                            Text(
                                text = it.name,
                                fontSize = TextUnit(18F, TextUnitType.Sp)
                            )
                        }
                        Divider()
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState = lazyListState)
                )

                if (showCreateButton) {
                    CreateDialog(text = text, keyValues = pairs) {
                        showCreateButton = it
                        mainInputFocusRequester.requestFocus()
                    }
                }
            }
        }

        if (mainWindowVisible.value) {
            LaunchedEffect(Unit) {
                mainInputFocusRequester.requestFocus()
            }
        }
    }
}

private fun copyClipboard(text: String) {
    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringTransferable(text), null)
}

private fun refresh(): MutableList<KeyValue> {
    return KeyValue.loadPairs()
}
