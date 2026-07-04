package com.demoshop.qa.core.config;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import lombok.Data;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

@Data
@Configuration
@ConfigurationProperties(prefix = "mobile")
@Profile("mobile")
public class MobileDriverConfig implements WebDriverProvider {

    private String platform = "android";
    private String deviceName = "emulator-5554";
    private String appPath = "apps/demo-shop.apk";
    private String appiumUrl = "http://localhost:4723";
    private String udid;
    private String platformVersion;

    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        try {
            URL appiumServerUrl = new URL(appiumUrl);
            if ("ios".equalsIgnoreCase(platform)) {
                return createIOSDriver(appiumServerUrl);
            }
            return createAndroidDriver(appiumServerUrl);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid Appium URL: " + appiumUrl, e);
        }
    }

    private AndroidDriver createAndroidDriver(URL url) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(deviceName)
                .setApp(appPath)
                .setAutomationName("UiAutomator2")
                .setNoReset(false)
                .setFullReset(false);

        if (platformVersion != null && !platformVersion.isBlank()) {
            options.setPlatformVersion(platformVersion);
        }
        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }

        return new AndroidDriver(url, options);
    }

    private IOSDriver createIOSDriver(URL url) {
        XCUITestOptions options = new XCUITestOptions()
                .setDeviceName(deviceName)
                .setApp(appPath)
                .setAutomationName("XCUITest")
                .setNoReset(false);

        if (platformVersion != null && !platformVersion.isBlank()) {
            options.setPlatformVersion(platformVersion);
        }
        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }

        return new IOSDriver(url, options);
    }

    public boolean isAndroid() {
        return "android".equalsIgnoreCase(platform);
    }

    public boolean isIOS() {
        return "ios".equalsIgnoreCase(platform);
    }
}
