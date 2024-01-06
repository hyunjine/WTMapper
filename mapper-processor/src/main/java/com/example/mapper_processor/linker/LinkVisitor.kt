package com.example.mapper_processor.linker

import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.ErrorType
import javax.lang.model.type.ExecutableType
import javax.lang.model.type.IntersectionType
import javax.lang.model.type.NoType
import javax.lang.model.type.NullType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable
import javax.lang.model.type.TypeVisitor
import javax.lang.model.type.UnionType
import javax.lang.model.type.WildcardType
import javax.lang.model.util.AbstractTypeVisitor14
import javax.lang.model.util.AbstractTypeVisitor7
import javax.lang.model.util.AbstractTypeVisitor8
import javax.lang.model.util.AbstractTypeVisitor9

class LinkVisitor<FROM, TO> : AbstractTypeVisitor8<FROM, TO>() {
    override fun visitPrimitive(p0: PrimitiveType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitNull(p0: NullType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitArray(p0: ArrayType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitDeclared(p0: DeclaredType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitError(p0: ErrorType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitTypeVariable(p0: TypeVariable?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitWildcard(p0: WildcardType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitExecutable(p0: ExecutableType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitNoType(p0: NoType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitUnion(p0: UnionType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }

    override fun visitIntersection(p0: IntersectionType?, p1: TO): FROM {
        TODO("Not yet implemented")
    }
}