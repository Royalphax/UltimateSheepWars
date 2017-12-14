package fr.asynchronous.sheepwars.core.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.asynchronous.sheepwars.core.util.Utils;

public class ScoreboardManager {

	private static final List<ChatColor> colors = Arrays.asList(ChatColor.values());

	private final Scoreboard scoreboard;
	private final Objective objective;

	private final List<BoardLine> boardLines = new ArrayList<>();

	public ScoreboardManager(String title, String objName) {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		if (scoreboard.getObjective(objName) == null) {
			objective = scoreboard.registerNewObjective(objName, "dummy");
		} else {
			objective = scoreboard.getObjective(objName);
		}
		try {
			objective.setDisplayName(title);
		} catch (IllegalArgumentException ex) {
			new ExceptionManager(ex).register(true);
		}
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		for (int i = 0; i < colors.size(); i++) {
			final ChatColor color = colors.get(i);
			final Team team = scoreboard.registerNewTeam("line" + i);
			team.addEntry(color.toString());
			boardLines.add(new BoardLine(color, i, team));
		}
	}

	public ScoreboardManager(Objective obj) {
		scoreboard = obj.getScoreboard();
		objective = obj;
	}

	public void setTitle(String title) {
		objective.setDisplayName(title);
	}

	public void setLine(int line, String value, boolean trim) {
		if (!validate(line, value))
			return;
		value = (trim == true ? value.trim() : value);
	    final BoardLine boardLine = getBoardLine(line);
	    Validate.notNull(boardLine, "Unable to find scoreboard line with index of " + line + ".");
	    objective.getScore(boardLine.getColor().toString()).setScore(line);
	    final int mid = Utils.halfSplit(value);
    	String prefix = value.substring(0, mid);
    	String suffix = ChatColor.getLastColors(prefix) + value.substring(mid);
    	boardLine.getTeam().setPrefix(prefix);
    	boardLine.getTeam().setSuffix(suffix);
	}

	public void removeLine(int line) {
		if (!validate(line, ""))
			return;
	    final BoardLine boardLine = getBoardLine(line);
	    Validate.notNull(boardLine, "Unable to find scoreboard line with index of " + line + ".");
	    scoreboard.resetScores(boardLine.getColor().toString());
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	private BoardLine getBoardLine(int line) {
		return boardLines.stream().filter(boardLine -> boardLine.getLine() == line).findFirst().orElse(null);
	}
	
	private boolean validate(int line, String value)
	{
		if (Math.abs(line) > 15) {
			new ExceptionManager(new IndexOutOfBoundsException("The received line number is higher than maximum allowed (" + line + " > 15)")).register(true);
			return false;
		}
		if (value.length() > 32) {
			new ExceptionManager(new IndexOutOfBoundsException("The received string length is longer than maximum allowed (" + value.length() + " > 32)")).register(true);
			return false;
		}
		return true;
	}

	public class BoardLine {

		private final ChatColor color;
		private final int line;
		private final Team team;

		public BoardLine(ChatColor color, int line, Team team) {
			this.color = color;
			this.line = line;
			this.team = team;
		}

		public ChatColor getColor() {
			return color;
		}

		public int getLine() {
			return line;
		}

		public Team getTeam() {
			return team;
		}
	}
}
