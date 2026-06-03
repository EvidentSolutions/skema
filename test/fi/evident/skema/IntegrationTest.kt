package fi.evident.skema

import fi.evident.skema.builders.foreignKey
import fi.evident.skema.builders.schema
import fi.evident.skema.model.int
import fi.evident.skema.model.text
import fi.evident.skema.model.varchar
import fi.evident.skema.output.generateSql
import kotlin.test.Test
import kotlin.test.assertEquals

internal class IntegrationTest {

    private val exampleSchema = schema {
        val department = table("department") {
            "id" primaryKey int
            "name" required varchar(255)
            "description" optional text()
        }

        table("employee") {
            "id" primaryKey int
            "name" required varchar(255)
            "department_id" required foreignKey(department)
        }
    }

    @Test
    fun `generate schema for SQL Server`() {
        assertEquals(
            """
            create table department
            (
                id int
                    constraint pk_department primary key,
                name varchar(255) not null,
                description varchar(max),
            )

            go

            create table employee
            (
                id int
                    constraint pk_employee primary key,
                name varchar(255) not null,
                department_id int not null
                    constraint fk_employee_department references department,
            )

            go


            """.trimIndent(),
            exampleSchema.generateSql()
        )
    }
}
