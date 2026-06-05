package fi.evident.skema.model

public sealed class TableConstraint {
    public data class Check(
        val name: ConstraintName,
        val condition: SqlExpression,
    ) : TableConstraint()

    public data class Unique(
        val columns: List<ColumnName>,
    ) : TableConstraint()

    public data class PrimaryKey(
        val columns: List<ColumnName>
    ) : TableConstraint()
}
