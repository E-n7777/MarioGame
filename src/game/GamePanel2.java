package game;

import bean.Brick;
import bean.Character;
import bean.Surprise;
import util.HistoryUtil;
import util.StaticValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static util.Create.*;
import static util.StaticValue.*;

/**
 * description ： 第二关的游戏面板
 * date : 2025/3/1 17:40
 */
public class GamePanel2 extends JLayeredPane implements Runnable {
    private Character character;
    private JLabel scoreLabel;
    private JLabel healthLabel;
    Component component;
    Component stairComponent;

    public GamePanel2() {
        //绝对布局
        setLayout(null);
        //面板获得焦点
        setFocusable(true);
        addKeyListener(new TwoListener(this));

        initGame();
    }

    private void initGame() {
        //人物初始化
        character = new Character(new ImageIcon(new ImageIcon("src/image/mario/mi.jpg").getImage()
                .getScaledInstance(MARIO_WIDTH, MARIO_HEIGHT, Image.SCALE_SMOOTH)));
        character.setOnPlat(false);
        character.setBounds(mario_x = 15, mario_y = 350, MARIO_WIDTH, MARIO_HEIGHT);
        character.setLayout(null);
        add(character);

        //食人花初始化
        flowers.clear();
        add(createFlower(100, GROUND));
        add(createFlower(320, GROUND));
        // 初始化金币
        add(createCoin(400, 210));
        add(createCoin(350, GROUND));
        add(createCoin(560, 250));

        // 初始化分数
        scoreLabel = new JLabel("分数: " + score);
        scoreLabel.setFont(new Font("方正舒体", Font.BOLD, 20));
        scoreLabel.setBounds(15, 20, 100, 20);
        add(scoreLabel);
        // 初始化生命值
        healthLabel = new JLabel("生命值: " + health);
        healthLabel.setFont(new Font("方正舒体", Font.BOLD, 20));
        healthLabel.setBounds(15, 55, 100, 20);
        add(healthLabel);

        // 初始化惊喜方块
        add(createSurprise(290, 230));
        add(createSurprise(210, 300));

        //初始化平台
        createPlat(170, 300, 20, 20, 2);
        createPlat(230, 300, 20, 20, 4);
        createPlat(310, 230, 20, 20, 5);
        for (Brick b : StaticValue.platBricks)
            add(b);

        //初始化楼梯
        createStairs(480, GROUND, 20, 20, 5);
        for (Brick b : StaticValue.stairBricks)
            add(b);
    }

    private class TwoListener implements KeyListener {
        private final GamePanel2 panel;

        private TwoListener(GamePanel2 panel) {
            this.panel = panel;
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            //得到人物附近位置坐标
            component = panel.getComponentAt(mario_x + MARIO_WIDTH/2, mario_y + MARIO_HEIGHT);
            stairComponent = panel.getComponentAt(mario_x + 50, mario_y);
            int key = e.getKeyCode();

            //键盘按下向左键，人物向左移动
            if (key == KeyEvent.VK_LEFT) {
                mario_x -= 10;
                if (mario_x < 0)
                    mario_x = 0; //防止人物走出左边界限
                new Thread(() -> {
                    component = panel.getComponentAt(mario_x + MARIO_WIDTH/2, mario_y + MARIO_HEIGHT);
                    stairComponent = panel.getComponentAt(mario_x + 50, mario_y);
                    //实现人物自由落体，当碰到砖块、惊喜方块、地面时停止下落
                    while (!(component instanceof Brick)
                            && !(component instanceof Surprise)
                            && (mario_y + 6 < GROUND)
                            && !character.isJumping()) {
                        mario_y += 6;
                        component = panel.getComponentAt(mario_x + MARIO_WIDTH/2, mario_y + MARIO_HEIGHT);
                        stairComponent = panel.getComponentAt(mario_x + 50, mario_y);
                        //更新人物位置
                        character.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                        //控制更新人物位置速度
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                    }
                }).start();
            }
            //键盘按下向右键，且右侧不为砖块时，向右移动
            else if (key == KeyEvent.VK_RIGHT && !(stairComponent instanceof Brick)) {
                mario_x += 10;
                //获得人物前方位置坐标，判断前方是否有砖块
                stairComponent = panel.getComponentAt(mario_x + 50, mario_y);
                if (mario_x > StaticValue.WIDTH - MARIO_WIDTH)
                    mario_x = StaticValue.WIDTH - MARIO_WIDTH; //防止人物走出右边界限

                new Thread(() -> {
                    //实现人物自由落体，当碰到砖块、惊喜方块、地面时停止下落
                    while (!(component instanceof Brick) &&
                            !(component instanceof Surprise) &&
                            (mario_y + 6 < GROUND) &&
                            !character.isJumping()) {
                        mario_y += 6;
                        component = panel.getComponentAt(mario_x + MARIO_WIDTH/2, mario_y);
                        stairComponent = panel.getComponentAt(mario_x + 50, mario_y);
                        character.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                        //控制更新人物位置速度
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                    }
                }).start();
            }
            //键盘按下向上键，起跳
            else if (key == KeyEvent.VK_UP) {
                if (!character.isJumping()) {
                    character.setJumping(true);
                    new Thread(() -> character.jump()).start();
                }
            }
            // 更新马里奥的位置
            character.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);

            // 检查是否吃到金币
            for (int i = 0; i < coins.size(); i++) {
                JLabel coin = coins.get(i);
                if (character.getBounds().intersects(coin.getBounds())) {
                    remove(coin); // 移除金币
                    coins.remove(i);
                    score += 10;
                    scoreLabel.setText("分数: " + score);
                    gameOver();
                    break;
                }
            }

            // 检查是否碰到食人花
            for (JLabel flower : flowers) {
                if (character.getBounds().intersects(flower.getBounds())) {
                    health--;
                    healthLabel.setText("生命值: " + health);
                    if (health <= 0) {
                        gameOver();
                    }
                    break;
                }
            }

            // 检查是否碰到 ? 方块
            for (JLabel block : surprise) {
                if (character.getBounds().intersects(block.getBounds())) {
                    block.setText(""); //清空惊喜方块的文字?
                    surprise.remove(block);
                    add(createCoin(block.getX() + 10, block.getY() - 20)); //在惊喜方块上方生成金币
                    break;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    //游戏结束判断条件
    private void gameOver() {
        System.out.println(coins.size());
        if (health <= 0) {
            JOptionPane.showMessageDialog(this, "诶呀，死掉了TAT\n本轮分数是：" + score);
            //记录较高分
            HistoryUtil.saveScore(Math.max(StaticValue.score, HistoryUtil.readHighScore()));
            System.exit(0);
        }
        if (coins.isEmpty() && surprise.isEmpty()) {
            JOptionPane.showMessageDialog(this, "very Newbe!\n恭喜泥已经把所有金币吃掉喽，解锁最高分：" + score);
            //记录较高分
            HistoryUtil.saveScore(score);
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //设置第二关背景图
        g.drawImage(Toolkit.getDefaultToolkit()
                        .getImage("src/image/scene/bg_2.jpg")
                , 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void run() {
        //游戏循环进行
        while (true) {
            repaint();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
