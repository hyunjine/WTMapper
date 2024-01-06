package com.example.wtmapper

import com.example.reflection.ReflectionLinker
import com.example.wtmapper.dd.StructEntity
import com.example.wtmapper.linker.LinkerModel

import com.example.wtmapper.reflection.ReflectionEntity
import com.example.wtmapper.reflection.ReflectionModel
import org.junit.Test
import kotlin.time.measureTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MapperUnitTest {
    @Test
    fun addition_isCorrect() {
        println("start------------------------------------------------------------------\n")
//        executeReflection()
        executeLinker()

        println("\nend------------------------------------------------------------------")
    }

    private fun executeReflection() {
        val entity = ReflectionEntity(
            seq = 1998L,
            name = "양현진",
            phoneNumber = "010-8544-7118",
            birthday = "1998.01.30"
        )
        val duration = measureTime {
            val model = ReflectionLinker.from(entity, ReflectionModel::class)
            println(model)
        }
        println("duration: $duration")
    }

    private fun executeLinker() {
        val entity = StructEntity(
            seq = 1998L,
            name = "양현진",
            phoneNumber = "010-8544-7118",
            birthday = "1998.01.30"
        )

        val model = LinkerModel(
            seq = 1998L,
            name = "양현진",
            phoneNumber = "010-8544-7118",
            birthday = "1998.01.30"
        )

//        val a = KaptModelBuilder
//            .birthday("")
//            .build()


//        val a = KaptLinker.execute(entity, KaptModel::class)
    }
}