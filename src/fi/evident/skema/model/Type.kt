package fi.evident.skema.model

public sealed class Type {

    internal object BigInt : Type()
    internal object Boolean : Type()
    internal object Date : Type()
    internal object DateTime : Type()
    internal class Decimal(val precision: Int, val scale: Int) : Type()
    internal object Float : Type()
    internal object Integer : Type()
    internal class Raw internal constructor(internal val name: String) : Type()
    internal class Time(val fractionalSecondsPrecision: Int?) : Type()
    internal object Text : Type()
    internal object Uuid : Type()
    internal class Varbinary(val len: Int) : Type()
    internal object VarbinaryMax : Type()
    internal class Varchar(val len: Int) : Type()
}
