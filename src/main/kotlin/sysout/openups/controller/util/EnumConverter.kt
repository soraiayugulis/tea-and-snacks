package sysout.openups.controller.util

/**
 * Utility class for generic conversion of enums
 */
object EnumConverter {
    /**
     * Converts a string to a specified enum type
     *
     * @param value String to be converted
     * @return Enum instance or null if conversion fails
     */
    inline fun <reified T : Enum<T>> fromString(value: String?): T? {
        return value?.let {
            try {
                enumValueOf<T>(it.uppercase())
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }

    /**
     * Non-reified version for cases where the type cannot be inferred at compile time
     *
     * @param value String to be converted
     * @param enumClass Class of the enum to convert to
     * @return Enum instance or null if conversion fails
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Enum<T>> fromString(value: String?, enumClass: Class<T>): T? {
        return value?.let {
            try {
                java.lang.Enum.valueOf(enumClass, it.uppercase()) as T
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
}
