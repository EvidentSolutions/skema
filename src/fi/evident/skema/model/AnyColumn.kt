package fi.evident.skema.model

public sealed class AnyColumn {
    public abstract val name: ColumnName
}

public data class Column(
    override val name: ColumnName,
    val type: Type,
    val nullable: Boolean,
    val generated: Boolean = false,
    val constraints: List<ColumnConstraint> = emptyList(),
) : AnyColumn() {

    val isPrimaryKey: Boolean
        get() = ColumnConstraint.PrimaryKey in constraints
}

public data class ComputedColumn(
    override val name: ColumnName,
    val expression: SqlExpression,
) : AnyColumn()
