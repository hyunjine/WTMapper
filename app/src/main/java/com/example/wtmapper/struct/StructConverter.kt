package com.example.wtmapper.struct

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper
interface StructConverter {
    @Mapping(source = "seq", target = "seqT")
    fun from(from: StructEntity): StructModel
}