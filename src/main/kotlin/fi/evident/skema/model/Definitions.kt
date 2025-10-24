package fi.evident.skema.model

data class Schema(
    val tables: List<Table>,
)

data class Table(
    val name: String,
    val primaryKey: PrimaryKey?,
    val columns: List<AnyColumn>,
    val indices: List<Index>,
    val checkConstraints: List<CheckConstraint>,
    val uniques: List<List<String>>,
    val comment: String?,
)

sealed class AnyColumn {
    abstract val name: String
    abstract val typeLength: Int
}

data class Column(
    override val name: String,
    val spec: ColumnSpec,
    val nullable: Boolean,
    val foreignKey: ForeignKey?,
) : AnyColumn() {
    override val typeLength: Int
        get() = spec.type.name.length
}

data class ComputedColumn(
    override val name: String,
    val sql: String,
) : AnyColumn() {
    override val typeLength: Int
        get() = 0
}

sealed class PrimaryKey {
    class Single(val column: Column) : PrimaryKey()
    class ForeignKeyRef(val name: String, val fk: ForeignKey) : PrimaryKey()
    class Composite(val columns: List<String>) : PrimaryKey()

    val columnType: Type
        get() = when (this) {
            is Single -> column.spec.type
            is ForeignKeyRef -> fk.type
            is Composite -> error("can't get type for composite primary key")
        }
}

data class ForeignKey(
    val target: String,
    val type: Type,
    val cascadeDelete: Boolean
)

data class Index(
    val name: String?,
    val columns: List<String>,
    val include: List<String>,
    val where: String?,
    val unique: Boolean = false
)

data class CheckConstraint(
    val name: String,
    val condition: String,
)
