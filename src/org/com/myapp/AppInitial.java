package org.com.myapp;

import java.util.ArrayList;

import org.com.myapp.model.Item;
import org.com.myapp.model.Position;
import org.com.myapp.model.Word;

public class AppInitial {

	public static int sizeBoard = 15;

	public static String loginUrl = "http://192.168.1.69:8080/login";
	public static String userInforUrl = "http://192.168.1.69:8080/user/userinfor";

	public static String getMatchUrl = "http://192.168.1.69:8080/user/match/getmatch";
	public static String updateScoreurl = "http://192.168.1.69:8080/user/updatescore";
	
	
	public static final int REQUEST_PREFERENCES = 2;
	public static final float KEYBOARD_OVERLAY_OFFSET = 90;
	// intent infor
	public static String USER_NAME = "USERNAME";
	public static String SCORE = "SCORE";
	public static String RANK = "RANK";

	public static ArrayList<Item> getItems() {
		ArrayList<Item> items = new ArrayList<Item>();

		items.add(new Item(1, "Thing that makes Daddy fell dumb",
				"Start with letter 'H'", "HOMEWORK", false));
		items.add(new Item(2, "Not gonna happen", "Start with letter 'N'",
				"NAP", false));
		items.add(new Item(3, "Worst word in the English language",
				"Start with letter 'N'", "NO", false));
		items.add(new Item(4, "Pure, frosty happiness",
				"Start with letter 'P'", "POPSICLES", false));
		items.add(new Item(5, "These are awful", "Start with letter 'P'",
				"PANTS", false));
		items.add(new Item(6, "Makes everything better",
				"Start with letter 'B'", "BALLOON", false));
		items.add(new Item(7, "Like razor blades, only worse",
				"Start with letter 'S'", "SHIRTTAGS", false));
		items.add(new Item(8,
				"When dinner comes from, even when Mom calss it \"homemad\"'",
				"Start with letter 'M'", "MICROWAVE", false));

		return items;
	}

	public static ArrayList<Word> getWords() {
		ArrayList<Word> words = new ArrayList<Word>();

		ArrayList<Item> items = getItems();
		for (Item item : items) {

			words.add(new Word(0, new Position(), item));

		}

		return words;
	}
}
