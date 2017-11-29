package io.mokshjn.cosmo.loaders

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import io.mokshjn.cosmo.models.Playlist
import io.reactivex.Observable

/**
 * Created by moksh on 28/11/17.
 */
class PlaylistLoader {
    companion object {
        fun getAllPlaylists(context: Context) = getAllPlaylists(
                makePlaylistCursor(context, null, null)
        )

        fun getAllPlaylists(cursor: Cursor?) = Observable.create<ArrayList<Playlist>> { e ->
            val playlists = ArrayList<Playlist>()
            if (cursor?.moveToFirst()!!)
                do {
                    playlists.add(getPlaylistFromCursor(cursor))
                } while (cursor.moveToNext())
            cursor.close()
            e.onNext(playlists)
            e.onComplete()
        }

        fun getPlaylist(context: Context, playlistId: Int) = getPlaylist(
                makePlaylistCursor(context,
                        "${BaseColumns._ID}=?",
                        arrayOf("$playlistId"))
        )

        fun getPlaylist(context: Context, playlistName: String) = getPlaylist(
                makePlaylistCursor(context,
                        "${MediaStore.Audio.PlaylistsColumns.NAME}=?",
                        arrayOf(playlistName))
        )

        fun getPlaylist(cursor: Cursor?) = Observable.create<Playlist> { e ->
            var playlist = Playlist()
            if (cursor?.moveToFirst()!!)
                playlist = getPlaylistFromCursor(cursor)

            cursor.close()

            e.onNext(playlist)
            e.onComplete()
        }

        fun getPlaylistFromCursor(cursor: Cursor) = with(cursor) {
            Playlist(getInt(0), getString(1))
        }

        fun makePlaylistCursor(context: Context, selection: String?, values: Array<String>?) = try {
            context.contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    arrayOf(BaseColumns._ID,
                            MediaStore.Audio.PlaylistsColumns.NAME),
                    selection,
                    values,
                    MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER)
        } catch (e: SecurityException) {
            null
        }
    }
}