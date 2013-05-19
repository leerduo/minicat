package com.mcxiaoke.fanfouapp.adapter;

import com.mcxiaoke.fanfouapp.dao.model.StatusModel;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.ui.widget.ItemView;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 2.0 2012.03.28
 */
public class UIHelper {
    public static String getDateString(long date) {
        return DateTimeHelper.getInterval(date);
    }

    public static void setItemTextSize(final ItemView view, int fontSize) {
        view.setContentTextSize(fontSize);
        view.setTitleTextSize(fontSize + 2);
        view.setMetaTextSize(fontSize - 2, fontSize - 2);
    }

    public static void setMetaInfo(final ItemView view, final StatusModel s) {
        view.showIconThread(s.isThread());
        view.showIconFavorite(s.isFavorited());
        view.showIconPhoto(s.isPhoto());
        view.showIconRetweet(s.isRetweeted());
        boolean lock = s.getUser() != null && s.getUser().isProtect();
        view.showIconLock(lock);
        view.setTitle(s.getUserScreenName());

        StringBuilder metaA = new StringBuilder();
        if (s.isRetweeted()) {
            metaA.append("由");
            metaA.append(s.getUserScreenName());
            metaA.append("转发");
        }

        StringBuilder metaB = new StringBuilder();
        metaB.append(getDateString(s.getTime()));
        metaB.append(" 通过");
        metaB.append(s.getSource());
        view.setMeta(metaA.toString(), metaB.toString());
    }

    public static void setContent(final ItemView view, final UserModel u) {
        view.showIconLock(u.isProtect());
        view.setTitle(u.getScreenName());
        StringBuilder content = new StringBuilder();
        content.append(u.getLocation());
        content.append(" ");
        content.append(u.getGender());
        view.setContent(content.toString());
        view.showMeta(false, false);
    }

}