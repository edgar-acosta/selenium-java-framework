package com.robot.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class InventoryPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // 1. Locators (Los puntos de referencia de la página)
    private By lblTitle = By.className("title"); // El texto que dice "Products"
    private By inventoryContainer = By.id("inventory_container");
    private By btnBurgerMenu = By.id("react-burger-menu-btn");
    private By lnkLogout = By.id("logout_sidebar_link");

    // 2. Constructor
    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // 3. Acciones (Métodos de negocio)

    /**
     * Verifica si el título "Products" es visible. 
     * Este es nuestro "Assertion Point" para confirmar que el login fue exitoso.
     */
    public boolean isTitleVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(lblTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getTitleText() {
        return driver.findElement(lblTitle).getText();
    }

    /**
     * Método extra: Para cerrar sesión y dejar el sistema limpio
     */
    public void logout() {
        driver.findElement(btnBurgerMenu).click();
        wait.until(ExpectedConditions.elementToBeClickable(lnkLogout)).click();
    }
}