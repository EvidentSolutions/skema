package fi.evident.skema.model

public sealed class PrimaryKey {
    internal class Single(val column: Column) : PrimaryKey()

    internal val columnType: Type
        get() = when (this) {
            is Single -> column.type
        }
}
