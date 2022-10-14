package com.palestiner.pass.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.focus.focusRequester
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
    ExperimentalFoundationApi::class
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
    val state = rememberLazyListState()

    MaterialTheme(darkColors(primary = Color(fontColor))) {
        Column(
            modifier = columnModifier.onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Escape) {
                    text = ""
                    showCreateButton = pairs.none { it.name.contains(text) }
                    mainInputFocusRequester.requestFocus()
                    coroutineScope.launch {
                        state.scrollToItem(0)
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
            )

            Box {
                val filteredPairs = mutableStateListOf<KeyValue>().apply {
                    addAll(pairs.filter { it.name.contains(text) }.toMutableList())
                }
                val itemsFocusRequesters = remember { filteredPairs.map {  } }
                LazyColumn(state = state) {
                    if (text.isEmpty()) {
                        coroutineScope.launch {
                            state.scrollToItem(0)
                        }
                    }
                    items(
                        items = filteredPairs,
                        key = { it.name }
                    ) {
                        TextButton(
                            onClick = {
                                copyClipboard(it.value)
                                showOrNo(windowState, mainWindowVisible)
                            },
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp)
                                .onKeyEvent { keyEvent ->
                                    if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Delete) {
                                        pairs.remove(it)
                                        filteredPairs.remove(it)
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
                    adapter = rememberScrollbarAdapter(scrollState = state)
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

fun copyClipboard(text: String) {
    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringTransferable(text), null)
}

private fun refresh(): MutableList<KeyValue> {
    return KeyValue.loadPairs()
}
