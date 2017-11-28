package io.mokshjn.cosmo.loaders

import android.content.Context
import android.provider.MediaStore
import io.mokshjn.cosmo.models.Album
import io.mokshjn.cosmo.models.Song
import io.mokshjn.cosmo.utils.PreferenceUtils
import io.reactivex.Observable

/**
 * Created by moksh on 28/11/17.
 */
class AlbumLoader {
    companion object {
        fun getAllAlbums(context: Context) = splitIntoAlbums(
                SongLoader.getSongs(SongLoader.makeSongCursor(
                        context,
                        null,
                        null,
                        getSortOrder(context)
                ))
        )

        fun getAlbums(context: Context, query: String) = splitIntoAlbums(
                SongLoader.getSongs(SongLoader.makeSongCursor(
                        context,
                        "${MediaStore.Audio.AudioColumns.ALBUM} LIKE ?",
                        arrayOf("%$query%"),
                        getSortOrder(context)
                ))
        )

        fun getAlbum(context: Context, albumId: Int) = Observable.create<Album> { e ->
            val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                    context,
                    "${MediaStore.Audio.AudioColumns.ALBUM_ID} =?",
                    arrayOf("$albumId"),
                    getSortOrder(context)
            ))
            songs.subscribe { song ->
                e.onNext(Album(song))
                e.onComplete()
            }
        }

        fun splitIntoAlbums(songs: Observable<ArrayList<Song>>?) = Observable.create<ArrayList<Album>>
        { e ->
            val albums = ArrayList<Album>()
            songs?.subscribe { albumSongs ->
                for (song in albumSongs)
                    getOrCreateAlbum(albums, song.albumId).subscribe { album -> album.songs?.add(song) }
            }
            e.onNext(albums)
            e.onComplete()
        }

        fun splitIntoAlbums(songs: ArrayList<Song>?): ArrayList<Album> {
            val albums = ArrayList<Album>()
            if (songs != null)
                for (song in songs)
                    getOrCreateAlbum(albums, song.albumId).subscribe { album -> album.songs?.add(song) }
            return albums
        }

        fun getOrCreateAlbum(albums: ArrayList<Album>, albumId: Int) = Observable.create<Album>
        { e ->
            for (album in albums)
                if (!album.songs?.isEmpty()!! && album.songs[0].albumId == albumId) {
                    e.onNext(album)
                    e.onComplete()
                    return@create
                }
            val album = Album()
            albums.add(album)
            e.onNext(album)
            e.onComplete()
        }

        fun getSortOrder(context: Context): String = "${PreferenceUtils.getInstance(context)
                .albumSortOrder}, ${PreferenceUtils.getInstance(context).albumDetailSongSortOrder}"
    }
}