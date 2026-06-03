package fi.evident.skema.output.dialect

import fi.evident.skema.model.ComputedColumn
import fi.evident.skema.model.Type

internal interface Dialect {

    val statementSeparator: String
        get() = ";"

    val supportsTrailingCommas: Boolean

    fun quoteIdentifier(name: String) = name

    fun computedColumn(column: ComputedColumn): String

    fun encodeType(type: Type): String
}
