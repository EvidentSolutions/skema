package fi.evident.skema.output

internal class DdlWriter(val dialect: Dialect) {
    private val ddl = StringBuilder()

    fun append(str: String) {
        ddl.append(str)
    }

    fun appendLine(str: String = "") {
        ddl.appendLine(str)
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
