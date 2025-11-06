package fi.evident.skema.output

import fi.evident.skema.model.ComputedColumn

internal interface Dialect {

    val statementSeparator: String
        get() = ";"

    val supportsTrailingCommas: Boolean

    fun computedColumn(column: ComputedColumn): String
}

internal object SqlServerDialect: Dialect {
    override val statementSeparator: String = "\ngo"

    override val supportsTrailingCommas: Boolean
        get() = true

    override fun computedColumn(column: ComputedColumn): String =
        "${column.name} as ${column.sql}"
}
