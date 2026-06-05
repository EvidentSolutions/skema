package fi.evident.skema.model

@JvmInline
public value class SqlExpression internal constructor(public val sql: String) {
    override fun toString(): String = sql
}

internal fun String.toSqlExpression() = SqlExpression(this)
