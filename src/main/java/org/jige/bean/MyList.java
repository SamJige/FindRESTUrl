package org.jige.bean;

import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MyList extends BaseListPopupStep<ListItem> {
    public List<ListItem> data;

    public MyList(@Nls(capitalization = Nls.Capitalization.Title) @Nullable String title, List<ListItem> values) {
        super(title, values);
        data = values;
    }

    @NotNull
    @Override
    public String getTextFor(ListItem value) {
        return value.name;
    }

    @Nullable
    @Override
    public PopupStep onChosen(ListItem selectedValue, boolean finalChoice) {
        return super.onChosen(selectedValue, finalChoice);
    }
}
