package ir.mostafa.launcher.ui.ktx

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.dp

val ButtonDefaults.TextButtonWithTrailingIconContentPadding
    get() = PaddingValues(
        start = 16.dp,
        top = ContentPadding.calculateTopPadding(),
        end = 12.dp,
        bottom = ContentPadding.calculateBottomPadding()
    )