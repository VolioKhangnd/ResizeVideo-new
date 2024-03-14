package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.join_video

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

interface ItemTouchListenner {
    fun onMove(oldPosition: Int, newPosition: Int)
    fun swipe(position: Int, direction: Int)
}


class DragDropCallback(private val adapter: VideoJoinAdapter, private val onTouchListenner: ItemTouchListenner) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return super.isLongPressDragEnabled()
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        onTouchListenner.onMove(fromPosition,toPosition)
        adapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Không cần thực hiện gì với swipe
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }
}
