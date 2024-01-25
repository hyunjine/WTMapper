import org.junit.Test
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers


class MapstructTest {
    data class MapstructEntity(
        val seq: Long,
        val title: String,
        val subTitle: String?,
        val rank: Int,
        val content: String,
    )

    data class MapstructModel(
        val seq: Long,
        val title: Type,
        val subTitle: String?,
        val rank: Int,
        val content: String,
    ) {
        enum class Type(val code: String) {
            A("제목"),
            B("제목인가?")
        }
    }

    @Mapper
    interface ModelMapper {
        // RequestDto -> MessageBodyDto 매핑
        @Mapping(source = "requestDto.title", target = "title", qualifiedByName = ["typeToEnum"])
        fun toMapstructModel(requestDto: MapstructEntity?): MapstructModel?

        @Named("typeToEnum")
        fun typeToEnum(type: String): MapstructModel.Type {
            return if (type == "제목") MapstructModel.Type.A else MapstructModel.Type.B
        }
    }
    @Test
    fun main() {
//        val instance = Mappers.getMapper(ModelMapper::class.java)
//
//        val entity = MapstructEntity(
//            seq = 20L,
//            title = "제목",
//            subTitle = "부제",
//            rank = 1,
//            content = "내용"
//        )
//        println(instance.toMapstructModel(entity))
    }
}