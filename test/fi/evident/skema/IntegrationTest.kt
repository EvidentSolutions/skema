package fi.evident.skema

import fi.evident.skema.builders.foreignKey
import fi.evident.skema.builders.schema
import fi.evident.skema.model.boolean
import fi.evident.skema.model.identity
import fi.evident.skema.model.int
import fi.evident.skema.model.text
import fi.evident.skema.model.varchar
import fi.evident.skema.output.generateSql
import kotlin.test.Test
import kotlin.test.assertEquals

internal class IntegrationTest {

    private val exampleSchema = schema {
        val department = table("department") {
            "id" primaryKey identity
            "name" required varchar(255)
            "description" optional text
        }

        table("employee") {
            "id" primaryKey identity
            "name" required varchar(255)
            "department_id" required foreignKey(department)
            "active" required boolean
        }
    }

    @Test
    fun `generate schema for SQL Server`() {
        assertEquals(
            """
            create table department
            (
                id int identity
                    constraint pk_department primary key,
                name varchar(255) not null,
                description varchar(max),
            )

            go

            create table employee
            (
                id int identity
                    constraint pk_employee primary key,
                name varchar(255) not null,
                department_id int not null
                    constraint fk_employee_department references department,
                active bit not null,
            )

            go


            """.trimIndent(),
            exampleSchema.generateSql()
        )
    }
}
