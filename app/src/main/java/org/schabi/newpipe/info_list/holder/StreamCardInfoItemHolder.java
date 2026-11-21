package org.schabi.newpipe.info_list.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.info_list.InfoItemBuilder;
import org.schabi.newpipe.local.history.HistoryRecordManager;
import org.schabi.newpipe.util.PicassoHelper;

import java.util.List;

/**
 * Card layout for stream.
 */
public class StreamCardInfoItemHolder extends StreamInfoItemHolder {
    private final ImageView avatarView;
    private final View moreButton;
    private final View saveButton;

    public StreamCardInfoItemHolder(final InfoItemBuilder infoItemBuilder, final ViewGroup parent) {
        super(infoItemBuilder, R.layout.list_stream_card_item, parent);
        avatarView = itemView.findViewById(R.id.itemAvatarView);
        moreButton = itemView.findViewById(R.id.itemMoreButton);
        saveButton = itemView.findViewById(R.id.itemSaveButton);
    }

    @Override
    public void updateFromItem(final InfoItem infoItem,
                               final HistoryRecordManager historyRecordManager) {
        super.updateFromItem(infoItem, historyRecordManager);
        if (!(infoItem instanceof StreamInfoItem)) {
            return;
        }

        // The buttons open the same menu that a long press on the card opens
        final View.OnClickListener openMenu = view -> itemView.performLongClick();
        moreButton.setOnClickListener(openMenu);
        saveButton.setOnClickListener(openMenu);

        final String avatarUrl = findAvatarUrl(infoItem);
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            PicassoHelper.loadAvatar(avatarUrl).into(avatarView);
        } else {
            avatarView.setImageDrawable(null);
        }
    }

    /**
     * Reads the channel avatar without depending on a specific extractor version.
     */
    private static String findAvatarUrl(final InfoItem item) {
        try {
            final Object url = item.getClass().getMethod("getUploaderAvatarUrl").invoke(item);
            if (url instanceof String) {
                return (String) url;
            }
        } catch (final Exception ignored) {
            // method not present in this extractor version, try the next one
        }
        try {
            final Object list = item.getClass().getMethod("getUploaderAvatars").invoke(item);
            if (list instanceof List && !((List<?>) list).isEmpty()) {
                final Object first = ((List<?>) list).get(0);
                final Object url = first.getClass().getMethod("getUrl").invoke(first);
                if (url instanceof String) {
                    return (String) url;
                }
            }
        } catch (final Exception ignored) {
            // no avatar available
        }
        return null;
    }
}
