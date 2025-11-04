package fi.evident.skema.builders

import fi.evident.skema.model.Schema
import fi.evident.skema.model.Table

public fun schema(init: SchemaBuilder.() -> Unit): Schema =
    SchemaBuilder().apply(init).build()

public class SchemaBuilder internal constructor() {

    private val tables = mutableListOf<Table>()

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
