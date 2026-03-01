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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.robot.pages.LoginPage;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;

public class LoginTest {
    WebDriver driver;

    @BeforeMethod
    public void setup() {
        
        // 1. Leemos lo que viene de Jenkins/Maven (si no hay nada, usamos chrome por defecto)
        String browser = System.getProperty("browser", "chrome");
        String url = System.getProperty("url", "https://www.saucedemo.com/");

        System.out.println("🚀 Iniciando prueba en: " + browser + " para la URL: " + url);

        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--no-sandbox", 
            "--disable-dev-shm-usage","--disable-gpu","--window-size=1920,100",
            "--remote-allow-origins=*");
            
            driver = new ChromeDriver(options);
        } 
        else if (browser.equalsIgnoreCase("firefox")) {
            FirefoxOptions ffOptions = new FirefoxOptions();
            ffOptions.addArguments("-headless","--width=1920","--height=1080","--disable-gpu","--no-sandbox"); 
 
            WebDriverManager.firefoxdriver().setup();
            driver = new org.openqa.selenium.firefox.FirefoxDriver(ffOptions);
        }
      
        Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
       
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
            captureScreenshotAllure(driver);
        }

        if (driver != null) {
            driver.quit();
        }
    }

    @Attachment(value = "Screenshot on Failure", type = "image/png")
    public byte[] captureScreenshotAllure(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}