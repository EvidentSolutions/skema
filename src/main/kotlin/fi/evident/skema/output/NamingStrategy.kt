package fi.evident.skema.output

import fi.evident.skema.model.Index
import fi.evident.skema.model.Table

public interface NamingStrategy {
    public fun indexName(table: Table, index: Index): String = index.name ?: ""
}
