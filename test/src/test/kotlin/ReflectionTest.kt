import com.example.reflection.ReflectionLinker
import com.example.reflection.ReflectionSwap
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class ReflectionTest {
    data class ReflectionEntity(
        val seq: Long,
        val title: String,
        val content: String,
    )

    data class ReflectionModel(
        val seq: Long,
        val title: String,
        @ReflectionSwap("content")
        val subTitle: String,
    ) {
        companion object {
            fun from(entity: ReflectionEntity): ReflectionModel {
                return ReflectionModel(
                    seq = entity.seq,
                    title = entity.title,
                    subTitle = entity.content
                )
            }
        }
    }

    fun ReflectionEntity.map(): ReflectionModel {
        return ReflectionModel(
            seq = seq,
            title = title,
            subTitle = content
        )
    }

    @Test
    fun main() {
        val entity = ReflectionEntity(
            seq = 19290L,
            title = "원룸은 동그란 형태의 방인가?",
            content = "저녁 메뉴 추천좀 ㅠㅠ"
        )
        val duration = measureTime {
            val model = entity.map()
        }
        println(duration)
    }
}