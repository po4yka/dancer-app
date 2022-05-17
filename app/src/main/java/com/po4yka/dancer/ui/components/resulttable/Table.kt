package com.po4yka.dancer.ui.components.resulttable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * The horizontally scrollable table with header and content.
 *
 * @param columnCount how many columns is needed.
 * @param data the data to populate table.
 * @param colorSettings stroke and background colors.
 * @param modifier the modifier to apply to this layout node.
 * @param headerCellContent a block which describes the header cell content.
 * @param cellBodyContent a block which describes the cell content.
 */
@Composable
fun <T> Table(
    columnCount: Int,
    data: List<T>,
    colorSettings: TableColorSettings,
    modifier: Modifier = Modifier,
    headerCellContent: @Composable (index: Int, item: T?) -> Unit,
    cellBodyContent: @Composable (index: Int, item: T?) -> Unit,
    customCells: List<Pair<Int, @Composable (index: Int, item: T?) -> Unit>> = emptyList(),
) {
    Surface(
        modifier =
            modifier
                .background(colorSettings.backgroundColor)
                .padding(top = 80.dp),
        shape = RoundedCornerShape(6.dp),
        color = colorSettings.backgroundColor,
    ) {
        LazyRow(
            modifier =
                Modifier
                    .border(
                        BorderStroke(1.dp, colorSettings.strokeColor),
                        RoundedCornerShape(30.dp),
                    ),
        ) {
            items((0 until columnCount).toList()) { columnIndex ->
                Column(
                    modifier =
                        Modifier.padding(
                            start = 8.dp,
                            top = 6.dp,
                            end = 8.dp,
                            bottom = 8.dp,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    data.forEachIndexed { index, _ ->
                        Surface(
                            border = BorderStroke(1.dp, Color.Transparent),
                            contentColor = Color.Transparent,
                            color = colorSettings.backgroundColor,
                            modifier = Modifier.background(colorSettings.backgroundColor),
                        ) {
                            val customRowContent =
                                customCells.firstOrNull { customCell -> customCell.first == index }
                            if (customRowContent != null) {
                                customRowContent.second(columnIndex, data[index - 1])
                            } else {
                                when (index) {
                                    0 -> {
                                        headerCellContent(columnIndex, null)
                                    }
                                    else -> {
                                        cellBodyContent(columnIndex, data[index - 1])
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
