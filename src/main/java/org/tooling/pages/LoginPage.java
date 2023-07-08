package org.tooling.pages;


import lombok.extern.log4j.Log4j2;
import org.tooling.selenium.BrowserUtils;
import org.tooling.pages.base.SeoMonitorBasePage;


@Log4j2
public class LoginPage extends SeoMonitorBasePage {

    public LoginPage(BrowserUtils driver) {
        super(driver);
    }

    @Override
    public void initElements() {
        getElementIfVisibleUsingProperty("seomonitor.login.header");
    }

    public void setEmailInput(String email) {
        setText(
                getElementIfVisibleUsingProperty("seomonitor.login.email.input"),
                email
        );
    }

    public void setPasswordInput(String password) {
        setText(
                getElementIfVisibleUsingProperty("seomonitor.login.password.input"),
                password
        );
    }

    public void clickLoginButton() {
       waitAndClickUsingProperty("seomonitor.login.loginButton");
    }

}
