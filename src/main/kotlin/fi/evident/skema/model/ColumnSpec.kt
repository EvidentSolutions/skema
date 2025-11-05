package fi.evident.skema.model

public data class ColumnSpec(
    val type: Type,
    var unique: Boolean = false,
    val comment: String? = null,
    val identity: Boolean = false,
    val constraints: List<ColumnConstraint> = emptyList(),
)

public sealed class ColumnConstraint {

    public data class Default(val constraint: String, val name: String? = null) : ColumnConstraint()
}

public data class Type(
    val name: String,
)

public fun unique(spec: ColumnSpec): ColumnSpec = spec.copy(unique = true)
