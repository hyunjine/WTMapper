package com.example.wtmapper.dd

import org.mapstruct.Mapper

@Mapper
interface StructConverter {
    fun convertKapt(from: StructEntity): StructModel
}