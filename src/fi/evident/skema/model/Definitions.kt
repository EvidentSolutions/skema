package fi.evident.skema.model

public data class Schema(
    val tables: List<Table>,
)

public data class Table(
    val name: String,
    val primaryKey: PrimaryKey?,
    val columns: List<AnyColumn>,
    val indices: List<Index>,
    val checkConstraints: List<CheckConstraint>,
    val uniques: List<List<String>>,
    val comment: String?,
)

public sealed class AnyColumn {
    public abstract val name: String
}

public data class Column(
    override val name: String,
    val spec: ColumnSpec,
    val nullable: Boolean,
    val foreignKey: ForeignKey?,
) : AnyColumn()

public data class ComputedColumn(
    override val name: String,
    val sql: String,
) : AnyColumn()

public sealed class PrimaryKey {
    internal class Single(val column: Column) : PrimaryKey()
    internal class ForeignKeyRef(val name: String, val fk: ForeignKey) : PrimaryKey()
    internal class Composite(val columns: List<String>) : PrimaryKey()

    internal val columnType: Type
        get() = when (this) {
            is Single -> column.spec.type
            is ForeignKeyRef -> fk.type
            is Composite -> error("can't get type for composite primary key")
        }
}

public data class ForeignKey(
    val target: String,
    val type: Type,
    val cascadeDelete: Boolean
)

public data class Index(
    val name: String?,
    val columns: List<String>,
    val include: List<String>,
    val where: String?,
    val unique: Boolean = false
)

public data class CheckConstraint(
    val name: String,
    val condition: String,
)
