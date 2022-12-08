package com.paddi.utils;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月08日 16:48:00
 */
public class GolangUtil {

    public static void main(String[] args) {
        int[][] square = new int[15][15];
        square[0][7] = 1;
        square[0][6] = 1;
        square[0][5] = 1;
        square[0][4] = 1;
        square[0][3] = 1;
        square[0][2] = 1;
        for(int i = 0; i < square.length; i++) {
            for(int j = 0; j < square[0].length; j++) {
                System.out.print(square[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(checkWin(square, 0, 5, 1));
    }

    public static boolean checkWin(int[][] square, Integer m, Integer n, Integer golangPiecesType) {
        return countchessX(square, m, n, golangPiecesType) >= 5  || countchessY(square, m, n, golangPiecesType) >= 5
                || countchessM(square, m, n, golangPiecesType) >= 5 || countchessN(square, m, n, golangPiecesType) >= 5;
    }

    /**
     * 判断水平方向
     * @param square
     * @param m
     * @param n
     * @param golangPiecesType
     * @return
     */
    private static int countchessX(int[][] square, Integer m, Integer n, Integer golangPiecesType) {
        // 设置计数器记录同色棋子
        int chess = 1;
        // 往右侧方向
        for (int nextN = n + 1; nextN < square.length; nextN++) {
            if (square[m][nextN] == golangPiecesType) {
                // 如果右侧棋子同色，则计数器加1
                chess++;
            } else {
                // 不同色则退出循环
                break;
            }
        }
        // 往左侧方向
        for (int nextN = n - 1; nextN >= 0; nextN--) {
            if (square[m][nextN] == golangPiecesType) {
                chess++;
            } else {
                break;
            }
        }
        // 返回chess值用于判断输赢
        return chess;

    }

    /**
     * 判断竖直方向
     * @param square
     * @param m
     * @param n
     * @param golangPiecesType
     * @return
     */
    private static int countchessY(int[][] square, Integer m, Integer n, Integer golangPiecesType) {
        int chess = 1;

        for (int nextM = m + 1; nextM < square.length; nextM++) {
            if (square[nextM][n] == golangPiecesType) {
                chess++;
            } else {
                break;
            }
        }
        for (int nextM = m - 1; nextM >= 0; nextM--) {
            if (square[nextM][n] == golangPiecesType) {
                chess++;
            } else {
                break;
            }
        }
        return chess;
    }

    /**
     * 判断左上、右下方向
     * @param square
     * @param m
     * @param n
     * @param golangPiecesType
     * @return
     */
    private static int countchessM(int[][] square, Integer m, Integer n, Integer golangPiecesType) {
        int chess = 1;
        // 右下方向
        for (int nextM = m + 1, nextN = n + 1; nextM < square.length && nextN < square.length; nextM++, nextN++) {
            if (square[nextM][nextN] == golangPiecesType) {
                chess++;
            } else {
                break;
            }
        }
        // 左上方向
        for (int nextM = m - 1, nextN = n - 1; nextM >= 0 && nextN >= 0; nextM--, nextN--) {
            if (square[nextM][nextN] == golangPiecesType) {
                chess++;
            } else {
                break;
            }
        }
        return chess;
    }

    /**
     * 判断右上、左下方向
     * @param square
     * @param m
     * @param n
     * @param golangPiecesType
     * @return
     */
    private static int countchessN(int[][] square, Integer m, Integer n, Integer golangPiecesType) {
        int chess = 1;
        // 右上方向
        for (int nextM = m + 1, nextN = n - 1; nextM < square.length && nextN >= 0; nextM++, nextN--) {
            if (square[nextM][nextN] == golangPiecesType) {
                chess++;
            } else {
                break;
            }
        }
        // 左下方向
        for (int nextM = m - 1, nextN = n + 1; nextM >= 0 && nextN < square.length; nextM--, nextN++) {
            if (square[nextM][nextN] == golangPiecesType) {
                chess++;
            } else {
                break;
            }
        }
        return chess;
    }
}
