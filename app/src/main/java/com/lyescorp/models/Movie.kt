package com.lyescorp.models

import com.google.gson.annotations.SerializedName


//Data Class de Movies normal para nuestro JSON

data class Movie(
    var title: String, //
    var adult: Boolean = false, //

    @SerializedName("backdrop_path")
    var backdropPath: String = "", //

    @SerializedName("genre_ids")
    var genreIDS: List<Long> = emptyList(), //

    var id: Long = 0, //

    @SerializedName("original_language")
    var originalLanguage: String = "", //

    @SerializedName("original_title")
    var originalTitle: String = "",//

    var overview: String = "", //
    var popularity: Double = 0.0, //

    @SerializedName("poster_path")
    var posterPath: String = "", //

    @SerializedName("release_date")
    var releaseDate: String = "", //

    var video: Boolean = false, //

    @SerializedName("vote_average")
    var voteAverage: Double = 0.0, //

    @SerializedName("vote_count")
    var voteCount: Long = 0, //

    var favorite: Boolean = false,
) : java.io.Serializable

