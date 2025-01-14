package com.example.moviesapp.domain.model

enum class SeriesGenre(val id: Int, val displayName: String) {
    ACTION_AND_ADVENTURE(10759, "Action & Adventure"),
    ANIMATION(16, "Animation"),
    COMEDY(35, "Comedy"),
    CRIME(80, "Crime"),
    DOCUMENTARY(99, "Documentary"),
    DRAMA(18, "Drama"),
    FAMILY(10751, "Family"),
    KIDS(10762, "Kids"),
    MYSTERY(9648, "Mystery"),
    NEWS(10763, "News"),
    REALITY(10764, "Reality"),
    SCI_FI_AND_FANTASY(10765, "Sci-Fi & Fantasy"),
    SOAP(10766, "Soap"),
    TALK(10767, "Talk"),
    WAR_AND_POLITICS(10768, "War & Politics"),
    WESTERN(37, "Western");

    companion object {
        // Find a genre by its ID
        fun fromId(id: Int): SeriesGenre? {
            return entries.find { it.id == id }
        }
    }
}
