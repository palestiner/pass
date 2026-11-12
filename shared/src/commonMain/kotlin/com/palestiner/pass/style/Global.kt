package com.palestiner.pass.style

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

const val mainColor = 0xFF282c34
const val fontColor = 0xFF51afef

val columnModifier = Modifier.background(Color(mainColor))
    .border(border = BorderStroke(1.dp, Color(fontColor)))
    .fillMaxSize()

