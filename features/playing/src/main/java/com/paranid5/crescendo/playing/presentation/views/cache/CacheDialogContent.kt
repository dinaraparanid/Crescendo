package com.paranid5.crescendo.playing.presentation.views.cache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun CacheDialogContent(
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    Spacer(Modifier.height(15.dp))

    CacheDialogLabel(Modifier.align(Alignment.CenterHorizontally))

    Spacer(Modifier.height(25.dp))

    FilenameInput(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    )

    Spacer(Modifier.height(10.dp))

    SaveOptionsMenu(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    )

    Spacer(Modifier.height(20.dp))

    ConfirmButton(
        isDialogShownState = isDialogShownState,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Spacer(Modifier.height(10.dp))
}