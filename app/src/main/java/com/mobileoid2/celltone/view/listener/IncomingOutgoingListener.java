package com.mobileoid2.celltone.view.listener;

import com.mobileoid2.celltone.network.model.treadingMedia.Song;

public interface IncomingOutgoingListener {
    public void setIncomingOutComingListener(int  isOutgoing,Song song);
    public void setMedia(String id,String url,int songPostion);
}
