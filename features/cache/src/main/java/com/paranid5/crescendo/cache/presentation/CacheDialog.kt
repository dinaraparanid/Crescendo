package com.paranid5.crescendo.cache.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.cache.presentation.composition_local.LocalDownloadUrl
import com.paranid5.crescendo.cache.presentation.view.CacheDialogContent
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CacheDialog(
    url: String,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    var isDialogShown by isDialogShownState

    if (isDialogShown)
        CompositionLocalProvider(LocalDownloadUrl provides url) {
            BasicAlertDialog(
                onDismissRequest = { isDialogShown = false },
                modifier = modifier
            ) {
                Card(
                    modifier = modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.background)
                ) {
                    CacheDialogContent(
                        isDialogShownState = isDialogShownState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
}