package fi.evident.skema.model

// TODO: consider making the classes implement this directly instead of wrapping them
internal sealed interface TableDeclaration {
    data class ColumnDef(val column: Column) : TableDeclaration
    data class ComputedColumnDef(val column: ComputedColumn) : TableDeclaration
    data class SinglePrimaryKey(val primaryKey: PrimaryKey.Single) : TableDeclaration
    data class ForeignKeyPrimaryKey(val primaryKey: PrimaryKey.ForeignKeyRef) : TableDeclaration
    data class CompositePrimaryKey(val primaryKey: PrimaryKey.Composite) : TableDeclaration
    data class UniqueDef(val columns: List<String>) : TableDeclaration
    data class CheckDef(val check: CheckConstraint) : TableDeclaration
}

internal fun Table.tableDeclarations(): List<TableDeclaration> = buildList {
    when (primaryKey) {
        is PrimaryKey.ForeignKeyRef ->
            add(TableDeclaration.ForeignKeyPrimaryKey(primaryKey))
        is PrimaryKey.Single ->
            add(TableDeclaration.SinglePrimaryKey(primaryKey))
        is PrimaryKey.Composite -> {
            // handled after the columns
        }
        null -> {
            // nothing to do
        }
    }

    for (column in columns)
        when (column) {
            is Column -> add(TableDeclaration.ColumnDef(column))
            is ComputedColumn -> add(TableDeclaration.ComputedColumnDef(column))
        }

    if (primaryKey is PrimaryKey.Composite)
        add(TableDeclaration.CompositePrimaryKey(primaryKey))

    for (unique in uniques)
        add(TableDeclaration.UniqueDef(unique))

    for (check in checkConstraints)
        add(TableDeclaration.CheckDef(check))
}
