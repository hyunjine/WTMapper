import com.example.kps_annotations.KspLink
import com.example.kps_annotations.KspLinkApp
import com.example.kps_annotations.KspLinkName
import com.example.kps_annotations.KspLinkTest
import com.example.kps_annotations.KspLinkTestInterface
import org.junit.Before
import org.junit.Test

class KspTest {

    data class KspEntity(
        val title: String?,
        val subTitle: String?,
        val content: String,
        val type: String?
    )

    @KspLink(KspEntity::class)
    data class KspModel(
        val title: String?,
        @KspLinkName(target = "subTitle", strategy = " ?: String.DEFAULT")
        val titleSub: String?,
        val content: String,
        @KspLinkName(target = "type", custom = "" +
                "if (entity.type == \"normal\") " +
                "KspTest.Type.NORMAL " +
                "else " +
                "KspTest.Type.DISABLE" +
                "")
        val userType: Type
    ) {
        fun mapUserType(type: String): Type {
            return when (type) {
                "normal" -> Type.NORMAL
                else -> Type.DISABLE
            }
        }
    }
    enum class Type {
        NORMAL,
        DISABLE
    }

    @Test
    fun main() {
        println(Type::class.qualifiedName)
    }

    private fun getA(): String {
        return "a b c" + "d e f"
    }

    @KspLinkApp(String::class, " ?: String.DEFAULT")
    @KspLinkApp(Int::class, " ?: Int.DEFAULT")
    class Initializer
}

val String.Companion.DEFAULT
    get() = ""

val Int.Companion.DEFAULT
    get() = 0