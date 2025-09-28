package com.atarusov.justcounter.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Color.getContrastContentColor() = if (this.luminance() < 0.5f) Color.White else Color.Black