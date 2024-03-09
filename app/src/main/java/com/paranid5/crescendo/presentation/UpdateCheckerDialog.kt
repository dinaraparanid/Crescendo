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
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.data.ktor_client.github.Release
import com.paranid5.crescendo.data.ktor_client.github.checkForUpdates
import com.paranid5.crescendo.utils.extensions.simpleShadow
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.composition_locals.LocalActivity
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.ktor.client.HttpClient
import org.koin.compose.koinInject

private const val TAG = "UpdateCheckerDialog"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateCheckerDialog(modifier: Modifier = Modifier, ktorClient: HttpClient = koinInject()) {
    val colors = LocalAppColors.current
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
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.background),
                modifier = modifier.fillMaxWidth()
            ) {
                UpdateCheckerDialogContent(
                    newVersion = newVersion!!,
                    modifier = Modifier.padding(10.dp)
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

        Spacer(Modifier.height(20.dp))

        VersionContent(versionBody = newVersion.body)

        Spacer(Modifier.height(12.dp))

        DownloadButton(
            versionUrl = newVersion.htmlUrl,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }

@Composable
private fun VersionLabel(versionName: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    MarkdownText(
        markdown = "# ${stringResource(R.string.update_available)}: $versionName",
        color = colors.primary,
        fontSize = 8.sp,
        maxLines = 1,
        modifier = modifier.simpleShadow(elevation = 25.dp)
    )
}

@Composable
private fun VersionContent(versionBody: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    MarkdownText(
        markdown = versionBody,
        color = colors.primary,
        fontSize = 11.sp,
        modifier = modifier
    )
}

@Composable
private fun DownloadButton(
    versionUrl: String,
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current
    val colors = LocalAppColors.current

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = colors.backgroundAlternative),
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
private fun DownloadLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.download),
        fontSize = 14.sp,
        color = colors.primary,
        modifier = modifier
    )
}