package com.po4yka.dancer.ui.components.resulttable

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.po4yka.dancer.R
import com.po4yka.dancer.models.RecognitionModelName
import com.po4yka.dancer.models.RecognitionModelPredictionResult
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.random.Random

@Composable
fun ResultTable(
    recognitionModelPredictionResults: List<RecognitionModelPredictionResult>
) {
    val headerCellWidth = dimensionResource(id = R.dimen.result_table_header_cell_width)
    val bodyCellWidth = dimensionResource(id = R.dimen.result_table_body_cell_width)
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            0 -> headerCellWidth
            else -> bodyCellWidth
        }
    }

    val headerFontSize = dimensionResource(id = R.dimen.result_table_header_font_size).value.sp
    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> stringResource(id = R.string.table_movement)
            1 -> stringResource(id = R.string.table_probability)
            else -> ""
        }

        Text(
            text = value,
            fontSize = headerFontSize,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Black
        )
    }

    val cellText: @Composable (Int, RecognitionModelPredictionResult) -> Unit = { index, item ->
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        val value = when (index) {
            0 -> stringResource(id = item.name.movementNameId)
            1 -> String.format(df.format(item.probability))
            else -> ""
        }

        Text(
            text = value,
            fontSize = 6.sp,
            color = Color.White,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .padding(18.dp, 4.dp, 4.dp, 4.dp)
                .height(6.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    Table(
        columnCount = 2,
        cellWidth = cellWidth,
        data = recognitionModelPredictionResults,
        colorSettings = TableColorSettings(
            backgroundColor = Color.Transparent,
            strokeColor = Color.White
        ),
        modifier = Modifier.verticalScroll(rememberScrollState()),
        headerCellContent = headerCellTitle,
        cellContent = cellText
    )
}

@Preview
@Composable
fun ResultTablePreview() {
    val lowerBound = 0.0
    val upperBound = 1.0
    val resultTables = RecognitionModelName.values().map {
        RecognitionModelPredictionResult(
            it,
            Random.nextDouble(lowerBound, upperBound)
        )
    }

    ResultTable(resultTables)
}
