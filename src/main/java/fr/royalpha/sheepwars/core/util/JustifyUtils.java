package fr.royalpha.sheepwars.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shahroz Saleem
 * @link https://stackoverflow.com/a/51118373
 */
public class JustifyUtils {
	
	private JustifyUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static List<String> fullJustify(String[] words, int maxWidth) {
		int n = words.length;
		List<String> justifiedText = new ArrayList<>();
		int currLineIndex = 0;
		int nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
		while (currLineIndex < n) {
			StringBuilder line = new StringBuilder();
			for (int i = currLineIndex; i < nextLineIndex; i++) {
				line.append(words[i] + " ");
			}
			currLineIndex = nextLineIndex;
			nextLineIndex = getNextLineIndex(currLineIndex, maxWidth, words);
			justifiedText.add(line.toString());
		}
		for (int i = 0; i < justifiedText.size() - 1; i++) {
			String fullJustifiedLine = getFullJustifiedString(justifiedText.get(i).trim(), maxWidth);
			justifiedText.remove(i);
			justifiedText.add(i, fullJustifiedLine);
		}
		String leftJustifiedLine = getLeftJustifiedLine(justifiedText.get(justifiedText.size() - 1).trim(), maxWidth);
		justifiedText.remove(justifiedText.size() - 1);
		justifiedText.add(leftJustifiedLine);
		return justifiedText;
	}

	public static int getNextLineIndex(int currLineIndex, int maxWidth, String[] words) {
		int n = words.length;
		int width = 0;
		while (currLineIndex < n && width < maxWidth) {
			width += words[currLineIndex++].length() + 1;
		}
		if (width > maxWidth + 1)
			currLineIndex--;
		return currLineIndex;
	}

	public static String getFullJustifiedString(String line, int maxWidth) {
		StringBuilder justifiedLine = new StringBuilder();
		String[] words = line.split(" ");
		int occupiedCharLength = 0;
		for (String word : words) {
			occupiedCharLength += word.length();
		}
		int remainingSpace = maxWidth - occupiedCharLength;
		int spaceForEachWordSeparation = words.length > 1 ? remainingSpace / (words.length - 1) : remainingSpace;
		int extraSpace = remainingSpace - spaceForEachWordSeparation * (words.length - 1);
		for (int j = 0; j < words.length - 1; j++) {
			justifiedLine.append(words[j]);
			for (int i = 0; i < spaceForEachWordSeparation; i++)
				justifiedLine.append(" ");
			if (extraSpace > 0) {
				justifiedLine.append(" ");
				extraSpace--;
			}
		}
		justifiedLine.append(words[words.length - 1]);
		for (int i = 0; i < extraSpace; i++)
			justifiedLine.append(" ");
		return justifiedLine.toString();
	}

	public static String getLeftJustifiedLine(String line, int maxWidth) {
		int lineWidth = line.length();
		StringBuilder justifiedLine = new StringBuilder(line);
		for (int i = 0; i < maxWidth - lineWidth; i++)
			justifiedLine.append(" ");
		return justifiedLine.toString();
	}
}
