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
        for ((i, decl) in table.tableDeclarations().withIndex()) {
            renderDeclaration(table, decl, namingStrategy)

            if (dialect.supportsTrailingCommas || i != table.columns.lastIndex)
                append(",")

            appendLine()
        }
    }
    appendLine(")")
    endStatement()
}

private fun DdlWriter.renderDeclaration(table: Table, declaration: TableDeclaration, namingStrategy: NamingStrategy) {
    when (declaration) {
        is TableDeclaration.SinglePrimaryKey -> {
            val pk = declaration.primaryKey.column

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
        }
        is TableDeclaration.ForeignKeyPrimaryKey -> {
            val pk = declaration.primaryKey

            append(pk.name)
            append(" ")
            append(pk.fk.type.name)

            val pkConstraintName = namingStrategy.primaryKeyConstraintName(table, listOf(pk.name))
            if (pkConstraintName.isNotEmpty()) {
                appendIndented("constraint $pkConstraintName primary key")
            } else {
                append(" primary key")
            }

            val fkConstraintName =
                namingStrategy.foreignKeyConstraintName(table, pk.fk.target, pk.name)
            if (fkConstraintName.isNotEmpty())
                appendIndented("constraint $fkConstraintName references ${pk.fk.target}")
            else
                appendIndented("references ${pk.fk.target}")
        }
        is TableDeclaration.CompositePrimaryKey -> {
            val pk = declaration.primaryKey
            val constraintName = namingStrategy.primaryKeyConstraintName(table, pk.columns)
            val pkDefinition = pk.columns.joinToString(", ", "(", ")")

            if (constraintName.isNotEmpty()) {
                append("constraint $constraintName ")
                appendIndented("primary key $pkDefinition")
            } else {
                append("primary key $pkDefinition")
            }
        }
        is TableDeclaration.ColumnDef -> {
            val column = declaration.column
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
        }
        is TableDeclaration.ComputedColumnDef -> {
            val column = declaration.column
            append(dialect.computedColumn(column))
        }
        is TableDeclaration.UniqueDef -> {
            val unique = declaration.columns
            val constraintName = namingStrategy.uniqueConstraintName(table, unique)
            if (constraintName.isNotEmpty()) {
                appendLine("constraint $constraintName")
                appendIndented("unique (${unique.joinToString(", ")})")
            } else {
                append("unique (${unique.joinToString(", ")})")
            }
        }
        is TableDeclaration.CheckDef -> {
            val check = declaration.check
            appendLine("constraint ${check.name}")
            appendIndented("check (${check.condition})")
        }
    }
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
