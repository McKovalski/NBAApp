package com.example.myapplication.helpers

import java.util.regex.Matcher
import java.util.regex.Pattern

class YoutubeVideoHelper {

    fun isValidUrl(youTubeURl: String): Boolean {
        val success: Boolean
        val pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+"
        success = !youTubeURl.isEmpty() && youTubeURl.matches(pattern.toRegex())
        return success
    }

    fun getVideoId(url: String): String? {
        /*
           Possible Youtube urls.
           http://www.youtube.com/watch?v=WK0YhfKqdaI
           http://www.youtube.com/embed/WK0YhfKqdaI
           http://www.youtube.com/v/WK0YhfKqdaI
           http://www.youtube-nocookie.com/v/WK0YhfKqdaI?version=3&hl=en_US&rel=0
           http://www.youtube.com/watch?v=WK0YhfKqdaI
           http://www.youtube.com/watch?feature=player_embedded&v=WK0YhfKqdaI
           http://www.youtube.com/e/WK0YhfKqdaI
           http://youtu.be/WK0YhfKqdaI
        */
        val pattern =
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
        val compiledPattern: Pattern = Pattern.compile(pattern)
        //url is youtube url for which you want to extract the id.
        //url is youtube url for which you want to extract the id.
        val matcher: Matcher = compiledPattern.matcher(url)
        return if (matcher.find()) {
            matcher.group()
        } else null
    }

    fun getVideoThumbnailUrl(videoId: String): String {
        return "https://img.youtube.com/vi/$videoId/mqdefault.jpg"
    }
}