package fi.evident.skema.output

public interface SchemaCallbacks : NamingStrategy {
    public fun convertName(name: String): String = name
    public fun tableEndHook(name: String): String = ""
}
