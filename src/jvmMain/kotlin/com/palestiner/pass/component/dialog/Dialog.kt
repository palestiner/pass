package com.palestiner.pass.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.palestiner.pass.model.KeyValue
import com.palestiner.pass.style.columnModifier
import com.palestiner.pass.style.fontColor
import com.palestiner.pass.style.mainColor

@Composable
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalUnitApi::class
)
fun CreateDialog(
    text: String,
    keyValues: MutableList<KeyValue>,
    onCloseRequest: (showCreateButton: Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (!showDialog) {
            Button(
                content = { Text("Create") },
                onClick = { showDialog = true }
            )
        }
    }
    if (showDialog) {
        Dialog(
            title = "Create",
            undecorated = true,
            state = rememberDialogState(
                width = 200.dp,
                height = 300.dp
            ),
            onCloseRequest = {
                showDialog = false
                onCloseRequest(false)
            },
            onKeyEvent = {
                if (it.type == KeyEventType.KeyUp && it.key == Key.Escape) {
                    showDialog = false
                    onCloseRequest(true)
                }
                false
            }) {
            var name by remember { mutableStateOf(text) }
            var value by remember { mutableStateOf("") }
            Column(modifier = columnModifier.fillMaxSize()) {

                TextField(value = name,
                    singleLine = true,
                    label = { Text("Name") },
                    textStyle = TextStyle(
                        Color(fontColor),
                        fontSize = TextUnit(
                            20F,
                            TextUnitType.Sp
                        )
                    ),
                    modifier = Modifier.background(Color(mainColor)).fillMaxWidth().height(60.dp),
                    onValueChange = {
                        name = it
                    })
                TextField(value = value,
                    singleLine = true,
                    label = { Text("Value") },
                    textStyle = TextStyle(
                        Color(fontColor),
                        fontSize = TextUnit(
                            20F,
                            TextUnitType.Sp
                        )
                    ),
                    modifier = Modifier.background(Color(mainColor)).fillMaxWidth().height(60.dp),
                    onValueChange = {
                        value = it
                    })
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        Button(onClick = {
                            if (name.isNotEmpty() && value.isNotEmpty()) {
                                val keyValue = KeyValue(
                                    name,
                                    value
                                )
                                KeyValue.savePair(keyValue)
                                keyValues.add(keyValue)
                                onCloseRequest(false)
                            }
                        },
                            content = { Text("Save") })
                    }
                )
            }
        }
    }
}
