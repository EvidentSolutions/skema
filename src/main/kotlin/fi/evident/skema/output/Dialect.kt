package fi.evident.skema.output

import fi.evident.skema.model.Index

interface Dialect {

    val statementSeparator: String
        get() = "\ngo\n\n"

    fun writeIndex(name: String, tableName: String, index: Index) = buildString {
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

class SqlServerDialect: Dialect
