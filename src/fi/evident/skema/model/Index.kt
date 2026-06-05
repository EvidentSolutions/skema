package fi.evident.skema.model

public data class Index(
    val name: IndexName?,
    val columns: List<ColumnName>,
    val include: List<ColumnName>,
    val where: SqlExpression?,
    val unique: Boolean = false
)
