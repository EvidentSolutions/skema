package fi.evident.skema.model

@ConsistentCopyVisibility
public data class ColumnSpec internal constructor(
    val type: Type,
    internal val unique: Boolean = false,
    internal val generated: Boolean = false,
    internal val constraints: List<ColumnConstraint> = emptyList(),
)

internal sealed class ColumnConstraint {
    data class Default(val constraint: String) : ColumnConstraint()
}

public fun unique(spec: ColumnSpec): ColumnSpec = spec.copy(unique = true)
