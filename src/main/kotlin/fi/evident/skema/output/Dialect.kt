package fi.evident.skema.output

import fi.evident.skema.model.Index

internal interface Dialect {

    val statementSeparator: String
        get() = ";"

    fun writeIndex(name: String, tableName: String, index: Index): String = buildString {
        append("create ")

        if (index.unique)
            append("unique ")

        append("index $name")

        append("\n    on $tableName ").append(index.columns.joinToString(", ", "(", ")"))
        if (index.include.isNotEmpty())
            append(" include ").append(index.include.joinToString(", ", "(", ")"))

        if (index.where != null)
            append("\n    where ").append(index.where)
    }
}

internal object SqlServerDialect: Dialect {
    override val statementSeparator: String = "\ngo"
}
