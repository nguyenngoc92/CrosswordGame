package org.com.myapp.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.com.myapp.model.Position;

public class CrossWordFactory {

	private final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private int[] dirX = { 0, 1 };
	private int[] dirY = { 1, 0 };

	private String[][] board;
	private String[][] tmp;

	private int[][] hWords;
	private int[][] vWords;
	private int n;
	private int m;

	int hCount, vCount;

	private Random _rand;

	public CrossWordFactory(int wDimension, int hDimension) {

		board = new String[wDimension][hDimension];
		tmp = new String[wDimension][hDimension];
		hWords = new int[wDimension][hDimension];
		vWords = new int[wDimension][hDimension];

		n = wDimension;
		m = hDimension;
		_rand = new Random();

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				board[i][j] = " ";
			}
		}

	}

	@Override
	public String toString() {

		String result = "";
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				result += letters.contains((CharSequence) board[i][j]) ? board[i][j]
						: " ";
			}

			if (i < n - 1)
				result += "\n";
		}

		return result;
	}

	public boolean isLetter(Character c) {

		return letters.contains(c.toString());
	}

	public boolean isValidPosition(int x, int y) {
		return x >= 0 && y >= 0 && x < n && y < m;
	}

	// check if can be place a word
	public int canBePlaced(String word, int x, int y, int dir) {

		int result = 0;
		if (dir == 0) {

			for (int j = 0; j < word.length(); j++) {

				int x1 = x, y1 = y + j;
				if (!(isValidPosition(x1, y1) && (board[x1][y1] == " " || board[x1][y1] == word
						.substring(j, j + 1)))) {
					return -1;
				}

				if (isValidPosition(x1 - 1, y1)) {
					if (hWords[x1 - 1][y1] > 0)
						return -1;
				}
				if (isValidPosition(x1 + 1, y1)) {
					if (hWords[x1 + 1][y1] > 0)
						return -1;
				}

				if (board[x1][y1] == word.substring(j, j + 1))
					result++;
			}
		} else {

			for (int j = 0; j < word.length(); j++) {

				int x1 = x + j, y1 = y;

				if (!(isValidPosition(x1, y1) && (board[x1][y1] == " " || board[x1][y1] == word
						.substring(j, j + 1))))
					return -1;

				if (isValidPosition(x1, y1 - 1)) {
					if (vWords[x1][y1 - 1] > 0)
						return -1;
				}

				if (isValidPosition(x1, y1 + 1)) {
					if (vWords[x1][y1 + 1] > 0)
						return -1;
				}

				if (board[x1][y1] == word.substring(j, j + 1))
					result++;
			}
		}

		int xStar = x - dirX[dir], yStar = y - dirY[dir];

		if (isValidPosition(xStar, yStar)) {
			if (!(board[xStar][yStar] == " " || board[xStar][yStar] == "*"))
				return -1;
		}

		xStar = x + dirX[dir] * word.length();
		yStar = y + dirY[dir] * word.length();
		if (isValidPosition(xStar, yStar))
			if (!(board[xStar][yStar] == " " || board[xStar][yStar] == "*"))
				return -1;

		return result == word.length() ? -1 : result;

	}

	// find positions to put a word
	public List<Position> findPositions(String word) {

		int max = 0;
		List<Position> positions = new ArrayList<Position>();

		for (int x = 0; x < n; x++) {
			for (int y = 0; y < m; y++) {
				for (int i = 0; i < dirX.length; i++) {
					int dir = i;

					int count = canBePlaced(word, x, y, dir);

					if (count < max)
						continue;
					if (count > max)
						positions.clear();

					max = count;
					positions.add(new Position(x, y, dir));
				}
			}
		}

		return positions;

	}

	// find a best position to put a word
	public Position bestPosition(String word) {
		List<Position> positions = findPositions(word);
		if (positions.size() > 0) {
			int index = _rand.nextInt(positions.size());
			return positions.get(index);
		}

		return null;
	}

	// add a word into board
	public Position addWord(String word) {

		Position info = bestPosition(word);
		if (info != null) {
			if (info.getDir() == 0) {
				hCount++;

			} else
				vCount++;

			int value = info.getDir() == 0 ? hCount : vCount;
			putWord(word, info, value);

			// /////////////////////////////////////////////////////////////////////////////
			System.out.println("x: " + info.getX() + " y: " + info.getY()
					+ " dir: " + info.getDir());

			return info;
		}

		return null;
	}

	// put a word
	public void putWord(String word, Position p, int value) {

		int[][] mat = p.getDir() == 0 ? hWords : vWords;
		for (int i = 0; i < word.length(); i++) {
			int x1 = p.getX() + dirX[p.getDir()] * i;
			int y1 = p.getY() + dirY[p.getDir()] * i;
			board[x1][y1] = word.substring(i, i + 1);
			mat[x1][y1] = value;
		}

		int xStar = p.getX() - dirX[p.getDir()];
		int yStar = p.getY() - dirY[p.getDir()];
		if (isValidPosition(xStar, yStar))
			board[xStar][yStar] = "*";

		xStar = p.getX() + dirX[p.getDir()] * word.length();
		yStar = p.getY() + dirY[p.getDir()] * word.length();

		if (isValidPosition(xStar, yStar))
			board[xStar][yStar] = "*";

	}

	public void reset() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				board[i][j] = " ";
				vWords[i][j] = 0;
				hWords[i][j] = 0;
				hCount = vCount = 0;
			}
		}
	}

	public int freeSpaces() {
		int count = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (board[i][j] == " " || board[i][j] == "*")
					count++;
			}
		}

		return count;
	}

	public String[][] getBoard() {

		String[][] b = new String[n][m];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {

				b[i][j] = letters.contains((CharSequence) board[i][j]) ? board[i][j]
						: null;
			}
		}
		return b;
	}

	public String[][] getTmp() {

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {

				tmp[i][j] = letters.contains((CharSequence) board[i][j]) ? " "
						: null;
			}
		}
		return tmp;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public String tempToString() {
		String result = "";
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {

				result += tmp[i][j] == null ? "*" : " ";
			}

			if (i < n - 1)
				result += "\n";
		}

		return result;
	}

}
