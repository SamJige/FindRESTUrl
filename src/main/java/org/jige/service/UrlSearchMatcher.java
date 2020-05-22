package org.jige.service;

import org.jige.bean.ControllerItem;

import java.util.List;
import java.util.stream.Collectors;

public class UrlSearchMatcher {
    /**
     * 使用关键字搜索url
     */
    public List<ControllerItem> searchUrl(List<ControllerItem> allControllers, String searchText) {
        return allControllers
                .stream()
                .filter(it -> it.url.stream().anyMatch(url -> url.toLowerCase().contains(searchText.toLowerCase())))
                .collect(Collectors.toList());
    }
}
