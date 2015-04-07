package org.com.myapp.model;

public class Word {

	private int order;

	private Position position;

	private Item item;

	public Word() {

	}

	public Word(int _order, Position _position, Item _item) {

		this.order = _order;
		this.position = _position;
		this.item = _item;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

}
