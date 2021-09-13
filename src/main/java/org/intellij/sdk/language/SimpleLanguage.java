// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.language;

import com.intellij.lang.Language;

public class SimpleLanguage extends Language {
    private static final SimpleLanguage instance = new SimpleLanguage();

    private SimpleLanguage() {
        super("Simple");
    }

    public static SimpleLanguage getInstance() {
        return instance;
    }
}
