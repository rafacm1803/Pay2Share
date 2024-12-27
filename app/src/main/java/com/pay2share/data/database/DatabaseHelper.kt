package com.pay2share.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Pay2Share.db"
        private const val DATABASE_VERSION = 1

        // Tablas
        const val TABLE_GROUPS = "groups"
        const val TABLE_EXPENSES = "expenses"
        const val TABLE_PARTICIPANTS = "participants"
        const val TABLE_DEBTS = "debts"

        // Columnas - Groups
        const val COL_GROUP_ID = "id"
        const val COL_GROUP_NAME = "name"

        // Columnas - Expenses
        const val COL_EXPENSE_ID = "id"
        const val COL_EXPENSE_NAME = "name"
        const val COL_EXPENSE_AMOUNT = "amount"
        const val COL_EXPENSE_DATE = "date"
        const val COL_EXPENSE_PAYER = "payer"
        const val COL_EXPENSE_GROUP_ID = "group_id"

        // Columnas - Participants
        const val COL_PARTICIPANT_ID = "id"
        const val COL_PARTICIPANT_NAME = "name"
        const val COL_PARTICIPANT_EXPENSE_ID = "expense_id"
        const val COL_PARTICIPANT_GROUP_ID = "group_id"

        // Columnas - Debts
        const val COL_DEBT_ID = "id"
        const val COL_DEBT_CREDITOR = "creditor"
        const val COL_DEBT_DEBTOR = "debtor"
        const val COL_DEBT_AMOUNT = "amount"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla de grupos
        val createGroupsTable = """
            CREATE TABLE $TABLE_GROUPS (
                $COL_GROUP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_GROUP_NAME TEXT NOT NULL
            )
        """.trimIndent()

        // Crear tabla de gastos
        val createExpensesTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COL_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EXPENSE_NAME TEXT NOT NULL,
                $COL_EXPENSE_AMOUNT REAL NOT NULL,
                $COL_EXPENSE_DATE TEXT NOT NULL,
                $COL_EXPENSE_PAYER TEXT NOT NULL,
                $COL_EXPENSE_GROUP_ID INTEGER,
                FOREIGN KEY($COL_EXPENSE_GROUP_ID) REFERENCES $TABLE_GROUPS($COL_GROUP_ID)
            )
        """.trimIndent()

        // Crear tabla de participantes
        val createParticipantsTable = """
            CREATE TABLE $TABLE_PARTICIPANTS (
                $COL_PARTICIPANT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PARTICIPANT_NAME TEXT NOT NULL,
                $COL_PARTICIPANT_EXPENSE_ID INTEGER,
                $COL_PARTICIPANT_GROUP_ID INTEGER,
                FOREIGN KEY($COL_PARTICIPANT_EXPENSE_ID) REFERENCES $TABLE_EXPENSES($COL_EXPENSE_ID),
                FOREIGN KEY($COL_PARTICIPANT_GROUP_ID) REFERENCES $TABLE_GROUPS($COL_GROUP_ID)
            )
        """.trimIndent()

        // Crear tabla de deudas
        val createDebtsTable = """
            CREATE TABLE $TABLE_DEBTS (
                $COL_DEBT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DEBT_CREDITOR TEXT NOT NULL,
                $COL_DEBT_DEBTOR TEXT NOT NULL,
                $COL_DEBT_AMOUNT REAL NOT NULL
            )
        """.trimIndent()

        db.execSQL(createGroupsTable)
        db.execSQL(createExpensesTable)
        db.execSQL(createParticipantsTable)
        db.execSQL(createDebtsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GROUPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PARTICIPANTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DEBTS")
        onCreate(db)
    }

    // Obtener suma total de deudas de una persona
    fun getTotalDebtForPerson(name: String): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($COL_DEBT_AMOUNT) FROM $TABLE_DEBTS WHERE $COL_DEBT_DEBTOR = ?",
            arrayOf(name)
        )
        var totalDebt = 0.0
        if (cursor.moveToFirst()) {
            totalDebt = cursor.getDouble(0)
        }
        cursor.close()
        return totalDebt
    }

    // Obtener suma total que le deben a una persona
    fun getTotalCreditForPerson(name: String): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($COL_DEBT_AMOUNT) FROM $TABLE_DEBTS WHERE $COL_DEBT_CREDITOR = ?",
            arrayOf(name)
        )
        var totalCredit = 0.0
        if (cursor.moveToFirst()) {
            totalCredit = cursor.getDouble(0)
        }
        cursor.close()
        return totalCredit
    }

    // Insertar grupo
    fun insertGroup(name: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_GROUP_NAME, name)

        return db.insert(TABLE_GROUPS, null, values)
    }

    // Obtener todos los grupos
    fun getAllGroups(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_GROUPS", null)
    }

    // Obtener todos los participantes
    fun getAllParticipants(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_PARTICIPANTS", null)
    }

    // Insertar gasto
    fun insertExpense(name: String, amount: Double, date: String, payer: String, groupId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_EXPENSE_NAME, name)
        values.put(COL_EXPENSE_AMOUNT, amount)
        values.put(COL_EXPENSE_DATE, date)
        values.put(COL_EXPENSE_PAYER, payer)
        values.put(COL_EXPENSE_GROUP_ID, groupId)

        return db.insert(TABLE_EXPENSES, null, values)
    }

    // Obtener todos los gastos
    fun getAllExpenses(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_EXPENSES", null)
    }

    // Insertar participante
    fun insertParticipant(name: String, expenseId: Int, groupId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_PARTICIPANT_NAME, name)
        values.put(COL_PARTICIPANT_EXPENSE_ID, expenseId)
        values.put(COL_PARTICIPANT_GROUP_ID, groupId)

        return db.insert(TABLE_PARTICIPANTS, null, values)
    }

    // Obtener participantes por grupo
    fun getParticipantsByGroup(groupId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_PARTICIPANTS WHERE $COL_PARTICIPANT_GROUP_ID = ?",
            arrayOf(groupId.toString())
        )
    }

    // Eliminar gasto
    fun deleteExpense(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_EXPENSES, "$COL_EXPENSE_ID = ?", arrayOf(id.toString()))
    }

    // Eliminar grupo
    fun deleteGroup(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_GROUPS, "$COL_GROUP_ID = ?", arrayOf(id.toString()))
    }

    //Obtener grupo por ID
    fun getGroupById(id: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_GROUPS WHERE $COL_GROUP_ID = ?",
            arrayOf(id.toString())
        )
    }

    //Actualizar grupo
    fun updateGroup(id: Int, nuevoNombre: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_GROUP_NAME, nuevoNombre)
        return db.update(TABLE_GROUPS, values, "$COL_GROUP_ID = ?", arrayOf(id.toString()))
    }
}
