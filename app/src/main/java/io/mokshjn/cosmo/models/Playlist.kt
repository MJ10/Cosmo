package io.mokshjn.cosmo.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by moksh on 26/11/17.
 */
class Playlist : Parcelable {
    val id: Int
    val name: String?

    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }

    constructor() {
        this.id = -1
        this.name = ""
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val playlist = o as Playlist?
        if (id != playlist!!.id) return false
        return if (name != null) name == playlist.name else playlist.name == null
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "Playlist{id=$id, name='$name}"

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
    }

    protected constructor(parcel: Parcel) {
        this.id = parcel.readInt()
        this.name = parcel.readString()
    }

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(source: Parcel): Playlist = Playlist(source)
        override fun newArray(size: Int): Array<Playlist?> = arrayOfNulls(size)
    }
}
