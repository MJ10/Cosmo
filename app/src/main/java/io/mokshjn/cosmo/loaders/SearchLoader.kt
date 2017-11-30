package io.mokshjn.cosmo.loaders

import android.content.Context
import android.text.TextUtils
import io.reactivex.Observable

/**
 * Created by moksh on 30/11/17.
 */
class SearchLoader {
    companion object {
        fun searchAll(context: Context, query: String) = Observable.create<ArrayList<Any>> { e ->
            val results = ArrayList<Any>()
            if (!TextUtils.isEmpty(query)) {
                SongLoader.getSongs(context, query).subscribe { songs ->
                    if (!songs.isEmpty()) {
                        results.add("Songs")
                        results.addAll(songs)
                    }
                }

                AlbumLoader.getAlbums(context, query).subscribe { albums ->
                    if (!albums.isEmpty()) {
                        results.add("Albums")
                        results.addAll(albums)
                    }
                }

                ArtistLoader.getArtists(context, query).subscribe { artists ->
                    if (!artists.isEmpty()) {
                        results.add("Artists")
                        results.addAll(artists)
                    }
                }
            }

            e.onNext(results)
            e.onComplete()
        }
    }
}