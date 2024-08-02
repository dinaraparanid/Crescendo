package com.paranid5.crescendo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.presentation.entity.ReleaseUiState
import com.paranid5.crescendo.ui.foundation.getOrNull
import com.paranid5.crescendo.utils.extensions.simpleShadow
import com.paranid5.crescendo.view_model.MainState
import com.paranid5.crescendo.view_model.MainUiIntent
import dev.jeziellago.compose.markdowntext.MarkdownText

private val TitleShadow = 25.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateCheckerDialog(
    state: MainState,
    onUiIntent: (MainUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isUpdateDialogShown)
        state.releaseState.getOrNull()?.let { release ->
            BasicAlertDialog(
                modifier = modifier,
                onDismissRequest = { onUiIntent(MainUiIntent.DismissVersionDialog) },
            ) {
                UpdateCheckerDialogContent(
                    newVersion = release,
                    onUiIntent = onUiIntent,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(dimensions.padding.medium)
                        .clip(RoundedCornerShape(dimensions.corners.medium))
                        .background(brush = colors.background.gradient),
                )
            }
        }
}

@Composable
private fun UpdateCheckerDialogContent(
    newVersion: ReleaseUiState,
    onUiIntent: (MainUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    VersionLabel(
        versionName = newVersion.name,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )

    Spacer(Modifier.height(dimensions.padding.big))

    VersionContent(versionBody = newVersion.body)

    Spacer(Modifier.height(dimensions.padding.medium))

    DownloadButton(
        versionUrl = newVersion.htmlUrl,
        onUiIntent = onUiIntent,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
}

@Composable
private fun VersionLabel(
    versionName: String,
    modifier: Modifier = Modifier,
) = MarkdownText(
    markdown = "# ${stringResource(R.string.update_available)}: $versionName",
    color = colors.primary,
    style = typography.captionSm,
    maxLines = 1,
    modifier = modifier.simpleShadow(elevation = TitleShadow),
)

@Composable
private fun VersionContent(
    versionBody: String,
    modifier: Modifier = Modifier,
) = MarkdownText(
    markdown = versionBody,
    color = colors.primary,
    style = typography.caption,
    modifier = modifier,
)

@Composable
private fun DownloadButton(
    versionUrl: String,
    onUiIntent: (MainUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Button(
    modifier = modifier,
    colors = ButtonDefaults.buttonColors(containerColor = colors.background.alternative),
    content = { DownloadLabel() },
    onClick = { onUiIntent(MainUiIntent.OpenVersionPage(versionUrl)) },
)

@Composable
private fun DownloadLabel(modifier: Modifier = Modifier) = Text(
    text = stringResource(R.string.download),
    color = colors.primary,
    style = typography.regular,
    modifier = modifier,
)
