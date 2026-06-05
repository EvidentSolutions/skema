package fi.evident.skema.model

public sealed class ColumnConstraint {

    @ConsistentCopyVisibility
    public data class Default internal constructor(
        val expression: SqlExpression,
    ) : ColumnConstraint()

    @ConsistentCopyVisibility
    public data class ForeignKey internal constructor(
        val target: TableName,
        val type: Type,
        val cascadeDelete: Boolean,
    ) : ColumnConstraint()

    public data object PrimaryKey : ColumnConstraint()

    public data object Unique : ColumnConstraint()
}
