package kotlins.pring.snippet.config.extension

import jakarta.persistence.Column
import jakarta.persistence.Table
import kotlin.reflect.KClass


// 캐시를 위한 맵을 추가하여 성능 최적화
val tableNameCache = mutableMapOf<KClass<*>, String?>()
val columnNameCache = mutableMapOf<Pair<KClass<*>, String>, String?>()

// 엔티티 테이블의 실제 테이블명
fun getTableName(clazz: KClass<*>): String? {
    // 캐시에서 먼저 확인
    return tableNameCache.getOrPut(clazz) {
        clazz.java.getAnnotation(Table::class.java)?.name
    }
}

// 엔티티 컬럼의 실제 컬럼명
fun getColumnName(clazz: KClass<*>, entityPropertyName: String): String? {
    // 캐시에서 먼저 확인
    return columnNameCache.getOrPut(clazz to entityPropertyName) {
        clazz.java.declaredFields.find { it.name == entityPropertyName }
            ?.getAnnotation(Column::class.java)?.name
    }
}