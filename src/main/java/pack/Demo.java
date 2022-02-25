package pack;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import org.testng.annotations.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;

		

public class Demo {
	public static void main(String[] args) throws InterruptedException, IOException {
			
			//1. Start the proxy on some port
			BrowserMobProxy myProxy = new BrowserMobProxyServer();
			
			myProxy.start(0);
			
			//2. Set SSL and HTTP proxy in SeleniumProxy
			Proxy seleniumProxy=new Proxy();
			seleniumProxy.setHttpProxy("localhost:" +myProxy.getPort());
			seleniumProxy.setSslProxy("localhost:" +myProxy.getPort());
			
			//3. Add Capability for PROXY in DesiredCapabilities
			DesiredCapabilities capability=new DesiredCapabilities();
			capability.setCapability(CapabilityType.PROXY, seleniumProxy);
			capability.acceptInsecureCerts();
			capability.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
			
			//4. Set captureTypes
			
			  EnumSet <CaptureType> captureTypes=CaptureType.getAllContentCaptureTypes();
			  captureTypes.addAll(CaptureType.getCookieCaptureTypes());
			  captureTypes.addAll(CaptureType.getHeaderCaptureTypes());
			  captureTypes.addAll(CaptureType.getRequestCaptureTypes());
			  captureTypes.addAll(CaptureType.getResponseCaptureTypes());
			  
			  //5. setHarCaptureTypes with above captureTypes
			  myProxy.setHarCaptureTypes(captureTypes);
			 
			//6. HAR name
			myProxy.newHar("Mahi");
			
			//7. Start browser and open URL
			
			WebDriverManager.chromedriver().setup();
			
			ChromeOptions options=new ChromeOptions();
			options.merge(capability);
			WebDriver driver=new ChromeDriver(options);
			
			//Print Driver Capabilities
			System.out.println("Driver Capabilities===> \n" +((RemoteWebDriver)driver).getCapabilities().asMap().toString());
			
			driver.get("https://www.google.com");
			
			driver.manage().window().maximize();
			
			Thread.sleep(5000);
			
			Har har = myProxy.getHar();
			
			File myHARFile=new File(System.getProperty("user.dir")+"/HARFolderr/googleHAR1.har");
			har.writeTo(myHARFile);
			
			System.out.println("==> HAR details has been successfully written in the file.....");
		
			driver.close();
		
}

}
/*public class Demo {
    public static void main(String[] args) throws IOException, InterruptedException {

        //BrowserMobProxy
        BrowserMobProxy server = new BrowserMobProxyServer();
        server.start(0);
        server.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
        server.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        server.newHar("Google");

        //PHANTOMJS_CLI_ARGS
        ArrayList<String> cliArgsCap = new ArrayList<String>();
        cliArgsCap.add("--proxy=localhost:"+server.getPort());
        cliArgsCap.add("--ignore-ssl-errors=yes");

        //DesiredCapabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,"C:\\phantomjs.exe");

        //WebDriver
        WebDriver driver = new PhantomJSDriver(capabilities);
        driver.get("https://www.google.co.in");

        //HAR
        Har har = server.getHar();
        FileOutputStream fos = new FileOutputStream("C:\\HAR-Information.har");
        har.writeTo(fos);
        server.stop();
        driver.close();
    }
}*/