package zaitsev.a.d.mirea.speachtextcompose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * This is a builder class that can highlight the text
 * @param text: String
 * @param index: AnnotationIndex
 */
data class TextHighlightBuilder(
    val text: String,
    val index: AnnotationIndex,
    val style: SpanStyle = SpanStyle(
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
) {
    val annotatedString = buildAnnotatedString {
        // Append the text and highlight the start and end with old text to black
        append(text)
        addStyle(
            style = style,
            start = index.start,
            end = index.end
        )
    }
}