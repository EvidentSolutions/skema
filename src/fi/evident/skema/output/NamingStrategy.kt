package fi.evident.skema.output

import fi.evident.skema.model.Index
import fi.evident.skema.model.Table

public interface NamingStrategy {

    public fun indexName(table: Table, index: Index): String =
        index.name ?: "idx_${table.name}_${index.columns.joinToString("_")}"

    public fun primaryKeyConstraintName(table: Table, columnNames: List<String>): String =
        "pk_${table.name}"

    public fun foreignKeyConstraintName(ownerTable: Table, targetTableName: String, columnName: String): String =
        "fk_${ownerTable.name}_${columnName.removeSuffix("_id")}"

    public fun uniqueConstraintName(table: Table, columnNames: List<String>): String =
        "uq_${table.name}_${columnNames.joinToString("_")}"

    public fun defaultConstraintName(table: Table, columnName: String): String =
        "df_${table.name}_${columnName}"
}

internal object DefaultNamingStrategy : NamingStrategy
