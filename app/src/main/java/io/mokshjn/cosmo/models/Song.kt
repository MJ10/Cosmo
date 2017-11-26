package io.mokshjn.cosmo.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by moksh on 26/11/17.
 */
open class Song : Parcelable {

    var id: Int = 0
    lateinit var title: String
    var trackNumber: Int = 0
    var year: Int = 0
    var duration: Long = 0L
    lateinit var data: String
    var dateModified: Long = 0L
    var albumId: Int = 0
    lateinit var albumName: String
    var artistId: Int = 0
    lateinit var artistName: String

    constructor(parcel: Parcel) {
        this.id = parcel.readInt()
        this.title = parcel.readString()
        this.trackNumber = parcel.readInt()
        this.year = parcel.readInt()
        this.duration = parcel.readLong()
        this.data = parcel.readString()
        this.dateModified = parcel.readLong()
        this.albumId = parcel.readInt()
        this.albumName = parcel.readString()
        this.artistId = parcel.readInt()
        this.artistName = parcel.readString()
    }

    constructor(id: Int, title: String, trackNumber: Int, year: Int, duration: Long, data: String, dateModified: Long, albumId: Int, albumName: String, artistId: Int, artistName: String) {
        this.id = id
        this.title = title
        this.trackNumber = trackNumber
        this.year = year
        this.duration = duration
        this.data = data
        this.dateModified = dateModified
        this.albumId = albumId
        this.albumName = albumName
        this.artistId = artistId
        this.artistName = artistName
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(title)
        writeInt(trackNumber)
        writeInt(year)
        writeLong(duration)
        writeString(data)
        writeLong(dateModified)
        writeInt(albumId)
        writeString(albumName)
        writeInt(artistId)
        writeString(artistName)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel) = Song(parcel)
        override fun newArray(size: Int): Array<Song?> = arrayOfNulls<Song?>(size)
        val EMPTY_SONG = Song(-1, "", -1, -1, -1, "", -1, -1, "", -1, "")
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + trackNumber
        result = 31 * result + year
        result = 31 * result + (duration xor duration.ushr(32)).toInt()
        result = 31 * result + data.hashCode()
        result = 31 * result + (dateModified xor dateModified.ushr(32)).toInt()
        result = 31 * result + albumId
        result = 31 * result + albumName.hashCode()
        result = 31 * result + artistId
        result = 31 * result + artistName.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val song = other as Song?

        if (id != song!!.id) return false
        if (trackNumber != song.trackNumber) return false
        if (year != song.year) return false
        if (duration != song.duration) return false
        if (dateModified != song.dateModified) return false
        if (albumId != song.albumId) return false
        if (artistId != song.artistId) return false
        if (title != song.title) return false
        if (data != song.data) return false
        if (albumName != song.albumName) return false
        return artistName == song.artistName

    }

    override fun toString(): String = "Song{id=$id, title='$title'," +
            "trackNumber=$trackNumber, year=$year, duration=$duration," +
            "data='$data', dateModified=$dateModified, albumId=$albumId," +
            "albumName='$albumName', artistId=$artistId, artistId='$artistName'}"
}