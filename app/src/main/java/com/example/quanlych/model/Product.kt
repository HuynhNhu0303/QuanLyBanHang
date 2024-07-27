package com.example.quanlych.model

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val imageResource: String,
    val price: Double,
    var quantity: Int,
    var isSelected: Boolean = false,
    val categoryId: Int // Add this field
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(imageResource)
        parcel.writeDouble(price)
        parcel.writeInt(quantity)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeInt(categoryId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
