package com.example.wtmapper

import com.example.reflection.ReflectionLinker

import com.example.wtmapper.struct.StructEntity
import com.example.wtmapper.kson.KsonEntity

import com.example.wtmapper.linker.LinkerEntity
import com.example.wtmapper.linker.LinkerModel
import com.example.wtmapper.linker.build
//import com.example.wtmapper.linker.LinkerModelLinkerUtil


import com.example.wtmapper.reflection.ReflectionEntity
import com.example.wtmapper.reflection.ReflectionModel
import com.example.wtmapper.struct.StructConverterImpl
import com.example.wtmapper.struct.StructModel
import org.junit.Test
import org.mapstruct.factory.Mappers
import kotlin.reflect.full.declaredMemberProperties
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
//        val d = measureTime {
//            executeReflection()
//        }
//        println("$d")
//        println()
//        executeBuilder()
//        println()
//        executeKson()
//        println()
//        executeStruct()
//        println()
        val duration = measureTime {
//            map()
            executeLinker()
        }
        val duration2 = measureTime {
            map()
//            executeLinker()
        }

        println("duration: $duration")
        println("duration2: $duration2")
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
        val a = Mappers.getMapper(StructModel::class.java)

        val model = StructConverterImpl().from(entity)
        println(model)
    }

    private fun map() {
        val entity = LinkerEntity(
            seqT= 1998L,
            nameT = "양현진",
            phoneNumberT = "010-8544-7118",
            birthdayT = "1998.01.30"
        )
        requireNotNull(entity.seqT)
        requireNotNull(entity.nameT)
        requireNotNull(entity.phoneNumberT)
        requireNotNull(entity.birthdayT)
        val model =  LinkerModel(
            seq = entity.seqT!!,
            name = entity.nameT!!,
            phoneNumber = entity.phoneNumberT!!,
            birthday = entity.birthdayT!!,
        )
        println(model)
    }
    private fun executeLinker() {
        val entity = LinkerEntity(
            seqT= 1998L,
            nameT = "양현진",
            phoneNumberT = "010-8544-7118",
            birthdayT = "1998.01.30"
        )

        val model = LinkerModel(
            seq= 1998L,
            name = "양현진",
            phoneNumber = "010-8544-7118",
            birthday = "1998.01.30"
        )
//        model.build(entity)
//        val model = LinkerModelLinkerUtil.build(entity)
//        println(model)
    }
}