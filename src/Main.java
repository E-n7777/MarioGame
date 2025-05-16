import game.StartFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        //确保所有 UI 操作都在事件调度线程中执行
        SwingUtilities.invokeLater(() -> {
            try {
                new StartFrame().setTitle("小猫狂吃金币");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
