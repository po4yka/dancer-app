package com.po4yka.dancer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.po4yka.dancer.R
import com.po4yka.dancer.navigation.Router
import com.po4yka.dancer.ui.theme.DancerTheme

@Composable
fun GalleryScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    @Suppress("UNUSED_PARAMETER") router: Router?,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.work_in_progress),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Here will be the gallery",
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview("default")
@Preview("large font", fontScale = 2f)
@Composable
fun GalleryScreenPreview() {
    DancerTheme {
        val navController = rememberNavController()
        GalleryScreen(navController, null)
    }
}
