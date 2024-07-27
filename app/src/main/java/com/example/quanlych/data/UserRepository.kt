package com.example.quanlych.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Đăng ký người dùng mới
    fun registerUser(name: String, email: String, password: String) {
        val db = dbHelper.writableDatabase

        // Insert into TaiKhoan
        val values = ContentValues().apply {
            put("TenNguoiDung", name)
            put("Email", email)
            put("MatKhau", password)
        }

        val newRowId = db.insert("TaiKhoan", null, values)

        // Gán quyền "người dùng"
        val query = "SELECT MaQuyen FROM Quyen WHERE TenQuyen = 'người dùng'"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val maQuyen = cursor.getLong(0)
            val detailValues = ContentValues().apply {
                put("MaTaiKhoan", newRowId)
                put("MaQuyen", maQuyen)
            }
            db.insert("ChiTietTaiKhoan", null, detailValues)
        }
        cursor.close()
    }
    object UserSession {
        var email: String? = null
    }
    // Đăng nhập người dùng
    fun loginUser(email: String, password: String): Pair<Boolean, String?> {
        val db = dbHelper.readableDatabase

        // Check login credentials
        val query = "SELECT MaTaiKhoan, TenNguoiDung FROM TaiKhoan WHERE Email = ? AND MatKhau = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        return if (cursor.moveToFirst()) {
            val maTaiKhoan = cursor.getLong(0)
            val tenNguoiDung = cursor.getString(1)

            // Store the email in the session
            UserSession.email = email

            // Check user role
            val roleQuery = "SELECT TenQuyen FROM ChiTietTaiKhoan ct JOIN Quyen q ON ct.MaQuyen = q.MaQuyen WHERE ct.MaTaiKhoan = ?"
            val roleCursor = db.rawQuery(roleQuery, arrayOf(maTaiKhoan.toString()))

            if (roleCursor.moveToFirst()) {
                val tenQuyen = roleCursor.getString(0)
                roleCursor.close()
                Pair(true, tenQuyen)
            } else {
                Pair(false, null)
            }
        } else {
            Pair(false, null)
        }
    }

    // Fetch all users from the database
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM TaiKhoan", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("MaTaiKhoan"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("TenNguoiDung"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("Email"))
                val password = cursor.getString(cursor.getColumnIndexOrThrow("MatKhau"))
                users.add(User(id, name, email, password))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }
    // Search for users by name or email
    fun searchUsers(query: String): List<User> {
        val userList = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM TaiKhoan WHERE TenNguoiDung LIKE ? OR Email LIKE ?",
            arrayOf("%$query%", "%$query%")
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("MaTaiKhoan"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("TenNguoiDung"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("Email"))
                val password = cursor.getString(cursor.getColumnIndexOrThrow("MatKhau"))
                userList.add(User(id, name, email, password))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    // Get users by role ID
    fun getUsersByRole(roleId: Long): List<User> {
        val users = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT t.* 
            FROM TaiKhoan t 
            JOIN ChiTietTaiKhoan ctt ON t.MaTaiKhoan = ctt.MaTaiKhoan 
            WHERE ctt.MaQuyen = ?
        """
        val cursor: Cursor = db.rawQuery(query, arrayOf(roleId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("MaTaiKhoan"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("TenNguoiDung"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("Email"))
                val password = cursor.getString(cursor.getColumnIndexOrThrow("MatKhau"))
                users.add(User(id, name, email, password))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }

    // Get roles for a user
    fun getUserRoles(userId: Long): List<Long> {
        val roles = mutableListOf<Long>()
        val db = dbHelper.readableDatabase
        val query = "SELECT MaQuyen FROM ChiTietTaiKhoan WHERE MaTaiKhoan = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val roleId = cursor.getLong(cursor.getColumnIndexOrThrow("MaQuyen"))
                roles.add(roleId)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return roles
    }



}
data class User(val id: Long, val name: String, val email: String, val password: String)