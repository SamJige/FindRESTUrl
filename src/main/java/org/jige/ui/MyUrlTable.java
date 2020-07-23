package org.jige.ui;

import org.jige.bean.ControllerItem;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * 搜索结果菜单显示封装
 * 除了输入框其他的都在这个里面
 */
public interface MyUrlTable {
    /**
     * 初始化菜单
     *
     * @param selectEventAct  选择结果事件 参数是选择了第几行
     * @param searchTextField 输入框控件 放在外面
     */
    JPanel initPanel(Consumer<Integer> selectEventAct, JTextField searchTextField);

    /**
     * 搜索过程中的显示
     */
    void setLoading();

    /**
     * 设置搜索结果
     *
     * @param searchText 输入框的值 用于高亮显示结果
     * @param dataIn     搜索到的结果
     */
    void setListData(String searchText, List<ControllerItem> dataIn);
}