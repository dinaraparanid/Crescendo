package com.paranid5.crescendo.presentation.main.trimmer.views

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.trimRangeFlow
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun FileSaveButton(
    viewModel: TrimmerViewModel,
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var isFileSaveDialogShown by isFileSaveDialogShownState
    val trimRange by viewModel.trimRangeFlow.collectAsState(initial = TrimRange())

    val isClickable by remember(trimRange) {
        derivedStateOf { trimRange.totalDurationSecs > 0 }
    }

    Button(
        modifier = modifier,
        enabled = isClickable,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.backgroundAlternative
        ),
        onClick = { isFileSaveDialogShown = true }
    ) {
        Text(
            text = stringResource(R.string.save),
            color = colors.fontColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = textModifier
        )
    }
}