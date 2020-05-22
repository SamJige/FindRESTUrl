package org.jige.service;

import org.jige.bean.ControllerItem;
import org.jige.util.StringTools;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class UrlSearchMatcherTest {
    @Test
    public void test1Test() {
        UrlSearchMatcher matcher = new UrlSearchMatcher();
        List<ControllerItem> result = matcher.searchUrl(
                Arrays.asList(
                        new ControllerItem().testInit("/t1")
                        , new ControllerItem().testInit("/t2")
                        , new ControllerItem().testInit("/t2", "/t2/tt2")
                        , new ControllerItem().testInit("/tt2", "/tt2/tt2")
                ),
                "/t2"
        );
        result.forEach(it -> StringTools.log("it ", it.toString()));
    }
}