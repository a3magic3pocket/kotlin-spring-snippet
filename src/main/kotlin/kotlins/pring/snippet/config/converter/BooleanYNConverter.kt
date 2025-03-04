package kotlins.pring.snippet.config.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class BooleanYNConverter: AttributeConverter<Boolean, String> {

    // boolean 값을 Y/N로 변환
    override fun convertToDatabaseColumn(attribute: Boolean?): String? {
        return if (attribute == null) null else if (attribute) "Y" else "N"
    }

    // Y/N 값을 boolean 으로 변환
    override fun convertToEntityAttribute(dbData: String?): Boolean {
        return dbData == "Y"
    }
}
