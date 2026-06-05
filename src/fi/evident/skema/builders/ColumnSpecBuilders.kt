package fi.evident.skema.builders

import fi.evident.skema.model.Type

public class ColumnSpecContext internal constructor()

context(_: ColumnSpecContext)
public fun unique(spec: ColumnSpec): ColumnSpec =
    spec.unique()

context(_: ColumnSpecContext)
public fun rawType(sql: String): ColumnSpec =
    ColumnSpec(Type.Raw(sql))

context(_: ColumnSpecContext)
public fun varchar(length: Int): ColumnSpec =
    ColumnSpec(Type.Varchar(length))

context(_: ColumnSpecContext)
public fun varbinary(length: Int): ColumnSpec =
    ColumnSpec(Type.Varbinary(length))

context(_: ColumnSpecContext)
public val varbinaryMax: ColumnSpec
    get() = ColumnSpec(Type.VarbinaryMax)

context(_: ColumnSpecContext)
public val text: ColumnSpec
    get() = ColumnSpec(Type.Text)

context(_: ColumnSpecContext)
public fun decimal(precision: Int, scale: Int = 0): ColumnSpec =
    ColumnSpec(Type.Decimal(precision, scale))

context(_: ColumnSpecContext)
public fun time(fractionalSecondsPrecision: Int? = null): ColumnSpec =
    ColumnSpec(Type.Time(fractionalSecondsPrecision))

context(_: ColumnSpecContext)
public val int: ColumnSpec
    get() = ColumnSpec(Type.Integer)

context(_: ColumnSpecContext)
public val bigint: ColumnSpec
    get() = ColumnSpec(Type.BigInt)

context(_: ColumnSpecContext)
public val boolean: ColumnSpec
    get() = ColumnSpec(Type.Boolean)

context(_: ColumnSpecContext)
public val date: ColumnSpec
    get() = ColumnSpec(Type.Date)

context(_: ColumnSpecContext)
public val datetime: ColumnSpec
    get() = ColumnSpec(Type.DateTime)

context(_: ColumnSpecContext)
public val float: ColumnSpec
    get() = ColumnSpec(Type.Float)

context(_: ColumnSpecContext)
public val uuid: ColumnSpec
    get() = ColumnSpec(Type.Uuid)

context(_: ColumnSpecContext)
public val generatedInt: ColumnSpec
    get() = int.copy(generated = true)

