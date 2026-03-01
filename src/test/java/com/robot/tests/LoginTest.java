package com.robot.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import com.robot.pages.InventoryPage;
import com.robot.pages.LoginPage;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import org.testng.annotations.Parameters;

public class LoginTest {
    // 1. ThreadLocal para el driver y las páginas (Seguridad total en hilos)
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<LoginPage> loginPage = new ThreadLocal<>();
    private static ThreadLocal<InventoryPage> inventoryPage = new ThreadLocal<>();
    
    // Métodos auxiliares para obtener las instancias del hilo actual
    public WebDriver getDriver() { return driver.get(); }
    public LoginPage getLoginPage() { return loginPage.get(); }
    public InventoryPage getInventoryPage() { return inventoryPage.get(); }

    @Parameters("browser")
    @BeforeMethod
    public void setup(@Optional("chrome") String browser) {
        WebDriver localDriver;

        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
            localDriver = new ChromeDriver(options);
        } else {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions ffOptions = new FirefoxOptions();
            ffOptions.addArguments("-headless", "--no-sandbox", "--width=1920", "--height=1080");
            localDriver = new FirefoxDriver(ffOptions);
        }

        // Guardamos todo en sus respectivos hilos
        driver.set(localDriver);
        loginPage.set(new LoginPage(getDriver()));
        inventoryPage.set(new InventoryPage(getDriver()));
        
        getDriver().get("https://www.saucedemo.com/");
    }

    @DataProvider(name = "loginData", parallel = true) // Agregamos parallel=true aquí también
    public Object[][] getData() {
        return new Object[][] {
            {"standard_user", "secret_sauce", "valid"},
            {"locked_out_user", "secret_sauce", "locked"},
            {"problem_user", "secret_sauce", "valid"}
        };
    }

    @Test(dataProvider = "loginData")
    public void validarLoginMultiple(String user, String pass, String type) {
        getLoginPage().escribirUsuario(user);
        getLoginPage().escribirPassword(pass);
        getLoginPage().clickEnLogin();
        
        if(type.equals("valid")) {
            Assert.assertTrue(getInventoryPage().isTitleVisible(), "El título no es visible para el usuario: " + user);
        } else {
            Assert.assertTrue(getLoginPage().getErrorMsg().contains("locked out"), "No se mostró el error de bloqueo para: " + user);
        }
    }

    @Test
    public void validarLoginExitoso() {
        getLoginPage().escribirUsuario("standard_user");
        getLoginPage().escribirPassword("secret_sauce");
        getLoginPage().clickEnLogin();

        // Corregido: getTitleText() devuelve "Products", no la URL. 
        // Si quieres la URL usa getDriver().getCurrentUrl()
        Assert.assertEquals(getInventoryPage().getTitleText(), "Products");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (getDriver() != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                // Captura para Allure
                captureScreenshotAllure(getDriver());
                
                // Captura para carpeta local (Corregido el casting)
                try {
                    File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
                    File destFile = new File("target/screenshots/" + result.getName() + ".png");
                    Files.createDirectories(destFile.getParentFile().toPath());
                    Files.copy(scrFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Cierre correcto
            getDriver().quit();
            driver.remove(); // Limpieza vital
            loginPage.remove();
            inventoryPage.remove();
        }
    }

    @Attachment(value = "Screenshot on Failure", type = "image/png")
    public byte[] captureScreenshotAllure(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}