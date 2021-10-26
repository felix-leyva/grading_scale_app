package de.felixlf.gradingscale.ui.importremote.recyclerview

sealed class ImportRemoteListItem {
  object ImportRemoteHeader : ImportRemoteListItem()
  data class ImportRemoteItem(val country: String, val nameScale: String, ) : ImportRemoteListItem()
}
