package fi.evident.skema.builders

import fi.evident.skema.model.ColumnConstraint
import fi.evident.skema.model.Type

@ConsistentCopyVisibility
public data class ColumnSpec internal constructor(
    internal val type: Type,
    internal val generated: Boolean = false,
    internal val constraints: List<ColumnConstraint> = emptyList(),
) {
    public fun unique(): ColumnSpec = copy(constraints = constraints + ColumnConstraint.Unique)
}
