# Skema

A Kotlin DSL for defining database schemas with type safety and composability.

## Why Skema?

Database schemas are typically defined in raw SQL DDL, which has several problems:

- **No abstraction** - Repetitive patterns can't be extracted into reusable components
- **Poor maintainability** - Some databases generate random constraint names (e.g., `FK__table1__fk_col__5DCAEF64` in SQL Server) unless you explicitly name them, making schema changes difficult to track
- **Tied to concrete database** - Syntax between databases varies  

Skema solves these problems with a clean Kotlin DSL that lets you:

- **Define schemas once** using domain vocabulary
- **Abstract repetition** with reusable components
- **Generate consistent DDL** with semantic constraint names

> [!WARNING]
> Skema is still under development and definition syntax might change wildly.

## Example
```kotlin
val appSchema = schema {
    val user = table("user") {
        "id"           primaryKey identity
        "username"     required varchar(64)
        "email"        required email()
        "display_name" required varchar(128)
        "created_at"   required instant
        "updated_at"   required instant
    }

    val post = table("post") {
        "id"         primaryKey identity
        "author_id"  required foreignKey(user)
        "title"      required varchar(256)
        "content"    required text()
        "status"     required varchar(32)
        "created_at" required instant
        "updated_at" required instant
    }
}
```

## Features

### Composable Type Definitions

Define domain types once, use them everywhere:
```kotlin
fun email() = varchar(256)
fun passwordHash() = varchar(128)
fun instant() = datetime

val user = table("user") {
    "email"         required email()
    "password_hash" optional passwordHash()
    "created_at"    required instant()
}
```

### Reusable Schema Components

Extract common patterns into functions:
```kotlin
fun TableBuilder.auditable() {
    "created_at" required instant
    "updated_at" required instant
}

fun TableBuilder.contactDetails(prefix: String) {
    "${prefix}_address"      required varchar(256)
    "${prefix}_postal_code"  required varchar(32)
    "${prefix}_country_code" required countryCode()
}

val company = table("company") {
    "id"   primaryKey identity
    "name" required varchar(255)
    auditable()
    contactDetails("billing")
    contactDetails("shipping")
}
```

Or even:
```kotlin
fun SchemaBuilder.junction(
    table1: Table, 
    table2: Table,
    cascadeDelete: Boolean = true,
    extraDefinitions: TableBuilder.() -> Unit = {}
): Table {
    val column1 = "${table1.name}_id"
    val column2 = "${table2.name}_id"

    return table("${table1.name}_${table2.name}") {
        column1 required foreignKey(table1, cascadeDelete = cascadeDelete)
        column2 required foreignKey(table2, cascadeDelete = cascadeDelete)

        primaryKey(column1, column2)

        extraDefinitions()
    }
}

// Simple junction - just the two FKs
junction(user, role)

// Junction with metadata
junction(user, role) {
    "granted_at" required instant
    "granted_by" required foreignKey(user)
    "notes"      optional text()
}

// Junction with indexes
junction(post, tag) {
    "created_at" required instant

    index("idx_post_tag_created", columns = listOf("created_at"))
}
```

### Rich Column Specifications

Compose column properties functionally:
```kotlin
"id"        primaryKey identity
"email"     required unique(email())
"code"      required varchar(2) check "code IN ('A', 'B', 'C')"
"status"    required varchar(32) default "'PENDING'"
"is_active" computed "CASE WHEN deleted_at IS NULL THEN 1 ELSE 0 END"
```

### Foreign Keys

Type of the foreign key is inferred from the referenced table:
```kotlin
val comment = table("comment") {
    "id"        primaryKey identity
    "post_id"   required foreignKey(post, cascadeDelete = true)
    "author_id" required foreignKey(user)
    "content"   required text()
}
```

### Indices

```kotlin
table("session") {
    "id"         primaryKey identity
    "user_id"    required foreignKey(user)
    "expires_at" required instant
    "token"      required varchar(128)
    
    // Unique index with included columns
    uniqueIndex("idx_session_token", 
        columns = listOf("token"),
        include = listOf("user_id", "expires_at"))
    
    // Filtered index
    index("idx_active_sessions",
        columns = listOf("user_id", "expires_at"),
        where = "expires_at > GETDATE()")
}
```

### Composite Primary Keys
```kotlin
val user_role = table("user_role") {
    "user_id"    required foreignKey(user)
    "role_id"    required foreignKey(role)
    "granted_at" required instant
    
    primaryKey("user_id", "role_id")
}
```

### Table-Level Constraints
```kotlin
val account = table("account") {
    "id"             primaryKey identity
    "personal_email" optional email()
    "business_email" optional email()
    
    check("account_at_least_one_email",
        "personal_email IS NOT NULL OR business_email IS NOT NULL")
}
```

