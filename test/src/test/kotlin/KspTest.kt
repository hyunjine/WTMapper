import com.example.kps_annotations.KspLink
import com.example.kps_annotations.KspLinkName
import org.junit.Test

class KspTest {
    data class KspEntity(
        val title: String?,
        val subTitle: String?,
        val content: String,
    )

    @KspLink(KspEntity::class)
    data class KspModel(
        val title: String?,
        @KspLinkName(target = "subTitle", strategy = "?: String.DEFAULT")
        val titleSub: String?,
        val content: String,
    )

    @Test
    fun main() {

    }
}

val String.Companion.DEFAULT
    get() = ""