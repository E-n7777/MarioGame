package util;

import java.io.*;

/**
 * description ： 关于记录历史最高分的工具类
 * date : 2025/3/5 16:52
 */
public class HistoryUtil {
    private static final String SCORE_FILE = "score.txt";

    //读取历史最高分
    public static int readHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0; //文件不存在时返回0
        }
    }

    // 保存当前分数（覆盖写入）
    public static void saveScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
