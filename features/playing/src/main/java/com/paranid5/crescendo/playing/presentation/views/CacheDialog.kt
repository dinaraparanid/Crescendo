package com.paranid5.crescendo.playing.presentation.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.playing.presentation.views.cache.CacheDialogContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CacheDialog(
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    var isDialogShown by isDialogShownState

    if (isDialogShown)
        BasicAlertDialog(
            onDismissRequest = { isDialogShown = false },
            modifier = modifier
        ) {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.background)
            ) {
                CacheDialogContent(
                    isDialogShownState = isDialogShownState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
}