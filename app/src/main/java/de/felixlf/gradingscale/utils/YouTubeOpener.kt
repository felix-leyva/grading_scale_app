package de.felixlf.gradingscale.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

fun Fragment.openHelpVideo(youtubeID: String){
  val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID))
  val intentBrowser =
    Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeID))
  try {
    startActivity(intentApp)
  } catch (ex: ActivityNotFoundException) {
    startActivity(intentBrowser)
  }

}
