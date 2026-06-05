package fi.evident.skema.builders

import fi.evident.skema.model.*
import fi.evident.skema.model.Index

/**
 * Builder for a table.
 */
public class TableBuilder internal constructor(
    public val tableName: TableName,
    private val comment: String?
) {

    internal var primaryKey: PrimaryKey? = null
    private val columns = mutableListOf<AnyColumnBuilder>()
    private val indices = mutableListOf<Index>()
    private val constraints = mutableListOf<TableConstraint>()

    public infix fun String.primaryKey(spec: ColumnSpec) {
        columns += ColumnBuilder(
            name = this.toColumnName(),
            type = spec.type,
            generated = spec.generated,
            constraints = listOf(ColumnConstraint.PrimaryKey) + spec.constraints,
            nullable = false,
        )
    }

    public infix fun String.primaryKey(fk: ColumnConstraint.ForeignKey) {
        columns += ColumnBuilder(
            name = this.toColumnName(),
            type = fk.type,
            nullable = false,
            constraints = listOf(ColumnConstraint.PrimaryKey, fk)
        )
    }

    public fun primaryKey(vararg columns: String) {
        constraints += TableConstraint.PrimaryKey(columns.map(String::toColumnName))
    }

    @IgnorableReturnValue
    public infix fun String.required(spec: ColumnSpec): ColumnBuilder =
        ColumnBuilder(
            name = this.toColumnName(),
            type = spec.type,
            generated = spec.generated,
            constraints = spec.constraints,
            nullable = false
        ).also { columns.add(it) }

    @IgnorableReturnValue
    public infix fun String.optional(spec: ColumnSpec): ColumnBuilder =
        ColumnBuilder(
            name = this.toColumnName(),
            type = spec.type,
            generated = spec.generated,
            constraints = spec.constraints,
            nullable = true
        ).also { columns.add(it) }

    public infix fun String.computed(sql: String) {
        columns.add(ConstantColumnBuilder(ComputedColumn(this.toColumnName(), sql.toSqlExpression())))
    }

    @IgnorableReturnValue
    public infix fun String.required(fk: ColumnConstraint.ForeignKey): ColumnBuilder =
        ColumnBuilder(name = this.toColumnName(), type = fk.type, nullable = false, constraints = listOf(fk)).also { columns.add(it) }

    @IgnorableReturnValue
    public infix fun String.optional(fk: ColumnConstraint.ForeignKey): ColumnBuilder =
        ColumnBuilder(name = this.toColumnName(), type = fk.type, nullable = true, constraints = listOf(fk)).also { columns.add(it) }

    public fun index(
        vararg columns: String,
        include: List<String> = emptyList(),
        where: String? = null,
        name: String? = null
    ) {
        indices.add(Index(name?.toIndexName(), columns.map(String::toColumnName), include.map(String::toColumnName), where?.toSqlExpression()))
    }

    public fun uniqueIndex(
        vararg columns: String,
        include: List<String> = emptyList(),
        where: String? = null,
        name: String? = null
    ) {
        indices.add(Index(name?.toIndexName(), columns.map(String::toColumnName), include.map(String::toColumnName), where?.toSqlExpression(), unique = true))
    }

    public fun check(name: String, condition: String) {
        constraints.add(TableConstraint.Check(name.toConstraintName(), condition.toSqlExpression()))
    }

    public fun unique(vararg columns: String) {
        constraints.add(TableConstraint.Unique(columns.map(String::toColumnName)))
    }

    internal fun build() = Table(
        name = tableName,
        primaryKey = primaryKey,
        columns = columns.map { it.build() },
        indices = indices,
        constraints = constraints,
        comment = comment,
    )
}

public fun foreignKey(
    target: String,
    type: Type,
    cascadeDelete: Boolean = false,
): ColumnConstraint.ForeignKey  = foreignKey(target.toTableName(), type, cascadeDelete = cascadeDelete)

public fun foreignKey(
    target: TableName,
    type: Type,
    cascadeDelete: Boolean = false,
): ColumnConstraint.ForeignKey  = ColumnConstraint.ForeignKey(target, type, cascadeDelete = cascadeDelete)

public fun foreignKey(target: Table, cascadeDelete: Boolean = false): ColumnConstraint.ForeignKey {
    val pk = target.primaryKeyColumn ?: error("no primary key for table ${target.name}")
    return foreignKey(
        target = target.name,
        type = pk.type,
        cascadeDelete = cascadeDelete,
    )
}

public fun TableBuilder.foreignKeySelf(): ColumnConstraint.ForeignKey  {
    val pk = primaryKey ?: error("no primary key for table $tableName")
    return foreignKey(tableName, pk.columnType)
}
