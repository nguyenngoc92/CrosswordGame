package org.com.myapp;

import java.util.ArrayList;

import org.com.myapp.model.Item;

public class AppInitial {

	public static String loginUrl = "http://192.168.1.69:8080/login";
	public static String userInforUrl = "http://192.168.1.69:8080/user/userinfor";

	public static String getMatchUrl = "http://192.168.1.69:8080/user/match/getmatch";

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
}
