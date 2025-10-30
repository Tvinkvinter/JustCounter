package com.atarusov.justcounter.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.atarusov.justcounter.ui.theme.black
import com.atarusov.justcounter.ui.theme.white

fun Color.getContrastContentColor() = if (this.luminance() < 0.5f) white else black