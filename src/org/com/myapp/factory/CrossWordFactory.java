package org.com.myapp.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.com.myapp.model.Position;

public class CrossWordFactory {

	private final String letters = "abcdefghijklmnopqrstuvwxyz";

	private int[] dirX = { 0, 1 };
	private int[] dirY = { 1, 0 };

	char[][] board;

	private int[][] hWords;
	private int[][] vWords;
	private int n;
	private int m;

	int hCount, vCount;

	private Random _rand;

//	private List<Word> _wordsToInsert;
	//private char[][] tempBoard;
	//private static int beastSol;
	//private long initialTime;

	public CrossWordFactory(int wDimension, int hDimension) {

		board = new char[wDimension][hDimension];
		hWords = new int[wDimension][hDimension];
		vWords = new int[wDimension][hDimension];

		n = wDimension;
		m = hDimension;
		_rand = new Random();

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				board[i][j] = ' ';
			}
		}

	}

	@Override
	public String toString() {

		String result = "";
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				result += letters.contains(Character.toString(board[i][j])) ? board[i][j]
						: '*';
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
				if (!(isValidPosition(x1, y1) && (board[x1][y1] == ' ' || board[x1][y1] == word
						.charAt(j)))) {
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

				if (board[x1][y1] == word.charAt(j))
					result++;
			}
		} else {

			for (int j = 0; j < word.length(); j++) {

				int x1 = x + j, y1 = y;

				if (!(isValidPosition(x1, y1) && (board[x1][y1] == ' ' || board[x1][y1] == word
						.charAt(j))))
					return -1;

				if (isValidPosition(x1, y1 - 1)) {
					if (vWords[x1][y1 - 1] > 0)
						return -1;
				}

				if (isValidPosition(x1, y1 + 1)) {
					if (vWords[x1][y1 + 1] > 0)
						return -1;
				}

				if (board[x1][y1] == word.charAt(j))
					result++;
			}
		}

		int xStar = x - dirX[dir], yStar = y - dirY[dir];

		if (isValidPosition(xStar, yStar)) {
			if (!(board[xStar][yStar] == ' ' || board[xStar][yStar] == '*'))
				return -1;
		}

		xStar = x + dirX[dir] * word.length();
		yStar = y + dirY[dir] * word.length();
		if (isValidPosition(xStar, yStar))
			if (!(board[xStar][yStar] == ' ' || board[xStar][yStar] == '*'))
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

	/*
	 * public void gen(int pos) {
	 * 
	 * if (pos >= _wordsToInsert.size() || (System.currentTimeMillis() -
	 * initialTime > 60000)) return;
	 * 
	 * for (int i = pos; i < _wordsToInsert.size(); i++) { Position posi =
	 * bestPosition(_wordsToInsert.get(i).getItem() .getAnswer()); if (posi !=
	 * null) {
	 * 
	 * int value = posi.getDir() == 0 ? hCount : vCount;
	 * putWord(_wordsToInsert.get(i).getItem().getAnswer(), posi, value);
	 * gen(pos + 1);
	 * 
	 * removeWord(_wordsToInsert.get(i).getItem().getAnswer(), posi); } else {
	 * gen(pos + 1); } }
	 * 
	 * int c = freeSpaces(); if (c >= beastSol) return; beastSol = c; tempBoard
	 * = board.clone();
	 * 
	 * }
	 * 
	 * private void removeWord(String word, Position posi) {
	 * 
	 * int mat[][] = posi.getDir() == 0 ? hWords : vWords; int mat1[][] =
	 * posi.getDir() == 0 ? vWords : hWords;
	 * 
	 * for (int i = 0; i < word.length(); i++) { int x1 = posi.getX() +
	 * dirX[posi.getDir()] * i; int y1 = posi.getY() + dirY[posi.getDir()] * i;
	 * 
	 * if (mat1[x1][y1] == 0) board[x1][y1] = ' '; mat[x1][y1] = 0; }
	 * 
	 * int xStar = posi.getX() - dirX[posi.getDir()]; int yStar = posi.getY() -
	 * dirY[posi.getDir()];
	 * 
	 * if (isValidPosition(xStar, yStar) && hasFactibleValueAround(xStar,
	 * yStar)) board[xStar][yStar] = ' '; }
	 * 
	 * private boolean hasFactibleValueAround(int x, int y) { for (int i = 0; i
	 * < dirX.length; i++) { int x1 = x + dirX[i], y1 = y + dirY[i]; if
	 * (isValidPosition(x1, y1) && (board[x1][y1] != ' ' || board[x1][y1] ==
	 * '*')) return true; x1 = x - dirX[i]; y1 = y - dirY[i]; if
	 * (isValidPosition(x1, y1) && (board[x1][y1] != ' ' || board[x1][y1] ==
	 * '*')) return true;
	 * 
	 * } return false; }
	 */
	// put a word
	public void putWord(String word, Position p, int value) {

		int[][] mat = p.getDir() == 0 ? hWords : vWords;
		for (int i = 0; i < word.length(); i++) {
			int x1 = p.getX() + dirX[p.getDir()] * i;
			int y1 = p.getY() + dirY[p.getDir()] * i;
			board[x1][y1] = word.charAt(i);
			mat[x1][y1] = value;
		}

		int xStar = p.getX() - dirX[p.getDir()];
		int yStar = p.getY() - dirY[p.getDir()];
		if (isValidPosition(xStar, yStar))
			board[xStar][yStar] = '*';

		xStar = p.getX() + dirX[p.getDir()] * word.length();
		yStar = p.getY() + dirY[p.getDir()] * word.length();

		if (isValidPosition(xStar, yStar))
			board[xStar][yStar] = '*';

	}

	public void reset() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				board[i][j] = ' ';
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
				if (board[i][j] == ' ' || board[i][j] == '*')
					count++;
			}
		}

		return count;
	}

	public char[][] getBoard() {
		return board;
	}

	public int[] getDirX() {
		return dirX;
	}

	public void setDirX(int[] dirX) {
		this.dirX = dirX;
	}

	public int[] getDirY() {
		return dirY;
	}

	public void setDirY(int[] dirY) {
		this.dirY = dirY;
	}

	public int[][] gethWords() {
		return hWords;
	}

	public void sethWords(int[][] hWords) {
		this.hWords = hWords;
	}

	public int[][] getvWords() {
		return vWords;
	}

	public void setvWords(int[][] vWords) {
		this.vWords = vWords;
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

	public int gethCount() {
		return hCount;
	}

	public void sethCount(int hCount) {
		this.hCount = hCount;
	}

	public int getvCount() {
		return vCount;
	}

	public void setvCount(int vCount) {
		this.vCount = vCount;
	}

	public Random get_rand() {
		return _rand;
	}

	public void set_rand(Random _rand) {
		this._rand = _rand;
	}





	public String getLetters() {
		return letters;
	}

}
