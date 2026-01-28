import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryModel(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val backgroundColor: Color
)