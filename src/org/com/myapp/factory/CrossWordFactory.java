package org.com.myapp.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.com.myapp.model.Cell;
import org.com.myapp.model.CellNode;
import org.com.myapp.model.Direction;
import org.com.myapp.model.Position;
import org.com.myapp.model.Word;

public class CrossWordFactory {

	private int GRID_ROWS = 50;
	private int GRID_COLS = 50;

	private Cell[][] grid;

	private ArrayList<Word> bad_words = new ArrayList<Word>();

	private ArrayList<Word> words = new ArrayList<Word>();
	private HashMap<String, ArrayList<Position>> letterMap = new HashMap<String, ArrayList<Position>>();

	private final int across = 0;
	private final int down = 1;

	public CrossWordFactory(ArrayList<Word> words) {

		this.words = words;
		this.createGrid();
	}

	// pick best grid from 10 grids
	public Cell[][] pickBestGrid() {

		int index = 0;
		int size = this.GRID_ROWS;
		ArrayList<Cell[][]> listGrid = new ArrayList<Cell[][]>();
		for (int i = 0; i < 10; i++) {

			Cell[][] gridTemp = this.getSqareGrid(10);
			listGrid.add(gridTemp);

			if (gridTemp != null && size > gridTemp.length) {
				size = gridTemp.length;
				index = i;
			}
		}

		Cell[][] bestGrid = listGrid.get(index);

		this.updatePositionForWord(bestGrid);
		return bestGrid;
	}

	private void updatePositionForWord(Cell[][] bestGrid) {

		int order = 1;
		for (int r = 0; r < bestGrid.length; r++) {
			for (int c = 0; c < bestGrid[0].length; c++) {

				Cell cell = bestGrid[r][c];
				if (cell.getCellNode() != null
						&& cell.getCellNode().isStartOfWord()
						&& cell.getLetter() != null) {
					List<Integer> indexList = cell.getCellNode().getIndexList();
					cell.getCellNode().setOrder(order);
					order++;
					for (int index : indexList) {

						for (int i = 0; i < words.size(); i++) {
							String answer = words.get(i).getAnswer();

							if (index == words.get(i).getIndex()) {

								Word tempWord = null;

								if (r + answer.length() - 1 < bestGrid.length) {

									Cell maxRowCell = bestGrid[r
											+ answer.length() - 1][c];

									if (maxRowCell.getLetter() != null
											&& maxRowCell
													.getLetter()
													.equalsIgnoreCase(
															answer.charAt(answer
																	.length() - 1)
																	+ "")) {

										boolean check = true;
										for (int j = 0; j < answer.length(); j++) {
											Cell tmpCell = bestGrid[r + j][c];
											if (!(tmpCell.getLetter() != null && tmpCell
													.getLetter()
													.equalsIgnoreCase(
															answer.charAt(j)
																	+ ""))) {
												check = false;
											}
										}

										if (check) {
											tempWord = words.get(i);
											Position p = new Position(r, c,
													Direction.DOWN);
											words.get(i).setPosition(p);
										}

									}
								}
								if (c + answer.length() - 1 < bestGrid[0].length
										&& tempWord == null) {

									Cell maxColCell = bestGrid[r][c
											+ answer.length() - 1];
									if (maxColCell.getLetter() != null
											&& maxColCell
													.getLetter()
													.equalsIgnoreCase(
															answer.charAt(answer
																	.length() - 1)
																	+ "")) {

										Position p = new Position(r, c,
												Direction.ACROSS);
										words.get(i).setPosition(p);
									}
								}
							}
						}

					}

				}

			}
		}

	}

	private Cell[][] getSqareGrid(int maxTries) {

		Cell[][] best_grid = null;
		double best_ratio = 0;
		for (int i = 0; i < maxTries; i++) {

			Cell[][] aGrid = this.getGrid(1);
			if (aGrid == null)
				continue;

			double ratio = Math.min(aGrid.length, aGrid[0].length) * 1.0
					/ Math.max(aGrid.length, aGrid[0].length);

			if (ratio > best_ratio) {
				best_grid = aGrid;
				best_ratio = ratio;
			}

			if (best_ratio == 1)
				break;

		}

		return best_grid;

	}

	private Cell[][] getGrid(int maxTries) {

		ArrayList<ArrayList<Word>> groups = new ArrayList<ArrayList<Word>>();
		for (int tries = 0; tries < maxTries; tries++) {

			this.clearGrid();

			// place the first word in the middle os the grid
			Direction startDir = this.randomDirection();

			int r = this.GRID_ROWS / 2;
			int c = this.GRID_COLS / 2;

			Word wordElement = words.get(0);

			if (startDir == Direction.ACROSS) {

				c -= wordElement.getAnswer().length() / 2;
			} else {
				r -= wordElement.getAnswer().length() / 2;
			}

			if (canPlaceWordAt(wordElement.getAnswer(), r, c, startDir) != -1) {
				this.placeWordAt(wordElement.getAnswer(),
						wordElement.getIndex(), r, c, startDir);

			} else {
				bad_words.add(wordElement);

				return null;
			}

			// start with a group containing all the words (except the first)
			// as we go, we try to place each word in the group onto the grid
			// if the word can't go on the grid, we add that word to the next
			// group

			ArrayList<Word> elements = new ArrayList<Word>();
			elements.addAll(words);
			elements.remove(0);

			groups.add(elements);

			boolean check = true;
			for (int g = 0; g < groups.size(); g++) {

				boolean wordAddedToGrid = false;

				ArrayList<Word> list = groups.get(g);

				for (int i = 0; i < list.size(); i++) {

					Word element = list.get(i);

					HashMap<String, Integer> bestPosition = this
							.findPositionForWord(element.getAnswer());

					if (bestPosition == null) {

						// make the new group (if needed)
						if (groups.size() - 1 == g)
							groups.add(new ArrayList<Word>());
						// place the word in the next group
						groups.get(g + 1).add(element);

					} else {

						int rElement = bestPosition.get("row");
						int cElement = bestPosition.get("col");
						int dir = bestPosition.get("direction");

						Direction direction = dir == across ? Direction.ACROSS
								: Direction.DOWN;
						this.placeWordAt(element.getAnswer(),
								element.getIndex(), rElement, cElement,
								direction);
						wordAddedToGrid = true;
					}

				}
				// if we haven't made any progress, there is no point in going
				// on to the next group
				if (!wordAddedToGrid) {
					check = wordAddedToGrid;
					break;
				}
			}

			if (check)
				return this.minimizeGrid();

		}

		this.bad_words = groups.get(groups.size() - 1);
		return null;

	}

	private void placeWordAt(String word, int indexWord, int row, int col,
			Direction direction) {

		if (direction == Direction.ACROSS) {

			for (int c = col, i = 0; c < col + word.length(); c++, i++) {
				this.addCellToGrid(word, indexWord, i, row, c, direction);
			}

		} else {
			for (int r = row, i = 0; r < row + word.length(); r++, i++) {
				this.addCellToGrid(word, indexWord, i, r, col, direction);
			}

		}
	}

	// move the grid onto the smallest grid that will fit it
	private Cell[][] minimizeGrid() {
		// find bounds
		int r_min = GRID_ROWS - 1, r_max = 0, c_min = GRID_COLS - 1, c_max = 0;
		for (int r = 0; r < GRID_ROWS; r++) {
			for (int c = 0; c < GRID_COLS; c++) {
				Cell cell = grid[r][c];
				if (cell.getLetter() != null) {
					if (r < r_min)
						r_min = r;
					if (r > r_max)
						r_max = r;
					if (c < c_min)
						c_min = c;
					if (c > c_max)
						c_max = c;
				}
			}
		}
		// initialize new grid
		int rows = r_max - r_min + 1;
		int cols = c_max - c_min + 1;
		Cell[][] newGrid = new Cell[rows][cols];

		// copy the grid onto the smaller grid
		for (int r = r_min, r2 = 0; r2 < rows; r++, r2++) {
			for (int c = c_min, c2 = 0; c2 < cols; c++, c2++) {
				newGrid[r2][c2] = grid[r][c];
			}
		}

		return newGrid;
	}

	private void addCellToGrid(String word, int indexWord, int i, int r, int c,
			Direction direction) {

		String letter = word.charAt(i) + "";
		if (grid[r][c].getLetter() == null) {

			grid[r][c].setLetter(letter);
			// add to index

			if (letterMap.get(letter) == null)
				letterMap.put(letter, new ArrayList<Position>());

			letterMap.get(letter).add(new Position(r, c));

		}

		boolean isStartOfWord = i == 0;
		if (grid[r][c].getCellNode() == null) {
			grid[r][c].setCellNode(new CellNode(isStartOfWord));
		} else {
			if (grid[r][c].getCellNode().isStartOfWord() != true) {
				grid[r][c].getCellNode().setStartOfWord(isStartOfWord);
			}
		}
		this.grid[r][c].getCellNode().addIndex(indexWord);

	}

	private HashMap<String, Integer> findPositionForWord(String word) {

		// check the char_index for every letter, and see if we can put it there
		// in a direction
		ArrayList<HashMap<String, Integer>> bests = new ArrayList<HashMap<String, Integer>>();

		for (int i = 0; i < word.length(); i++) {
			List<Position> positions = letterMap.get(word.charAt(i) + "");
			if (positions == null)
				continue;

			for (int j = 0; j < positions.size(); j++) {

				Position position = positions.get(j);
				int r = position.getR();
				int c = position.getC();

				// the c - i, and r - i here compensate for the offset of
				// character in the word

				int intersections_across = canPlaceWordAt(word, r, c - i,
						Direction.ACROSS);
				int intersections_down = canPlaceWordAt(word, r - i, c,
						Direction.DOWN);

				if (intersections_across != -1) {

					HashMap<String, Integer> map = new HashMap<String, Integer>();
					map.put("intersections", intersections_across);
					map.put("row", r);
					map.put("col", c - i);
					map.put("direction", this.across);
					bests.add(map);
				}

				if (intersections_down != -1) {

					HashMap<String, Integer> map = new HashMap<String, Integer>();
					map.put("intersections", intersections_down);
					map.put("row", r - i);
					map.put("col", c);
					map.put("direction", this.down);
					bests.add(map);
				}
			}
		}

		if (bests.size() == 0)
			return null;
		// find a good random position
		int index = new Random().nextInt(bests.size());
		return bests.get(index);

	}

	private int canPlaceWordAt(String word, int row, int col,
			Direction direction) {

		// out of bounds
		if (row < 0 || row >= grid.length || col < 0 || col >= grid[row].length)
			return -1;

		if (direction == Direction.ACROSS) {

			// out of bounds (word too long)
			if (col + word.length() > grid[row].length)
				return -1;
			// can't have a word directly to the left
			if (col - 1 >= 0 && grid[row][col - 1].getLetter() != null)
				return -1;
			// can't have word directly to the right
			if (col + word.length() < grid[row].length
					&& grid[row][col + word.length()].getLetter() != null)
				return -1;

			// check the row above to make sure there isn't another word
			// running parallel. It is ok if there is a character above, only if
			// the character below it intersects with the current word
			for (int r = row - 1, c = col, i = 0; r >= 0
					&& c < col + word.length(); c++, i++) {
				boolean is_empty = grid[r][c].getLetter() == null;
				boolean is_intersection = grid[row][c].getLetter() != null
						&& grid[row][c].getLetter().equals(word.charAt(i) + "");
				boolean can_place_here = is_empty || is_intersection;
				if (!can_place_here)
					return -1;
			}

			// same deal as above, we just search in the row below the word
			for (int r = row + 1, c = col, i = 0; r < grid.length
					&& c < col + word.length(); c++, i++) {
				boolean is_empty = grid[r][c].getLetter() == null;
				boolean is_intersection = grid[row][c].getLetter() != null
						&& grid[row][c].getLetter().equals(word.charAt(i) + "");
				boolean can_place_here = is_empty || is_intersection;
				if (!can_place_here)
					return -1;
			}

			// check to make sure we aren't overlapping a char (that doesn't
			// match)
			// and get the count of intersections
			int intersections = 0;
			for (int c = col, i = 0; c < col + word.length(); c++, i++) {
				int result = canPlaceLetterAt(word.charAt(i) + "", row, c);
				if (result == -1)
					return -1;
				intersections += result;
			}

			return intersections;

		} else {

			// out of bounds
			if (row + word.length() > grid.length)
				return -1;

			// can't have a word directly above
			if (row - 1 >= 0 && grid[row - 1][col].getLetter() != null)
				return -1;
			// can't have a word directly below
			if (row + word.length() < grid.length
					&& grid[row + word.length()][col].getLetter() != null)
				return -1;

			// check the column to the left to make sure there isn't another
			// word running parallel. It is ok if there is a character to the
			// left, only if the character to the right intersects with the
			// current word
			for (int c = col - 1, r = row, i = 0; c >= 0
					&& r < row + word.length(); r++, i++) {
				boolean is_empty = grid[r][c].getLetter() == null;
				boolean is_intersection = grid[r][col].getLetter() != null
						&& grid[r][col].getLetter().equals(word.charAt(i) + "");
				boolean can_place_here = is_empty || is_intersection;
				if (!can_place_here)
					return -1;
			}

			// same deal, but look at the column to the right
			for (int c = col + 1, r = row, i = 0; r < row + word.length()
					&& c < grid[r].length; r++, i++) {
				boolean is_empty = grid[r][c].getLetter() == null;
				boolean is_intersection = grid[r][col].getLetter() != null
						&& grid[r][col].getLetter().equals(word.charAt(i) + "");
				boolean can_place_here = is_empty || is_intersection;
				if (!can_place_here)
					return -1;
			}

			// check to make sure we aren't overlapping a char (that doesn't
			// match)
			// and get the count of intersections
			int intersections = 0;
			for (int r = row, i = 0; r < row + word.length(); r++, i++) {
				int result = canPlaceLetterAt(word.charAt(i) + "", r, col);
				if (result == -1)
					return -1;
				intersections += result;
			}
			return intersections;

		}

	}

	private int canPlaceLetterAt(String letter, int row, int col) {

		if (grid[row][col].getLetter() == null)
			return 0;

		if (grid[row][col].getLetter().equals(letter))
			return 1;

		return -1;
	}

	private Direction randomDirection() {

		Random random = new Random();

		int i = random.nextInt(2);
		Direction dir = i == 0 ? Direction.ACROSS : Direction.DOWN;

		return dir;

	}

	private void createGrid() {

		grid = new Cell[this.GRID_ROWS][this.GRID_COLS];
		for (int r = 0; r < this.GRID_ROWS; r++) {
			for (int c = 0; c < this.GRID_COLS; c++) {

				grid[r][c] = new Cell();
			}
		}

	}

	private void clearGrid() {

		for (int r = 0; r < this.grid.length; r++) {
			for (int c = 0; c < this.grid[0].length; c++) {

				grid[r][c] = new Cell();
			}
		}

		letterMap = new HashMap<String, ArrayList<Position>>();
	}

	public ArrayList<Word> getBad_words() {
		return bad_words;
	}

	public void setBad_words(ArrayList<Word> bad_words) {
		this.bad_words = bad_words;
	}

	public ArrayList<Word> getWords() {
		return words;
	}

	public void setWords(ArrayList<Word> words) {
		this.words = words;
	}

}
