package com.paranid5.crescendo.presentation.main.playing.views.cache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CacheDialogContent(
    fileSaveOptions: Array<String>,
    format: Formats,
    trimRange: TrimRange,
    isButtonClickable: Boolean,
    isDialogShownState: MutableState<Boolean>,
    filenameState: MutableState<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var isDialogShown by isDialogShownState

    AlertDialog(
        modifier = modifier,
        onDismissRequest = { isDialogShown = false }
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.background)
        ) {
            CacheDialogContentImpl(
                fileSaveOptions = fileSaveOptions,
                format = format,
                trimRange = trimRange,
                isButtonClickable = isButtonClickable,
                isDialogShownState = isDialogShownState,
                filenameState = filenameState,
                selectedSaveOptionIndexState = selectedSaveOptionIndexState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CacheDialogContentImpl(
    fileSaveOptions: Array<String>,
    format: Formats,
    trimRange: TrimRange,
    isButtonClickable: Boolean,
    isDialogShownState: MutableState<Boolean>,
    filenameState: MutableState<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val filename by filenameState

    Column(modifier) {
        Spacer(Modifier.height(15.dp))

        CacheDialogLabel(Modifier.align(Alignment.CenterHorizontally))

        Spacer(Modifier.height(25.dp))

        FilenameInput(
            filenameState = filenameState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )

        Spacer(Modifier.height(10.dp))

        SaveOptionsMenu(
            fileSaveOptions = fileSaveOptions,
            selectedSaveOptionIndexState = selectedSaveOptionIndexState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )

        Spacer(Modifier.height(20.dp))

        ConfirmButton(
            format = format,
            trimRange = trimRange,
            isButtonClickable = isButtonClickable,
            filename = filename,
            isDialogShownState = isDialogShownState,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(10.dp))
    }
}