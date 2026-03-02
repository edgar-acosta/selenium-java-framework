package com.robot.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage{
    WebDriver driver;

    // 1. Localizadores (Nuestros "objetos")
    private By txtUsername  = By.id("user-name");
    private By txtPassword  = By.id("password");
    private By btnLogin     = By.id("login-button");
    private By errorMessage = By.cssSelector("h3[data-test='error']");

    // 2. Constructor
    public LoginPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }

    // 3. Acciones (Nuestros "servicios")
    public void escribirUsuario(String usuario) {    
        type(txtUsername, usuario);
    }

    public void escribirPassword(String password) {
        type(txtPassword, password);
    }

    public void clickEnLogin() {
        click(btnLogin);
    }

    // 4. Métodos de Alto Nivel (Flujos)
    // Este método combina acciones para hacer el test más rápido de escribir
    public void loginAs(String user, String pass) {
        escribirUsuario(user);
        escribirPassword(pass);
        clickEnLogin();
    }

    // 5. Validaciones (Para el caso del usuario bloqueado)
    
    public String getErrorText() {
        return getText(errorMessage);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }
}