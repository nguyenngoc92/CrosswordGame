package org.com.myapp.model;

public class Cell {

	private String letter;

	private CellNode cellNode;

	public Cell() {

		this.letter = null;
		this.cellNode = null;
	}

	public Cell(String letter) {
		this.letter = letter;
		this.cellNode = null;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public CellNode getCellNode() {
		return cellNode;
	}

	public void setCellNode(CellNode cellNode) {
		this.cellNode = cellNode;
	}

}
