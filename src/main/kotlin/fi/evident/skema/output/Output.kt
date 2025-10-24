package fi.evident.skema.output

import fi.evident.skema.model.*

interface SchemeCallbacks : NamingStrategy {
    fun convertName(name: String): String = name
    fun tableEndHook(name: String): String = ""
}

fun Schema.dump(
    callbacks: SchemeCallbacks = object : SchemeCallbacks {},
    dialect: Dialect,
) = buildString {
    for (table in tables)
        append(table.dump(callbacks, dialect))
}

fun Table.dump(callbacks: SchemeCallbacks, dialect: Dialect) = buildString {
    val table = this@dump

    if (table.comment != null)
        appendLine("-- ${table.comment}")

    appendLine("create table ${table.name}")
    appendLine("(")

    val allColumns = columns + listOfNotNull((primaryKey as? PrimaryKey.Single)?.column)
    val longestName = allColumns.maxOf { it.name.length }
    val longestType = allColumns.maxOf { it.typeLength }

    if (primaryKey is PrimaryKey.Single) {
        val pk = primaryKey.column
        val typeDef = buildString {
            append(pk.spec.type.name)
            if (pk.spec.type.identity)
                append(" identity")
            append(" primary key")
        }
        append("    ")
        append(String.format("%-${longestName}s %-${longestType}s", pk.name, typeDef))

        appendLine(",")
    } else if (primaryKey is PrimaryKey.ForeignKeyRef) {
        val typeDef = buildString {
            append(primaryKey.fk.type.name)
            append(" primary key")
        }
        append("    ")
        append(String.format("%-${longestName}s %-${longestType}s", primaryKey.name, typeDef))
        val constraintName = callbacks.convertName(createFkName(table.name, primaryKey.name))
        if (constraintName.isNotEmpty())
            append("\n        constraint $constraintName references ${primaryKey.fk.target}")
        else
            append("\n        references ${primaryKey.fk.target}")
        appendLine(",")
    }

    for (column in table.columns) {
        when (column) {
            is Column -> {
                val str = buildString {
                    append(String.format("%-${longestName}s %-${longestType}s", column.name, column.spec.type.name))

                    if (!column.nullable)
                        append(" not null")

                    for (constraint in column.spec.constraints) {
                        when (constraint) {
                            is ColumnConstraint.Default -> {
                                if (constraint.name != null)
                                    append(" constraint ${constraint.name}")
                                append(" default ${constraint.constraint}")
                            }
                        }
                    }

                    // remove trailing spaces from the currently built string
                    setLength(trimEnd().length)

                    if (column.spec.unique) {
                        val constraintName = callbacks.convertName("${table.name}_${column.name}_unique")
                        if (constraintName.isNotEmpty())
                            append("\n        constraint $constraintName unique")
                        else
                            append(" unique")
                    }

                    val fk = column.foreignKey
                    if (fk != null) {
                        val constraintName = callbacks.convertName(createFkName(table.name, column.name))
                        if (constraintName.isNotEmpty())
                            append("\n        constraint $constraintName references ${fk.target}")
                        else
                            append("\n        references ${fk.target}")
                        if (fk.cascadeDelete)
                            append(" on delete cascade")
                    }

                }.trim()

                append("    $str,")

                if (column.spec.comment != null)
                    append(" -- ${column.spec.comment}")

                appendLine()
            }

            is ComputedColumn -> {
                appendLine(String.format("    %-${longestName}s as %s,", column.name, column.sql))
            }
        }
    }

    if (table.primaryKey is PrimaryKey.Composite) {
        appendLine("    primary key (${table.primaryKey.columns.joinToString(", ")}),")
    }

    for (unique in table.uniques)
        appendLine("    unique (${unique.joinToString(", ")}),")

    for (check in table.checkConstraints) {
        appendLine("    constraint ${callbacks.convertName(check.name)}")
        appendLine("        check (${check.condition}),")
    }

    append(callbacks.tableEndHook(table.name))

    appendLine(")")
    append(dialect.statementSeparator)

    for (index in table.indices) {
        appendLine(
            dialect.writeIndex(
                name = callbacks.indexName(table, index),
                tableName = table.name,
                index = index
            )
        )
        append(dialect.statementSeparator)
    }
}

private fun createFkName(tableName: String, columnName: String) =
    "fk_${tableName}_${columnName.removeSuffix("_id")}"
