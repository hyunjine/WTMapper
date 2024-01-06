package com.example.wtmapper.struct

import org.mapstruct.Mapper

@Mapper
interface StructConverter {
    fun from(from: StructEntity): StructModel
}