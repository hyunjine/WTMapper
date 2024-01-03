package com.example.wtmapper

import com.example.reflection.Mapper
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
        executeReflection()
    }

    private fun executeReflection() {
        val entity = TestEntity(
            seq = 20L,
            name = "양현진",
            phoneNumber = "010-1234-5678",
            age = 27
        )

        val duration = measureTime {
            val model = Mapper.from(entity, TestModel::class)
            println(model)
        }
        println("duration: $duration")
        println("\nend------------------------------------------------------------------")
    }
}