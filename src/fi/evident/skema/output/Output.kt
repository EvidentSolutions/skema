package fi.evident.skema.output

import fi.evident.skema.model.*

public fun Schema.generateSql(namingStrategy: NamingStrategy = DefaultNamingStrategy): String {
    val writer = DdlWriter(SqlServerDialect, namingStrategy)
    for (table in tables)
        writer.generateSqlForTable(table)

    return writer.toString()
}

private fun DdlWriter.generateSqlForTable(table: Table) {
    if (table.comment != null)
        lineComment(table.comment)

    renderTable(table)

    for (index in table.indices)
        renderIndex(table, index)
}

private fun DdlWriter.renderTable(table: Table) {
    append("create table ")
    appendIdentifier(table.name)
    appendLine()
    appendLine("(")
    indent {
        for ((i, decl) in table.tableDeclarations().withIndex()) {
            renderTableDeclaration(table, decl)

            if (dialect.supportsTrailingCommas || i != table.columns.lastIndex)
                append(",")

            appendLine()
        }
    }
    appendLine(")")
    endStatement()
}

private fun DdlWriter.renderTableDeclaration(table: Table, decl: TableDeclaration) {
    when (decl) {
        is TableDeclaration.SinglePrimaryKey -> renderSinglePrimaryKey(table, decl)
        is TableDeclaration.ForeignKeyPrimaryKey -> renderForeignKeyPrimaryKey(table, decl)
        is TableDeclaration.CompositePrimaryKey -> renderCompositePrimaryKey(table, decl)
        is TableDeclaration.ColumnDef -> renderColumn(table, decl)
        is TableDeclaration.ComputedColumnDef -> renderComputedColumn(decl)
        is TableDeclaration.UniqueDef -> renderUniqueConstraint(table, decl)
        is TableDeclaration.CheckDef -> renderCheckConstraint(decl)
    }
}

private fun DdlWriter.renderCheckConstraint(decl: TableDeclaration.CheckDef) {
    val check = decl.check
    append("constraint ")
    appendIdentifier(check.name)
    appendLine()
    appendIndented("check (${check.condition})")
}

private fun DdlWriter.renderUniqueConstraint(table: Table, decl: TableDeclaration.UniqueDef) {
    val unique = decl.columns
    val constraintName = namingStrategy.uniqueConstraintName(table, unique)
    if (constraintName.isNotEmpty()) {
        append("constraint ")
        appendIdentifier(constraintName)
        indent {
            append("unique ")
            renderColumnList(unique)
        }
    } else {
        append("unique ")
        renderColumnList(unique)
    }
}

private fun DdlWriter.renderComputedColumn(decl: TableDeclaration.ComputedColumnDef) {
    append(dialect.computedColumn(decl.column))
}

private fun DdlWriter.renderColumn(table: Table, decl: TableDeclaration.ColumnDef) {
    val column = decl.column
    appendIdentifier(column.name)
    append(" ")
    appendType(column.spec.type)

    if (!column.nullable)
        append(" not null")

    for (constraint in column.spec.constraints) {
        when (constraint) {
            is ColumnConstraint.Default -> {
                val constraintName = namingStrategy.defaultConstraintName(table, column.name)
                if (constraintName.isNotEmpty())
                    indent {
                        append("constraint ")
                        appendIdentifier(constraintName)
                        append(" default ${constraint.constraint}")
                    }
                else
                    append(" default ${constraint.constraint}")
            }
        }
    }

    if (column.spec.unique) {
        val constraintName = namingStrategy.uniqueConstraintName(table, listOf(column.name))
        if (constraintName.isNotEmpty()) {
            indent {
                append("constraint ")
                appendIdentifier(constraintName)
                append(" unique")
            }
        } else
            append(" unique")
    }

    val fk = column.foreignKey
    if (fk != null) {
        indent {
            val constraintName = namingStrategy.foreignKeyConstraintName(table, fk.target, column.name)
            if (constraintName.isNotEmpty()) {
                append("constraint ")
                appendIdentifier(constraintName)
                append(" ")
            }

            append("references ")
            appendIdentifier(fk.target)

            if (fk.cascadeDelete)
                append(" on delete cascade")
        }
    }
}

private fun DdlWriter.renderSinglePrimaryKey(table: Table, decl: TableDeclaration.SinglePrimaryKey) {
    val pk = decl.primaryKey.column

    appendIdentifier(pk.name)
    append(" ")
    appendType(pk.spec.type)

    if (pk.spec.identity)
        append(" identity")

    val constraintName = namingStrategy.primaryKeyConstraintName(table, listOf(pk.name))
    if (constraintName.isNotEmpty()) {
        indent {
            append("constraint ")
            appendIdentifier(constraintName)
            append(" primary key")
        }
    } else {
        append(" primary key")
    }
}

private fun DdlWriter.renderForeignKeyPrimaryKey(table: Table, decl: TableDeclaration.ForeignKeyPrimaryKey) {
    val pk = decl.primaryKey

    append(pk.name)
    append(" ")
    appendType(pk.fk.type)

    val pkConstraintName = namingStrategy.primaryKeyConstraintName(table, listOf(pk.name))
    if (pkConstraintName.isNotEmpty()) {
        indent {
            append("constraint ")
            appendIdentifier(pkConstraintName)
            append(" primary key")
        }
    } else {
        append(" primary key")
    }

    val fkConstraintName = namingStrategy.foreignKeyConstraintName(table, pk.fk.target, pk.name)

    indent {
        if (fkConstraintName.isNotEmpty()) {
            append("constraint ")
            appendIdentifier(fkConstraintName)
            append(" ")
        }
        append("references ${pk.fk.target}")
    }
}

private fun DdlWriter.renderCompositePrimaryKey(table: Table, decl: TableDeclaration.CompositePrimaryKey) {
    val pk = decl.primaryKey
    val constraintName = namingStrategy.primaryKeyConstraintName(table, pk.columns)

    if (constraintName.isNotEmpty()) {
        append("constraint ")
        appendIdentifier(constraintName)
        append(" ")
        indent {
            append("primary key ")
            renderColumnList(pk.columns)
        }
    } else {
        append("primary key ")
        renderColumnList(pk.columns)
    }
}

private fun DdlWriter.renderIndex(
    table: Table,
    index: Index
) {
    append("create ")

    if (index.unique)
        append("unique ")

    append("index ")
    appendIdentifier(namingStrategy.indexName(table, index))

    indent {
        append("on ")
        appendIdentifier(table.name)
        append(" ")

        renderColumnList(columns = index.columns)

        if (index.include.isNotEmpty()) {
            append(" include ")
            renderColumnList(columns = index.include)
        }

        if (index.where != null) {
            appendLine()
            append("where ${index.where}")
        }
    }
    appendLine()
    endStatement()
}

private fun DdlWriter.renderColumnList(columns: List<String>) {
    append(columns.joinToString(", ", "(", ")") { dialect.quoteIdentifier(it) })
}

