package game;

import util.StaticValue;

import javax.swing.*;

/**
 * description ： 游戏启动界面
 * date : 2025/2/25 23:59
 */
public class StartFrame extends JFrame {
    public StartFrame() {
        //设置窗体宽高属性
        this.setSize(StaticValue.WIDTH, StaticValue.HEIGHT);
        //关闭窗口语句
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗口可见
        this.setVisible(true);

        //创建游戏面板对象，并添加到窗体上去
        StartPanel startPanel = new StartPanel();
        add(startPanel);
    }
}
