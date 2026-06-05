package fi.evident.skema.builders

import fi.evident.skema.model.*

public sealed class AnyColumnBuilder {
    internal abstract fun build(): AnyColumn
}

public class ColumnBuilder internal constructor(
    private val name: ColumnName,
    private val type: Type,
    private val nullable: Boolean,
    private val generated: Boolean = false,
    private var constraints: List<ColumnConstraint> = emptyList(),
) : AnyColumnBuilder() {

    override fun build() = Column(
        name = name,
        type = type,
        nullable = nullable,
        generated = generated,
        constraints = constraints,
    )

    public infix fun default(expression: String) {
        constraints += ColumnConstraint.Default(expression.toSqlExpression())
    }
}

internal class ConstantColumnBuilder internal constructor(
    private val column: AnyColumn,
) : AnyColumnBuilder() {
    override fun build() = column
}
