package fi.evident.skema.output

import fi.evident.skema.model.*

public fun Schema.generateSql(
    namingStrategy: NamingStrategy = DefaultNamingStrategy,
): String = DdlWriter(SqlServerDialect).apply {
    for (table in tables)
        generateSqlForTable(table, namingStrategy)
}.toString()

private fun DdlWriter.generateSqlForTable(table: Table, namingStrategy: NamingStrategy) {
    if (table.comment != null)
        lineComment(table.comment)

    createTable(table, namingStrategy)

    for (index in table.indices)
        createIndex(
            name = namingStrategy.indexName(table, index),
            tableName = table.name,
            columns = index.columns,
            include = index.include,
            where = index.where,
            unique = index.unique
        )
}

private fun DdlWriter.createTable(table: Table, namingStrategy: NamingStrategy) {
    val allColumns = table.columns + listOfNotNull((table.primaryKey as? PrimaryKey.Single)?.column)
    val longestName = allColumns.maxOf { it.name.length }
    val longestType = allColumns.maxOf { it.typeLength }

    appendLine("create table ${table.name}")
    appendLine("(")

    val primaryKey = table.primaryKey
    when (primaryKey) {
        is PrimaryKey.Single -> {
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
        }

        is PrimaryKey.ForeignKeyRef -> {
            val typeDef = buildString {
                append(primaryKey.fk.type.name)
                append(" primary key")
            }
            append("    ")
            append(String.format("%-${longestName}s %-${longestType}s", primaryKey.name, typeDef))

            val constraintName = namingStrategy.foreignKeyName(table, primaryKey.fk.target, primaryKey.name)
            if (constraintName.isNotEmpty())
                append("\n        constraint $constraintName references ${primaryKey.fk.target}")
            else
                append("\n        references ${primaryKey.fk.target}")
            appendLine(",")
        }

        is PrimaryKey.Composite -> {
            // handle in the end
        }
        null -> {
            // nothing to do
        }
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
                                // TODO: create name for default constraints automatically if unspecified
                                if (constraint.name != null)
                                    append(" constraint ${constraint.name}")
                                append(" default ${constraint.constraint}")
                            }
                        }
                    }

                    // remove trailing spaces from the currently built string
                    setLength(trimEnd().length)

                    if (column.spec.unique) {
                        val constraintName = namingStrategy.uniqueColumnConstraintName(table, column.name)
                        if (constraintName.isNotEmpty())
                            append("\n        constraint $constraintName unique")
                        else
                            append(" unique")
                    }

                    val fk = column.foreignKey
                    if (fk != null) {
                        val constraintName = namingStrategy.foreignKeyName(table, fk.target, column.name)
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
        // TODO: names
        appendLine("    primary key (${table.primaryKey.columns.joinToString(", ")}),")
    }

    for (unique in table.uniques) {
        // TODO: names
        appendLine("    unique (${unique.joinToString(", ")}),")
    }

    for (check in table.checkConstraints) {
        appendLine("    constraint ${check.name}")
        appendLine("        check (${check.condition}),")
    }

    appendLine(")")
    endStatement()
}

private fun DdlWriter.createIndex(
    name: String,
    tableName: String,
    columns: List<String>,
    include: List<String>,
    where: String?,
    unique: Boolean,
) {
    val prefix = if (unique) "create unique index" else "create index"

    appendLine("$prefix $name")
    append("    on $tableName ")

    appendColumnList(columns)

    if (include.isNotEmpty()) {
        append(" include ")
        appendColumnList(include)
    }

    if (where != null) {
        appendLine()
        append("    where $where")
    }

    endStatement()
}
