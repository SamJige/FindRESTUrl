package org.jige.service;

import org.jige.bean.ControllerItem;
import org.jige.util.StringTools;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UrlSearchMatcher {
    /*
    有时候填写的url前面没有加 /
    补充一个/方便搜索
     */
    private String fixUrl(String rawUrl) {
        return rawUrl.startsWith("/") ? rawUrl : "/" + rawUrl;
    }

    /**
     * 使用关键字搜索url
     */
    public List<ControllerItem> searchUrl(List<ControllerItem> allControllers, String searchText) {
        StringTools.log("searchUrl input size:", allControllers.size());
        List<ControllerItem> list = allControllers
                .stream()
                .filter(it -> fixUrl(it.url).toLowerCase().contains(searchText.toLowerCase()))
                .peek(it -> StringTools.log("search result : ", it.toString()))
                .sorted(Comparator.comparingInt(a -> a.url.length()))
                .collect(Collectors.toList());
        StringTools.log("searchUrl finish size:", list.size());
        return list;
    }
}
