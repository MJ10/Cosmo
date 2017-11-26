package io.mokshjn.cosmo.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by moksh on 26/11/17.
 */
class PlaylistSong : Song {
    val playlistId: Int
    val idInPlayList: Int

    constructor(id: Int, title: String, trackNumber: Int, year: Int, duration: Long, data: String, dateModified: Long, albumId: Int, albumName: String, artistId: Int, artistName: String, playlistId: Int, idInPlayList: Int) : super(id, title, trackNumber, year, duration, data, dateModified, albumId, albumName, artistId, artistName) {
        this.playlistId = playlistId
        this.idInPlayList = idInPlayList
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass !== other.javaClass) return false
        if (!super.equals(other)) return false
        val that = other as PlaylistSong?
        return if (playlistId != that!!.playlistId) false else idInPlayList == that.idInPlayList

    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + playlistId
        result = 31 * result + idInPlayList
        return result
    }

    override fun toString(): String = super.toString() +
            "PlaylistSong{playlistId=$playlistId, idInPlayList=$idInPlayList}"

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeInt(this.playlistId)
        dest.writeInt(this.idInPlayList)
    }

    protected constructor(parcel: Parcel) : super(parcel) {
        this.playlistId = parcel.readInt()
        this.idInPlayList = parcel.readInt()
    }

    companion object CREATOR : Parcelable.Creator<PlaylistSong> {
        override fun createFromParcel(source: Parcel) = PlaylistSong(source)
        override fun newArray(size: Int): Array<PlaylistSong?> = arrayOfNulls(size)

        val EMPTY_PLAYLIST_SONG = PlaylistSong(-1, "", -1, -1, -1, "", -1, -1, "", -1, "", -1, -1)
    }
}