package com.hyh.mallchat.common.common.utils.discover;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/*
     工程模式：注册

 */
@Slf4j
public class PrioritizedUrlDiscover extends AbstractUrlDiscover {
    public static final int INITIAL_CAPACITY = 2;
    private final List<UrlDiscover> urlDiscovers = new ArrayList<>(INITIAL_CAPACITY);

    public PrioritizedUrlDiscover() {
        urlDiscovers.add(new WxUrlDiscover());
        urlDiscovers.add(new CommonUrlDiscover());
    }

    @Override
    public String getTitle(Document document) {
        for (UrlDiscover urlDiscover : urlDiscovers) {
            String urlTitle = urlDiscover.getTitle(document);
            if (urlTitle != null) {
                return urlTitle;
            }

        }
        return null;
    }

    @Override
    public String getDescription(Document document) {
        for (UrlDiscover urlDiscover : urlDiscovers) {
            String urlDescription = urlDiscover.getDescription(document);
            if (urlDescription != null) {
                return urlDescription;
            }
        }
        return null;
    }

    @Override
    public String getImage(String url, Document document) {
        for (UrlDiscover urlDiscover : urlDiscovers) {
            String urlImage = urlDiscover.getImage(url, document);
            if (urlImage != null) {
                return urlImage;
            }
        }
        return null;
    }
}
