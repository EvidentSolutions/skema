package fi.evident.skema

import fi.evident.skema.builders.foreignKey
import fi.evident.skema.builders.schema
import fi.evident.skema.builders.boolean
import fi.evident.skema.builders.generatedInt
import fi.evident.skema.builders.text
import fi.evident.skema.builders.unique
import fi.evident.skema.builders.varchar
import fi.evident.skema.output.generateSql
import kotlin.test.Test
import kotlin.test.assertEquals

internal class IntegrationTest {

    private val exampleSchema = schema {
        val department = table("department") {
            "id" primaryKey generatedInt
            "name" required unique(varchar(255))
            "description" optional text default "''"
        }

        val employee = table("employee") {
            "id" primaryKey generatedInt
            "name" required varchar(255)
            "department_id" required foreignKey(department)
            "active" required boolean

            check("employee_name_not_empty", "name <> ''")
            unique("department_id", "name")
        }

        table("employee_account") {
            "id" primaryKey foreignKey(employee)
            "login" required varchar(255).unique()
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
                name varchar(255) not null
                    constraint uq_department_name unique,
                description varchar(max)
                    constraint df_department_description default '',
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
                constraint employee_name_not_empty
                    check (name <> ''),
                constraint uq_employee_department_id_name
                    unique (department_id, name),
            )

            go

            create table employee_account
            (
                id int
                    constraint pk_employee_account primary key
                    constraint fk_employee_account_id references employee,
                login varchar(255) not null
                    constraint uq_employee_account_login unique,
            )

            go


            """.trimIndent(),
            exampleSchema.generateSql()
        )
    }
}
