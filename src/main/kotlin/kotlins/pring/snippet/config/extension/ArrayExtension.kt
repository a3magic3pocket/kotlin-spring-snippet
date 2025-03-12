package kotlins.pring.snippet.config.extension

inline fun <reified T> Array<*>.safeGet(index: Int): T? {
    val value = this.getOrNull(index)
    return if (value is T) value else null
}