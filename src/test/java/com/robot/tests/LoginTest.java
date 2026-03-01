package com.robot.tests;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
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
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        
        // Descomenta la siguiente línea para correr sin ver la ventana (obligatorio en Jenkins)
        // options.addArguments("--headless"); 

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
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}