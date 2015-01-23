package com.duad.spider;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import org.apache.http.protocol.HTTP;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class App 
{
    public static void main( String[] args )
    {
        startEngine();
    }

    public static void startEngine()
    {
        System.getProperties().setProperty("webdriver.chrome.driver","D:/chromedriver.exe");
        WebDriver webDriver=new ChromeDriver();
        WebDriver adDriver=new ChromeDriver();

        webDriver.get("http://www.yingmoo.com/shanghai/");
        List<Advertise> lstAdvertise = new ArrayList<Advertise>();

        WebElement divElementContainer=webDriver.findElement(By.className("meacon"));
        List<WebElement> lstADElement = divElementContainer.findElements(By.className("core"));

        List<String> lstAD = new ArrayList<String>();
        for(WebElement adElement:lstADElement)
        {
            Advertise advertise=new Advertise();
            WebElement titleElement = adElement.findElement(By.tagName("a"));
            String title = titleElement.getAttribute("title");
            String strHref = titleElement.getAttribute("href");
            advertise.setTitle(title);
            adDriver.get(strHref);
            WebElement adDetailElement = adDriver.findElement(By.id("baseinfo1"));
            WebElement imgElement = adDriver.findElement(By.className("MagicZoom"));

            String imgHref = imgElement.getAttribute("href");
            String domainName="http://img.yingmoo.com/";
            int domainLength=domainName.length();
            String imgName=imgHref.substring(domainLength);
            imgName=imgName.replaceAll("/","###");
            advertise.setImgName(imgName);
            List<WebElement> lstTdElement=adDetailElement.findElements(By.tagName("td"));
            String strCity=lstTdElement.get(3).getText();
            int firstSlash=strCity.indexOf("/");
            int lastSlash=strCity.lastIndexOf("/");
            if(firstSlash==lastSlash)
            {
                strCity=strCity.substring(0,firstSlash+1)+strCity;
            }
            advertise.setCity(strCity);
            advertise.setStyle(lstTdElement.get(1).getText());
            String strLocation = lstTdElement.get(35).getText();
            advertise.setLocation(strLocation);

            try
            {
                URL url = new URL(imgHref);
                URLConnection imgConnection =url.openConnection();
                InputStream imgInputStream = imgConnection.getInputStream();
                FileOutputStream fileOutputStream=new FileOutputStream("D:/spiderimg/"+strLocation+"---"+imgName);

                byte[] imgData=new byte[1024];
                int len=10;
                while ((len=imgInputStream.read(imgData))!=-1)
                {
                    fileOutputStream.write(imgData,0,len);
                }
                imgInputStream.close();
                fileOutputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            lstAdvertise.add(advertise);
            System.out.println(advertise.toString());
        }
        FileWriter fw;
        try
        {
            fw = new FileWriter("D:/Advertise.txt");
            String strAdvertises = JSON.toJSONString(lstAdvertise.toArray());
            fw.write(strAdvertises);
            fw.flush();
            fw.close();
        }
        catch (IOException e)
        {
            System.out.println("Could not find Advertise.txt");
        }
        webDriver.quit();
        adDriver.quit();
    }
}
