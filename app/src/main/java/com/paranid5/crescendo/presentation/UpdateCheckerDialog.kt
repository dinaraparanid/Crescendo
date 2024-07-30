package com.paranid5.crescendo.presentation

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.data.ktor_client.github.Release
import com.paranid5.crescendo.data.ktor_client.github.checkForUpdates
import com.paranid5.crescendo.presentation.composition_locals.LocalActivity
import com.paranid5.crescendo.utils.extensions.simpleShadow
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.ktor.client.HttpClient
import org.koin.compose.koinInject

private const val TAG = "UpdateCheckerDialog"

private val TitleShadow = 25.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateCheckerDialog(modifier: Modifier = Modifier, ktorClient: HttpClient = koinInject()) {
    var newVersion by remember { mutableStateOf<Release?>(null) }
    val isUpdateAvailable by remember { derivedStateOf { newVersion != null } }
    var isDialogShown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        newVersion = ktorClient.checkForUpdates()

        if (isUpdateAvailable) {
            Log.d(TAG, "New version is available: $newVersion")
            isDialogShown = true
        }
    }

    if (isDialogShown)
        BasicAlertDialog(onDismissRequest = { isDialogShown = false }) {
            Card(
                shape = RoundedCornerShape(dimensions.corners.medium),
                colors = CardDefaults.cardColors(containerColor = colors.background.primary),
                modifier = modifier.fillMaxWidth(),
            ) {
                UpdateCheckerDialogContent(
                    newVersion = newVersion!!,
                    modifier = Modifier.padding(dimensions.padding.medium),
                )
            }
        }
}

@Composable
private fun UpdateCheckerDialogContent(newVersion: Release, modifier: Modifier = Modifier) =
    Column(modifier) {
        VersionLabel(
            versionName = newVersion.name,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(dimensions.padding.big))

        VersionContent(versionBody = newVersion.body)

        Spacer(Modifier.height(dimensions.padding.medium))

        DownloadButton(
            versionUrl = newVersion.htmlUrl,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }

@Composable
private fun VersionLabel(versionName: String, modifier: Modifier = Modifier) {
    MarkdownText(
        markdown = "# ${stringResource(R.string.update_available)}: $versionName",
        color = colors.primary,
        style = typography.captionSm,
        maxLines = 1,
        modifier = modifier.simpleShadow(elevation = TitleShadow),
    )
}

@Composable
private fun VersionContent(versionBody: String, modifier: Modifier = Modifier) {
    MarkdownText(
        markdown = versionBody,
        color = colors.primary,
        style = typography.caption,
        modifier = modifier
    )
}

@Composable
private fun DownloadButton(
    versionUrl: String,
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = colors.background.alternative),
        content = { DownloadLabel() },
        onClick = {
            activity?.startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(versionUrl))
            )
        }
    )
}

@Composable
private fun DownloadLabel(modifier: Modifier = Modifier) = Text(
    text = stringResource(R.string.download),
    color = colors.primary,
    style = typography.regular,
    modifier = modifier,
)
