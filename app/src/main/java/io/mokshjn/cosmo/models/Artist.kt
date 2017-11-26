package io.mokshjn.cosmo.models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by moksh on 26/11/17.
 */
class Artist : Parcelable {
    val albums: ArrayList<Album>?

    val id: Int
        get() = safeGetFirstAlbum().artistId

    val name: String
        get() = safeGetFirstAlbum().artistName

    val songCount: Int
        get() {
            var songCount = 0
            for (album in albums!!) {
                songCount += album.songCount
            }
            return songCount
        }

    val albumCount: Int
        get() = albums!!.size

    val songs: ArrayList<Song>
        get() {
            val songs = ArrayList<Song>()
            for (album in albums!!) {
                songs.addAll(album.songs!!)
            }
            return songs
        }

    constructor(albums: ArrayList<Album>) {
        this.albums = albums
    }

    constructor() {
        this.albums = ArrayList()
    }

    fun safeGetFirstAlbum(): Album {
        return if (albums!!.isEmpty()) Album() else albums[0]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val artist = other as Artist?
        return if (albums != null) albums == artist!!.albums else artist!!.albums == null
    }

    override fun hashCode(): Int = albums?.hashCode() ?: 0

    override fun toString(): String = "Artist{albums=$albums}"


    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) { writeTypedList(albums) }

    protected constructor(parcel: Parcel) {
        this.albums = parcel.createTypedArrayList(Album.CREATOR)
    }

    companion object CREATOR : Parcelable.Creator<Artist> {
        override fun createFromParcel(source: Parcel) = Artist(source)
        override fun newArray(size: Int): Array<Artist?> = arrayOfNulls(size)
    }
}
