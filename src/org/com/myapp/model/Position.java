package org.com.myapp.model;

import java.io.Serializable;


public class Position implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int x;
	private int y;
	private int dir;
	
	public Position() {
		this.x = 0;
		this.y = 0;
		this.dir = 0;
	
	}
	
	public Position(int x,int y, int dir){
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	
	
	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public int getDir() {
		return dir;
	}


	public void setDir(int dir) {
		this.dir = dir;
	}


	@Override
	public boolean equals(Object obj) {

		
		if(this == obj) return true;
		
		if(obj == null || getClass() != obj.getClass()) return false;
		
		Position p = (Position) obj;
		
		if(x != p.getX() && y != p.getY() && dir != p.getDir()) return false;
		
		return true;
	}

	@Override
	public String toString() {
		String result = "Posistion: x = "+x+"y = "+y+"dir = "+dir;
		return result;
	}
	
	
	
	
}
