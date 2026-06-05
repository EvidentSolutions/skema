package fi.evident.skema.model

public data class Table(
    val name: TableName,
    val primaryKey: PrimaryKey?,
    val columns: List<AnyColumn>,
    val indices: List<Index>,
    val constraints: List<TableConstraint>,
    val comment: String?,
) {

    val primaryKeyColumn: Column?
        get() = columns.filterIsInstance<Column>().find { it.isPrimaryKey }
}
