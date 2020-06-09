package org.jige.service;

import org.jige.bean.ControllerItem;
import org.junit.Test;
import org.slf4j.LoggerFactory;

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
                        , new ControllerItem().testInit("/t2/tt2")
                        , new ControllerItem().testInit("/tt2")
                        , new ControllerItem().testInit("/tt2/tt2")
                        , new ControllerItem().testInit("tt3")
                        , new ControllerItem().testInit("tt3/tt3")
                        , new ControllerItem().testInit("/tt3/tt3")
                ),
                "t3"
        );
        result.forEach(it -> LoggerFactory.getLogger(getClass()).info("it -> {}", it));
    }
}