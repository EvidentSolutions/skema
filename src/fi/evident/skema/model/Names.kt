package fi.evident.skema.model

@JvmInline
public value class TableName internal constructor(public val name: String) {
    override fun toString(): String = name
}

@JvmInline
public value class ColumnName internal constructor(public val name: String) {
    override fun toString(): String = name
}

@JvmInline
public value class ConstraintName internal constructor(public val name: String) {
    override fun toString(): String = name
}

@JvmInline
public value class IndexName internal constructor(public val name: String) {
    override fun toString(): String = name
}

internal fun String.toTableName() = TableName(this)
internal fun String.toColumnName() = ColumnName(this)
internal fun String.toConstraintName() = ConstraintName(this)
internal fun String.toIndexName() = IndexName(this)
