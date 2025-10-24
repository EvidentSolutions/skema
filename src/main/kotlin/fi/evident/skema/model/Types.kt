package fi.evident.skema.model

import fi.evident.skema.builders.TableBuilder

fun TableBuilder.sqlType(type: Type) = ColumnSpec(type)

fun TableBuilder.sqlType(name: String) = sqlType(Type(name))

fun TableBuilder.varchar(length: Int) = sqlType("varchar($length)")

fun TableBuilder.varbinary(length: Int) = sqlType("varbinary($length)")

fun TableBuilder.varbinaryMax() = sqlType("varbinary(max)")

fun TableBuilder.text() = sqlType("varchar(max)")

fun TableBuilder.decimal(x: Int, y: Int) = sqlType("decimal($x, $y)")

fun TableBuilder.time(x: Int) = sqlType("time($x)")

val TableBuilder.int: ColumnSpec
    get() = sqlType("int")

val TableBuilder.bigint: ColumnSpec
    get() = sqlType("bigint")

val TableBuilder.boolean: ColumnSpec
    get() = sqlType("bit")

val TableBuilder.date: ColumnSpec
    get() = sqlType("date")

val TableBuilder.identity: ColumnSpec
    get() = ColumnSpec(Type("int", identity = true))

val TableBuilder.datetime: ColumnSpec
    get() = sqlType("datetime")

val TableBuilder.float: ColumnSpec
    get() = sqlType("float")

val TableBuilder.uniqueidentifier: ColumnSpec
    get() = sqlType("uniqueidentifier")
