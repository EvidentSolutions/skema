package fi.evident.skema.model

import fi.evident.skema.builders.TableBuilder

public fun TableBuilder.sqlType(type: Type): ColumnSpec = ColumnSpec(type)

public fun TableBuilder.sqlType(name: String, dimensions: List<String> = emptyList()): ColumnSpec = sqlType(Type(name, dimensions))

public fun TableBuilder.varchar(length: Int): ColumnSpec = sqlType("varchar", listOf(length.toString()))

public fun TableBuilder.varbinary(length: Int): ColumnSpec = sqlType("varbinary", listOf(length.toString()))

public fun TableBuilder.varbinaryMax(): ColumnSpec = sqlType("varbinary", listOf("max"))

public fun TableBuilder.text(): ColumnSpec = sqlType("varchar", listOf("max"))

public fun TableBuilder.decimal(x: Int, y: Int): ColumnSpec = sqlType("decimal", listOf(x.toString(), y.toString()))

public fun TableBuilder.time(x: Int): ColumnSpec = sqlType("time", listOf(x.toString()))

public val TableBuilder.int: ColumnSpec
    get() = sqlType("int")

public val TableBuilder.bigint: ColumnSpec
    get() = sqlType("bigint")

public val TableBuilder.boolean: ColumnSpec
    get() = sqlType("bit")

public val TableBuilder.date: ColumnSpec
    get() = sqlType("date")

public val TableBuilder.identity: ColumnSpec
    get() = int.copy(identity = true)

public val TableBuilder.datetime: ColumnSpec
    get() = sqlType("datetime")

public val TableBuilder.float: ColumnSpec
    get() = sqlType("float")

public val TableBuilder.uniqueidentifier: ColumnSpec
    get() = sqlType("uniqueidentifier")
