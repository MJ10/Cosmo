package io.mokshjn.cosmo.loaders

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import io.mokshjn.cosmo.models.Song
import io.mokshjn.cosmo.utils.PreferenceUtils
import io.reactivex.Observable

/**
 * Created by moksh on 28/11/17.
 */
class SongLoader {
    companion object {
        val BASE_SELECTION = "${MediaStore.Audio.AudioColumns.IS_MUSIC}=1 AND" +
                "${MediaStore.Audio.AudioColumns.TITLE} != ''"

        fun makeSongCursor(context: Context, selection: String?,
                           selectionValues: Array<String>?): Cursor? {
            return makeSongCursor(context, selection, selectionValues,
                    PreferenceUtils.getInstance(context).songSortOrder!!)
        }

        fun makeSongCursor(context: Context, selection: String?,
                           selectionValues: Array<String>?, sortOrder: String): Cursor? {

            val select: String = if (!selection?.trim().equals(""))
                "$BASE_SELECTION AND $selection"
            else
                BASE_SELECTION

            try {
                return context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(BaseColumns._ID,
                                MediaStore.Audio.AudioColumns.TITLE,
                                MediaStore.Audio.AudioColumns.TRACK,
                                MediaStore.Audio.AudioColumns.YEAR,
                                MediaStore.Audio.AudioColumns.DURATION,
                                MediaStore.Audio.AudioColumns.DATA,
                                MediaStore.Audio.AudioColumns.DATE_MODIFIED,
                                MediaStore.Audio.AudioColumns.ALBUM_ID,
                                MediaStore.Audio.AudioColumns.ALBUM,
                                MediaStore.Audio.AudioColumns.ARTIST_ID,
                                MediaStore.Audio.AudioColumns.ARTIST),
                        select, selectionValues, sortOrder)
            } catch (e: SecurityException) {
                return null
            }

        }

        fun getSongFromCursor(cursor: Cursor): Song = with(cursor) {
            val id = getInt(0)
            val title = getString(1)
            val track = getInt(2)
            val year = getInt(3)
            val duration = getLong(4)
            val data = getString(5)
            val dateModified = getLong(6)
            val albumId = getInt(7)
            val albumName = getString(8)
            val artistId = getInt(9)
            val artistName = getString(10)
            Song(id, title, track, year, duration, data, dateModified,
                    albumId, albumName, artistId, artistName)
        }

        fun getAllSongs(context: Context): Observable<ArrayList<Song>> {
            val cursor = makeSongCursor(context, null, null)
            return getSongs(cursor)
        }

        fun getSongs(context: Context, query: String): Observable<ArrayList<Song>> {
            val cursor = makeSongCursor(context,
                    "${MediaStore.Audio.AudioColumns.TITLE} LIKE ?",
                    arrayOf("%$query%"))
            return getSongs(cursor)
        }

        fun getSongs(cursor: Cursor?): Observable<ArrayList<Song>> = Observable.create<ArrayList<Song>> { e ->
            val songs = ArrayList<Song>()
            if (cursor?.moveToFirst()!!)
                do {
                    songs.add(getSongFromCursor(cursor))
                } while (cursor.moveToNext())
            cursor.close()
            e.onNext(songs)
            e.onComplete()
        }

        fun getSong(context: Context, queryId: Int): Observable<Song> {
            val cursor = makeSongCursor(context,
                    "${MediaStore.Audio.AudioColumns._ID}?=",
                    arrayOf("$queryId"))
            return getSong(cursor)
        }

        fun getSong(cursor: Cursor?): Observable<Song> = Observable.create { e ->
            val song: Song = if (cursor?.moveToFirst()!!)
                getSongFromCursor(cursor)
            else
                Song.EMPTY_SONG
            cursor.close()
            e.onNext(song)
            e.onComplete()
        }
    }
}