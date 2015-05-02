package org.com.myapp;

import java.util.ArrayList;
import java.util.Collections;

import org.com.myapp.model.ItemData;
import org.com.myapp.model.Position;
import org.com.myapp.model.Word;

public class AppConfig {

	public static String host = "192.168.0.72";
	public static int port = 9000;

	public static String registerUrl = "http://192.168.0.72:9000/register";
	public static String loginUrl = "http://192.168.0.72:9000/login";
	public static String userInforUrl = "http://192.168.1.69:9000/user/userinfor";

	public static String getMatchUrl = "http://192.168.0.72:9000/user/match/getmatch";
	public static String updateScoreUrl = "http://192.168.0.72:9000/user/updatescore";
	public static String getUserInforListUrl = "http://192.168.0.72:9000/user/list/{id}/{limit}";
	public static String updateItemInforAnswer = "http://192.168.0.72:9000/user/match/updateItemInfor";

	// //////////////////////////////////////////////////////////
	public static String getAllSubjectUrl = "http://192.168.0.72:9000/user/subject";
	public static String getMatchBySubjectUrl = "http://192.168.0.72:9000/user/subject/{idsubject}/match";

	public static String getRankUserByMatchUrl = "http://192.168.0.72:9000/user/match/{id}/rank";
	public static String getTopRankUserByMatchUrl = "http://192.168.0.72:9000/user/match/{id}/rank/list/{lenght}";
	public static String getRankUserByCompetitionUrl = "http://192.168.0.72:9000/user/competition/{id}/rank";
	public static String getTopRankUserByCompetitionUrl = "http://192.168.0.72:9000/user/competition/{id}/rank/list/{lenght}";

	public static String getCurrentCompetitionUrl = "http://192.168.0.72:9000/user/competition/current";
	public static String getMatchByCompetitionUrl = "http://192.168.0.72:9000/user/competition/{idcompetition}/match";

	public static final int REQUEST_PREFERENCES = 2;
	public static final float KEYBOARD_OVERLAY_OFFSET = 90;
	// intent infor
	public static String USER_NAME = "USERNAME";
	public static String SCORE = "SCORE";
	public static String RANK = "RANK";
	public static String FLAG_SUBJECT = "SUBJECT";
	public static String FLAG_COMPETITION = "COMPETITION";
	public static String FLAG = "FLAG";

	public static final String USER_DATA_LIST = "USER_DATA_LIST";

	public static ArrayList<ItemData> getItems() {
		ArrayList<ItemData> items = new ArrayList<ItemData>();

		items.add(new ItemData(1, "Thing that makes Daddy fell dumb",
				"HOMEWORK", false));
		items.add(new ItemData(2, "Not gonna happen", "NAP", false));
		items.add(new ItemData(3, "Worst word in the English language", "NO",
				false));
		items.add(new ItemData(4, "Pure, frosty happiness", "POPSICLES", false));
		items.add(new ItemData(5, "These are awful", "PANTS", false));
		items.add(new ItemData(6, "Makes everything better", "BALLOON", false));
		items.add(new ItemData(7, "Like razor blades, only worse", "SHIRTTAGS",
				false));
		items.add(new ItemData(8,
				"When dinner comes from, even when Mom calss it \"homemad\"'",
				"MICROWAVE", false));

		return items;
	}

	public static ArrayList<Word> getWords() {
		ArrayList<Word> words = new ArrayList<Word>();

		ArrayList<ItemData> items = getItems();
		Collections.sort(items);
		for (int i = 0; i < items.size(); i++) {

			words.add(new Word(i + 1, new Position(), items.get(i)));

		}

		return words;
	}
}
