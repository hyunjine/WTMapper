import com.example.kapt_annotations.KaptBuilder
import com.example.kapt_annotations.KaptLink
import org.junit.Test

class KapTest {

    @KaptBuilder
    @KaptLink(KaptEntity::class)
    data class KaptModel(
        val seq: Long,
        val title: String,
        val subTitle: String?,
        val rank: Int,
        val content: String,
    )

    data class KaptEntity(
        val seq: Long,
        val title: String,
        val subTitle: String?,
        val rank: Int,
        val content: String,
    )

    @Test
    fun test() {
        println("hi test")
    }
}