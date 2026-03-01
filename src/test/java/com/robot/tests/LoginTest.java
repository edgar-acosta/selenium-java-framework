package com.robot.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.robot.pages.LoginPage;

import io.github.bonigarcia.wdm.WebDriverManager;


public class LoginTest {
    WebDriver driver;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
        
        // CONFIGURACIÓN PARA LINUX/CI
        options.addArguments("--headless");
        options.addArguments("--no-sandbox"); // Vital para Jenkins
        options.addArguments("--disable-dev-shm-usage"); // Evita que se quede sin memoria en contenedores
        options.addArguments("--disable-gpu"); // Recomendado para Linux sin tarjeta gráfica
        options.addArguments("--window-size=1920,1080"); // Para que los elementos no se amontonen
        options.addArguments("--remote-allow-origins=*"); // Evita bloqueos de seguridad de red

        driver = new ChromeDriver(options);
        driver.get("https://www.saucedemo.com/");
    }

    @Test
    public void validarLoginExitoso() {
        LoginPage login = new LoginPage(driver); // Instanciamos la página
        login.escribirUsuario("standard_user");
        login.escribirPassword("secret_sauce");
        login.clickEnLogin();

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Solo tomamos la foto si el test FALLÓ
        if (result.getStatus() == ITestResult.FAILURE) {
            // 1. Convertimos el driver a "Tomador de capturas"
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            
            try {
                // 2. Creamos la carpeta de destino si no existe
                File destDir = new File("target/screenshots");
                if (!destDir.exists()) destDir.mkdirs();
                
                // 3. Guardamos la foto con el nombre del test que falló
                File destFile = new File("target/screenshots/" + result.getName() + ".png");
                Files.copy(scrFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                System.out.println("❌ Test fallido. Captura guardada en: " + destFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (driver != null) {
            driver.quit();
        }
    }
}