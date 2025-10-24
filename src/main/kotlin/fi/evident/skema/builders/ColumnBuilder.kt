package fi.evident.skema.builders

import fi.evident.skema.model.*

sealed class AnyColumnBuilder {
    abstract fun build(): AnyColumn
}

class ColumnBuilder(
    private val name: String,
    private var spec: ColumnSpec,
    private val nullable: Boolean,
    private val foreignKey: ForeignKey? = null
) : AnyColumnBuilder() {

    override fun build() = Column(name, spec, nullable, foreignKey)

    infix fun default(expression: String) {
        constraint(ColumnConstraint.Default(expression))
    }

    infix fun constraint(constraint: ColumnConstraint) {
        spec = spec.copy(constraints = spec.constraints + constraint)
    }

    infix fun comment(comment: String) {
        spec = spec.copy(comment = comment)
    }
}

class ComputedColumnBuilder(private val name: String, private val expression: String) : AnyColumnBuilder() {
    override fun build() = ComputedColumn(name, expression)
}
