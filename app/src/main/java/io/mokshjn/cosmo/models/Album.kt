package io.mokshjn.cosmo.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by moksh on 26/11/17.
 */
class Album : Parcelable {
    val songs: ArrayList<Song>?

    val id: Int
        get() = safeGetFirstSong().albumId

    val title: String
        get() = safeGetFirstSong().albumName

    val artistId: Int
        get() = safeGetFirstSong().artistId

    val artistName: String
        get() = safeGetFirstSong().artistName

    val year: Int
        get() = safeGetFirstSong().year

    val dateModified: Long
        get() = safeGetFirstSong().dateModified

    val songCount: Int
        get() = songs!!.size

    constructor(songs: ArrayList<Song>) {
        this.songs = songs
    }

    constructor() {
        this.songs = ArrayList()
    }

    protected constructor(`in`: Parcel) {
        this.songs = `in`.createTypedArrayList(Song.CREATOR)
    }

    fun safeGetFirstSong(): Song {
        return if (songs!!.isEmpty()) Song.EMPTY_SONG else songs[0]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Album?
        return if (songs != null) songs == that!!.songs else that!!.songs == null
    }

    override fun hashCode(): Int = songs?.hashCode() ?: 0

    override fun toString(): String = "Album{songs=$songs}"

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(songs)
    }

    companion object CREATOR : Parcelable.Creator<Album> {
        override fun createFromParcel(source: Parcel) = Album(source)
        override fun newArray(size: Int): Array<Album?> = arrayOfNulls(size)
    }
}