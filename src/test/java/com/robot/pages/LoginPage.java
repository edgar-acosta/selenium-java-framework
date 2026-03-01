package com.robot.pages;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    WebDriver driver;

    // 1. Localizadores (Nuestros "objetos")
    By txtUsername = By.id("user-name");
    By txtPassword = By.id("password");
    By btnLogin = By.id("login-button");
    

    // 2. Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // 3. Acciones (Nuestros "servicios")
    public void escribirUsuario(String usuario) {    
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(txtUsername));
        driver.findElement(txtUsername).sendKeys(usuario);
    }

    public void escribirPassword(String password) {
        driver.findElement(txtPassword).sendKeys(password);
    }

    public void clickEnLogin() {
        driver.findElement(btnLogin).click();
    }
}