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
        private const val DATABASE_VERSION = 6

        // Tablas
        const val TABLE_GROUPS = "groups"
        const val TABLE_EXPENSES = "expenses"
        const val TABLE_USERS = "users"
        const val TABLE_DEBTS = "debts"
        const val TABLE_USER_GROUPS = "user_groups"
        const val TABLE_CONTACTS = "contacts"

        // Columnas - Contacts
        const val COL_CONTACT_ID = "id"
        const val COL_CONTACT_USER_ID = "user_id"
        const val COL_CONTACT_EMAIL = "email"

        // Columnas - Groups
        const val COL_GROUP_ID = "id"
        const val COL_GROUP_NAME = "name"
        const val COL_GROUP_CREATOR_ID = "creator_id"

        // Columnas - Expenses
        const val COL_EXPENSE_ID = "id"
        const val COL_EXPENSE_NAME = "name"
        const val COL_EXPENSE_AMOUNT = "amount"
        const val COL_EXPENSE_DATE = "date"
        const val COL_EXPENSE_PAYER = "payer"
        const val COL_EXPENSE_GROUP_ID = "group_id"

        // Columnas - Users
        const val COL_USER_ID = "id"
        const val COL_USER_NAME = "name"
        const val COL_USER_EMAIL = "email"

        // Columnas - Debts
        const val COL_DEBT_ID = "id"
        const val COL_DEBT_CREDITOR = "creditor"
        const val COL_DEBT_DEBTOR = "debtor"
        const val COL_DEBT_AMOUNT = "amount"
        const val COL_DEBT_GROUP_ID = "group_id"

        // Columnas - User Groups
        const val COL_USER_GROUP_USER_ID = "user_id"
        const val COL_USER_GROUP_GROUP_ID = "group_id"
        const val COL_USER_GROUP_DEBT = "debt"
    }



    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla de contactos
        val createContactsTable = """
            CREATE TABLE $TABLE_CONTACTS (
                $COL_CONTACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CONTACT_USER_ID INTEGER NOT NULL,
                $COL_CONTACT_EMAIL TEXT NOT NULL,
                FOREIGN KEY($COL_CONTACT_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
            )
        """.trimIndent()

        // Crear tabla de grupos
        val createGroupsTable = """
            CREATE TABLE $TABLE_GROUPS (
                $COL_GROUP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_GROUP_NAME TEXT NOT NULL,
                $COL_GROUP_CREATOR_ID INTEGER NOT NULL
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

        // Crear tabla de user
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_NAME TEXT NOT NULL,
                $COL_USER_EMAIL TEXT NOT NULL
            )
        """.trimIndent()

        // Crear tabla de deudas
        val createDebtsTable = """
            CREATE TABLE $TABLE_DEBTS (
                $COL_DEBT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DEBT_CREDITOR TEXT NOT NULL,
                $COL_DEBT_DEBTOR TEXT NOT NULL,
                $COL_DEBT_AMOUNT REAL NOT NULL,
                $COL_DEBT_GROUP_ID INTEGER
            )
        """.trimIndent()

        val createUserGroupsTable = """
            CREATE TABLE $TABLE_USER_GROUPS (
                $COL_USER_GROUP_USER_ID INTEGER,
                $COL_USER_GROUP_GROUP_ID INTEGER,
                $COL_USER_GROUP_DEBT REAL DEFAULT 0,
                PRIMARY KEY ($COL_USER_GROUP_USER_ID, $COL_USER_GROUP_GROUP_ID),
                FOREIGN KEY($COL_USER_GROUP_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
                FOREIGN KEY($COL_USER_GROUP_GROUP_ID) REFERENCES $TABLE_GROUPS($COL_GROUP_ID)
            )
        """.trimIndent()

        db.execSQL(createContactsTable)
        db.execSQL(createGroupsTable)
        db.execSQL(createExpensesTable)
        db.execSQL(createUsersTable)
        db.execSQL(createDebtsTable)
        db.execSQL(createUserGroupsTable)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Borramos las viejas tablas
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GROUPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DEBTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_GROUPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")

        // Recreamos las tables
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
    fun insertGroup(name: String, creatorId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_GROUP_NAME, name)
        values.put(COL_GROUP_CREATOR_ID, creatorId)

        return db.insert(TABLE_GROUPS, null, values)
    }

    // Obtener todos los grupos
    fun getAllGroups(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_GROUPS", null)
    }

    // Obtener todos los
    fun getAllUsers(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_USERS", null)
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

    // Insertar user
    fun insertUser(name: String, email: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_USER_NAME, name)
        values.put(COL_USER_EMAIL, email)
        return db.insert(TABLE_USERS, null, values)
    }

    //Insertar deuda
    fun insertDebt(creditor: String, debtor: String, amount: Double, group_id: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_DEBT_CREDITOR, creditor)
        values.put(COL_DEBT_DEBTOR, debtor)
        values.put(COL_DEBT_AMOUNT, amount)
        values.put(COL_DEBT_GROUP_ID, group_id)

        return db.insert(TABLE_DEBTS, null, values)
    }

    // Obtener user por grupo
    fun getUsersByGroup(groupId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT u.* FROM $TABLE_USERS u INNER JOIN $TABLE_USER_GROUPS ug ON u.$COL_USER_ID = ug.$COL_USER_GROUP_USER_ID WHERE ug.$COL_USER_GROUP_GROUP_ID = ?",
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

    //Deudas por acreedor
    fun getDebtsByCreditor(creditor: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_DEBTS WHERE $COL_DEBT_CREDITOR = ?",
            arrayOf(creditor)
        )
    }

    //Deudas por deudor
    fun getDebtsByDebtor(debtor: String): Cursor {
        val db = this.readableDatabase

        return db.rawQuery(
            "SELECT * FROM $TABLE_DEBTS WHERE $COL_DEBT_DEBTOR = ?",
            arrayOf(debtor)
        )
    }

    //Actualizar deuda
    fun updateDebt(id: Int, nuevoCredor: String, nuevoDeudor: String, nuevoMonto: Double): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_DEBT_CREDITOR, nuevoCredor)
        values.put(COL_DEBT_DEBTOR, nuevoDeudor)

        return db.update(TABLE_DEBTS, values, "$COL_DEBT_ID = ?", arrayOf(id.toString()))
    }

    //Eliminar deuda
    fun deleteDebt(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_DEBTS, "$COL_DEBT_ID = ?", arrayOf(id.toString()))
    }

    //Obtener deuda por ID
    fun getDebtById(id: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_DEBTS WHERE $COL_DEBT_ID = ?",
            arrayOf(id.toString()))
    }

    //Obtener total de deudas por miembros de un grupo
    fun getTotalDebtForGroup(groupId: Int): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(d.$COL_DEBT_AMOUNT) AS totalDebt FROM $TABLE_DEBTS d INNER JOIN $TABLE_USER_GROUPS ug ON d.$COL_DEBT_DEBTOR = ug.$COL_USER_GROUP_USER_ID WHERE ug.$COL_USER_GROUP_GROUP_ID = ?",
            arrayOf(groupId.toString())
        )
        var totalDebt = 0.0
        if (cursor.moveToFirst()) {
            totalDebt = cursor.getDouble(cursor.getColumnIndexOrThrow("totalDebt"))
        }
        cursor.close()
        return totalDebt
    }

    fun getExpensesByGroup(groupId: Int): Cursor{
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_EXPENSES WHERE $COL_EXPENSE_GROUP_ID = ?",
            arrayOf(groupId.toString())
        )
    }

    fun getExpenseById(id: Int): Cursor{
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_EXPENSES WHERE $COL_EXPENSE_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun getUserByEmail(email: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COL_USER_EMAIL = ?",
            arrayOf(email)
        )
    }

    fun getUserById(id: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COL_USER_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun updateUser(id: Int, nuevoNombre: String, nuevoEmail: String, nuevaPassword: String): Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_USER_NAME, nuevoNombre)
        values.put(COL_USER_EMAIL, nuevoEmail)
        return db.update(TABLE_USERS, values, "$COL_USER_ID = ?", arrayOf(id.toString()))
    }

    fun deleteUser(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_USERS, "$COL_USER_ID = ?", arrayOf(id.toString()))
    }

    fun addUserToGroup(userId: Int, groupId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_USER_GROUP_USER_ID, userId)
        values.put(COL_USER_GROUP_GROUP_ID, groupId)

        return db.insert(TABLE_USER_GROUPS, null, values)
    }

    fun removeUserFromGroup(userId: Int, groupId: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_USER_GROUPS, "$COL_USER_GROUP_USER_ID = ? AND $COL_USER_GROUP_GROUP_ID = ?", arrayOf(userId.toString(), groupId.toString()))
    }

    fun obtenerGruposDeUsuario(usuarioId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT g.id, g.name, g.creator_id FROM ${DatabaseHelper.TABLE_GROUPS} g " +
                    "INNER JOIN ${DatabaseHelper.TABLE_USER_GROUPS} ug ON g.id = ug.group_id " +
                    "WHERE ug.user_id = ?",
            arrayOf(usuarioId.toString())
        )
    }

    fun obtenerDeudaPorUsuarioYGrupo(userId: Int, groupId: Int): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_USER_GROUP_DEBT FROM ${DatabaseHelper.TABLE_USER_GROUPS} WHERE user_id = ? AND group_id = ?",
            arrayOf(userId.toString(), groupId.toString())
        )
        return if (cursor.moveToFirst()) {
            cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_GROUP_DEBT))
        } else {
            0.0
        }.also {
            cursor.close()
        }
    }

    fun increaseDebt(userId: Int, groupId: Int, amount: Double): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_USER_GROUP_DEBT, amount)

        return db.update(TABLE_USER_GROUPS, values, "$COL_USER_GROUP_USER_ID = ? AND $COL_USER_GROUP_GROUP_ID = ?", arrayOf(userId.toString(), groupId.toString()))
    }

    fun getNombreUsuarioPorId(userId: Int): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT name FROM $TABLE_USERS WHERE id = ?",
            arrayOf(userId.toString())
        )
        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow("name"))
        } else {
            ""
        }.also {
            cursor.close()
        }
    }

    fun getIdUsuarioPorNombre(nombre: String): Int? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM $TABLE_USERS WHERE name = ?",
            arrayOf(nombre)
        )
        return if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        } else {
            null
        }.also {
        }
    }

    fun getDebtsByGroup(groupId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT payer, amount, participants FROM expenses WHERE group_id = ?", arrayOf(groupId.toString()))
    }

    fun getDebtsByUser(userId: Int): Cursor {
        val db = this.readableDatabase
        val userName = getNombreUsuarioPorId(userId)
        return db.rawQuery("SELECT g.name as group_name, SUM(d.amount) as amount FROM debts d JOIN groups g ON d.group_id = g.id WHERE d.debtor = ? GROUP BY g.name", arrayOf(userName))
    }

    fun insertContact(userId: Int, email: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_CONTACT_USER_ID, userId)
            put(COL_CONTACT_EMAIL, email)
        }
        return db.insert(TABLE_CONTACTS, null, values)
    }

    fun deleteContact(userId: Int, email: String): Int {
        val db = writableDatabase
        return db.delete(TABLE_CONTACTS, "$COL_CONTACT_USER_ID = ? AND $COL_CONTACT_EMAIL = ?", arrayOf(userId.toString(), email))
    }
    fun getUsersWithPositiveDebt(groupId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_USER_GROUPS WHERE $COL_USER_GROUP_DEBT > 0 AND $COL_USER_GROUP_GROUP_ID = ? ORDER BY $COL_USER_GROUP_DEBT ASC", arrayOf(groupId.toString()))
    }

    fun getUsersWithNegativeDebt(groupId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_USER_GROUPS WHERE $COL_USER_GROUP_DEBT < 0 AND $COL_USER_GROUP_GROUP_ID = ? ORDER BY $COL_USER_GROUP_DEBT ASC", arrayOf(groupId.toString()))
    }


}

