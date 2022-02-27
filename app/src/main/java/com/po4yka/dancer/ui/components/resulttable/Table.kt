package com.po4yka.dancer.ui.components.resulttable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The horizontally scrollable table with header and content.
 *
 * @param columnCount the count of columns in the table
 * @param cellWidth the width of column, can be configured based on index of the column.
 * @param data the data to populate table.
 * @param modifier the modifier to apply to this layout node.
 * @param headerCellContent a block which describes the header cell content.
 * @param cellContent a block which describes the cell content.
 */
@Composable
fun <T> Table(
    columnCount: Int,
    cellWidth: (index: Int) -> Dp,
    data: List<T>,
    colorSettings: TableColorSettings,
    modifier: Modifier = Modifier,
    headerCellContent: @Composable (index: Int) -> Unit,
    cellContent: @Composable (index: Int, item: T) -> Unit,
) {
    Surface(
        modifier = modifier.background(colorSettings.backgroundColor),
        shape = RoundedCornerShape(6.dp),
        color = colorSettings.backgroundColor
    ) {
        LazyRow(
            modifier = Modifier
                .padding(4.dp)
                .border(
                    BorderStroke(1.dp, colorSettings.strokeColor),
                    RoundedCornerShape(30.dp)
                )
        ) {
            items((0 until columnCount).toList()) { columnIndex ->
                Column {
                    (0..data.size).forEach { index ->
                        Surface(
                            border = BorderStroke(1.dp, Color.Transparent),
                            contentColor = Color.Transparent,
                            color = colorSettings.backgroundColor,
                            modifier = Modifier
                                .width(cellWidth(columnIndex))
                                .background(colorSettings.backgroundColor)
                        ) {
                            if (index == 0) {
                                headerCellContent(columnIndex)
                            } else {
                                cellContent(columnIndex, data[index - 1])
                                Divider(color = colorSettings.strokeColor, thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}
