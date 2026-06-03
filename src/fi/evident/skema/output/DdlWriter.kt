package fi.evident.skema.output

import fi.evident.skema.model.Type
import fi.evident.skema.output.dialect.Dialect
import fi.evident.skema.output.naming.NamingStrategy

internal class DdlWriter(val dialect: Dialect, val namingStrategy: NamingStrategy) {
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

    fun appendLine(str: String) {
        append(str)
        appendLine()
    }

    fun appendIndented(str: String) {
        indent {
            append(str)
        }
    }

    fun appendIdentifier(identifier: String) {
        append(dialect.quoteIdentifier(identifier))
    }

    fun appendType(type: Type) {
        append(dialect.encodeType(type))
    }

    fun appendLine() {
        append("\n")
    }

    fun endStatement() {
        append(dialect.statementSeparator)
        appendLine()
        appendLine()
    }

    fun lineComment(comment: String) {
        appendLine("-- $comment")
    }

    override fun toString() = ddl.toString()
}
