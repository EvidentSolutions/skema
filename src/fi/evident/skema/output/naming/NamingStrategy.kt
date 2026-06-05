package fi.evident.skema.output.naming

import fi.evident.skema.model.*
import fi.evident.skema.model.toConstraintName

public interface NamingStrategy {

    public fun indexName(table: Table, index: Index): String =
        index.name?.name ?: "idx_${table.name}_${index.columns.joinToString("_")}"

    public fun primaryKeyConstraintName(table: Table, columnNames: List<ColumnName>): String? =
        "pk_${table.name}"

    public fun foreignKeyConstraintName(ownerTable: Table, targetTableName: TableName, columnName: ColumnName): String? =
        "fk_${ownerTable.name}_${columnName.name.removeSuffix("_id")}"

    public fun uniqueConstraintName(table: Table, columnNames: List<ColumnName>): String? =
        "uq_${table.name}_${columnNames.joinToString("_")}"

    public fun defaultConstraintName(table: Table, columnName: ColumnName): String? =
        "df_${table.name}_${columnName}"
}

internal object DefaultNamingStrategy : NamingStrategy

context(table: Table, namingStrategy: NamingStrategy)
internal fun indexName(index: Index) =
    namingStrategy.indexName(table, index).toIndexName()

context(table: Table, namingStrategy: NamingStrategy)
internal val Column.primaryKeyConstraintName: ConstraintName?
    get() = namingStrategy.primaryKeyConstraintName(table, listOf(name))?.toConstraintName()

context(table: Table, namingStrategy: NamingStrategy)
internal val Column.defaultConstraintName: ConstraintName?
    get() = namingStrategy.defaultConstraintName(table, name)?.toConstraintName()

context(table: Table, namingStrategy: NamingStrategy)
internal val TableConstraint.PrimaryKey.constraintName: ConstraintName?
    get() = namingStrategy.primaryKeyConstraintName(table, columns)?.toConstraintName()

context(table: Table, namingStrategy: NamingStrategy)
internal fun Column.foreignKeyConstraintName(targetTableName: TableName) =
    namingStrategy.foreignKeyConstraintName(table, targetTableName, name)?.toConstraintName()

context(table: Table, namingStrategy: NamingStrategy)
internal fun uniqueConstraintName(columnNames: List<ColumnName>) =
    namingStrategy.uniqueConstraintName(table, columnNames)?.toConstraintName()

