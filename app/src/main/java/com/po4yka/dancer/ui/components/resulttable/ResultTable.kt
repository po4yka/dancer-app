package com.po4yka.dancer.ui.components.resulttable

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.po4yka.dancer.R
import com.po4yka.dancer.models.PoseDetectionStateResult
import com.po4yka.dancer.models.RecognitionModelName
import com.po4yka.dancer.models.RecognitionModelPredictionResult
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale
import kotlin.random.Random

@Composable
fun ResultTable(
    modifier: Modifier = Modifier,
    isDetected: PoseDetectionStateResult = PoseDetectionStateResult.NOT_DETECTED,
    recognitionModelPredictionResults: List<RecognitionModelPredictionResult>
) {

    val context = LocalContext.current

    val headerCellTitle = getHeaderCell(context)
    val cellText = getDefaultCell(context)
    val detectedRow = getDetectedRow(context)

    Table(
        columnCount = integerResource(id = R.integer.result_table_column_count),
        data = recognitionModelPredictionResults,
        colorSettings = TableColorSettings(
            backgroundColor = Color.Transparent,
            strokeColor = if (isDetected == PoseDetectionStateResult.DETECTED) {
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
        customCells = if (isDetected == PoseDetectionStateResult.DETECTED) {
            listOf(
                Pair(1, detectedRow)
            )
        } else {
            emptyList()
        }
    )
}

@Composable
private fun getHeaderCell(context: Context) = getCellText(
    context = context,
    textGenerator = { index, _ ->
        when (index) {
            0 -> stringResource(id = R.string.table_movement)
            1 -> stringResource(id = R.string.table_probability)
            else -> ""
        }
    },
    fontSize = dimensionResource(id = R.dimen.result_table_header_font_size).value.sp,
    startMarginGenerator = { index ->
        context.resources.getDimension(
            when (index) {
                0 -> R.dimen.result_table_header_first_column_start_margin
                1 -> R.dimen.result_table_header_second_column_start_margin
                else -> R.dimen.result_table_zero_size
            }
        )
    },
    endPaddingGenerator = { index ->
        context.resources.getDimension(
            when (index) {
                0 -> R.dimen.result_table_header_first_column_end_margin
                1 -> R.dimen.result_table_header_second_column_end_margin
                else -> R.dimen.result_table_zero_size
            }
        )
    }
)

@Composable
private fun getDefaultCell(context: Context) = getCellText(
    context = context,
    textGenerator = textGenerator@{ index, item ->
        if (item == null) return@textGenerator ""
        when (index) {
            0 -> stringResource(id = item.name.movementNameId)
            1 -> String.format(Locale.US, "%.2f", item.probability)
            else -> ""
        }
    },
    fontSize = dimensionResource(id = R.dimen.result_table_body_font_size).value.sp,
    startMarginGenerator = { index ->
        context.resources.getDimension(
            when (index) {
                0 -> R.dimen.result_table_first_default_column_start_margin
                1 -> R.dimen.result_table_second_default_column_start_margin
                else -> R.dimen.result_table_zero_size
            }
        )
    },
    endPaddingGenerator = {
        context.resources.getDimension(R.dimen.result_table_default_column_end_margin)
    }
)

@Composable
private fun getDetectedRow(context: Context) = getCellText(
    context = context,
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
    startMarginGenerator = { index ->
        context.resources.getDimension(
            when (index) {
                0 -> R.dimen.result_table_first_default_column_start_margin
                1 -> R.dimen.result_table_second_default_column_start_margin
                else -> R.dimen.result_table_zero_size
            }
        )
    },
    endPaddingGenerator = {
        context.resources.getDimension(R.dimen.result_table_default_column_end_margin)
    }
)

@Composable
private fun getCellText(
    context: Context,
    textGenerator: @Composable (Int, RecognitionModelPredictionResult?) -> String,
    fontSize: TextUnit,
    textColor: Color = Color.White,
    startMarginGenerator: (index: Int) -> Float = { _ ->
        context.resources.getDimension(R.dimen.result_table_zero_size)
    },
    endPaddingGenerator: (index: Int) -> Float = { _ ->
        context.resources.getDimension(R.dimen.result_table_zero_size)
    }
): @Composable (Int, RecognitionModelPredictionResult?) -> Unit {
    return { index, item ->
        Text(
            text = textGenerator(index, item),
            fontSize = fontSize,
            color = textColor,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(
                    start = Dp(startMarginGenerator(index)),
                    end = Dp(endPaddingGenerator(index))
                )
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
