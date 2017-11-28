package io.mokshjn.cosmo.loaders

import android.content.Context
import android.provider.MediaStore
import io.mokshjn.cosmo.models.Album
import io.mokshjn.cosmo.models.Artist
import io.mokshjn.cosmo.utils.PreferenceUtils
import io.reactivex.Observable

/**
 * Created by moksh on 28/11/17.
 */
class ArtistLoader {
    companion object {
        fun getAllArtists(context: Context) = Observable.create<ArrayList<Artist>> { e ->
            SongLoader.getSongs(SongLoader.makeSongCursor(
                    context,
                    null,
                    null,
                    getSortOrder(context)
            )).subscribe { song ->
                e.onNext(splitIntoArtists(AlbumLoader.splitIntoAlbums(song)))
                e.onComplete()
            }
        }

        fun getArtists(context: Context, query: String) = Observable.create<ArrayList<Artist>> { e ->
            SongLoader.getSongs(SongLoader.makeSongCursor(
                    context,
                    "${MediaStore.Audio.AudioColumns.ARTIST} LIKE ?",
                    arrayOf("%$query%"),
                    getSortOrder(context)
            )).subscribe { songs ->
                e.onNext(splitIntoArtists(AlbumLoader.splitIntoAlbums(songs)))
                e.onComplete()
            }
        }

        fun getArtist(context: Context, artistId: Int) = Observable.create<Artist> { e ->
            SongLoader.getSongs(SongLoader.makeSongCursor(
                    context,
                    "${MediaStore.Audio.AudioColumns.ARTIST_ID} = ?",
                    arrayOf("$artistId"),
                    getSortOrder(context)
            )).subscribe { songs ->
                e.onNext(Artist(AlbumLoader.splitIntoAlbums(songs)))
                e.onComplete()
            }
        }

        fun splitIntoArtists(
                albums: Observable<ArrayList<Album>>) = Observable.create<ArrayList<Artist>> { e ->
            val artists = ArrayList<Artist>()
            albums.subscribe { localAlbums ->
                if (localAlbums != null)
                    for (album in localAlbums)
                        getOrCreateArtist(artists, album.artistId).albums?.add(album)
                e.onNext(artists)
                e.onComplete()
            }
        }

        fun splitIntoArtists(albums: ArrayList<Album>?): ArrayList<Artist> {
            val artists = ArrayList<Artist>()
            if (albums != null)
                for (album in albums)
                    getOrCreateArtist(artists, album.artistId).albums?.add(album)
            return artists
        }

        fun getOrCreateArtist(artists: ArrayList<Artist>, artistId: Int): Artist {
            artists
                    .filter {
                        !it.albums!!.isEmpty() &&
                                !it.albums.get(0).songs!!.isEmpty() &&
                                it.albums[0].songs!![0].artistId == artistId
                    }
                    .forEach { return it }
            val artist = Artist()
            artists.add(artist)
            return artist
        }

        fun getSortOrder(context: Context) = "${PreferenceUtils.getInstance(context).artistSortOrder}" +
                ", ${PreferenceUtils.getInstance(context).artistAlbumSortOrder}, ${PreferenceUtils.
                        getInstance(context).albumDetailSongSortOrder}, ${PreferenceUtils.getInstance(context)
                        .artistDetailSongSortOrder}"
    }
}