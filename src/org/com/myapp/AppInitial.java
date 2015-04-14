package org.com.myapp;

import java.util.ArrayList;

import org.com.myapp.model.ItemData;
import org.com.myapp.model.Position;
import org.com.myapp.model.Word;

public class AppInitial {

	public static String host = "192.168.1.69";
	public static int port = 8080;
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

	public static ArrayList<ItemData> getItems() {
		ArrayList<ItemData> items = new ArrayList<ItemData>();

		items.add(new ItemData(1, "Thing that makes Daddy fell dumb",
				"Start with letter 'H'", "HOMEWORK", false));
		items.add(new ItemData(2, "Not gonna happen", "Start with letter 'N'",
				"NAP", false));
		items.add(new ItemData(3, "Worst word in the English language",
				"Start with letter 'N'", "NO", false));
		items.add(new ItemData(4, "Pure, frosty happiness",
				"Start with letter 'P'", "POPSICLES", false));
		items.add(new ItemData(5, "These are awful", "Start with letter 'P'",
				"PANTS", false));
		items.add(new ItemData(6, "Makes everything better",
				"Start with letter 'B'", "BALLOON", false));
		items.add(new ItemData(7, "Like razor blades, only worse",
				"Start with letter 'S'", "SHIRTTAGS", false));
		items.add(new ItemData(8,
				"When dinner comes from, even when Mom calss it \"homemad\"'",
				"Start with letter 'M'", "MICROWAVE", false));

		return items;
	}

	public static ArrayList<Word> getWords() {
		ArrayList<Word> words = new ArrayList<Word>();

		ArrayList<ItemData> items = getItems();
		for (ItemData item : items) {

			words.add(new Word(0, new Position(), item));

		}

		return words;
	}
}
