package com.priv.messenger.my.ChatRecycler;

public interface ChatTouchHelperCallback {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);

}
