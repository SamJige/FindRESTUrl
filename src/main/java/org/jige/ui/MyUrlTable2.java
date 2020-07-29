package org.jige.ui;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jige.bean.ControllerItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class MyUrlTable2 implements MyUrlTable {
    JBTable showResultTable = new JBTable();
    JBScrollPane jScrollPanel = new JBScrollPane();
    JPanel panel;

    public JPanel initPanel(Consumer<Integer> selectEventAct, JTextField searchTextField) {
        String[] head = new String[]{"URL", "PATH", "METHOD"};
        if (panel != null) {
            return panel;
        }
        panel = new JPanel(new BorderLayout());
        /////////////////////////////////////////////////////////////
        // 构造弹出框 使用netbeans构造再copy
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        springLayout.putConstraint(SpringLayout.NORTH, searchTextField, 0, SpringLayout.NORTH, panel);
        springLayout.putConstraint(SpringLayout.WEST, searchTextField, 0, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.SOUTH, searchTextField, 21, SpringLayout.NORTH, panel);
        springLayout.putConstraint(SpringLayout.EAST, searchTextField, 0, SpringLayout.EAST, panel);
        panel.add(searchTextField);
//        searchTextField.setColumns(10);

        springLayout.putConstraint(SpringLayout.NORTH, jScrollPanel, 0, SpringLayout.SOUTH, searchTextField);
        springLayout.putConstraint(SpringLayout.WEST, jScrollPanel, 0, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.SOUTH, jScrollPanel, 0, SpringLayout.SOUTH, panel);
        springLayout.putConstraint(SpringLayout.EAST, jScrollPanel, 0, SpringLayout.EAST, panel);
        panel.add(jScrollPanel);

        //监听点击事件
        showResultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selected = showResultTable.getSelectedRow();
                selectEventAct.accept(selected);
            }
        });
        showResultTable.setShowGrid(false);
        showResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showResultTable.setFillsViewportHeight(true);
        showResultTable.setModel(new DefaultTableModel(new Object[][]{{"loading...", "", ""},}, head));
        jScrollPanel.setViewportView(showResultTable);
        /////////////////////////////////////////////////////////////
        panel.setPreferredSize(new Dimension(700, 300)); //使用该方法
        return panel;
    }

    public void setLoading() {
        String[] head = new String[]{"URL", "PATH", "METHOD"};
        showResultTable.setModel(new DefaultTableModel(new Object[][]{
                {"loading...", "", ""},}, head));
    }

    public void setListData(String searchText, List<ControllerItem> dataIn) {
        String[] head = new String[]{"URL", "PATH", "METHOD"};
        //todo 关键字高亮
        Object[][] dataArr = dataIn.stream()
                .map(it -> new Object[]{
                        it.url, it.fileWithPath, it.className + "." + it.methodName
                })
                .toArray(Object[][]::new);

        showResultTable.setModel(new DefaultTableModel(dataArr, head));
    }
}
