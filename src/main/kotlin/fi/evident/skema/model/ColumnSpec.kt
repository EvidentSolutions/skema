package fi.evident.skema.model

data class ColumnSpec(
    val type: Type,
    var unique: Boolean = false,
    val comment: String? = null,
    val constraints: List<ColumnConstraint> = emptyList(),
)

sealed class ColumnConstraint {

    data class Default(val constraint: String, val name: String? = null) : ColumnConstraint()
}

data class Type(val name: String, val identity: Boolean = false)

fun unique(spec: ColumnSpec) = spec.copy(unique = true)
