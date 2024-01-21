import com.example.kapt_annotations.KaptBuilder
import com.example.kapt_annotations.KaptLink
import com.example.kps_annotations.KspBuilder
import com.example.kps_annotations.KspLink

import org.junit.Test

class KspTest {
    data class KspEntity(
        val title: String,
        val subTitle: String,
        val content: String,
    )

    @KspLink(KspEntity::class)
    data class KspModel(
        val title: String,
        val subTitle: String,
        val content: String,
    )

    @Test
    fun main() {
//        val a = KspModelBuilder().build()
//        println(a)
    }
}