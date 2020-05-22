package org.jige.service;

import org.jige.bean.ControllerItem;
import org.jige.util.StringTools;

import java.util.List;
import java.util.stream.Collectors;

public class UrlSearchMatcher {
    /**
     * 使用关键字搜索url
     */
    public List<ControllerItem> searchUrl(List<ControllerItem> allControllers, String searchText) {
        StringTools.log("searchUrl input size:", allControllers.size());
        List<ControllerItem> list = allControllers
                .stream()
                .filter(it -> it.url.toLowerCase().contains(searchText.toLowerCase()))
                .peek(it -> StringTools.log("search result : ", it.toString()))
                .collect(Collectors.toList());
        StringTools.log("searchUrl finish size:", list.size());
        return list;
    }
}
