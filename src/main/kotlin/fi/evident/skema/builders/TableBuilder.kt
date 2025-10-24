package fi.evident.skema.builders

import fi.evident.skema.model.*

class TableBuilder(internal val tableName: String, private val comment: String?) {

    internal var primaryKey: PrimaryKey? = null
    private val columns = mutableListOf<AnyColumnBuilder>()
    private val indices = mutableListOf<Index>()
    private val checkConstraints = mutableListOf<CheckConstraint>()
    private val uniques = mutableListOf<List<String>>()

    infix fun String.primaryKey(spec: ColumnSpec) {
        primaryKey = PrimaryKey.Single(ColumnBuilder(this, spec, nullable = false).build())
    }

    infix fun String.primaryKey(fk: ForeignKey) {
        primaryKey = PrimaryKey.ForeignKeyRef(this, fk)
    }

    fun primaryKey(vararg columns: String) {
        primaryKey = PrimaryKey.Composite(columns.toList())
    }

    infix fun String.required(spec: ColumnSpec) =
        ColumnBuilder(this, spec, nullable = false).also { columns.add(it) }

    infix fun String.optional(spec: ColumnSpec) =
        ColumnBuilder(this, spec, nullable = true).also { columns.add(it) }

    infix fun String.computed(sql: String) {
        columns.add(ComputedColumnBuilder(this, sql))
    }

    infix fun String.required(fk: ForeignKey) =
        ColumnBuilder(this, ColumnSpec(fk.type), nullable = false, foreignKey = fk).also { columns.add(it) }

    infix fun String.optional(fk: ForeignKey) =
        ColumnBuilder(this, ColumnSpec(fk.type), nullable = true, foreignKey = fk).also { columns.add(it) }

    fun index(name: String?, columns: List<String>, include: List<String> = emptyList(), where: String? = null) {
        indices.add(Index(name, columns, include, where))
    }

    fun uniqueIndex(name: String?, columns: List<String>, include: List<String> = emptyList(), where: String? = null) {
        indices.add(Index(name, columns, include, where, unique = true))
    }

    fun check(name: String, condition: String) {
        checkConstraints.add(CheckConstraint(name, condition))
    }

    fun unique(vararg columns: String) {
        uniques.add(columns.toList())
    }

    fun build() = Table(
        name = tableName,
        primaryKey = primaryKey,
        columns = columns.map { it.build() },
        indices = indices,
        checkConstraints = checkConstraints,
        uniques = uniques,
        comment = comment,
    )
}

fun foreignKey(
    target: String,
    type: Type,
    cascadeDelete: Boolean = false,
) = ForeignKey(target, type, cascadeDelete = cascadeDelete)

fun foreignKey(target: Table, cascadeDelete: Boolean = false): ForeignKey {
    val pk = target.primaryKey ?: error("no primary key for table ${target.name}")
    return foreignKey(
        target = target.name,
        type = pk.columnType,
        cascadeDelete = cascadeDelete,
    )
}

fun TableBuilder.foreignKeySelf(): ForeignKey {
    val pk = primaryKey ?: error("no primary key for table $tableName")
    return foreignKey(tableName, pk.columnType)
}
