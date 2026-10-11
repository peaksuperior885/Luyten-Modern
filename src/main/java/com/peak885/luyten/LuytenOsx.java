package com.peak885.luyten;

import com.apple.eawt.*;
import java.io.File;

/**
 * An OS X-specific initialization method for dragging/dropping
 */
public class LuytenOsx extends Luyten {

    public static void main(String[] args) {
        JFontChooser fontConfig = new JFontChooser();
        fontConfig.loadAndApplyConfig();
        // Add an adapter as the handler to a new instance of the application
        // class
        @SuppressWarnings("deprecation")
        Application app = new Application();
        app.addApplicationListener(new ApplicationAdapter() {
            public void handleOpenFile(ApplicationEvent e) {
                Luyten.addToPendingFiles(new File(e.getFilename()));
                Luyten.processPendingFiles();
            }

            public void handleQuit(ApplicationEvent e) {
                Luyten.quitInstance();
            }
        });

        // Call the superclass's main function
        Luyten.main(args);
    }

}
