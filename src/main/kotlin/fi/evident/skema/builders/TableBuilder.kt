package fi.evident.skema.builders

import fi.evident.skema.model.*

public class TableBuilder(
    public val tableName: String,
    private val comment: String?
) {

    internal var primaryKey: PrimaryKey? = null
    private val columns = mutableListOf<AnyColumnBuilder>()
    private val indices = mutableListOf<Index>()
    private val checkConstraints = mutableListOf<CheckConstraint>()
    private val uniques = mutableListOf<List<String>>()

    public infix fun String.primaryKey(spec: ColumnSpec) {
        primaryKey = PrimaryKey.Single(ColumnBuilder(this, spec, nullable = false).build())
    }

    public infix fun String.primaryKey(fk: ForeignKey) {
        primaryKey = PrimaryKey.ForeignKeyRef(this, fk)
    }

    public fun primaryKey(vararg columns: String) {
        primaryKey = PrimaryKey.Composite(columns.toList())
    }

    public infix fun String.required(spec: ColumnSpec): ColumnBuilder =
        ColumnBuilder(this, spec, nullable = false).also { columns.add(it) }

    public infix fun String.optional(spec: ColumnSpec): ColumnBuilder =
        ColumnBuilder(this, spec, nullable = true).also { columns.add(it) }

    public infix fun String.computed(sql: String) {
        columns.add(ComputedColumnBuilder(this, sql))
    }

    public infix fun String.required(fk: ForeignKey): ColumnBuilder =
        ColumnBuilder(this, ColumnSpec(fk.type), nullable = false, foreignKey = fk).also { columns.add(it) }

    public infix fun String.optional(fk: ForeignKey): ColumnBuilder =
        ColumnBuilder(this, ColumnSpec(fk.type), nullable = true, foreignKey = fk).also { columns.add(it) }

    public fun index(name: String?, columns: List<String>, include: List<String> = emptyList(), where: String? = null) {
        indices.add(Index(name, columns, include, where))
    }

    public fun uniqueIndex(name: String?, columns: List<String>, include: List<String> = emptyList(), where: String? = null) {
        indices.add(Index(name, columns, include, where, unique = true))
    }

    public fun check(name: String, condition: String) {
        checkConstraints.add(CheckConstraint(name, condition))
    }

    public fun unique(vararg columns: String) {
        uniques.add(columns.toList())
    }

    internal fun build() = Table(
        name = tableName,
        primaryKey = primaryKey,
        columns = columns.map { it.build() },
        indices = indices,
        checkConstraints = checkConstraints,
        uniques = uniques,
        comment = comment,
    )
}

public fun foreignKey(
    target: String,
    type: Type,
    cascadeDelete: Boolean = false,
): ForeignKey = ForeignKey(target, type, cascadeDelete = cascadeDelete)

public fun foreignKey(target: Table, cascadeDelete: Boolean = false): ForeignKey {
    val pk = target.primaryKey ?: error("no primary key for table ${target.name}")
    return foreignKey(
        target = target.name,
        type = pk.columnType,
        cascadeDelete = cascadeDelete,
    )
}

public fun TableBuilder.foreignKeySelf(): ForeignKey {
    val pk = primaryKey ?: error("no primary key for table $tableName")
    return foreignKey(tableName, pk.columnType)
}
