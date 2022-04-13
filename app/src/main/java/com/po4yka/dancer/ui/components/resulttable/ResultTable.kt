package com.po4yka.dancer.ui.components.resulttable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.po4yka.dancer.R
import com.po4yka.dancer.models.RecognitionModelName
import com.po4yka.dancer.models.RecognitionModelPredictionResult
import com.po4yka.dancer.models.RecognitionResult
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.random.Random

@Composable
fun ResultTable(
    modifier: Modifier = Modifier,
    isDetected: RecognitionResult = RecognitionResult.NOT_DETECTED,
    recognitionModelPredictionResults: List<RecognitionModelPredictionResult>
) {

    val headerCellTitle = getHeaderCell()
    val cellText = getDefaultCell()
    val detectedRow = getDetectedRow()

    Table(
        columnCount = 2,
        data = recognitionModelPredictionResults,
        colorSettings = TableColorSettings(
            backgroundColor = Color.Transparent,
            strokeColor = if (isDetected == RecognitionResult.DETECTED) {
                Color.White
            } else {
                Color.Red
            }
        ),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .wrapContentWidth(),
        headerCellContent = headerCellTitle,
        cellBodyContent = cellText,
        customCells = if (isDetected == RecognitionResult.DETECTED) {
            listOf(
                Pair(1, detectedRow)
            )
        } else {
            emptyList()
        }
    )
}

@Composable
private fun getHeaderCell() = getCellText(
    textGenerator = { index, _ ->
        when (index) {
            0 -> stringResource(id = R.string.table_movement)
            1 -> stringResource(id = R.string.table_probability)
            else -> ""
        }
    },
    fontSize = dimensionResource(id = R.dimen.result_table_header_font_size).value.sp,
    startMarginGenerator = { index ->
        when (index) {
            0 -> 16
            1 -> 8
            else -> 0
        }.dp
    },
    endPaddingGenerator = { index ->
        when (index) {
            0 -> 8
            1 -> 16
            else -> 0
        }.dp
    }
)

@Composable
private fun getDefaultCell() = getCellText(
    textGenerator = textGenerator@{ index, item ->
        if (item == null) return@textGenerator ""
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        when (index) {
            0 -> stringResource(id = item.name.movementNameId)
            1 -> String.format(df.format(item.probability))
            else -> ""
        }
    },
    fontSize = dimensionResource(id = R.dimen.result_table_body_font_size).value.sp,
    startMarginGenerator = { 4.dp },
    endPaddingGenerator = { 4.dp }
)

@Composable
private fun getDetectedRow() = getCellText(
    textGenerator = textGenerator@{ index, item ->
        if (item == null) return@textGenerator ""
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        when (index) {
            0 -> stringResource(id = item.name.movementNameId)
            1 -> String.format(df.format(item.probability))
            else -> ""
        }
    },
    fontSize = dimensionResource(id = R.dimen.result_table_body_font_size).value.sp,
    textColor = Color.Green,
    startMarginGenerator = { 4.dp },
    endPaddingGenerator = { 4.dp }
)

@Composable
private fun getCellText(
    textGenerator: @Composable (Int, RecognitionModelPredictionResult?) -> String,
    fontSize: TextUnit,
    textColor: Color = Color.White,
    startMarginGenerator: (index: Int) -> Dp = { _ -> 0.dp },
    endPaddingGenerator: (index: Int) -> Dp = { _ -> 0.dp }
): @Composable (Int, RecognitionModelPredictionResult?) -> Unit {
    return { index, item ->
        Text(
            text = textGenerator(index, item),
            fontSize = fontSize,
            color = textColor,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(start = startMarginGenerator(index), end = endPaddingGenerator(index))
                .wrapContentSize()
                .fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
fun ResultTablePreview() {
    val resultTables = RecognitionModelName.values().map {
        RecognitionModelPredictionResult(
            it,
            Random.nextFloat()
        )
    }

    ResultTable(recognitionModelPredictionResults = resultTables)
}
