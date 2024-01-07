package com.example.wtmapper

import com.example.reflection.ReflectionLinker

import com.example.wtmapper.struct.StructEntity
import com.example.wtmapper.kson.KsonEntity

import com.example.wtmapper.linker.LinkerEntity


import com.example.wtmapper.reflection.ReflectionEntity
import com.example.wtmapper.reflection.ReflectionModel
import com.example.wtmapper.struct.StructConverterImpl
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
//        println()
//        executeBuilder()
//        println()
//        executeKson()
//        println()
//        executeStruct()
//        println()
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
        val model = ReflectionLinker.from(entity, ReflectionModel::class)
        println(model)
//        val duration = measureTime {
//            val model = ReflectionLinker.from(entity, ReflectionModel::class)
//            println(model)
//        }
//        println("duration: $duration")
    }

//    private fun executeBuilder() {
//        val entity = BuilderEntityBuilder
//            .seq(1998L)
//            .name("양현진")
//            .phoneNumber("010-8544-7118")
//            .birthday("1998.01.30")
//            .build()
//        println(entity)
//    }
//
//    private fun executeKson() {
//        val entity = KsonEntity(
//            seq = 1998L,
//            name = "양현진",
//            phoneNumber = "010-8544-7118",
//            birthday = "1998.01.30"
//        )
//        println(entity.toJson())
//    }

    private fun executeStruct() {
        val entity = StructEntity(
            seq = 1998L,
            name = "양현진",
            phoneNumber = "010-8544-7118",
            birthday = "1998.01.30"
        )
        val model = StructConverterImpl().from(entity)
        println(model)
    }

    private fun executeLinker() {
//        val entity = LinkerEntity(
//            seqT= 1998L,
//            nameT = "양현진",
//            phoneNumberT = "010-8544-7118",
//            birthdayT = "1998.01.30"
//        )
//        val model = LinkerModelLinkerUtil.build(entity)
//        println(model)
    }
}