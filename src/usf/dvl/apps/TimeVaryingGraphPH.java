package usf.dvl.apps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import processing.core.PApplet;
import usf.dvl.graph.timevarying.GraphSet;
import usf.dvl.graph.timevarying.visualization.MainWindow;

public class TimeVaryingGraphPH {

	
	public static void main( String [] args ) {
		
		boolean config_mode = false;
		
		BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));   

		
		if( args.length > 0 && args[0].equals("--config") ) config_mode = true;
		
		Configuration config = new Configuration("timevaryinggraphph.config");
		
		if( !config.isValid() ) { config_mode = true; }
		if( !config.isRisperValid() ) { config_mode = true; }
		if( !config.isHeraBottleneckValid() ) { config_mode = true; }
		if( !config.isHeraWassersteinValid() ) { config_mode = true; }
		
		
		if( config_mode ) {
			
			try {
				do {
					System.out.println();
					System.out.print(">>> Path to ripser executable (ripser): ");
				} while( !config.setRipser( obj.readLine( ) ) );
				
				do {
					System.out.println();
					System.out.print(">>> Path to hera/bottleneck executable (bottleneck_dist): ");
				} while( !config.setHeraBottleneck( obj.readLine() ) );			
	  
				do {
					System.out.println();
					System.out.print(">>> Path to hera/wasserstein executable (wasserstein_dist): ");
				} while( !config.setHeraWasserstein( obj.readLine() ) );			
			} catch( IOException e ) {
				e.printStackTrace();
			}
		}
		
		System.out.println();
		String userPath = null;
		System.out.print(">>> Resume previous job (y/n)? ");
		try {
			String resume = obj.readLine();
			if( resume.startsWith("y") || resume.startsWith("Y") ) {
				userPath = config.getPreviousDirectory();
			}
			
			if( userPath == null ) {
				System.out.print(">>> Enter directory containing files: ");
				userPath = obj.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

			
		try {
			config.setPreviousDirectory( userPath );
			config.saveConfiguration();
		} catch (IOException e) {
			System.out.println("Failed to save configuration file.");
		}
		
		System.out.println();
		
		boolean quitting = false;		
		
		GraphSet gs = null;
		try {
			gs = new GraphSet( config, userPath );
			//gs.runPH();
			
			System.out.println();
			System.out.println();
			System.out.println("#####################################################################");
			System.out.println("#####################################################################");
			System.out.println();
			System.out.println("    This next operation may take some time. If you'd like to stop " );
			System.out.println("    and restart later, type 'exit' or 'quit' at any time.");
			System.out.println();
			System.out.println("#####################################################################");
			System.out.println("#####################################################################");
			System.out.println();
			System.out.println();
			
			ExecutorService es = gs.runPH();

			while( ! es.awaitTermination(10, TimeUnit.SECONDS) ) {
				if( quitting ) {
					System.out.println("Still shutting down...");
				}
				else {
					System.out.println("Still working...");
				}
				if( obj.ready() ) {
					String line = obj.readLine();
					if( line.startsWith("quit") || line.startsWith("exit") ) {
						gs.quit();
						quitting = true;
						System.out.println("Shutdown started...");
					}
				}
			}

			
			if(!quitting ) {
				es = gs.runPDDistances();
				
	
				while( ! es.awaitTermination(10, TimeUnit.SECONDS) ) {
					if( quitting ) {
						System.out.println("Still shutting down...");
					}
					else {
						System.out.println("Still working...");
					}
					if( obj.ready() ) {
						String line = obj.readLine();
						if( line.startsWith("quit") || line.startsWith("exit") ) {
							gs.quit();
							quitting = true;
							System.out.println("Shutdown started...");
						}
					}
				}
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		if( !quitting ) {
			MainWindow.data = gs;
			PApplet.main(new String[] { "usf.dvl.graph.timevarying.visualization.MainWindow" });
		}
		
		System.out.println("calculation done");
		
	}
	
}
