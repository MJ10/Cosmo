package io.mokshjn.cosmo.loaders

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import io.mokshjn.cosmo.models.Playlist
import io.mokshjn.cosmo.models.PlaylistSong
import io.mokshjn.cosmo.models.Song
import io.reactivex.Observable

/**
 * Created by moksh on 29/11/17.
 */
class PlaylistSongLoader {
    companion object {
        fun getPlaylistSongList(context: Context, playlist: Playlist) = getPlaylistSongList(
                context, playlist.id
        )

        fun getPlaylistSongList(context: Context,
                                playlistId: Int) = Observable.create<ArrayList<Song>> { e ->
            val songs = ArrayList<PlaylistSong>()
            val cursor = makePlaylistSongCursor(context, playlistId)

            if (cursor?.moveToFirst()!!)
                do {
                    songs.add(getPlaylistSongFromCursor(cursor, playlistId))
                } while (cursor.moveToNext())

            cursor.close()
            e.onNext(songs as ArrayList<Song>)
            e.onComplete()
        }

        fun getPlaylistSongFromCursor(cursor: Cursor, playlistId: Int) = with(cursor) {
            PlaylistSong(getInt(0),
                    getString(1),
                    getInt(2),
                    getInt(3),
                    getLong(4),
                    getString(5),
                    getInt(6).toLong(),
                    getInt(7),
                    getString(8),
                    getInt(9),
                    getString(10),
                    playlistId,
                    getInt(11))
        }

        fun makePlaylistSongCursor(context: Context, playlistId: Int) = try {
            context.contentResolver.query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external",
                            playlistId.toLong()),
                    arrayOf(MediaStore.Audio.Playlists.Members.AUDIO_ID,
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
                    SongLoader.BASE_SELECTION, null,
                    MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
            )
        } catch (e: SecurityException) {
            null
        }
    }
}