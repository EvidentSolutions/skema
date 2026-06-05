package fi.evident.skema.output

import fi.evident.skema.model.ColumnName
import fi.evident.skema.model.ConstraintName
import fi.evident.skema.model.IndexName
import fi.evident.skema.model.TableName
import fi.evident.skema.model.Type
import fi.evident.skema.output.dialect.Dialect

internal class DdlWriter(val dialect: Dialect) {
    private val ddl = StringBuilder()
    private var indent = 0
    private var atStartOfLine = true

    fun append(str: String) {
        if (str.isEmpty()) return

        if (atStartOfLine)
            ddl.append("    ".repeat(indent))

        ddl.append(str)
        atStartOfLine = str.endsWith("\n")
    }

    fun indent(block: () -> Unit) {
        if (!atStartOfLine)
            appendLine()
        indent++
        block()
        indent--
    }

    fun appendLine() {
        append("\n")
    }

    override fun toString() = ddl.toString()
}

context(writer: DdlWriter)
internal fun append(str: String) = writer.append(str)

context(writer: DdlWriter)
internal fun appendLine() = writer.appendLine()

context(writer: DdlWriter)
internal fun appendLine(s: String) {
    append(s)
    appendLine()
}

context(writer: DdlWriter)
private fun appendIdentifier(identifier: String) {
    append(dialect.quoteIdentifier(identifier))
}

context(writer: DdlWriter)
internal fun appendIdentifier(name: ConstraintName) {
    appendIdentifier(name.name)
}

context(writer: DdlWriter)
internal fun appendIdentifier(name: ColumnName) {
    appendIdentifier(name.name)
}

context(writer: DdlWriter)
internal fun appendIdentifier(name: IndexName) {
    appendIdentifier(name.name)
}

context(writer: DdlWriter)
internal fun appendIdentifier(name: TableName) {
    appendIdentifier(name.name)
}

context(writer: DdlWriter)
internal fun indent(block: () -> Unit) = writer.indent { block() }

context(writer: DdlWriter)
internal fun endStatement() {
    append(dialect.statementSeparator)
    appendLine()
    appendLine()
}

context(writer: DdlWriter)
internal fun lineComment(comment: String) {
    require('\n' !in comment) { "Line comments cannot contain newlines" }
    appendLine("-- $comment")
}

context(writer: DdlWriter)
internal fun appendType(type: Type, generated: Boolean) {
    append(dialect.encodeType(type, generated))
}

context(writer: DdlWriter)
internal val dialect: Dialect
    get() = writer.dialect
