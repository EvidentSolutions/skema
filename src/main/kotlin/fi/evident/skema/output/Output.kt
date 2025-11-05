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

    for (index in table.indices) {
        createIndex(
            name = namingStrategy.indexName(table, index),
            tableName = table.name,
            columns = index.columns,
            include = index.include,
            where = index.where,
            unique = index.unique
        )
    }
}

private fun DdlWriter.createTable(table: Table, namingStrategy: NamingStrategy) {
    appendLine("create table ${table.name}")
    appendLine("(")
    indent {
        val primaryKey = table.primaryKey
        when (primaryKey) {
            is PrimaryKey.Single -> {
                val pk = primaryKey.column

                append(pk.name)
                append(" ")
                append(pk.spec.type.name)
                if (pk.spec.identity)
                    append(" identity")

                val constraintName = namingStrategy.primaryKeyConstraintName(table, listOf(pk.name))
                if (constraintName.isNotEmpty()) {
                    appendIndented("constraint $constraintName primary key")
                } else {
                    append(" primary key")
                }
                appendLine(",")
            }

            is PrimaryKey.ForeignKeyRef -> {
                append(primaryKey.name)
                append(" ")
                append(primaryKey.fk.type.name)

                val pkConstraintName = namingStrategy.primaryKeyConstraintName(table, listOf(primaryKey.name))
                if (pkConstraintName.isNotEmpty()) {
                    appendIndented("constraint $pkConstraintName primary key")
                } else {
                    append(" primary key")
                }

                val fkConstraintName =
                    namingStrategy.foreignKeyConstraintName(table, primaryKey.fk.target, primaryKey.name)
                if (fkConstraintName.isNotEmpty())
                    appendIndented("constraint $fkConstraintName references ${primaryKey.fk.target}")
                else
                    appendIndented("references ${primaryKey.fk.target}")

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
                    append(column.name)
                    append(" ")
                    append(column.spec.type.name)

                    if (!column.nullable)
                        append(" not null")

                    for (constraint in column.spec.constraints) {
                        when (constraint) {
                            is ColumnConstraint.Default -> {
                                val constraintName = namingStrategy.defaultConstraintName(table, column.name)
                                if (constraintName.isNotEmpty())
                                    appendIndented("constraint $constraintName default ${constraint.constraint}")
                                else
                                    append(" default ${constraint.constraint}")
                            }
                        }
                    }

                    if (column.spec.unique) {
                        val constraintName = namingStrategy.uniqueConstraintName(table, listOf(column.name))
                        if (constraintName.isNotEmpty()) {
                            appendIndented("constraint $constraintName unique")
                        } else
                            append(" unique")
                    }

                    val fk = column.foreignKey
                    if (fk != null) {
                        indent {
                            val constraintName = namingStrategy.foreignKeyConstraintName(table, fk.target, column.name)
                            if (constraintName.isNotEmpty())
                                append("constraint $constraintName ")

                            append("references ${fk.target}")

                            if (fk.cascadeDelete)
                                append(" on delete cascade")
                        }
                    }

                    append(",")

                    if (column.spec.comment != null)
                        append(" -- ${column.spec.comment}")

                    appendLine()
                }

                is ComputedColumn -> {
                    appendLine("${column.name} as ${column.sql},")
                }
            }
        }

        if (table.primaryKey is PrimaryKey.Composite) {
            val constraintName = namingStrategy.primaryKeyConstraintName(table, table.primaryKey.columns)
            val pkDefinition = table.primaryKey.columns.joinToString(", ", "(", ")")

            if (constraintName.isNotEmpty()) {
                append("constraint $constraintName ")
                appendIndentedLine("primary key $pkDefinition,")
            } else {
                appendLine("primary key $pkDefinition,")
            }
        }

        for (unique in table.uniques) {
            val constraintName = namingStrategy.uniqueConstraintName(table, unique)
            if (constraintName.isNotEmpty()) {
                appendLine("constraint $constraintName")
                appendIndentedLine("unique (${unique.joinToString(", ")}),")
            } else {
                appendLine("unique (${unique.joinToString(", ")}),")
            }
        }

        for (check in table.checkConstraints) {
            appendLine("constraint ${check.name}")
            appendIndentedLine("check (${check.condition}),")
        }
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
    indent {
        append("on $tableName ")

        appendColumnList(columns)

        if (include.isNotEmpty()) {
            append(" include ")
            appendColumnList(include)
        }

        if (where != null) {
            appendLine()
            append("where $where")
        }

        appendLine()
    }
    endStatement()
}
