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
 * description ： 第一关的游戏面板
 * date : 2025/2/26 15:21
 */
public class GamePanel extends JLayeredPane implements Runnable {
    private Character character;
    private JLabel scoreLabel;
    private JLabel healthLabel;
    private final Change change;
    Component component;

    //游戏面板初始化
    public GamePanel(Change change) {
        //绝对布局
        setLayout(null);
        //面板获得焦点
        setFocusable(true);
        addKeyListener(new OneListener(this));
        this.change = change;

        initGame();
    }

    private void initGame() {
        //人物初始化
        character = new Character(new ImageIcon(new ImageIcon("src/image/mario/mi.jpg").getImage()
                .getScaledInstance(MARIO_WIDTH, MARIO_HEIGHT, Image.SCALE_SMOOTH)));
        character.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
        character.setLayout(null);
        add(character);

        //食人花初始化
        add(createFlower(300, GROUND));
        add(createFlower(450, GROUND));
        // 初始化金币
        add(createCoin(200, GROUND));
        add(createCoin(500, GROUND));

        // 初始化分数
        scoreLabel = new JLabel("分数: " + score);
        scoreLabel.setFont(new Font("方正舒体", Font.BOLD, 20));
        scoreLabel.setBounds(15, 20, 100, 20);
        add(scoreLabel);
        //初始化生命值
        healthLabel = new JLabel("生命值: " + health);
        healthLabel.setFont(new Font("方正舒体", Font.BOLD, 20));
        healthLabel.setBounds(15, 55, 100, 20);
        add(healthLabel);

        // 初始化惊喜方块
        add(createSurprise(140, 300));
        add(createSurprise(280, 300));
        add(createSurprise(320, 300));
        add(createSurprise(300, 230));
        //初始化砖块
        add(createBrick(260, 300));
        add(createBrick(300, 300));
        add(createBrick(340, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //设置第一关背景图
        g.drawImage(Toolkit.getDefaultToolkit()
                        .getImage("src/image/scene/bg_1.jpg")
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

    //监听键盘点击事件的内部类
    private class OneListener implements KeyListener {
        private final GamePanel panel;

        public OneListener(GamePanel panel) {
            this.panel = panel;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            //键盘按下向左键
            if (key == KeyEvent.VK_LEFT) {
                mario_x -= 10;
                if (mario_x < 0)
                    mario_x = 0; //防止人物走出左边界限
                new Thread(() -> {
                    component = panel.getComponentAt(mario_x + 10, mario_y + MARIO_HEIGHT);
                    //实现人物自由落体，当碰到砖块、惊喜方块、地面时停止下落
                    while (!(component instanceof Brick)
                            && !(component instanceof Surprise)
                            && (mario_y + 6 < GROUND)
                            && !character.isJumping()) {
                        mario_y += 6;
                        component = panel.getComponentAt(mario_x + 10, mario_y + MARIO_HEIGHT);
                        character.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                        //控制更新人物位置速度
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                    }
                    character.setOnPlat(component instanceof Brick || component instanceof Surprise);
                }).start();
            }
            //键盘按下向右键
            else if (key == KeyEvent.VK_RIGHT) {
                mario_x += 10;
                if (mario_x > StaticValue.WIDTH - MARIO_WIDTH)
                    mario_x = StaticValue.WIDTH - MARIO_WIDTH; //防止人物走出右边界限
                new Thread(() -> {
                    component = panel.getComponentAt(mario_x + 10, mario_y + MARIO_HEIGHT);
                    //实现人物自由落体，当碰到砖块、惊喜方块、地面时停止下落
                    while (!(component instanceof Brick) &&
                            !(component instanceof Surprise) &&
                            (mario_y + 6 < GROUND) &&
                            !character.isJumping()) {
                        mario_y += 6;
                        component = panel.getComponentAt(mario_x + 10, mario_y + MARIO_HEIGHT);
                        character.setBounds(mario_x, mario_y, MARIO_WIDTH, MARIO_HEIGHT);
                        //控制更新人物位置速度
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }
                    }
                    character.setOnPlat(component instanceof Brick || component instanceof Surprise);
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
                    block.setText(""); // 清空惊喜方块的文字?
                    surprise.remove(block);
                    add(createCoin(block.getX() + 10, block.getY() - 20)); // 在惊喜方块上方生成金币
                    break;
                }
            }
            // 检查是否满足切换关卡的条件
            if (level == 1 && coins.isEmpty() && surprise.isEmpty()) {
                level++;
                change.change();
            }
            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    private void gameOver() {
        JOptionPane.showMessageDialog(this, "诶呀，死掉了TAT\n本轮分数是：" + score);
        // 保存当前分数
        HistoryUtil.saveScore(Math.max(StaticValue.score, HistoryUtil.readHighScore()));
        System.exit(0);
    }
}
