package com.example.wtmapper

import android.util.Log
import com.example.reflection.MapperObject
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
        executeAnnotationProcessor()
    }

    private fun executeReflection() {
        val entity = TestEntity(
            seq = 20L,
            name = "양현진",
            phoneNumber = "010-1234-5678",
            age = 27
        )

        val duration = measureTime {
            val model = MapperObject.from(entity, TestModel::class)
            println(model)
        }
        println("duration: $duration")
        println("\nend------------------------------------------------------------------")
    }

    private fun executeAnnotationProcessor() {
        val user = User(
            name = "Test",
            email = "test@email.com"
        )

        println(user.toJson())
    }
}