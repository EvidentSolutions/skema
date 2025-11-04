package fi.evident.skema.model

import fi.evident.skema.builders.TableBuilder

public fun TableBuilder.sqlType(type: Type): ColumnSpec = ColumnSpec(type)

public fun TableBuilder.sqlType(name: String): ColumnSpec = sqlType(Type(name))

public fun TableBuilder.varchar(length: Int): ColumnSpec = sqlType("varchar($length)")

public fun TableBuilder.varbinary(length: Int): ColumnSpec = sqlType("varbinary($length)")

public fun TableBuilder.varbinaryMax(): ColumnSpec = sqlType("varbinary(max)")

public fun TableBuilder.text(): ColumnSpec = sqlType("varchar(max)")

public fun TableBuilder.decimal(x: Int, y: Int): ColumnSpec = sqlType("decimal($x, $y)")

public fun TableBuilder.time(x: Int): ColumnSpec = sqlType("time($x)")

public val TableBuilder.int: ColumnSpec
    get() = sqlType("int")

public val TableBuilder.bigint: ColumnSpec
    get() = sqlType("bigint")

public val TableBuilder.boolean: ColumnSpec
    get() = sqlType("bit")

public val TableBuilder.date: ColumnSpec
    get() = sqlType("date")

public val TableBuilder.identity: ColumnSpec
    get() = ColumnSpec(Type("int", identity = true))

public val TableBuilder.datetime: ColumnSpec
    get() = sqlType("datetime")

public val TableBuilder.float: ColumnSpec
    get() = sqlType("float")

public val TableBuilder.uniqueidentifier: ColumnSpec
    get() = sqlType("uniqueidentifier")
