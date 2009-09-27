/**
 * There are a couple functions here that exist at the global scope, mostly serialization utility
 * functions.
 * TODO: can GameInformation, CurrentGameInformation, and the serialization routines all be
 *       refactored into one cleaned object?
 */

package com.daverin.dopewars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import android.util.Log;

public class Global {
	// These are the drug attributes that the game knows how to handle.
	public static String DRUG_ATTR_BASE_PRICE = "base_price";
	public static String DRUG_ATTR_PRICE_VARIANCE = "price_variance";
	public static HashMap<Integer,Integer> drug_icons_;
	
	// Initialize the array of available icons.
	// TODO: would be nice to have one of these for button drawables and colors?
	// TODO: aren't drug_icons_ specific to the main game screen? they could just go there.
	public static void loadIcons() {
		drug_icons_ = new HashMap<Integer,Integer>();
		drug_icons_.put(0, R.drawable.weed);
		drug_icons_.put(1, R.drawable.acid);
		drug_icons_.put(2, R.drawable.ludes);
		drug_icons_.put(3, R.drawable.heroin);
		drug_icons_.put(4, R.drawable.cocaine);
		drug_icons_.put(5, R.drawable.shrooms);
		drug_icons_.put(6, R.drawable.speed);
		drug_icons_.put(7, R.drawable.hashish);
	}
	
	// Uses a particular format to deserialize a string into a hash map of <string, float>
	// pairs.
	public static HashMap<String, Float> deserializeAttributes(String attribute_string) {
		HashMap<String, Float> attributes = new HashMap<String, Float>();
		String[] attribute_strings = attribute_string.split("\\|");
		for (int j = 0; j < attribute_strings.length; ++j) {
			String[] next_attribute = attribute_strings[j].split(":");
			if (next_attribute.length != 2) {
				Log.d("dopewars", "Invalid attribute: " + attribute_strings[j]);
			}
			attributes.put(next_attribute[0], Float.parseFloat(next_attribute[1]));
		}
		return attributes;
	}
	
	// Uses a particular format to deserialize a string into a hash map of
	// <string, hash_map<string, float>> where the target hash_maps are processed by
	// deserializeAttributes().
	public static HashMap<String, HashMap<String, Float>> deserializeAttributeGroup(
			String attribute_group_string) {
		HashMap<String, HashMap<String, Float>> new_attributes =
			new HashMap<String, HashMap<String, Float>>();
		String[] elements = attribute_group_string.split("__");
		for (int i = 0; i < elements.length; ++i) {
			String[] next_element = elements[i].split("==");
			if (next_element.length != 2) {
				Log.d("dopewars", "Invalid element description: " + elements[i]);
			} else {
				new_attributes.put(next_element[0], deserializeAttributes(next_element[1]));
			}
		}
		return new_attributes;
	}
	
	// Uses the same format as above to serialize a hash_map into a string.
	public static String serializeAttributes(HashMap<String, Float> attributes) {
		String serialized_attributes = "";
		Iterator<String> names = attributes.keySet().iterator();
		while (names.hasNext()) {
			String next_attribute = names.next();
			serialized_attributes += next_attribute + ":" + Float.toString(attributes.get(next_attribute));
			if (names.hasNext()) {
				serialized_attributes += "|";
			}
		}
		return serialized_attributes;
	}
	
	// Uses the save format as above to serialize a hash_map of hash_maps into a big
	// complicated string.
	public static String serializeAttributeGroup(
			HashMap<String, HashMap<String, Float>> attribute_group) {
		String serialized_attributes = "";
		Iterator<String> elements = attribute_group.keySet().iterator();
		while (elements.hasNext()) {
			String next_element = elements.next();
			serialized_attributes += next_element + "==" + serializeAttributes(
					attribute_group.get(next_element));
			if (elements.hasNext()) {
				serialized_attributes += "__";
			}
		}
		return serialized_attributes;
	}
	
	// Given a set of drug attributes this will determine a random price within the parameters
	// of that drug. It will return a price and also other messages about the price (notification
	// messages that the drug price is unusually high or low).
	// TODO: chooseDrugPrice seems to have nothing particularly global about it.
	public static Vector<String> chooseDrugPrice(
			String name, HashMap<String, Float> drug_attributes) {
		Vector<String> price_and_messages = new Vector<String>();
		int base_price = drug_attributes.get("base_price").intValue();
		int price_variance = drug_attributes.get("price_variance").intValue();
		int price = (int)(base_price - price_variance / 2.0 +
				Global.rand_gen_.nextDouble() * price_variance);
		// Check for price jumps
		if (drug_attributes.get("low_probability") != null) {
			float low_probability = drug_attributes.get("low_probability");
			if (rand_gen_.nextFloat() < low_probability) {
				float multiplier = drug_attributes.get("low_multiplier");
				price = (int)(price * multiplier);
				price_and_messages.add(name + " is being sold at very low prices!");
			}
		} else if (drug_attributes.get("high_probability") != null) {
			float high_probability = drug_attributes.get("high_probability");
			if (rand_gen_.nextFloat() < high_probability) {
				float multiplier = drug_attributes.get("high_multiplier");
				price = (int)(price * multiplier);
				price_and_messages.add(name + " is being sold at very high prices!");
			}
		}
		price_and_messages.insertElementAt(Integer.toString(price), 0);
		return price_and_messages;
	}
	
	// Global random number generator
	// TODO: is there anything truly gained by having a global random number generator as opposed to
	//       each activity having their own?
    public static Random rand_gen_ = new Random();
}
