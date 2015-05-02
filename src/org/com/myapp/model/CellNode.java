package org.com.myapp.model;

import java.util.ArrayList;
import java.util.List;

public class CellNode {

	private boolean isStartOfWord;
	private List<Integer> indexList = new ArrayList<Integer>();
	private List<Integer> indexOfWords = new ArrayList<Integer>();

	public CellNode() {

	}

	public CellNode(boolean isStartOfWord) {
		this.isStartOfWord = isStartOfWord;
	}

	public CellNode(boolean isStartOfWord, int index) {

	}

	public void addIndex(int index) {
		this.indexList.add(index);

	}

	public void addIndex(int index, boolean isStartOfword) {

		if (isStartOfword) {
			indexList.add(index);
			indexOfWords.add(index);
		}

	}

	public boolean isStartOfWord() {
		return isStartOfWord;
	}

	public void setStartOfWord(boolean isStartOfWord) {
		this.isStartOfWord = isStartOfWord;
	}

	public List<Integer> getIndexList() {
		return indexList;
	}

	public void setIndexList(List<Integer> indexList) {
		this.indexList = indexList;
	}

}
