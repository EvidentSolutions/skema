package fi.evident.skema.output

import fi.evident.skema.model.Type

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
        append(dialect.quoteIdentifier(type.name))
        if (type.dimensions.isNotEmpty()) {
            append("(")
            append(type.dimensions.joinToString(", "))
            append(")")
        }
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
