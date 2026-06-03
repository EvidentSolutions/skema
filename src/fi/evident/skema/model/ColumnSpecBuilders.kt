package fi.evident.skema.model

import fi.evident.skema.builders.TableBuilder

context(_: TableBuilder)
private fun specOf(type: Type): ColumnSpec =
    ColumnSpec(type)

context(_: TableBuilder)
public fun rawType(sql: String): ColumnSpec =
    specOf(Type.Raw(sql))

context(_: TableBuilder)
public fun varchar(length: Int): ColumnSpec =
    specOf(Type.Varchar(length))

context(_: TableBuilder)
public fun varbinary(length: Int): ColumnSpec =
    specOf(Type.Varbinary(length))

context(_: TableBuilder)
public val varbinaryMax: ColumnSpec
    get() = specOf(Type.VarbinaryMax)

context(_: TableBuilder)
public val text: ColumnSpec
    get() = specOf(Type.Text)

context(_: TableBuilder)
public fun decimal(precision: Int, scale: Int = 0): ColumnSpec =
    specOf(Type.Decimal(precision, scale))

context(_: TableBuilder)
public fun time(fractionalSecondsPrecision: Int? = null): ColumnSpec =
    specOf(Type.Time(fractionalSecondsPrecision))

context(_: TableBuilder)
public val int: ColumnSpec
    get() = specOf(Type.Integer)

context(_: TableBuilder)
public val bigint: ColumnSpec
    get() = specOf(Type.BigInt)

context(_: TableBuilder)
public val boolean: ColumnSpec
    get() = specOf(Type.Boolean)

context(_: TableBuilder)
public val date: ColumnSpec
    get() = specOf(Type.Date)

context(_: TableBuilder)
public val datetime: ColumnSpec
    get() = specOf(Type.DateTime)

context(_: TableBuilder)
public val float: ColumnSpec
    get() = specOf(Type.Float)

context(_: TableBuilder)
public val uuid: ColumnSpec
    get() = specOf(Type.Uuid)

// TODO: specific to SQL Server
context(_: TableBuilder)
public val generatedInt: ColumnSpec
    get() = int.copy(generated = true)

context(_: TableBuilder)
public val identity: ColumnSpec
    get() = generatedInt
