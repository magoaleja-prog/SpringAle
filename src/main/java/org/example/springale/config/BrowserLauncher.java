package org.example.springale.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

@Component
@ConditionalOnProperty(name = "app.open-browser", havingValue = "true")
public class BrowserLauncher {

    private static final String[] RUTAS_CHROME = {
            "C:/Program Files/Google/Chrome/Application/chrome.exe",
            "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe",
            System.getenv("LOCALAPPDATA") + "/Google/Chrome/Application/chrome.exe"
    };

    @EventListener(ApplicationReadyEvent.class)
    public void abrirNavegador(ApplicationReadyEvent event) {
        String puerto = event.getApplicationContext().getEnvironment()
                .getProperty("local.server.port", "8080");
        String url = "http://localhost:" + puerto + "/swagger-ui.html";

        for (String ruta : RUTAS_CHROME) {
            if (ruta != null && new File(ruta).exists()) {
                try {
                    new ProcessBuilder(ruta, url).start();
                    return;
                } catch (Exception ignored) {
                }
            }
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (Exception ignored) {
        }
    }
}
