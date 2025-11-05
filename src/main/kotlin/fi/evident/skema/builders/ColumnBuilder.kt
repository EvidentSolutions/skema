package fi.evident.skema.builders

import fi.evident.skema.model.*
import fi.evident.skema.model.ColumnSpec

public sealed class AnyColumnBuilder {
    internal abstract fun build(): AnyColumn
}

public class ColumnBuilder(
    private val name: String,
    private var spec: ColumnSpec,
    private val nullable: Boolean,
    private val foreignKey: ForeignKey? = null
) : AnyColumnBuilder() {

    override fun build() = Column(name, spec, nullable, foreignKey)

    public infix fun default(expression: String) {
        spec = spec.copy(constraints = spec.constraints + ColumnConstraint.Default(expression))
    }

    public infix fun comment(comment: String) {
        spec = spec.copy(comment = comment)
    }
}

internal class ComputedColumnBuilder(private val name: String, private val expression: String) : AnyColumnBuilder() {
    override fun build() = ComputedColumn(name, expression)
}
