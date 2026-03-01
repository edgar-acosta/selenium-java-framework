package com.robot.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.robot.pages.InventoryPage;
import com.robot.pages.LoginPage;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import org.testng.annotations.Parameters;

public class LoginTest {
    WebDriver driver;
    LoginPage loginPage;
    InventoryPage inventoryPage;

    @Parameters("browser")
    @BeforeMethod
    public void setup(String browser) {
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);

        
        //String browser = System.getProperty("browser", "chrome");
        String url = System.getProperty("url", "https://www.saucedemo.com/").trim();
        System.out.println("🚀 Iniciando prueba en: " + browser + " para la URL: " + url);

        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
            /*
            // 1. Usar el nuevo motor headless
            options.addArguments("--headless=new"); 
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            // 2. Corregir la altura de la ventana (1080)
            options.addArguments("--window-size=1920,1080"); 
            options.addArguments("--remote-allow-origins=*");
            
            // 3. Argumentos de estabilidad extra para entornos Snap
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--disable-software-rasterizer");
             */
            driver = new ChromeDriver(options);
            
            // 4. Darle tiempo a la página para cargar antes de buscar elementos
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        } 
        else if (browser.equalsIgnoreCase("firefox")) {
            FirefoxOptions ffOptions = new FirefoxOptions();
            ffOptions.addArguments("--headless","--width=1920","--height=1080","--disable-gpu","--no-sandbox"); 
 
            WebDriverManager.firefoxdriver().setup();
            driver = new org.openqa.selenium.firefox.FirefoxDriver(ffOptions);
        }
        
        driver.get(url);
        Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
       
    }

    @DataProvider(name = "loginData")
    public Object[][] getData() {
        return new Object[][] {
            {"standard_user", "secret_sauce", "valid"},
            {"locked_out_user", "secret_sauce", "locked"},
            {"problem_user", "secret_sauce", "valid"}
        };
    }

    @Test(dataProvider = "loginData")
    public void validarLoginMultiple(String user, String pass, String type) {
        
        loginPage.escribirUsuario(user);
        loginPage.escribirPassword(pass);
        loginPage.clickEnLogin();
        
        if(type.equals("valid")) {
            Assert.assertTrue(inventoryPage.isTitleVisible());
        } else {
            Assert.assertTrue(loginPage.getErrorMsg().contains("locked out"));
        }
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
            System.out.println("DEBUG: La URL actual es: " + driver.getCurrentUrl());
            System.out.println("DEBUG: Título de la página: " + driver.getTitle());

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