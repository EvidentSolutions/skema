package fi.evident.skema.output

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

    fun appendLine(str: String) {
        append(str)
        appendLine()
    }

    fun appendIndentedLine(str: String) {
        indent {
            append(str)
            appendLine()
        }
    }

    fun appendIndented(str: String) {
        indent {
            append(str)
        }
    }

    fun appendLine() {
        append("\n")
    }

    fun endStatement() {
        appendLine(dialect.statementSeparator)
        appendLine()
    }

    fun lineComment(comment: String) {
        appendLine("-- $comment")
    }

    fun appendColumnList(columns: List<String>) {
        append(columns.joinToString(", ", "(", ")"))
    }

    override fun toString() = ddl.toString()
}
