package fr.asynchronous.sheepwars.core.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import fr.asynchronous.sheepwars.core.SheepWarsPlugin;

public class ExceptionManager {
	
	private Throwable throwable;

	/**
	 * This class is called "ExceptionManager" so it hasn't to be initialized that way.
	 *
	 * @deprecated use {@link #register(Throwable, boolean)} instead.  
	 */
	@Deprecated
	public ExceptionManager(final java.lang.Exception ex) {
		this((Throwable) ex);
	}
	
	/**
	 * This class is called "ExceptionManager" so it hasn't to be initialized that way.
	 *
	 * @deprecated use {@link #register(Throwable, boolean)} instead.  
	 */
	@Deprecated
	public ExceptionManager(final Throwable th) {
		this.throwable = th;
	}
	
	/**
	 * This class is called "ExceptionManager" so it hasn't to be initialized that way.
	 *
	 * @deprecated use {@link #register(Throwable, boolean)} instead.  
	 */
	@Deprecated
	public boolean register(final boolean cast) {
		return register(this.throwable, cast);
	}
	
	public static boolean register(final Throwable th, final boolean cast) {
		if (cast) {
			Logger logger = Bukkit.getLogger();
			logger.warning("[UltimateSheepWars > ERR] An error occured : " + th.getMessage());
			logger.info("[UltimateSheepWars > ERR] For more information or any help, please contact the developer.");
		}
		String timeLog = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
        File folder = new File(SheepWarsPlugin.DATAFOLDER, "reports/");
        if (!folder.exists()) 
        	folder.mkdirs();
        File logFile = new File(SheepWarsPlugin.DATAFOLDER, "reports/" + timeLog + ".txt");

        StringWriter errors = new StringWriter();
        th.printStackTrace(new PrintWriter(errors));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
			writer.write(errors.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
        return true;
	}
	
	public static boolean register(final Throwable th) {
		return register(th, false);
	}
}