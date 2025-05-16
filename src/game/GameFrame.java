package game;

import util.StaticValue;

import javax.swing.*;
import java.awt.*;
/**
 * description ： 游戏关卡界面
 * date : 2025/2/25 23:50
 */
public class GameFrame extends JFrame {
    private final Container container = this.getContentPane();

    public GameFrame() {
        //设置关卡窗口属性
        setSize(StaticValue.WIDTH, StaticValue.HEIGHT);
        setTitle("小猫狂吃金币");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //可以被看见
        setVisible(true);

        if (StaticValue.level == 1)
            //加入第一关的Panel
            this.add(new GamePanel(()-> {
                //实现GamePanel的接口方法，设置切换关卡条件
                SwingUtilities.invokeLater(() -> {
                    container.removeAll();
                    container.add(new GamePanel2());
                    container.revalidate();
                    container.repaint();
                    //切换到第二关出现的弹窗
                    JOptionPane.showMessageDialog(this, "恭喜过关，欢迎来到第二关^_^");
                });
            }));
        else if (StaticValue.level == 2)
            this.add(new GamePanel2());
    }
}
