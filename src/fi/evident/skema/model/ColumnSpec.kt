package fi.evident.skema.model

@ConsistentCopyVisibility
public data class ColumnSpec internal constructor(
    val type: Type,
    internal var unique: Boolean = false,
    internal val identity: Boolean = false,
    internal val constraints: List<ColumnConstraint> = emptyList(),
)

internal sealed class ColumnConstraint {
    data class Default(val constraint: String) : ColumnConstraint()
}

@ConsistentCopyVisibility
public data class Type internal constructor(
    val name: String,
    val dimensions: List<String>,
)

public fun unique(spec: ColumnSpec): ColumnSpec = spec.copy(unique = true)
