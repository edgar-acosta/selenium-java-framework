package com.robot.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
        driver.findElement(txtUsername).sendKeys(usuario);
    }

    public void escribirPassword(String password) {
        driver.findElement(txtPassword).sendKeys(password);
    }

    public void clickEnLogin() {
        driver.findElement(btnLogin).click();
    }
}