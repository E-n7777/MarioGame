package game;

import util.HistoryUtil;
import util.StaticValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * description ： 首页的 Panel
 * date : 2025/2/27 16:26
 */
public class StartPanel extends JPanel implements KeyListener {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //设置Panel的背景图
        g.drawImage(Toolkit.getDefaultToolkit()
                        .getImage("src/image/scene/bg_Start.jpg")
                , 0, 0, getWidth(), getHeight(), this);
    }

    //Panel构造函数
    public StartPanel() {
        //绝对布局
        setLayout(null);
        //设置焦点
        setFocusable(true);
        //设置监听器
        addKeyListener(this);
        setPreferredSize(new Dimension(StaticValue.WIDTH, StaticValue.HEIGHT));
        //历史分数展示
        JLabel history = new JLabel("历史最高分为: " + HistoryUtil.readHighScore());
        history.setFont(new Font("方正舒体", Font.BOLD, 24));
        history.setBounds(250, 300, 200, 30);
        add(history);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //回车切换到游戏关卡界面
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                try {
                    new GameFrame();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
