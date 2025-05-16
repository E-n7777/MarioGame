package bean;

import javax.swing.*;
import java.awt.*;

import static util.StaticValue.*;
/**
 * description ： 人物类
 * date : 2025/2/26 22:20
 */
public class Character extends JLabel {
    private boolean isJumping = false;
    private boolean isOnPlat = false;
    private Component component;
    private Component brickComponent;

    public Character(Icon image) {
        super(image);
    }

    // 实现跳跃的方法
    public void jump() {
        //不在砖块或惊喜方块上的情况
        if (!isOnPlat()) {
            //向上跳，循环控制速度和跳跃高度
            for (int i = 0; i < 15; i++) {
                //获取人物附近障碍物是什么类型
                component = this.getParent().getComponentAt(mario_x + MARIO_WIDTH/2, mario_y+MARIO_HEIGHT);
                brickComponent = this.getParent().getComponentAt(mario_x+MARIO_WIDTH/2, mario_y-6-1);
                //上方是砖块时无法穿过
                if (brickComponent instanceof Brick) {
                    this.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                    break;
                }
                component = this.getParent().getComponentAt(mario_x + MARIO_WIDTH/2, mario_y+MARIO_HEIGHT);
                brickComponent = this.getParent().getComponentAt(mario_x+MARIO_WIDTH/2, mario_y-MARIO_HEIGHT);
                //更新人物位置
                mario_y -= 6;
                this.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                //控制更新人物位置速度
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //向下跳，循环控制速度和跳跃高度
            for (int i = 0; i < 15; i++) {
                mario_y += 6;
                component = this.getParent().getComponentAt(mario_x + MARIO_WIDTH/2, mario_y + MARIO_HEIGHT);
                //设置下落可以站在砖块或惊喜方块上
                if (component instanceof Brick || component instanceof Surprise) {
                    this.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                    this.setOnPlat(true);
                    break;
                }
                //设置下落到地面停止下落
                if (mario_y+6>GROUND) {
                    this.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                    this.setOnPlat(false);
                    break;
                }
                //更新人物位置
                this.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                //控制更新人物位置速度
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //在砖块或惊喜方块上的情况
            //向上起跳
            for (int i = 0; i < 15; i++) {
                mario_y -= 6;
                this.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                //控制更新人物位置速度
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            component = this.getParent().getComponentAt(mario_x + MARIO_WIDTH/2, mario_y + MARIO_HEIGHT);
            //下落，不能确定下落高度，使用while循环控制何时停止下落
            while (!(component instanceof Brick) && !(component instanceof Surprise) && (mario_y + 6 < GROUND)) {
                mario_y += 6;
                component = this.getParent().getComponentAt(mario_x + MARIO_WIDTH/2, mario_y + MARIO_HEIGHT);
                this.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                //控制更新人物位置速度
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //更新人物是否站在砖块或惊喜方块上的状态
            this.setOnPlat(component instanceof Brick || component instanceof Surprise);
        }
        //更新人物是否正在跳跃的状态
        isJumping = false;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public boolean isOnPlat() {
        return isOnPlat;
    }

    public void setOnPlat(boolean onPlat) {
        isOnPlat = onPlat;
    }
}
