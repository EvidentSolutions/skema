package fi.evident.skema.builders

import fi.evident.skema.model.Schema
import fi.evident.skema.model.Table

/**
 * Call the given initializer to build a schema.
 */
public fun schema(init: SchemaBuilder.() -> Unit): Schema =
    SchemaBuilder().apply(init).build()

/**
 * Builder for a schema.
 */
public class SchemaBuilder internal constructor() {

    private val tables = mutableListOf<Table>()

    /**
     * Add a table to the schema.
     */
    @IgnorableReturnValue
    public fun table(
        name: String,
        comment: String? = null,
        initialize: TableBuilder.() -> Unit,
    ): Table {
        val table = TableBuilder(name, comment).apply { initialize() }.build()
        tables += table
        return table
    }

    internal fun build() = Schema(tables)
}
