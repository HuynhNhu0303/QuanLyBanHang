package com.example.quanlych.data


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.quanlych.model.Account
import com.example.quanlych.model.Category
import com.example.quanlych.model.Order
import com.example.quanlych.model.OrderDetail
import com.example.quanlych.model.Product
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "quanlych.db"
        private const val DATABASE_VERSION = 3 // Incremented version to reflect schema changes


        // Table names
        private const val TABLE_SANPHAM = "SanPham"
        private const val TABLE_TAIKHOAN = "TaiKhoan"
        private const val TABLE_QUYEN = "Quyen"
        private const val TABLE_CHITIETTAIKHOAN = "ChiTietTaiKhoan"
        private const val TABLE_LOAISANPHAM = "LoaiSanPham"
        private const val TABLE_HOADON = "HoaDon"
        private const val TABLE_CHITIETHOADON = "ChiTietHoaDon"
        private const val TABLE_HINHTHUCTHANHTOAN = "HinhThucThanhToan"
        // Create table statements
        private const val CREATE_TABLE_LOAISANPHAM = """
        CREATE TABLE $TABLE_LOAISANPHAM (
            MaLoaiSanPham INTEGER PRIMARY KEY AUTOINCREMENT, 
            TenLoaiSanPham TEXT NOT NULL
        )
    """

        private const val CREATE_TABLE_SANPHAM = """
            CREATE TABLE $TABLE_SANPHAM (
                MaSanPham INTEGER PRIMARY KEY AUTOINCREMENT, 
                TenSanPham TEXT, 
                HinhAnh TEXT,  -- Ensure this column can store image paths or URLs
                MoTa TEXT, 
                Gia REAL, 
                TrangThai INTEGER, 
                SoLuong INTEGER,
                MaLoaiSanPham INTEGER,
                FOREIGN KEY (MaLoaiSanPham) REFERENCES $TABLE_LOAISANPHAM(MaLoaiSanPham)
            )
        """


        private const val CREATE_TABLE_TAIKHOAN = """
            CREATE TABLE $TABLE_TAIKHOAN (
                MaTaiKhoan INTEGER PRIMARY KEY AUTOINCREMENT, 
                TenNguoiDung TEXT, 
                Email TEXT UNIQUE, 
                MatKhau TEXT
            )
        """

        private const val CREATE_TABLE_QUYEN = """
            CREATE TABLE $TABLE_QUYEN (
                MaQuyen INTEGER PRIMARY KEY AUTOINCREMENT, 
                TenQuyen TEXT NOT NULL
            )
        """

        private const val CREATE_TABLE_CHITIETTAIKHOAN = """
            CREATE TABLE $TABLE_CHITIETTAIKHOAN (
                MaTaiKhoan INTEGER, 
                MaQuyen INTEGER, 
                PRIMARY KEY (MaTaiKhoan, MaQuyen), 
                FOREIGN KEY(MaTaiKhoan) REFERENCES $TABLE_TAIKHOAN(MaTaiKhoan), 
                FOREIGN KEY(MaQuyen) REFERENCES $TABLE_QUYEN(MaQuyen)
            )
        """
        private const val CREATE_TABLE_HOADON = """
        CREATE TABLE $TABLE_HOADON (
            MaHoaDon INTEGER PRIMARY KEY AUTOINCREMENT,
            MaTaiKhoan INTEGER,
            NgayLap TEXT,
            TongTien REAL,
            TrangThai INTEGER,
            DiaChi TEXT,
            Sdt TEXT,
            MaHinhThuc INTEGER,
            FOREIGN KEY (MaTaiKhoan) REFERENCES $TABLE_TAIKHOAN(MaTaiKhoan),
            FOREIGN KEY (MaHinhThuc) REFERENCES $TABLE_HINHTHUCTHANHTOAN(MaHinhThuc)
        )
        """

        private const val CREATE_TABLE_CHITIETHOADON = """
        CREATE TABLE $TABLE_CHITIETHOADON (
            MaChiTiet INTEGER PRIMARY KEY AUTOINCREMENT,
            MaHoaDon INTEGER,
            MaSanPham INTEGER,
            SoLuong INTEGER,
            Gia REAL,
            FOREIGN KEY (MaHoaDon) REFERENCES $TABLE_HOADON(MaHoaDon),
            FOREIGN KEY (MaSanPham) REFERENCES $TABLE_SANPHAM(MaSanPham)
        )
        """

        private const val CREATE_TABLE_HINHTHUCTHANHTOAN = """
        CREATE TABLE $TABLE_HINHTHUCTHANHTOAN (
            MaHinhThuc INTEGER PRIMARY KEY AUTOINCREMENT,
            TenHinhThuc TEXT NOT NULL
        )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_SANPHAM)
        db.execSQL(CREATE_TABLE_TAIKHOAN)
        db.execSQL(CREATE_TABLE_QUYEN)
        db.execSQL(CREATE_TABLE_CHITIETTAIKHOAN)
        db.execSQL(CREATE_TABLE_LOAISANPHAM)
        db.execSQL(CREATE_TABLE_HOADON)
        db.execSQL(CREATE_TABLE_CHITIETHOADON)
        db.execSQL(CREATE_TABLE_HINHTHUCTHANHTOAN)
        // Insert default roles
        db.execSQL("INSERT INTO $TABLE_QUYEN (TenQuyen) VALUES ('quản lý')")
        db.execSQL("INSERT INTO $TABLE_QUYEN (TenQuyen) VALUES ('người dùng')")

        // Insert default product categories related to cosmetics
        db.execSQL("INSERT INTO $TABLE_LOAISANPHAM (TenLoaiSanPham) VALUES ('Chăm sóc da')")
        db.execSQL("INSERT INTO $TABLE_LOAISANPHAM (TenLoaiSanPham) VALUES ('Trang điểm')")
        db.execSQL("INSERT INTO $TABLE_LOAISANPHAM (TenLoaiSanPham) VALUES ('Chăm sóc tóc')")
        // Insert payment methods
        db.execSQL("INSERT INTO $TABLE_HINHTHUCTHANHTOAN (TenHinhThuc) VALUES ('COD')")
        db.execSQL("INSERT INTO $TABLE_HINHTHUCTHANHTOAN (TenHinhThuc) VALUES ('MoMo')")
        // Insert admin account
        val adminValues = ContentValues().apply {
            put("TenNguoiDung", "admin1")
            put("Email", "admin2@gmail.com")
            put("MatKhau","123") // Hash the password
        }
        val adminId = db.insert(TABLE_TAIKHOAN, null, adminValues)

        // Insert admin role
        val adminRoleValues = ContentValues().apply {
            put("MaTaiKhoan", adminId)
            put("MaQuyen", 1) // Assuming '1' is the 'quản lý' role ID
        }
        db.insert(TABLE_CHITIETTAIKHOAN, null, adminRoleValues)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HOADON")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHITIETHOADON")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HINHTHUCTHANHTOAN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SANPHAM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAIKHOAN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUYEN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHITIETTAIKHOAN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOAISANPHAM")

        // Create new tables
        onCreate(db)
    }
    fun getProductsByQuantitySold(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = this.readableDatabase
        val query = """
        SELECT p.MaSanPham, p.TenSanPham, p.HinhAnh, p.MoTa, p.Gia, p.TrangThai, p.MaLoaiSanPham, 
               SUM(c.SoLuong) AS TotalQuantitySold
        FROM $TABLE_SANPHAM p
        JOIN $TABLE_CHITIETHOADON c ON p.MaSanPham = c.MaSanPham
        GROUP BY p.MaSanPham
        ORDER BY TotalQuantitySold DESC
    """
        val cursor = db.rawQuery(query, null)

        // Check if cursor has the expected columns
        val maSanPhamIndex = cursor.getColumnIndex("MaSanPham")
        val tenSanPhamIndex = cursor.getColumnIndex("TenSanPham")
        val hinhAnhIndex = cursor.getColumnIndex("HinhAnh")
        val moTaIndex = cursor.getColumnIndex("MoTa")
        val giaIndex = cursor.getColumnIndex("Gia")
        val totalQuantitySoldIndex = cursor.getColumnIndex("TotalQuantitySold")
        val maLoaiSanPhamIndex = cursor.getColumnIndex("MaLoaiSanPham")

        if (maSanPhamIndex != -1 && tenSanPhamIndex != -1 && hinhAnhIndex != -1 && moTaIndex != -1 &&
            giaIndex != -1 && totalQuantitySoldIndex != -1 && maLoaiSanPhamIndex != -1) {

            if (cursor.moveToFirst()) {
                do {
                    val product = Product(
                        id = cursor.getInt(maSanPhamIndex),
                        name = cursor.getString(tenSanPhamIndex),
                        description = cursor.getString(moTaIndex),
                        imageResource = cursor.getString(hinhAnhIndex),
                        price = cursor.getDouble(giaIndex),
                        quantity = cursor.getInt(totalQuantitySoldIndex), // Use TotalQuantitySold
                        isSelected = false, // Default value
                        categoryId = cursor.getInt(maLoaiSanPhamIndex)
                    )
                    productList.add(product)
                } while (cursor.moveToNext())
            }
        } else {
            Log.e("DatabaseHelper", "One or more column indices are invalid.")
        }

        cursor.close()
        db.close()
        return productList
    }

    fun updateProductQuantity(productId: Int, quantityToDeduct: Int) {
        val db = this.writableDatabase
        val query = "UPDATE SANPHAM SET SoLuong = SoLuong - ? WHERE MaSanPham = ?"
        val statement = db.compileStatement(query)
        statement.bindLong(1, quantityToDeduct.toLong())
        statement.bindLong(2, productId.toLong())
        statement.executeUpdateDelete()
        db.close()
    }

    fun searchOrders(query: String): List<Order> {
        val db = readableDatabase
        val orders = mutableListOf<Order>()
        val orderDetailsMap = mutableMapOf<Int, MutableList<OrderDetail>>()

        // Query to get orders
        val orderQuery = """
        SELECT DISTINCT h.MaHoaDon, h.MaTaiKhoan, h.NgayLap, h.TongTien, h.TrangThai, h.DiaChi, h.Sdt, h.MaHinhThuc
        FROM $TABLE_HOADON h
        LEFT JOIN $TABLE_CHITIETHOADON c ON h.MaHoaDon = c.MaHoaDon
        LEFT JOIN $TABLE_SANPHAM s ON c.MaSanPham = s.MaSanPham
        LEFT JOIN $TABLE_TAIKHOAN t ON h.MaTaiKhoan = t.MaTaiKhoan
        WHERE h.MaHoaDon LIKE ? 
        OR t.TenNguoiDung LIKE ? 
        OR s.TenSanPham LIKE ?
    """
        val cursor = db.rawQuery(orderQuery, arrayOf("%$query%", "%$query%", "%$query%"))

        val idIndex = cursor.getColumnIndex("MaHoaDon")
        val userIdIndex = cursor.getColumnIndex("MaTaiKhoan")
        val dateIndex = cursor.getColumnIndex("NgayLap")
        val totalIndex = cursor.getColumnIndex("TongTien")
        val statusIndex = cursor.getColumnIndex("TrangThai")
        val addressIndex = cursor.getColumnIndex("DiaChi")
        val phoneIndex = cursor.getColumnIndex("Sdt")
        val paymentMethodIndex = cursor.getColumnIndex("MaHinhThuc")

        if (cursor.moveToFirst()) {
            do {
                val orderId = cursor.getInt(idIndex)
                val order = Order(
                    id = orderId,
                    userId = cursor.getInt(userIdIndex),
                    date = cursor.getString(dateIndex) ?: "",
                    total = cursor.getDouble(totalIndex),
                    status = cursor.getInt(statusIndex),
                    address = cursor.getString(addressIndex) ?: "",
                    phone = cursor.getString(phoneIndex) ?: "",
                    paymentMethod = cursor.getInt(paymentMethodIndex)
                )
                orders.add(order)
                orderDetailsMap[orderId] = mutableListOf()  // Initialize list for this order
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Query to get order details
        val detailQuery = """
        SELECT c.MaHoaDon, s.MaSanPham, s.TenSanPham, c.SoLuong, c.Gia, s.HinhAnh
        FROM $TABLE_CHITIETHOADON c
        LEFT JOIN $TABLE_SANPHAM s ON c.MaSanPham = s.MaSanPham
        WHERE c.MaHoaDon IN (${orders.joinToString(",") { it.id.toString() }})
    """
        val detailCursor = db.rawQuery(detailQuery, null)

        val orderIdIndex = detailCursor.getColumnIndex("MaHoaDon")
        val productIdIndex = detailCursor.getColumnIndex("MaSanPham")
        val productNameIndex = detailCursor.getColumnIndex("TenSanPham")
        val quantityIndex = detailCursor.getColumnIndex("SoLuong")
        val priceIndex = detailCursor.getColumnIndex("Gia")
        val imageIndex = detailCursor.getColumnIndex("HinhAnh")

        if (detailCursor.moveToFirst()) {
            do {
                val orderId = detailCursor.getInt(orderIdIndex)
                val orderDetail = OrderDetail(
                    productId = detailCursor.getInt(productIdIndex),
                    productName = detailCursor.getString(productNameIndex) ?: "",
                    quantity = detailCursor.getInt(quantityIndex),
                    price = detailCursor.getDouble(priceIndex),
                    productImage = detailCursor.getString(imageIndex) ?: ""
                )
                orderDetailsMap[orderId]?.add(orderDetail)
            } while (detailCursor.moveToNext())
        }
        detailCursor.close()

        // Update orders with their details
        orders.forEach { order ->
            order.orderDetails = orderDetailsMap[order.id] ?: emptyList()
        }

        return orders
    }
    fun getProductsByCategory(categoryId: Int): List<Product> {
        val productList = mutableListOf<Product>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_SANPHAM WHERE MaLoaiSanPham = ?"
        val cursor = db.rawQuery(query, arrayOf(categoryId.toString()))

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("MaSanPham"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("TenSanPham"))
                    val image = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
                    val price = cursor.getDouble(cursor.getColumnIndexOrThrow("Gia"))
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai")) != 0 // Convert Int to Boolean
                    val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))
                    val categoryIdFromDb = cursor.getInt(cursor.getColumnIndexOrThrow("MaLoaiSanPham"))

                    val product = Product(id, name, description, image, price, quantity, status, categoryIdFromDb)
                    productList.add(product)
                } while (cursor.moveToNext())
            }
        }
        db.close()

        return productList
    }
    fun searchProducts(query: String): List<Product> {
        val productList = mutableListOf<Product>()
        val db = this.readableDatabase
        val searchQuery = """
        SELECT * FROM $TABLE_SANPHAM
        WHERE TenSanPham LIKE ? 
        OR Gia LIKE ? 
        OR MaLoaiSanPham IN (
            SELECT MaLoaiSanPham FROM $TABLE_LOAISANPHAM WHERE TenLoaiSanPham LIKE ?
        )
    """
        val cursor = db.rawQuery(
            searchQuery,
            arrayOf("%$query%", "%$query%", "%$query%")
        )

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("MaSanPham"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("TenSanPham"))
                    val image = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
                    val price = cursor.getDouble(cursor.getColumnIndexOrThrow("Gia"))
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai")) != 0
                    val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))
                    val categoryIdFromDb = cursor.getInt(cursor.getColumnIndexOrThrow("MaLoaiSanPham"))

                    val product = Product(id, name, description, image, price, quantity, status, categoryIdFromDb)
                    productList.add(product)
                } while (cursor.moveToNext())
            }
        }
        db.close()

        return productList
    }
    fun getAllCategories(): List<Category> {
        val categoryList = mutableListOf<Category>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_LOAISANPHAM"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("MaLoaiSanPham"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("TenLoaiSanPham"))
                    categoryList.add(Category(id, name))
                } while (cursor.moveToNext())
            }
        }
        db.close()

        return categoryList
    }
    fun getAccountCount(date: String?): Map<Int, Int> {
        val db = this.readableDatabase
        val counts = mutableMapOf<Int, Int>()

        // Query to get count of distinct MaTaiKhoan values
        val query = when {
            date.isNullOrEmpty() -> {
                // Count distinct MaTaiKhoan for all records
                "SELECT COUNT(DISTINCT MaTaiKhoan) AS UniqueAccountCount FROM $TABLE_HOADON"
            }
            date.contains("/") -> {
                val parts = date.split("/")
                when (parts.size) {
                    3 -> { // Format: day/month/year
                        val (day, month, year) = parts.map { it.padStart(2, '0') }
                        "SELECT COUNT(DISTINCT MaTaiKhoan) AS UniqueAccountCount FROM $TABLE_HOADON " +
                                "WHERE strftime('%d', NgayLap) = '$day' " +
                                "AND strftime('%m', NgayLap) = '$month' " +
                                "AND strftime('%Y', NgayLap) = '$year'"
                    }
                    2 -> { // Format: month/year (MM/YYYY)
                        val (month, year) = parts
                        val formattedMonth = month.padStart(2, '0')
                        "SELECT COUNT(DISTINCT MaTaiKhoan) AS UniqueAccountCount FROM $TABLE_HOADON " +
                                "WHERE strftime('%m', NgayLap) = '$formattedMonth' " +
                                "AND strftime('%Y', NgayLap) = '$year'"
                    }
                    else -> {
                        "SELECT COUNT(DISTINCT MaTaiKhoan) AS UniqueAccountCount FROM $TABLE_HOADON"
                    }
                }
            }
            date.length == 4 -> { // Format: year (YYYY)
                "SELECT COUNT(DISTINCT MaTaiKhoan) AS UniqueAccountCount FROM $TABLE_HOADON " +
                        "WHERE strftime('%Y', NgayLap) = '$date'"
            }
            else -> {
                "SELECT COUNT(DISTINCT MaTaiKhoan) AS UniqueAccountCount FROM $TABLE_HOADON"
            }
        }

        val cursor = db.rawQuery(query, null)
        cursor.use {
            val accountCountIndex = it.getColumnIndex("UniqueAccountCount")

            if (accountCountIndex == -1) {
                Log.e("DatabaseHelper", "Column index error: UniqueAccountCount column not found.")
                return emptyMap()
            }

            if (it.moveToFirst()) {
                val count = it.getInt(accountCountIndex)
                counts[0] = count
            }
        }

        db.close()
        return counts
    }
    // Method to fetch all products
    fun getAllProducts(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SANPHAM", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("MaSanPham"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("TenSanPham"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
                val imageResource = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("Gia"))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))
                val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("MaLoaiSanPham"))

                val product = Product(id, name, description, imageResource, price, quantity, categoryId = categoryId)
                productList.add(product)

                Log.d("DatabaseHelper", "Product - ID: $id, Name: $name, Image URL: $imageResource")
            } while (cursor.moveToNext())
        } else {
            Log.d("DatabaseHelper", "No products found.")
        }
        cursor.close()
        return productList
    }
    // Method to count records in a table
    fun getCount(tableName: String): Int {
        val db = readableDatabase
        val countQuery = "SELECT COUNT(*) FROM $tableName"
        val cursor = db.rawQuery(countQuery, null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }
    fun addProduct(product: Product) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("TenSanPham", product.name)
            put("HinhAnh", product.imageResource)
            put("MoTa", product.description)
            put("Gia", product.price)
            put("TrangThai", if (product.isSelected) 1 else 0) // Assuming 'isSelected' is used for TrangThai
            put("SoLuong", product.quantity)
            put("MaLoaiSanPham", product.categoryId)
        }

        val result = db.insert(TABLE_SANPHAM, null, values)
        Log.d("DatabaseHelper", "Product insertion result: $result")
    }
    fun getProductById(productId: Int): Product? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_SANPHAM WHERE MaSanPham = ?",
            arrayOf(productId.toString())
        )

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("MaSanPham"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("TenSanPham"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
            val imageResource = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow("Gia"))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))
            val categoryId =
                cursor.getInt(cursor.getColumnIndexOrThrow("MaLoaiSanPham"))

            cursor.close()
            Product(
                id,
                name,
                description,
                imageResource,
                price,
                quantity,
                categoryId = categoryId
            )
        } else {
            cursor.close()
            null
        }
    }
    // Method to delete a product
    fun deleteProduct(productId: Int) {
        val db = writableDatabase
        db.delete(TABLE_SANPHAM, "MaSanPham = ?", arrayOf(productId.toString()))
        db.close()
    }
    // Method to update a product
    fun updateProduct(product: Product) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("TenSanPham", product.name)
            put("HinhAnh", product.imageResource)
            put("MoTa", product.description)
            put("Gia", product.price)
            put("TrangThai", if (product.isSelected) 1 else 0) // Check if this field is used correctly
            put("SoLuong", product.quantity)
            put("MaLoaiSanPham", product.categoryId)
        }

        val result = db.update(TABLE_SANPHAM, values, "MaSanPham = ?", arrayOf(product.id.toString()))
        Log.d("DatabaseHelper", "Product update result: $result")
    }
//
    fun getProductCountsByType(): Int {
        val db = readableDatabase
        var totalCount = 0

        // Query to count the total number of products
        val query = "SELECT COUNT(*) FROM $TABLE_SANPHAM"

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            totalCount = cursor.getInt(0) // Total count of products
        }

        cursor.close()
        db.close()
        return totalCount
    }
    fun getCountOfAccountsWithRole(roleId: Int): Int {
        val db = readableDatabase
        val query =
            "SELECT COUNT(*) FROM $TABLE_TAIKHOAN WHERE MaTaiKhoan IN (SELECT MaTaiKhoan FROM $TABLE_CHITIETTAIKHOAN WHERE MaQuyen = ?)"
        val cursor = db.rawQuery(query, arrayOf(roleId.toString()))
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }
    fun getAllProductCategories(): List<Category> {
        val categoryList = mutableListOf<Category>()
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_LOAISANPHAM", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("MaLoaiSanPham"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("TenLoaiSanPham"))
                    val category = Category(id, name)
                    categoryList.add(category)
                } while (cursor.moveToNext())
            } else {
                Log.d("DatabaseHelper", "No categories found in database.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("DatabaseHelper", "Error fetching product categories", e)
        } finally {
            cursor?.close()
            db.close()
        }

        // Log the number of categories retrieved
        Log.d("DatabaseHelper", "Categories retrieved: ${categoryList.size}")

        return categoryList
    }
    fun getUserIdByEmail(email: String): Int {
        val db = readableDatabase
        val query = "SELECT MaTaiKhoan FROM $TABLE_TAIKHOAN WHERE Email = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("MaTaiKhoan"))
        }
        cursor.close()
        return userId
    }
    fun addOrder(userId: Int, orderDate: String, totalPrice: Double, address: String, phoneNumber: String, paymentMethod: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("MaTaiKhoan", userId)
            put("NgayLap", orderDate)
            put("TongTien", totalPrice)
            put("DiaChi", address)
            put("Sdt", phoneNumber)
            put("MaHinhThuc", paymentMethod)
        }

        return db.insert(TABLE_HOADON, null, values)
    }
    fun addOrderDetail(orderId: Long, productId: Int, quantity: Int, price: Double) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("MaHoaDon", orderId)
            put("MaSanPham", productId)
            put("SoLuong", quantity)
            put("Gia", price)
        }

        db.insert(TABLE_CHITIETHOADON, null, values)
    }
    fun getOrdersByUserId(userId: Int): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = """
        SELECT o.MaHoaDon, o.NgayLap, o.TongTien, o.TrangThai, o.DiaChi, o.Sdt, o.MaHinhThuc 
        FROM $TABLE_HOADON o 
        WHERE o.MaTaiKhoan = ?
    """
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query, arrayOf(userId.toString()))

            if (cursor.moveToFirst()) {
                do {
                    Log.d("DatabaseHelper", "Cursor columns: ${cursor.columnNames.joinToString()}") // Log column names

                    val orderId = cursor.getInt(cursor.getColumnIndexOrThrow("MaHoaDon"))
                    val orderDetails = getOrderDetailsByOrderId(orderId)

                    val order = Order(
                        id = orderId,
                        userId = userId,
                        date = cursor.getString(cursor.getColumnIndexOrThrow("NgayLap")),
                        total = cursor.getDouble(cursor.getColumnIndexOrThrow("TongTien")),
                        status = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai")),
                        address = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi")),
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("Sdt")),
                        paymentMethod = cursor.getInt(cursor.getColumnIndexOrThrow("MaHinhThuc")),
                        orderDetails = orderDetails // Initialize with order details
                    )
                    orders.add(order)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting orders by user ID", e)
        } finally {
            cursor?.close()
        }

        return orders
    }
    fun getOrderDetailsByOrderId(orderId: Int): List<OrderDetail> {
        val orderDetails = mutableListOf<OrderDetail>()
        val db = readableDatabase
        val query = """
        SELECT c.MaSanPham, s.TenSanPham, c.SoLuong, c.Gia, s.HinhAnh 
        FROM $TABLE_CHITIETHOADON c 
        JOIN $TABLE_SANPHAM s ON c.MaSanPham = s.MaSanPham 
        WHERE c.MaHoaDon = ?
    """
        val cursor = db.rawQuery(query, arrayOf(orderId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val productId = cursor.getInt(cursor.getColumnIndexOrThrow("MaSanPham"))
                val productName = cursor.getString(cursor.getColumnIndexOrThrow("TenSanPham"))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("Gia"))
                val productImage = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh")) // Lấy hình ảnh sản phẩm

                val orderDetail = OrderDetail(
                    productId = productId,
                    productName = productName,
                    quantity = quantity,
                    price = price,
                    productImage = productImage // Gán hình ảnh sản phẩm
                )
                orderDetails.add(orderDetail)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return orderDetails
    }
    fun getOrdersByUserIdWithDetails(userId: Int): List<Order> {
        val orders = getOrdersByUserId(userId)
        for (order in orders) {
            order.orderDetails = getOrderDetailsByOrderId(order.id)
        }
        return orders
    }
// Thêm vào class DatabaseHelper
    fun getAllOrders(): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_HOADON"

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val orderId = cursor.getInt(cursor.getColumnIndexOrThrow("MaHoaDon"))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("MaTaiKhoan"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("NgayLap"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("TongTien"))
                val status = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("Sdt"))
                val paymentMethod = cursor.getInt(cursor.getColumnIndexOrThrow("MaHinhThuc"))

                val orderDetails = getOrderDetailsByOrderId(orderId)
                val order = Order(orderId, userId, date, total, status, address, phone, paymentMethod, orderDetails)

                orders.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return orders
    }
    fun getOrdersByDay(date: String): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_HOADON WHERE strftime('%Y-%m-%d', NgayLap) = ?"
        val cursor = db.rawQuery(query, arrayOf(date))

        if (cursor.moveToFirst()) {
            do {
                val orderId = cursor.getInt(cursor.getColumnIndexOrThrow("MaHoaDon"))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("MaTaiKhoan"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("NgayLap"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("TongTien"))
                val status = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("Sdt"))
                val paymentMethod = cursor.getInt(cursor.getColumnIndexOrThrow("MaHinhThuc"))

                val orderDetails = getOrderDetailsByOrderId(orderId)
                val order = Order(orderId, userId, date, total, status, address, phone, paymentMethod, orderDetails)

                orders.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return orders
    }
    fun getOrdersByWeek(week: Int, year: Int): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = """
        SELECT * FROM $TABLE_HOADON 
        WHERE strftime('%Y', NgayLap) = ? 
        AND strftime('%W', NgayLap) = ?
    """
        val cursor = db.rawQuery(query, arrayOf(year.toString(), week.toString()))

        if (cursor.moveToFirst()) {
            do {
                val orderId = cursor.getInt(cursor.getColumnIndexOrThrow("MaHoaDon"))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("MaTaiKhoan"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("NgayLap"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("TongTien"))
                val status = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("Sdt"))
                val paymentMethod = cursor.getInt(cursor.getColumnIndexOrThrow("MaHinhThuc"))

                val orderDetails = getOrderDetailsByOrderId(orderId)
                val order = Order(orderId, userId, date, total, status, address, phone, paymentMethod, orderDetails)

                orders.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return orders
    }
    fun getOrdersByMonth(month: Int, year: Int): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = """
        SELECT * FROM $TABLE_HOADON 
        WHERE strftime('%Y', NgayLap) = ? 
        AND strftime('%m', NgayLap) = ?
    """
        val cursor = db.rawQuery(query, arrayOf(year.toString(), month.toString().padStart(2, '0')))

        if (cursor.moveToFirst()) {
            do {
                val orderId = cursor.getInt(cursor.getColumnIndexOrThrow("MaHoaDon"))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("MaTaiKhoan"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("NgayLap"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("TongTien"))
                val status = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("Sdt"))
                val paymentMethod = cursor.getInt(cursor.getColumnIndexOrThrow("MaHinhThuc"))

                val orderDetails = getOrderDetailsByOrderId(orderId)
                val order = Order(orderId, userId, date, total, status, address, phone, paymentMethod, orderDetails)

                orders.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return orders
    }
    fun getOrdersByYear(year: Int): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = """
        SELECT * FROM $TABLE_HOADON 
        WHERE strftime('%Y', NgayLap) = ?
    """
        val cursor = db.rawQuery(query, arrayOf(year.toString()))

        if (cursor.moveToFirst()) {
            do {
                val orderId = cursor.getInt(cursor.getColumnIndexOrThrow("MaHoaDon"))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("MaTaiKhoan"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("NgayLap"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("TongTien"))
                val status = cursor.getInt(cursor.getColumnIndexOrThrow("TrangThai"))
                val address = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("Sdt"))
                val paymentMethod = cursor.getInt(cursor.getColumnIndexOrThrow("MaHinhThuc"))

                val orderDetails = getOrderDetailsByOrderId(orderId)
                val order = Order(orderId, userId, date, total, status, address, phone, paymentMethod, orderDetails)

                orders.add(order)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return orders
    }
    fun addCategory(categoryName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("TenLoaiSanPham", categoryName)
        }
        db.insert(TABLE_LOAISANPHAM, null, values)
    }
    fun deleteCategory(categoryId: Int) {
        val db = writableDatabase
        db.delete(TABLE_LOAISANPHAM, "MaLoaiSanPham = ?", arrayOf(categoryId.toString()))
        db.close()
    }
    fun updateCategory(categoryId: Int, newName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("TenLoaiSanPham", newName)
        }
        val selection = "MaLoaiSanPham = ?"
        val selectionArgs = arrayOf(categoryId.toString())

        db.update(TABLE_LOAISANPHAM, values, selection, selectionArgs)
        db.close()
    }
    fun getTotalProductsCount(date: String?): Int {
        val db = this.readableDatabase

        val query = when {
            date.isNullOrEmpty() -> {
                "SELECT SUM(SoLuong) FROM $TABLE_CHITIETHOADON"
            }
            date.contains("/") -> {
                val parts = date.split("/")
                when (parts.size) {
                    3 -> { // Format: day/month/year
                        val (day, month, year) = parts.map { it.padStart(2, '0') }
                        "SELECT SUM(SoLuong) FROM $TABLE_CHITIETHOADON " +
                                "INNER JOIN $TABLE_HOADON ON $TABLE_CHITIETHOADON.MaHoaDon = $TABLE_HOADON.MaHoaDon " +
                                "WHERE strftime('%d', $TABLE_HOADON.NgayLap) = '$day' " +
                                "AND strftime('%m', $TABLE_HOADON.NgayLap) = '$month' " +
                                "AND strftime('%Y', $TABLE_HOADON.NgayLap) = '$year'"
                    }
                    2 -> { // Format: month/year (MM/YYYY)
                        val (month, year) = parts
                        val formattedMonth = month.padStart(2, '0')
                        "SELECT SUM(SoLuong) FROM $TABLE_CHITIETHOADON " +
                                "INNER JOIN $TABLE_HOADON ON $TABLE_CHITIETHOADON.MaHoaDon = $TABLE_HOADON.MaHoaDon " +
                                "WHERE strftime('%m', $TABLE_HOADON.NgayLap) = '$formattedMonth' " +
                                "AND strftime('%Y', $TABLE_HOADON.NgayLap) = '$year'"
                    }
                    else -> {
                        "SELECT SUM(SoLuong) FROM $TABLE_CHITIETHOADON"
                    }
                }
            }
            date.length == 4 -> { // Format: year (YYYY)
                "SELECT SUM(SoLuong) FROM $TABLE_CHITIETHOADON " +
                        "INNER JOIN $TABLE_HOADON ON $TABLE_CHITIETHOADON.MaHoaDon = $TABLE_HOADON.MaHoaDon " +
                        "WHERE strftime('%Y', $TABLE_HOADON.NgayLap) = '$date'"
            }
            else -> {
                "SELECT SUM(SoLuong) FROM $TABLE_CHITIETHOADON"
            }
        }

        // Debugging: print the query
        println("SQL Query: $query")

        val cursor = db.rawQuery(query, null)
        val total = cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
        db.close()
        return total
    }
    fun getTotalcoinCount(date: String?): Double {
        val db = this.readableDatabase

        val query = when {
            date.isNullOrEmpty() -> {
                "SELECT SUM(TongTien) FROM $TABLE_HOADON"
            }
            date.contains("/") -> {
                val parts = date.split("/")
                when (parts.size) {
                    3 -> { // Format: day/month/year
                        val (day, month, year) = parts.map { it.padStart(2, '0') }
                        "SELECT SUM(TongTien) FROM $TABLE_HOADON " +
                                "WHERE strftime('%d', $TABLE_HOADON.NgayLap) = '$day' " +
                                "AND strftime('%m', $TABLE_HOADON.NgayLap) = '$month' " +
                                "AND strftime('%Y', $TABLE_HOADON.NgayLap) = '$year'"
                    }
                    2 -> { // Format: month/year (MM/YYYY)
                        val (month, year) = parts
                        val formattedMonth = month.padStart(2, '0')
                        "SELECT SUM(TongTien) FROM $TABLE_HOADON " +
                                "WHERE strftime('%m', $TABLE_HOADON.NgayLap) = '$formattedMonth' " +
                                "AND strftime('%Y', $TABLE_HOADON.NgayLap) = '$year'"
                    }
                    else -> {
                        "SELECT SUM(TongTien) FROM $TABLE_HOADON"
                    }
                }
            }
            date.length == 4 -> { // Format: year (YYYY)
                "SELECT SUM(TongTien) FROM $TABLE_HOADON " +
                        "WHERE strftime('%Y', $TABLE_HOADON.NgayLap) = '$date'"
            }
            else -> {
                "SELECT SUM(TongTien) FROM $TABLE_HOADON"
            }
        }

        // Debugging: print the query
        println("SQL Query: $query")

        val cursor = db.rawQuery(query, null)
        val total = cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
        db.close()
        return total.toDouble()
    }
    fun searchCategories(query: String): List<Category> {
        val categories = mutableListOf<Category>()
        val db = readableDatabase

        // Use parameterized query to prevent SQL injection
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_LOAISANPHAM WHERE TenLoaiSanPham LIKE ?",
            arrayOf("%$query%")
        )

        // Get column indices
        val idColumnIndex = cursor.getColumnIndex("MaLoaiSanPham")
        val nameColumnIndex = cursor.getColumnIndex("TenLoaiSanPham")

        // Check if columns exist
        if (idColumnIndex == -1 || nameColumnIndex == -1) {
            // Handle the error, e.g., log or throw an exception
            throw IllegalArgumentException("Required columns are missing in the result set")
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(idColumnIndex)
                val name = cursor.getString(nameColumnIndex)
                categories.add(Category(id, name))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return categories
    }
}