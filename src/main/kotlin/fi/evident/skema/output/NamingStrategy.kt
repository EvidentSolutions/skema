package fi.evident.skema.output

import fi.evident.skema.model.Index
import fi.evident.skema.model.Table

public interface NamingStrategy {
    public fun indexName(table: Table, index: Index): String =
        index.name ?: ""

    public fun foreignKeyName(ownerTable: Table, targetTableName: String, columnName: String): String =
        "fk_${ownerTable.name}_${columnName.removeSuffix("_id")}"

    public fun uniqueColumnConstraintName(table: Table, columnName: String): String =
        "uq_${table.name}_$columnName"

}

internal object DefaultNamingStrategy : NamingStrategy
