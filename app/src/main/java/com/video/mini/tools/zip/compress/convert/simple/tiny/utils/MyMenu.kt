package com.video.mini.tools.zip.compress.convert.simple.tiny.utils


import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore.Video.Media
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.video.mini.tools.zip.compress.convert.simple.tiny.R


enum class SortOrder(val order: String) {
    ASCENDING(" ASC"), DESCENDING(" DESC"),
}


@SuppressLint("StaticFieldLeak")
object MyMenu {
    private var sortOrder = SortOrder.DESCENDING.order
    private var sortBy = Media.DATE_ADDED
    private lateinit var view: View
    fun MenuItem.handleClickSortAction(sortBy: (String) -> Unit) {
        isChecked = true
        if (itemId == R.id.sortByDesc || itemId == R.id.sortByAsc) {
            sortOrder = if (itemId == R.id.sortByDesc) {
                SortOrder.DESCENDING.order
            } else {
                SortOrder.ASCENDING.order
            }
        } else {
            MyMenu.sortBy = when (itemId) {
                R.id.sortByDateAdded -> Media.DATE_ADDED
                R.id.sortByTitle -> Media.TITLE
                R.id.sortBySize -> Media.SIZE
                R.id.sortByDuration -> Media.DURATION
                else -> Media.DATE_ADDED
            }
        }
        sortBy(MyMenu.sortBy + sortOrder)
    }


    private lateinit var darkModeManager: DarkModeManager
    fun View.showPopupMenuTheme(text: (String) -> Unit) {
        var wrapper: Context =  ContextThemeWrapper(context, R.style.popupMenuStyle)

        val popupMenu = PopupMenu(wrapper, this)
         if (!MyMenu::darkModeManager.isInitialized) {
            darkModeManager = DarkModeManager(this.context)
        }
        popupMenu.menuInflater.inflate(R.menu.menu_options_theme, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            menuItem.setChecked(true)
            text(menuItem.title.toString())
            when (menuItem.itemId) {
                R.id.mnDark -> darkModeManager.enableDarkMode()
                R.id.mnLight -> darkModeManager.disableDarkMode()
                R.id.mnSystem -> darkModeManager.darkModeDefault()
            }
            true
        }
        popupMenu.show()

    }

}