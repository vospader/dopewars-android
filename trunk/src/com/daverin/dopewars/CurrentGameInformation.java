package com.daverin.dopewars;

import java.util.HashMap;

import android.util.Log;

public class CurrentGameInformation {
	public CurrentGameInformation(String serialized_current_game_info) {
		dealer_inventory_ = new HashMap<String, Float>();
		location_inventory_ = new HashMap<String, Float>();
		String[] string_groups = serialized_current_game_info.split("&&");
		for (int i = 0; i < string_groups.length; ++i) {
			String[] group = string_groups[i].split("--");
			if (group.length != 2) {
				Log.d("dopewars", "Invalid game info group: " + string_groups[i]);
			} else if (group[0].equals("cash")) {
				cash_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("space")) {
				space_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("max_space")) {
				max_space_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("loan")) {
				loan_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("bank")) {
				bank_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("location")) {
				location_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("days_left")) {
				days_left_ = Integer.parseInt(group[1]);
			} else if (group[0].equals("dealer_inventory")) {
				dealer_inventory_ = Global.deserializeAttributes(group[1]);
			} else if (group[0].equals("location_inventory")) {
				location_inventory_ = Global.deserializeAttributes(group[1]);
			} else {
				Log.d("dopewars", "Unknown game info group");
			}
		}
	}
	
	public String serializeCurrentGameInformation() {
		String serialized_game_info = "";
		serialized_game_info += "cash--" + Integer.toString(cash_) + "&&" +
			"space--" + Integer.toString(space_) + "&&" +
			"max_space--" + Integer.toString(max_space_) + "&&" +
			"loan--" + Integer.toString(loan_) + "&&" +
			"bank--" + Integer.toString(bank_) + "&&" +
			"location--" + Integer.toString(location_) + "&&" +
			"days_left--" + Integer.toString(days_left_) + "&&" +
			"dealer_inventory--" + Global.serializeAttributes(dealer_inventory_) + "&&" +
			"location_inventory--" + Global.serializeAttributes(location_inventory_);
		return serialized_game_info;
	}
	
	int cash_;
	int space_;
	int max_space_;
	int loan_;
	int bank_;
	int location_;
	int days_left_;
	HashMap<String, Float> dealer_inventory_;
	HashMap<String, Float> location_inventory_;
}
