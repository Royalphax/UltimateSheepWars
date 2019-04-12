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

	public ExceptionManager(java.lang.Exception ex) {
		this.throwable = ex;
	}
	
	public ExceptionManager(Throwable th) {
		this.throwable = th;
	}
	
	public boolean register(boolean cast) {
		if (cast) {
			Logger logger = Bukkit.getLogger();
			logger.warning("[UltimateSheepWars > ExceptionManager] An error occured: " + throwable.getMessage());
			logger.info("[UltimateSheepWars > ExceptionManager] For more information or any help, please contact the developer.");
		}
		String timeLog = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
        File folder = new File(SheepWarsPlugin.DATAFOLDER, "reports/");
        if (!folder.exists()) 
        	folder.mkdirs();
        File logFile = new File(SheepWarsPlugin.DATAFOLDER, "reports/" + timeLog + ".txt");

        StringWriter errors = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errors));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
			writer.write(errors.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
        return true;
	}
}
