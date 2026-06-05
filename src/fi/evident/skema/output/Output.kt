package fi.evident.skema.output

import fi.evident.skema.model.*
import fi.evident.skema.output.dialect.SqlServerDialect
import fi.evident.skema.output.naming.*

public fun Schema.generateSql(namingStrategy: NamingStrategy = DefaultNamingStrategy): String {
    val writer = DdlWriter(SqlServerDialect)

    context(namingStrategy, writer) {
        for (table in tables)
            table.generateSqlForTable()
    }

    return writer.toString()
}

context(namingStrategy: NamingStrategy, writer: DdlWriter)
private fun Table.generateSqlForTable() {
    if (comment != null)
        lineComment(comment)

    renderTable()

    for (index in indices)
        index.renderIndex()
}

context(namingStrategy: NamingStrategy, writer: DdlWriter)
private fun Table.renderTable() {
    append("create table ")
    appendIdentifier(name)
    appendLine()
    appendLine("(")
    indent {
        for ((i, column) in columns.withIndex()) {
            column.renderAnyColumn()

            if (dialect.supportsTrailingCommas || i != columns.lastIndex || constraints.isNotEmpty())
                append(",")

            appendLine()
        }

        for ((i, constraint) in constraints.withIndex()) {
            constraint.renderTableConstraint()

            if (dialect.supportsTrailingCommas || i != constraints.lastIndex)
                append(",")

            appendLine()
        }
    }
    appendLine(")")
    endStatement()
}

context(namingStrategy: NamingStrategy, table: Table, writer: DdlWriter)
private fun TableConstraint.renderTableConstraint() = when (this) {
    is TableConstraint.Check -> renderCheckConstraint()
    is TableConstraint.Unique -> renderUniqueConstraint()
    is TableConstraint.PrimaryKey -> renderPrimaryKey()
}

context(writer: DdlWriter)
private fun TableConstraint.Check.renderCheckConstraint() {
    renderTableConstraint(name) {
        append("check ($condition)")
    }
}

context(namingStrategy: NamingStrategy, table: Table, writer: DdlWriter)
private fun TableConstraint.Unique.renderUniqueConstraint() {
    renderTableConstraint(uniqueConstraintName(columns)) {
        append("unique ")
        renderColumnList(columns)
    }
}

context(namingStrategy: NamingStrategy, table: Table, writer: DdlWriter)
private fun AnyColumn.renderAnyColumn() = when (this) {
    is Column -> renderColumn()
    is ComputedColumn -> renderComputedColumn()
}

context(namingStrategy: NamingStrategy, table: Table, writer: DdlWriter)
private fun Column.renderColumn() {
    appendIdentifier(name)
    append(" ")
    appendType(type, generated)

    if (!nullable && !isPrimaryKey)
        append(" not null")

    context(this) {
        for (constraint in this.constraints)
            constraint.render()
    }
}

context(writer: DdlWriter)
private fun ComputedColumn.renderComputedColumn() {
    append(dialect.computedColumn(this))
}

context(table: Table, column: Column, namingStrategy: NamingStrategy, writer: DdlWriter)
private fun ColumnConstraint.render() = when (this) {
    is ColumnConstraint.PrimaryKey ->
        renderColumnConstraint(column.primaryKeyConstraintName) {
            append("primary key")
        }

    is ColumnConstraint.Default ->
        renderColumnConstraint(column.defaultConstraintName) {
            append("default $expression")
        }

    is ColumnConstraint.Unique ->
        renderColumnConstraint(uniqueConstraintName(listOf(column.name))) {
            append("unique")
        }

    is ColumnConstraint.ForeignKey ->
        renderColumnConstraint(column.foreignKeyConstraintName(target), indentAlways = true) {
            append("references ")
            appendIdentifier(target)

            if (cascadeDelete)
                append(" on delete cascade")
        }
}

context(namingStrategy: NamingStrategy, table: Table, ddlWriter: DdlWriter)
private fun TableConstraint.PrimaryKey.renderPrimaryKey() {
    val constraintName = constraintName

    if (constraintName != null) {
        append("constraint ")
        appendIdentifier(constraintName)
        append(" ")
        indent {
            append("primary key ")
            renderColumnList(columns)
        }
    } else {
        append("primary key ")
        renderColumnList(columns)
    }
}

context(namingStrategy: NamingStrategy, table: Table, writer: DdlWriter)
private fun Index.renderIndex() {
    append("create ")

    if (unique)
        append("unique ")

    append("index ")
    appendIdentifier(indexName(this))

    indent {
        append("on ")
        appendIdentifier(table.name)
        append(" ")

        renderColumnList(columns = columns)

        if (include.isNotEmpty()) {
            append(" include ")
            renderColumnList(columns = include)
        }

        if (where != null) {
            appendLine()
            append("where $where")
        }
    }
    appendLine()
    endStatement()
}

context(writer: DdlWriter)
private fun renderTableConstraint(name: ConstraintName?, callback: context(DdlWriter) () -> Unit) {
    if (name != null) {
        append("constraint ")
        appendIdentifier(name)
        appendLine()
        indent {
            callback()
        }
    } else {
        callback()
    }
}

context(writer: DdlWriter)
private fun renderColumnConstraint(
    name: ConstraintName?,
    indentAlways: Boolean = false,
    callback: context(DdlWriter) () -> Unit
) {
    if (name != null) {
        indent {
            append("constraint ")
            appendIdentifier(name)
            append(" ")
            callback()
        }
    } else if (indentAlways) {
        indent {
            callback()
        }
    } else {
        callback()
    }
}

context(writer: DdlWriter)
private fun renderColumnList(columns: List<ColumnName>) {
    append(columns.joinToString(", ", "(", ")") { dialect.quoteIdentifier(it.name) })
}

