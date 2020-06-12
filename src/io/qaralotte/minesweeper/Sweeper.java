package io.qaralotte.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Sweeper extends JFrame {

    private static final int SIZE = 15;
    private static final int WIDTH = 40 * SIZE;
    private static final int HEIGHT = 40 * SIZE;

    private static final int MINE = 20;

    private JButton[][] btns = new JButton[SIZE + 1][SIZE + 1];
    private HashSet<Pos2> mines = new HashSet<>();

    public Sweeper() {
        super("Mine Sweeper");
        setBounds(600, 300, WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initRandomMine();
        init();
    }

    private void init() {
        JPanel panel = new JPanel();
        GridLayout gridLayout = new GridLayout(SIZE, SIZE);
        panel.setLayout(gridLayout);
        for (int i = 1; i <= SIZE; ++i) {
            JPanel btn_panel = new JPanel();
            for (int j = 1; j <= SIZE; ++j) {
                btns[i][j] = new JButton();
                btns[i][j].setFocusPainted(false);
                btns[i][j].setPreferredSize(new Dimension(WIDTH / SIZE - SIZE / 2, HEIGHT / SIZE - SIZE / 2));
                int x = i, y = j;
                btns[i][j].addActionListener(e -> traverse(x, y));
                btn_panel.add(btns[i][j]);
            }
            panel.add(btn_panel);
        }
        setContentPane(panel);
    }

    private void flushMap() {
        for (int i = 1; i <= SIZE; ++i) {
            for (int j = 1; j <= SIZE; ++j) {
                btns[i][j].setText("");
                btns[i][j].setEnabled(true);
            }
        }
        mines.clear();
        initRandomMine();
        repaint();
    }

    private int checkMine(int start_x, int start_y) {
        int count = 0;
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                if (x == 0 && y == 0) continue;
                if (mines.contains(new Pos2(start_x + x, start_y + y))) {
                    count += 1;
                }
            }
        }
        return count;
    }

    private void traverse(int start_x, int start_y) {
        boolean[][] visited = new boolean[SIZE + 1][SIZE + 1];
        Queue<Pos2> queue = new LinkedList<>();
        queue.add(new Pos2(start_x, start_y));
        while (!queue.isEmpty()) {
            Pos2 pos = queue.poll();
            if (pos.x == 0 || pos.y == 0 || pos.x == SIZE + 1 || pos.y == SIZE + 1) continue;
            if (!btns[pos.x][pos.y].isEnabled()) continue;
            if (visited[pos.x][pos.y]) continue;
            visited[pos.x][pos.y] = true;

            btns[pos.x][pos.y].setEnabled(false);
            if (mines.contains(new Pos2(pos.x, pos.y))) {
                btns[pos.x][pos.y].setText("M");
                JOptionPane.showMessageDialog(this, "你输了");
                flushMap();
            } else {
                int count = checkMine(pos.x, pos.y);
                if (count != 0) {
                    btns[pos.x][pos.y].setText(String.valueOf(count));
                } else {
                    for (int x = -1; x <= 1; ++x) {
                        for (int y = -1; y <= 1; ++y) {
                            if (x == 0 && y == 0) continue;
                            queue.offer(new Pos2(pos.x + x, pos.y + y));
                        }
                    }
                }
            }
        }
        repaint();
    }

    private void initRandomMine() {
        Random rd = new Random(System.currentTimeMillis());
        while (mines.size() < MINE) {
            int absolute_pos = rd.nextInt(SIZE * SIZE);
            mines.add(new Pos2(absolute_pos % SIZE + 1, absolute_pos / SIZE + 1));
        }
    }
}

class Pos2 {
    public int x;
    public int y;

    public Pos2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pos2 pos2 = (Pos2) o;
        return x == pos2.x && y == pos2.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}