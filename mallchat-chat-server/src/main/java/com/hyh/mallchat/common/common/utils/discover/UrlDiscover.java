package com.hyh.mallchat.common.common.utils.discover;

import com.hyh.mallchat.common.chat.domain.entity.msg.UrlInfo;
import org.jsoup.nodes.Document;
import org.springframework.lang.Nullable;

import java.util.Map;

public interface UrlDiscover {
    /**
     * 获取URL内容
     * @param content
     * @return
     */
    @Nullable
    Map<String, UrlInfo> getUrlContentMap(String content);

    @Nullable
    UrlInfo getContent(String url);
    @Nullable
    String getTitle(Document document);

    @Nullable
    String getDescription(Document document);
    @Nullable
    String getImage(String url, Document document);
}
