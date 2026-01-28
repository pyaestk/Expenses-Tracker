import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: Long,
    val note: String? = null
)