package com.eggplant.qiezisocial.widget.azlist;



import java.util.Comparator;

public class LettersComparator implements Comparator<AZItemEntity> {

	public int compare(AZItemEntity o1, AZItemEntity o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}
}
