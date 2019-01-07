package com.wipro.hps.businesslogic;

import com.wipro.hps.common.generalutils;
import com.wipro.taf.selenium.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class SalesLink {

    SeleniumAutomationManager mgr;
    SeleniumAutomationBrowser browser;
    SeleniumAutomationConfManager conf;
    SeleniumAutomationUtils utils;
    SeleniumAutomationLogger log;
    String keywordName;
    String xPath;
    String parameter;
    Connection connection;
    private WebDriver webDriver;

    public SalesLink(SeleniumAutomationBrowser browser_, SeleniumAutomationUtils utils_, SeleniumAutomationLogger log_,

                     WebDriver webDriver_, SeleniumAutomationConfManager conf_, String action, String property, String param) {
        log = log_;
        browser = browser_;
        conf = conf_;
        webDriver = webDriver_;
        utils = utils_;

        keywordName = action;
        xPath = property;
        parameter = param;
    }

    public int deselect_CheckBox(String parameter) {

        int returnValue = 0;
        try {

            WebElement element = webDriver.findElement(By.xpath(xPath));
            Actions actions = new Actions(webDriver);
            actions.moveToElement(element).click().perform();
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

	/*public int compareProduct(String parameter) {

		println("");
		println(generalutils.getMethodName());
		println("");

		int returnValue = 0;
		try {

			ArrayList<String> product_name_list = new ArrayList<>();
			String Xpath = "";
			List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
			for (WebElement match : matches) {
				product_name_list.add(match.getText());
			}

			int numberOfElements = product_name_list.size();
			println("Number of Plans available: " + numberOfElements);
			int randomNumber1 = randomNumberGenrator(numberOfElements);
			int randomNumber2 = randomNumberGenrator(numberOfElements);

			while (randomNumber1 == randomNumber2) {
				randomNumber1 = randomNumberGenrator(numberOfElements);
				randomNumber2 = randomNumberGenrator(numberOfElements);
			}
			String product1 = product_name_list.get(randomNumber1);
			String product2 = product_name_list.get(randomNumber2);

			Xpath = ".//div[@class='plan__header plan__header__metal-level�'][div[div[contains(" + "translate(., '"
					+ product1.toLowerCase() + "', '" + product1.toUpperCase() + "'),'" + product1 + "')]]]/div/button";
			println("");
			println("Xpath ----->" + Xpath);
			webDriver.findElement(By.xpath(Xpath)).click();
			println("");
			println(product1 + "is added to cart");

			Thread.sleep(50000);
			Xpath = ".//div[@class='plan__header plan__header__metal-level�'][div[div[contains(" + "translate(., '"
					+ product2.toLowerCase() + "', '" + product2.toUpperCase() + "'),'" + product2 + "')]]]/div/button";
			println("");
			println("Xpath ----->" + Xpath);
			webDriver.findElement(By.xpath(Xpath)).click();
			println("");
			println(product2 + "is added to cart");

			Thread.sleep(30000);
			webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "PlanExistError"))).isDisplayed();
			webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Compare"))).isDisplayed();
			webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Replace"))).isDisplayed();
			webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Continue"))).isDisplayed();
			webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Close"))).isDisplayed();
			println("");
			println("The popup contains error, compare, replace, continue and close buttons");

			webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Compare"))).click();

			Xpath = "//td[@class='table-style__data compare-plans__list'][1]"
					+ "//div[@class='plan__header__heading__main-heading']";
			String compareProduct1 = webDriver.findElement(By.xpath(Xpath)).getText();
			println("");
			println("compareproduct1: " + compareProduct1);

			Xpath = "//td[@class='table-style__data compare-plans__list'][2]"
					+ "//div[@class='plan__header__heading__main-heading']";
			String compareProduct2 = webDriver.findElement(By.xpath(Xpath)).getText();
			println("");
			println("compareproduct2: " + compareProduct2);

			if ((product1.equals(compareProduct1)) && (product2.equals(compareProduct2))) {
				println("");
				println("Selected products are displayed in the comparision page");
			} else {
				println("");
				println("Selected products are not displayed in the comparision page");
				returnValue = 1;
			}
		} catch (Exception e) {
			println(e.toString());
			e.printStackTrace();
			returnValue = 1;
		}
		return returnValue;
	}
*/

    public int compareProduct(String parameter) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {

            ArrayList<String> product_name_list = new ArrayList<>();
            String Xpath = "";
            List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
            for (WebElement match : matches) {
                product_name_list.add(match.getText());
            }

            int numberOfElements = product_name_list.size();
            println("Number of Plans available: " + numberOfElements);
            int randomNumber1 = randomNumberGenrator(numberOfElements);
            int randomNumber2 = randomNumberGenrator(numberOfElements);

            while (randomNumber1 == randomNumber2) {
                randomNumber1 = randomNumberGenrator(numberOfElements);
                randomNumber2 = randomNumberGenrator(numberOfElements);
            }
            //String product1 = product_name_list.get(randomNumber1);
            //String product2 = product_name_list.get(randomNumber2);

            //Xpath = ".//div[@class='plan__header plan__header__metal-level�'][div[div[contains("
            //		+ "translate(., '"+product1.toLowerCase()+"', '"+product1.toUpperCase()+"'),'"+product1+"')]]]/div/button";
            Xpath = "//ul[@class='plan__list__header--default__container']/li[" + randomNumber1 + "]//button[@class='plan__header__actions__add-to-cart']";
            println("");
            println("Xpath ----->" + Xpath);
            String product1 = webDriver.findElement(By.xpath("//ul[@class='plan__list__header--default__container']/li["
                    + randomNumber1 + "]//div[@class='plan__header__heading__main-heading']")).getText();
            webDriver.findElement(By.xpath(Xpath)).click();

            println("");
            println(product1 + "is added to cart");

            Thread.sleep(5000);
            //Xpath = ".//div[@class='plan__header plan__header__metal-level�'][div[div[contains("
            //		+ "translate(., '"+product2.toLowerCase()+"', '"+product2.toUpperCase()+"'),'"+product2+"')]]]/div/button";
            Xpath = "//ul[@class='plan__list__header--default__container']/li[" + randomNumber2 + "]//button[@class='plan__header__actions__add-to-cart']";
            println("");
            println("Xpath ----->" + Xpath);
            String product2 = webDriver.findElement(By.xpath("//ul[@class='plan__list__header--default__container']/li["
                    + randomNumber2 + "]//div[@class='plan__header__heading__main-heading']")).getText();
            webDriver.findElement(By.xpath(Xpath)).click();
            println("");
            println(product2 + "is added to cart");

            Thread.sleep(3000);
            webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "PlanExistError"))).isDisplayed();
            webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Compare"))).isDisplayed();
            webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Replace"))).isDisplayed();
            webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Continue"))).isDisplayed();
            webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Close"))).isDisplayed();
            println("");
            println("The popup contains error, compare, replace, continue and close buttons");

            webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Compare"))).click();
            Thread.sleep(5000);
            Xpath = "//td[@class='table-style__data compare-plans__list'][1]"
                    + "//div[@class='plan__header__heading__main-heading']";
            String compareProduct1 = webDriver.findElement(By.xpath(Xpath)).getText();
            println("");
            println("compareproduct1: " + compareProduct1);

            Xpath = "//td[@class='table-style__data compare-plans__list'][2]"
                    + "//div[@class='plan__header__heading__main-heading']";
            String compareProduct2 = webDriver.findElement(By.xpath(Xpath)).getText();
            println("");
            println("compareproduct2: " + compareProduct2);

            if ((product1.equalsIgnoreCase(compareProduct1)) && (product2.equalsIgnoreCase(compareProduct2))) {
                println("");
                println("Selected products are displayed in the comparision page");
            } else {
                println("");
                println("Selected products are not displayed in the comparision page");
                returnValue = 1;
            }
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int enterCurrentDate(String parameter) {
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(parameter);
            String currentdate = sdf.format(date.getTime());
            webDriver.findElement(By.xpath(xPath)).sendKeys(currentdate);
            return 0;

        } catch (Exception e) {
            println(e.toString());
            browser.strErrorInfo = "" + e;
            return 1;
        }

    }

    public int replaceProduct(String parameter) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {

            ArrayList<String> product_name_list = new ArrayList<>();
            String Xpath = "";
            List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
            for (WebElement match : matches) {
                println(match.toString());
                product_name_list.add(match.getText());
                println(match.getText());
            }

            int numberOfElements = matches.size();
            println("Number of Plans available: " + numberOfElements);
            int randomNumber = randomNumberGenrator(numberOfElements);
            println("RandomNumber: " + randomNumber);
            String product = product_name_list.get(randomNumber);
            println("productname: " + product);
            Xpath = ".//div[@class='plan__header plan__header__metal-level�'][div[div[contains(" + "translate(., '"
                    + product.toLowerCase() + "', '" + product.toUpperCase() + "'),'" + product + "')]]]/div/button";
            println("");
            println("Xpath ----->" + Xpath);
            webDriver.findElement(By.xpath(Xpath)).click();
            println("");
            String replaceProduct = "";
            webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Replace"))).click();
            Thread.sleep(30000);
            webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "MiniCartIcon"))).click();

            replaceProduct = webDriver
                    .findElement(By.xpath(conf.getObjectProperties("Quote_Results", "CartIcon_PlanName"))).getText();
            println("replaceProduct: " + replaceProduct);

            if (product.equalsIgnoreCase(replaceProduct)) {
                println("");
                println("Product is replaced");
            } else {
                println("");
                println("Product is not replaced");
                returnValue = 1;
            }
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int readPlan(String Parameter) {
        println("");
        println(generalutils.getMethodName());
        println("");
        println("XPATH--->" + this.xPath);

        String plans = this.xPath;

        int returnValue = 0;
        try {
            ArrayList<String> product_name_list = new ArrayList<String>();
            String Xpath = "";
            List<WebElement> matches = this.webDriver.findElements(By.xpath(plans));
            for (WebElement match : matches) {
                product_name_list.add(match.getText());
            }

            int numberOfElements = product_name_list.size();

            if (numberOfElements == 0) {
                println("FAIL:NO PLANS AVILABLE FOR " + Parameter);
                returnValue = 1;
            } else {
                println("PASS:Total number of plans avilable for the  " + Parameter + " is " + numberOfElements);
                println("PASS:Plan name " + product_name_list);
                returnValue = 0;
            }
        } catch (Exception e) {
            returnValue = 1;
        }
        return returnValue;
    }

	/*public int validateAddtoCart(String parameter) {

		println("");
		println(generalutils.getMethodName());
		println("");

		int returnValue = 0;
		try {

			ArrayList<String> product_name_list = new ArrayList<>();
			String Xpath = "";
			List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
			for (WebElement match : matches) {
				product_name_list.add(match.getText());
			}

			int numberOfElements = product_name_list.size();

			if (parameter.equalsIgnoreCase("B")) {
				for (int i = 0; i < numberOfElements; i++) {
					println("");
					println("product name : " + product_name_list.get(i));
					Xpath = ".//div[@class='plan__header plan__header__metal-level�'][div[div[contains("
							+ "translate(., '" + product_name_list.get(i).toLowerCase() + "', '"
							+ product_name_list.get(i).toUpperCase() + "')," + "'" + product_name_list.get(i)
							+ "')]]]/div/button";
					Boolean result = webDriver.findElement(By.xpath(Xpath)).isDisplayed();

					if (result == true) {
						println("");
						println("Add-to-Cart is present for all plans displayed");
					} else {
						println("");
						println("FAIL");
						returnValue = 1;
					}
				}
			} else if (parameter.equalsIgnoreCase("A")) {
				for (int i = 0; i < numberOfElements; i++) {
					println("");
					println("product name : " + product_name_list.get(i));
					Xpath = ".//div[@class='plans-info__summary__title'][div[h1[contains(text(),'"
							+ product_name_list.get(i) + "')]]]/div/div/div/button";
					Boolean result = webDriver.findElement(By.xpath(Xpath)).isDisplayed();

					if (result == true) {
						println("");
						println("Select button is present for all plans displayed");
					} else {
						println("");
						println("FAIL");
						returnValue = 1;
					}
				}
			}
		} catch (Exception e) {
			println(e.toString());
			e.printStackTrace();
			returnValue = 1;
		}
		return returnValue;
	}*/

    public int validateAddtoCart(String parameter) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {

            ArrayList<String> product_name_list = new ArrayList<>();
            String Xpath = "";
            List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
            for (WebElement match : matches) {
                product_name_list.add(match.getText());
            }

            int numberOfElements = product_name_list.size();

            if (parameter.equalsIgnoreCase("B")) {
                for (int i = 0; i < numberOfElements; i++) {
                    println("");
                    println("product name : " + product_name_list.get(i));
					/*Xpath = ".//div[@class='plan__header plan__header__metal-level�'][div[div[contains("
							+ "translate(., '"+product_name_list.get(i).toLowerCase()+"', '"+product_name_list.get(i).toUpperCase()+"'),"
							+ "'"+product_name_list.get(i)+"')]]]/div/button";*/
                    Xpath = "//ul[@class='plan__list__header--default__container']/li[" + (i + 1) + "]//button[@class='plan__header__actions__add-to-cart' and contains(text(),'ADD TO CART')]";
                    Boolean result = webDriver.findElement(By.xpath(Xpath)).isDisplayed();

                    if (result == true) {
                        println("");
                        println("Add-to-Cart is present for all plans displayed");
                    } else {
                        println("");
                        println("FAIL");
                        returnValue = 1;
                    }
                }
            } else if (parameter.equalsIgnoreCase("A")) {
                for (int i = 0; i < numberOfElements; i++) {
                    println("");
                    println("product name : " + product_name_list.get(i));
                    Xpath = ".//div[@class='plans-info__summary__title'][div[h1[contains(text(),'" + product_name_list.get(i) + "')]]]/div/div/div/button";
                    Boolean result = webDriver.findElement(By.xpath(Xpath)).isDisplayed();

                    if (result == true) {
                        println("");
                        println("Select button is present for all plans displayed");
                    } else {
                        println("");
                        println("FAIL");
                        returnValue = 1;
                    }
                }
            }
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

	/*public int addToCompareScreen(String parameter) {

		println("");
		println(generalutils.getMethodName());
		println("");

		int returnValue = 0;
		try {

			ArrayList<String> product_name_list = new ArrayList<>();
			String Xpath = "";
			List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
			for (WebElement match : matches) {
				product_name_list.add(match.getText());
			}

			int numberOfElements = product_name_list.size();
			println("Number of Plans available: " + numberOfElements);

			String product1 = product_name_list.get(0);
			String product2 = product_name_list.get(1);

			if (parameter.equalsIgnoreCase("B")) {
				Xpath = ".//div[@class='plan__header plan__header__metal-level�'][div[div[contains(" + "translate(., '"
						+ product1.toLowerCase() + "', '" + product1.toUpperCase() + "'),'" + product1
						+ "')]]]/div/div/label";
				println("");
				println("Xpath ----->" + Xpath);
				webDriver.findElement(By.xpath(Xpath)).click();
				println("");
				println(product1 + "is added to compare screen");

				Xpath = ".//div[@class='plan__header plan__header__metal-level�'][div[div[contains(" + "translate(., '"
						+ product2.toLowerCase() + "', '" + product2.toUpperCase() + "'),'" + product2
						+ "')]]]/div/div/label";
				println("");
				println("Xpath ----->" + Xpath);
				WebElement we = webDriver.findElement(By.xpath(Xpath));
				JavascriptExecutor jse = (JavascriptExecutor) webDriver;
				jse.executeScript("arguments[0].scrollIntoView()", we);
				webDriver.findElement(By.xpath(Xpath)).click();
				println("");
				println(product2 + "is added to compare screen");
				println("");

				webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Compare_Now"))).click();

				Thread.sleep(5000);
				;
				Xpath = ".//td/div[div[contains(translate(.,'" + product1.toLowerCase() + "','" + product1.toUpperCase()
						+ "'),'" + product1 + "')]]/div/button";
				println("");
				println("Xpath ----->" + Xpath);
				webDriver.findElement(By.xpath(Xpath)).click();
				println("");
				println("Add to cart of " + product1 + " is clicked");
				println("");

				Thread.sleep(2000);
				println("");
				println("Xpath ----->" + Xpath);
				println("");
				String button_Name = webDriver.findElement(By.xpath(Xpath)).getText();

				if (button_Name.equalsIgnoreCase("Remove")) {
					println("");
					println("Add to cart button is changed to remove button");
					println("");
				} else {
					println("");
					println("Add to cart button is not changed to remove button");
					println("");
					returnValue = 1;
				}
			} else if (parameter.equalsIgnoreCase("A")) {
				Thread.sleep(5000);
				Xpath = "//div[@class='plans-info__summary__title'][div[h1[contains(text(),'" + product1
						+ "')]]]/div/div/div/fieldset/label";
				println("");
				println("Xpath ----->" + Xpath);
				webDriver.findElement(By.xpath(Xpath)).click();
				;
				println("");
				println(product1 + "is added to compare screen");

				Thread.sleep(5000);
				Xpath = "//div[@class='plans-info__summary__title'][div[h1[contains(text(),'" + product2 + "')]]]"
						+ "//div[contains(@class,'plans-info__summary__actions__compare-box')]";
				println("");
				println("Xpath ----->" + Xpath);
				webDriver.findElement(By.xpath(Xpath)).click();
				println("");
				println(product2 + "is added to compare screen");
				println("");

				webDriver.findElement(By.xpath(conf.getObjectProperties("Quote Results - test1 A", "Compare_Now")))
						.click();

				Thread.sleep(5000);
				Xpath = ".//th[h2[contains(text(),'" + product1 + "')]]/div/button";
				println("");
				println("Xpath ----->" + Xpath);
				webDriver.findElement(By.xpath(Xpath)).click();
				println("");
				println("Add to cart of " + product1 + " is clicked");
				println("");

				Thread.sleep(2000);
				println("");
				println("Xpath ----->" + Xpath);
				println("");
				String button_Name = webDriver.findElement(By.xpath(Xpath)).getText();

				if (button_Name.equalsIgnoreCase("Remove")) {
					println("");
					println("Add to cart button is changed to remove button");
					println("");
				} else {
					println("");
					println("Add to cart button is not changed to remove button");
					println("");
					returnValue = 1;
				}
			}
		} catch (Exception e) {
			println(e.toString());
			e.printStackTrace();
			returnValue = 1;
		}
		return returnValue;
	}
*/

    public int addToCompareScreen(String parameter) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {

            ArrayList<String> product_name_list = new ArrayList<>();
            String Xpath = "";
            List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
            for (WebElement match : matches) {
                product_name_list.add(match.getText());
            }

            int numberOfElements = product_name_list.size();
            println("Number of Plans available: " + numberOfElements);

            String product1 = product_name_list.get(0);
            println("Product 1" + product1);
            String product2 = product_name_list.get(1);
            println("Product 2" + product2);

            if (parameter.equalsIgnoreCase("B")) {
				/*Xpath = ".//div[@class='plan plan--default']/div/div/div[contains("
					+ "translate(., '"+product1.toLowerCase()+"', '"+product1.toUpperCase()+"'),'"+product1+"')]/div/div/label";*/
                Xpath = "//ul[@class='plan__list__header--default__container']/li[" + 1 + "]//label[contains(text(),'Compare')]";
                println("");
                println("Xpath ----->" + Xpath);
                webDriver.findElement(By.xpath(Xpath)).click();
                println("");
                println(product1 + "is added to compare screen");


                Xpath = "//ul[@class='plan__list__header--default__container']/li[" + 2 + "]//label[contains(text(),'Compare')]";
                println("");
                println("Xpath ----->" + Xpath);
                WebElement we = webDriver.findElement(By.xpath(Xpath));
                JavascriptExecutor jse = (JavascriptExecutor) webDriver;
                jse.executeScript("arguments[0].scrollIntoView()", we);
                webDriver.findElement(By.xpath(Xpath)).click();
                println("");
                println(product2 + "is added to compare screen");
                println("");

                webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Compare_Now"))).click();

                Thread.sleep(5000);
                ;
                Xpath = ".//td/div[div[contains(translate(.,'" + product1.toLowerCase() + "','" + product1.toUpperCase() + "'),'" + product1 + "')]]/div/button";
                println("");
                println("Xpath ----->" + Xpath);
                webDriver.findElement(By.xpath(Xpath)).click();
                println("");
                println("Add to cart of " + product1 + " is clicked");
                println("");

                Thread.sleep(2000);
                println("");
                println("Xpath ----->" + Xpath);
                println("");
                String button_Name = webDriver.findElement(By.xpath(Xpath)).getText();

                if (button_Name.equalsIgnoreCase("Remove")) {
                    println("");
                    println("Add to cart button is changed to remove button");
                    println("");
                } else {
                    println("");
                    println("Add to cart button is not changed to remove button");
                    println("");
                    returnValue = 1;
                }
            } else if (parameter.equalsIgnoreCase("A")) {
                Thread.sleep(5000);
                /*Xpath = "//div[@class='plans-info__summary__title'][div[h1[contains(text(),'"+product1+"')]]]/div/div/div/fieldset/label";*/
                Xpath = "//ul[@class='plan-list__items']/li[" + 1 + "]//p[contains(text(),'Compare')]";
                println("");
                println("Xpath ----->" + Xpath);
                WebElement we = webDriver.findElement(By.xpath(Xpath));

                we.click();
                println("");
                println(product1 + "is added to compare screen");

                Thread.sleep(5000);
                Xpath = "//ul[@class='plan-list__items']/li[" + 2 + "]//p[contains(text(),'Compare')]";
					/*Xpath = "//div[@class='plans-info__summary__title'][div[h1[contains(text(),'"+product2+"')]]]"
							+ "//div[contains(@class,'plans-info__summary__actions__compare-box')]";*/
                println("");
                println("Xpath ----->" + Xpath);
                WebElement we1 = webDriver.findElement(By.xpath(Xpath));

                JavascriptExecutor jsx = (JavascriptExecutor) webDriver;
                jsx.executeScript("window.scrollBy(0,500)", "");
                println("Scroll down by 500 Pixel");
                Thread.sleep(7000);


                we1.click();
                println("");
                println(product2 + "is added to compare screen");
                println("");


                WebElement we2 = webDriver.findElement(By.xpath(conf.getObjectProperties("Quote Results - test1 A", "Compare_Now")));

                we2.click();
                Thread.sleep(5000);
                Xpath = ".//th[h2[contains(text(),'" + product1 + "')]]/div/button";
                println("");
                println("Xpath ----->" + Xpath);
                JavascriptExecutor jsx1 = (JavascriptExecutor) webDriver;
                jsx1.executeScript("window.scrollBy(0,500)", "");
                println("Scroll down by 500 Pixel");
                Thread.sleep(7000);
                webDriver.findElement(By.xpath(Xpath)).click();
                println("");
                println("Add to cart of " + product1 + " is clicked");
                println("");

                Thread.sleep(2000);
                println("");
                println("Xpath ----->" + Xpath);
                println("");
                String button_Name = webDriver.findElement(By.xpath(Xpath)).getText();

                if (button_Name.equalsIgnoreCase("Remove")) {
                    println("");
                    println("Add to cart button is changed to remove button");
                    println("");
                } else {
                    println("");
                    println("Add to cart button is not changed to remove button");
                    println("");
                    returnValue = 1;
                }
            }
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int validateSortByPremium() {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {
            ArrayList<String> premium_value_string = new ArrayList<>();
            ArrayList<Float> premium_value_int = new ArrayList<>();
            String Xpath = "";
            List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
            for (WebElement match : matches) {
                premium_value_string.add(match.getText());
            }

            for (int i = 0; i < premium_value_string.size(); i++) {
                String input[] = premium_value_string.get(i).split("\\$");
                input[1] = input[1].trim();
                if (input[1].contains(",")) {
                    input[1] = input[1].replaceAll(",", "");
                }
                float premium_value = Float.valueOf(input[1]);
                println("Float.valueOf(input[1]): " + Float.valueOf(input[1]));
                premium_value_int.add(premium_value);
            }

            boolean result = isSortedNumerically(premium_value_int);

            if (result == true) {
                println("");
                println("The plans are sorted on the basis of premium value");
                println("");
            } else {
                println("");
                println("The plans are not sorted on the basis of premium value");
                println("");
            }

        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int validateSortByCarrier() {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {
            ArrayList<String> carrierName = new ArrayList<>();
            String Xpath = "";
            List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
            for (WebElement match : matches) {
                carrierName.add(match.getText());
            }

            boolean result = isSortedAlphabeticaly(carrierName);
            if (result == true) {
                println("");
                println("The plans are alphabetically ordered on the basis of carrier name");
                println("");
            } else {
                println("");
                println("FAIL");
                println("The plans are not alphabetically ordered on the basis of carrier name");
                println("");
                returnValue = 1;
            }

        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int validateInsuranceCompanyFilter(String parameter) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {
            String Xpath = "";
            ArrayList<String> carrierName = new ArrayList<>();
            List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
            for (WebElement match : matches) {
                carrierName.add(match.getText());
            }

            Set<String> setcarrierNameUniqueValues = new HashSet<>(carrierName);
            ArrayList<String> carrierNameUniqueValues = new ArrayList<>(setcarrierNameUniqueValues);

            if (carrierNameUniqueValues.size() != 1) {

                Collections.sort(carrierNameUniqueValues);
                println("");
                for (int i = 0; i < carrierNameUniqueValues.size(); i++) {
                    println("carrier name unique values: " + carrierNameUniqueValues.get(i));
                }
                println("");

                if (parameter.equalsIgnoreCase("A")) {
                    ArrayList<String> carrierNameCheckBox = new ArrayList<>();
                    List<WebElement> matches_1 = webDriver.findElements(By
                            .xpath(conf.getObjectProperties("Quote Results - test1 A", "InsuranceFilterCarrierName")));
                    for (WebElement match : matches_1) {
                        carrierNameCheckBox.add(match.getText());
                    }

                    Collections.sort(carrierNameCheckBox);
                    println("");
                    for (int i = 0; i < carrierNameCheckBox.size(); i++) {
                        println("carrier name: " + carrierNameCheckBox.get(i));
                    }
                    println("");

                    boolean result = true;
                    for (int i = 0; i < carrierNameUniqueValues.size(); i++) {
                        for (int j = 0; j < carrierNameCheckBox.size(); j++)
                            if (carrierNameUniqueValues.get(i).equalsIgnoreCase(carrierNameCheckBox.get(j)))
                                result = true;
                            else
                                result = false;
                    }
                    println("Result: " + result);
                    println("");

                    if (result == true) {
                        println("");
                        println("The plans are present in the filter");
                        println("");
                    } else {
                        println("");
                        println("FAIL");
                        println("The plans are not present in the filter");
                        println("");
                        returnValue = 1;
                    }
                } else if (parameter.equalsIgnoreCase("B")) {
                    webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Company_Filter")))
                            .click();
                    ArrayList<String> carrierNameCheckBox = new ArrayList<>();
                    List<WebElement> matches_1 = webDriver.findElements(
                            By.xpath(conf.getObjectProperties("Quote_Results", "InsuranceFilterCarrierName")));
                    for (WebElement match : matches_1) {
                        carrierNameCheckBox.add(match.getText());
                    }

                    Collections.sort(carrierNameCheckBox);
                    println("");
                    for (int i = 0; i < carrierNameCheckBox.size(); i++) {
                        println("carrier name: " + carrierNameCheckBox.get(i));
                    }
                    println("");

                    boolean result = true;
                    for (int i = 0; i < carrierNameUniqueValues.size(); i++) {
                        for (int j = 0; j < carrierNameCheckBox.size(); j++)
                            if (carrierNameUniqueValues.get(i).equalsIgnoreCase(carrierNameCheckBox.get(j)))
                                result = true;
                            else
                                result = false;
                    }
                    println("Result: " + result);
                    println("");

                    if (result == true) {
                        println("");
                        println("The plans are present in the filter");
                        println("");
                    } else {
                        println("");
                        println("FAIL");
                        println("The plans are not present in the filter");
                        println("");
                        returnValue = 1;
                    }
                    webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "Company_Filter")))
                            .click();
                }
            } else {
                println("");
                println("Insurance company filter is not present in the Quote result page as only one carrier related plans are displayed");
                println("");
            }
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int validatePlanTypeFilter(String parameter) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {
            if (parameter.equalsIgnoreCase("A")) {
                String Xpath = conf.getObjectProperties("Quote Results - test1 A", "PlanType");
                int count = elementPresent(Xpath);
                if (count == 0) {
                    webDriver.findElement(By.xpath(conf.getObjectProperties("Quote Results - test1 A", "PlanType_1")))
                            .click();
                    String planType = webDriver
                            .findElement(By.xpath(conf.getObjectProperties("Quote Results - test1 A", "PlanType_1")))
                            .getText();

                    ArrayList<String> productName = new ArrayList<>();
                    List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
                    for (WebElement match : matches) {
                        productName.add(match.getText());
                    }

                    boolean result = true;
                    for (int i = 0; i < productName.size(); i++) {
                        if (productName.get(i).contains(planType))
                            result = true;
                        else
                            result = false;
                    }
                    println("Result: " + result);
                    println("");

                    if (result == true) {
                        println("");
                        println("The plans are filtered based on the Plantype selected");
                        println("");
                    } else {
                        println("");
                        println("FAIL");
                        println("The plans are not filtered based on the Plantype selected");
                        println("");
                        // returnValue = 1;
                    }
                } else {
                    println("");
                    println("Plan Type is not present in the Quote result page");
                    println("");
                }
            } else if (parameter.equalsIgnoreCase("B")) {

                String Xpath = conf.getObjectProperties("Quote_Results", "PlanType");
                int count = elementPresent(Xpath);
                if (count == 0) {
                    webDriver.findElement(By.xpath(conf.getObjectProperties("Quote_Results", "PlanType_Filter")))
                            .click();
                    String planType = webDriver
                            .findElement(By.xpath(conf.getObjectProperties("Quote_Results", "PlanType_1"))).getText();

                    ArrayList<String> productName = new ArrayList<>();
                    List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
                    for (WebElement match : matches) {
                        productName.add(match.getText());
                    }

                    boolean result = true;
                    for (int i = 0; i < productName.size(); i++) {
                        if (productName.get(i).contains(planType))
                            result = true;
                        else
                            result = false;
                    }
                    println("Result: " + result);
                    println("");

                    if (result == true) {
                        println("");
                        println("The plans are filtered based on the Plantype selected");
                        println("");
                    } else {
                        println("");
                        println("FAIL");
                        println("The plans are not filtered based on the Plantype selected");
                        println("");
                        // returnValue = 1;
                    }
                }
            } else {
                println("");
                println("Plan Type is not present in the Quote result page");
                println("");
            }
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int validateRangeFilter(String parameter) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {
            if (parameter.equalsIgnoreCase("A")) {
                String startXpath = conf.getObjectProperties("Quote Results - test1 A", "RangeFilterTo");
                WebElement start = webDriver.findElement(By.xpath(startXpath));

                String endXpath = conf.getObjectProperties("Quote Results - test1 A", "RangeFilterMid");
                WebElement end = webDriver.findElement(By.xpath(endXpath));
                int result = actionsMethod(start, end);

                Thread.sleep(5000);
                if (result == 0) {
                    String maxValue_string = webDriver
                            .findElement(By
                                    .xpath(conf.getObjectProperties("Quote Results - test1 A", "RangeFilterMaxValue")))
                            .getText();
                    String minValue_string = webDriver
                            .findElement(By
                                    .xpath(conf.getObjectProperties("Quote Results - test1 A", "RangeFilterMinValue")))
                            .getText();

                    if ((maxValue_string.contains("$")) && (minValue_string.contains("$"))) {
                        String input1[] = maxValue_string.split("\\$");
                        String input2[] = minValue_string.split("\\$");

                        maxValue_string = input1[1].trim();
                        minValue_string = input2[1].trim();
                    }

                    float minValue = Float.valueOf(minValue_string);
                    float maxValue = Float.valueOf(maxValue_string);
                    println("float minValue: " + minValue);
                    println("float maxValue: " + maxValue);

                    String Xpath = "";
                    ArrayList<String> premium_value_string = new ArrayList<>();
                    ArrayList<Float> premium_value_int = new ArrayList<>();
                    List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
                    for (WebElement match : matches) {
                        premium_value_string.add(match.getText());
                    }

                    for (int i = 0; i < premium_value_string.size(); i++) {
                        String input[] = premium_value_string.get(i).split("\\$");
                        input[1] = input[1].trim();
                        float premium_value = Float.valueOf(input[1]);
                        premium_value_int.add(premium_value);
                    }

                    int returnfor = 0;
                    for (int i = 0; i < premium_value_int.size(); i++) {
                        if ((premium_value_int.get(i) > minValue) && (premium_value_int.get(i) < maxValue)) {
                            returnfor = 0;
                        } else {
                            returnfor = 1;
                        }
                    }
                    if (returnfor == 0) {
                        println("");
                        println("The plans are sorted according to the range filter min max values");
                        println("");
                    } else {
                        println("");
                        println("FAIL");
                        println("The plans are not sorted according to the range filter min max values");
                        println("");
                        returnValue = 1;
                    }

                } else {
                    println("");
                    println("FAIL");
                    println("The range filter min max values are not changed");
                    println("");
                    returnValue = 1;
                }
            } else if (parameter.equalsIgnoreCase("B")) {
                String startXpath = conf.getObjectProperties("Quote_Results", "RangeFilterTo");
                WebElement start = webDriver.findElement(By.xpath(startXpath));

                String endXpath = conf.getObjectProperties("Quote_Results", "RangeFilterMid");
                WebElement end = webDriver.findElement(By.xpath(endXpath));
                int result = actionsMethod(start, end);

                Thread.sleep(5000);
                if (result == 0) {
                    String maxValue_string = webDriver
                            .findElement(By.xpath(conf.getObjectProperties("Quote_Results", "RangeFilterMaxValue")))
                            .getText();
                    String minValue_string = webDriver
                            .findElement(By.xpath(conf.getObjectProperties("Quote_Results", "RangeFilterMinValue")))
                            .getText();

                    if ((maxValue_string.contains("$")) && (minValue_string.contains("$"))) {
                        String input1[] = maxValue_string.split("\\$");
                        String input2[] = minValue_string.split("\\$");

                        maxValue_string = input1[1].trim();
                        minValue_string = input2[1].trim();
                    }

                    float minValue = Float.valueOf(minValue_string);
                    float maxValue = Float.valueOf(maxValue_string);
                    println("float minValue: " + minValue);
                    println("float maxValue: " + maxValue);

                    String Xpath = "";
                    ArrayList<String> premium_value_string = new ArrayList<>();
                    ArrayList<Float> premium_value_int = new ArrayList<>();
                    List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
                    for (WebElement match : matches) {
                        premium_value_string.add(match.getText());
                    }

                    for (int i = 0; i < premium_value_string.size(); i++) {
                        String input[] = premium_value_string.get(i).split("\\$");
                        input[1] = input[1].trim();
                        float premium_value = Float.valueOf(input[1]);
                        premium_value_int.add(premium_value);
                    }

                    int returnfor = 0;
                    for (int i = 0; i < premium_value_int.size(); i++) {
                        if ((premium_value_int.get(i) > minValue) && (premium_value_int.get(i) < maxValue)) {
                            returnfor = 0;
                        } else {
                            returnfor = 1;
                        }
                    }
                    if (returnfor == 0) {
                        println("");
                        println("The plans are sorted according to the range filter min max values");
                        println("");
                    } else {
                        println("");
                        println("FAIL");
                        println("The plans are not sorted according to the range filter min max values");
                        println("");
                        returnValue = 1;
                    }

                } else {
                    println("");
                    println("FAIL");
                    println("The range filter min max values are not changed");
                    println("");
                    returnValue = 1;
                }
            }
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int validateDeductibleFilter(String parameter) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {
            if (parameter.equalsIgnoreCase("A")) {
                String Xpath = conf.getObjectProperties("Quote Results - test1 A", "DeductibleFilter");
                int count = elementPresent(Xpath);

                if (count == 0) {
                    String startXpath = conf.getObjectProperties("Quote Results - test1 A", "DeductibleFilterTo");
                    WebElement start = webDriver.findElement(By.xpath(startXpath));

                    String endXpath = conf.getObjectProperties("Quote Results - test1 A", "DeductibleFilterMid");
                    WebElement end = webDriver.findElement(By.xpath(endXpath));
                    int result = actionsMethod(start, end);

                    Thread.sleep(5000);
                    if (result == 0) {
                        String maxValue_string = webDriver.findElement(By
                                .xpath(conf.getObjectProperties("Quote Results - test1 A", "DeductibleFilterMaxValue")))
                                .getText();
                        String minValue_string = webDriver.findElement(By
                                .xpath(conf.getObjectProperties("Quote Results - test1 A", "DeductibleFilterMinValue")))
                                .getText();

                        if ((maxValue_string.contains("$")) && (minValue_string.contains("$"))) {
                            String input1[] = maxValue_string.split("\\$");
                            String input2[] = minValue_string.split("\\$");

                            maxValue_string = input1[1].trim();
                            minValue_string = input2[1].trim();
                        }

                        int minValue = Integer.parseInt(minValue_string);
                        int maxValue = Integer.parseInt(maxValue_string);
                        println("Integer minValue: " + minValue);
                        println("Integer maxValue: " + maxValue);

                        ArrayList<String> Deductible_value_string = new ArrayList<>();
                        ArrayList<Integer> Deductible_value_int = new ArrayList<>();
                        ArrayList<String> Deductible_value = new ArrayList<>();
                        List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
                        println("Number of Elements found: " + matches.size());
                        for (WebElement match : matches) {
                            String value = match.getText();
                            Deductible_value_string.add(value);
                        }

                        for (int i = 0; i < Deductible_value_string.size() - 1; i++) {
                            println("Array values: " + Deductible_value_string.get(i));
                            if (Deductible_value_string.get(i).matches(".*\\d+.*")) {
                                Deductible_value.add(Deductible_value_string.get(i));
                                Deductible_value_string.remove(Deductible_value_string.get(i));
                            }
                        }

                        for (int i = 0; i < Deductible_value.size(); i++) {
                            String input[] = Deductible_value.get(i).split(" ");
                            input[1] = input[1].replace("$", "");
                            input[1] = input[1].replace(",", "");
                            int value = Integer.parseInt(input[1]);
                            Deductible_value_int.add(value);
                        }

                        int returnfor = 0;
                        for (int i = 0; i < Deductible_value_int.size(); i++) {
                            if ((Deductible_value_int.get(i) > minValue) && (Deductible_value_int.get(i) < maxValue)) {
                                returnfor = 0;
                            } else {
                                returnfor = 1;
                            }
                        }
                        if (returnfor == 0) {
                            println("");
                            println("The plans are sorted according to the deductible filter min max values");
                            println("");
                        } else {
                            println("");
                            println("The plans are not sorted according to the deductible filter min max values");
                            println("");
                        }

                    } else {
                        println("");
                        println("FAIL");
                        println("The deductible filter min max values are not changed");
                        println("");
                        returnValue = 1;
                    }
                } else {
                    println("");
                    println("Deductible Filter is not present in the Quote result page");
                    println("");
                }
            } else if (parameter.equalsIgnoreCase("B")) {
                String Xpath = conf.getObjectProperties("Quote_Results", "DeductibleFilter");
                int count = elementPresent(Xpath);

                if (count == 0) {
                    String startXpath = conf.getObjectProperties("Quote_Results", "DeductibleFilterTo");
                    WebElement start = webDriver.findElement(By.xpath(startXpath));

                    String endXpath = conf.getObjectProperties("Quote_Results", "DeductibleFilterMid");
                    WebElement end = webDriver.findElement(By.xpath(endXpath));
                    int result = actionsMethod(start, end);

                    Thread.sleep(5000);
                    if (result == 0) {
                        String maxValue_string = webDriver
                                .findElement(
                                        By.xpath(conf.getObjectProperties("Quote_Results", "DeductibleFilterMaxValue")))
                                .getText();
                        String minValue_string = webDriver
                                .findElement(
                                        By.xpath(conf.getObjectProperties("Quote_Results", "DeductibleFilterMinValue")))
                                .getText();

                        if ((maxValue_string.contains("$")) && (minValue_string.contains("$"))) {
                            String input1[] = maxValue_string.split("\\$");
                            String input2[] = minValue_string.split("\\$");
                            input1[1] = input1[1].trim();
                            input2[1] = input2[1].trim();
                            maxValue_string = input1[1].replace("K", "");
                            minValue_string = input2[1].replace("K", "");
                        }

                        int minValue = (int) (Float.valueOf(minValue_string) * 1000);
                        int maxValue = (int) (Float.valueOf(maxValue_string) * 1000);
                        println("Integer minValue: " + minValue);
                        println("Integer maxValue: " + maxValue);

                        ArrayList<String> Deductible_value_string = new ArrayList<>();
                        ArrayList<Integer> Deductible_value_int = new ArrayList<>();
                        ArrayList<String> Deductible_value = new ArrayList<>();
                        println("Deductible filter sorting");
                        List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
                        println("Number of Elements found: " + matches.size());
                        for (WebElement match : matches) {
                            Deductible_value_string.add(match.getText());
                            println("values: " + match.getText());
                        }

                        for (int i = 0; i < Deductible_value_string.size(); i++) {
                            if (Deductible_value_string.get(i).matches(".*\\d+.*")) {
                                Deductible_value.add(Deductible_value_string.get(i));
                                Deductible_value_string.remove(Deductible_value_string.get(i));
                            }
                        }

                        for (int i = 0; i < Deductible_value.size(); i++) {
                            String input[] = Deductible_value.get(i).split(" ");
                            input[1] = input[1].replace("$", "");
                            input[1] = input[1].replace(",", "");
                            int value = Integer.parseInt(input[1]);
                            Deductible_value_int.add(value);
                        }

                        int returnfor = 0;
                        for (int i = 0; i < Deductible_value_int.size(); i++) {
                            if ((Deductible_value_int.get(i) > minValue) && (Deductible_value_int.get(i) < maxValue)) {
                                returnfor = 0;
                            } else {
                                returnfor = 1;
                            }
                        }
                        if (returnfor == 0) {
                            println("");
                            println("The plans are sorted according to the deductible filter min max values");
                            println("");
                        } else {
                            println("");
                            println("FAIL");
                            println("The plans are not sorted according to the deductible filter min max values");
                            println("");
                            returnValue = 1;
                        }

                    } else {
                        println("");
                        println("FAIL");
                        println("The deductible filter min max values are not changed");
                        println("");
                        returnValue = 1;
                    }
                } else {
                    println("");
                    println("Deductible Filter is not present in the Quote result page");
                    println("");
                }
            }
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int validatePlanDetails(String parameter) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;
        try {
            if (parameter.equalsIgnoreCase("A")) {
                String startXpath = conf.getObjectProperties("Quote Results - test1 A", "DeductibleFilterTo");
                WebElement start = webDriver.findElement(By.xpath(startXpath));

                String endXpath = conf.getObjectProperties("Quote Results - test1 A", "DeductibleFilterMid");
                WebElement end = webDriver.findElement(By.xpath(endXpath));
                int result = actionsMethod(start, end);

                Thread.sleep(5000);
                if (result == 0) {
                    String maxValue_string = webDriver
                            .findElement(By.xpath(
                                    conf.getObjectProperties("Quote Results - test1 A", "DeductibleFilterMaxValue")))
                            .getText();
                    String minValue_string = webDriver
                            .findElement(By.xpath(
                                    conf.getObjectProperties("Quote Results - test1 A", "DeductibleFilterMinValue")))
                            .getText();

                    if ((maxValue_string.contains("$")) && (minValue_string.contains("$"))) {
                        String input1[] = maxValue_string.split("\\$");
                        String input2[] = minValue_string.split("\\$");

                        maxValue_string = input1[1].trim();
                        minValue_string = input2[1].trim();
                    }

                    float minValue = Float.valueOf(minValue_string);
                    float maxValue = Float.valueOf(maxValue_string);
                    println("float minValue: " + minValue);
                    println("float maxValue: " + maxValue);

                    String Xpath = "";
                    ArrayList<String> premium_value_string = new ArrayList<>();
                    ArrayList<Float> premium_value_int = new ArrayList<>();
                    List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
                    for (WebElement match : matches) {
                        premium_value_string.add(match.getText());
                    }

                    for (int i = 0; i < premium_value_string.size(); i++) {
                        String input[] = premium_value_string.get(i).split("\\$");
                        input[1] = input[1].trim();
                        float premium_value = Float.valueOf(input[1]);
                        premium_value_int.add(premium_value);
                    }

                    int returnfor = 0;
                    for (int i = 0; i < premium_value_int.size(); i++) {
                        if ((premium_value_int.get(i) > minValue) && (premium_value_int.get(i) < maxValue)) {
                            returnfor = 0;
                        } else {
                            returnfor = 1;
                        }
                    }
                    if (returnfor == 0) {
                        println("");
                        println("The plans are sorted according to the range filter min max values");
                        println("");
                    } else {
                        println("");
                        println("FAIL");
                        println("The plans are not sorted according to the range filter min max values");
                        println("");
                        returnValue = 1;
                    }

                } else {
                    println("");
                    println("FAIL");
                    println("The range filter min max values are not changed");
                    println("");
                    returnValue = 1;
                }
            } else if (parameter.equalsIgnoreCase("B")) {
                String startXpath = conf.getObjectProperties("Quote_Results", "DeductibleFilterTo");
                WebElement start = webDriver.findElement(By.xpath(startXpath));

                String endXpath = conf.getObjectProperties("Quote_Results", "DeductibleFilterMid");
                WebElement end = webDriver.findElement(By.xpath(endXpath));
                int result = actionsMethod(start, end);

                Thread.sleep(5000);
                if (result == 0) {
                    String maxValue_string = webDriver
                            .findElement(
                                    By.xpath(conf.getObjectProperties("Quote_Results", "DeductibleFilterMaxValue")))
                            .getText();
                    String minValue_string = webDriver
                            .findElement(
                                    By.xpath(conf.getObjectProperties("Quote_Results", "DeductibleFilterMinValue")))
                            .getText();

                    if ((maxValue_string.contains("$")) && (minValue_string.contains("$"))) {
                        String input1[] = maxValue_string.split("\\$");
                        String input2[] = minValue_string.split("\\$");

                        maxValue_string = input1[1].trim();
                        minValue_string = input2[1].trim();
                    }

                    float minValue = Float.valueOf(minValue_string);
                    float maxValue = Float.valueOf(maxValue_string);
                    println("float minValue: " + minValue);
                    println("float maxValue: " + maxValue);

                    String Xpath = "";
                    ArrayList<String> premium_value_string = new ArrayList<>();
                    ArrayList<Float> premium_value_int = new ArrayList<>();
                    List<WebElement> matches = webDriver.findElements(By.xpath(xPath));
                    for (WebElement match : matches) {
                        premium_value_string.add(match.getText());
                    }

                    for (int i = 0; i < premium_value_string.size(); i++) {
                        String input[] = premium_value_string.get(i).split("\\$");
                        input[1] = input[1].trim();
                        float premium_value = Float.valueOf(input[1]);
                        premium_value_int.add(premium_value);
                    }

                    int returnfor = 0;
                    for (int i = 0; i < premium_value_int.size(); i++) {
                        if ((premium_value_int.get(i) > minValue) && (premium_value_int.get(i) < maxValue)) {
                            returnfor = 0;
                        } else {
                            returnfor = 1;
                        }
                    }
                    if (returnfor == 0) {
                        println("");
                        println("The plans are sorted according to the range filter min max values");
                        println("");
                    } else {
                        println("");
                        println("FAIL");
                        println("The plans are not sorted according to the range filter min max values");
                        println("");
                        returnValue = 1;
                    }

                } else {
                    println("");
                    println("FAIL");
                    println("The range filter min max values are not changed");
                    println("");
                    returnValue = 1;
                }
            }
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public boolean isSortedAlphabeticaly(ArrayList<String> arraylist) {
        boolean isSorted = true;
        for (int i = 1; i < arraylist.size(); i++) {
            if (arraylist.get(i - 1).compareTo(arraylist.get(i)) > 0) {
                isSorted = false;
                break;
            }
        }
        return isSorted;
    }

    public boolean isSortedNumerically(ArrayList<Float> arraylist) {
        boolean isSorted = true;
        for (int i = 0; i < arraylist.size() - 1; i++) {

            if (arraylist.get(i) > arraylist.get(i + 1)) {
                isSorted = false;
            } else {
                isSorted = true;
            }
        }
        return isSorted;
    }

    public int actionsMethod(WebElement StartElement, WebElement endElement) {

        println("");
        println(generalutils.getMethodName());
        println("");

        int returnValue = 0;

        try {
            Actions builder = new Actions(webDriver);
            Action dragAndDrop = builder.clickAndHold(StartElement).moveToElement(endElement).release(endElement)
                    .build();

            dragAndDrop.perform();
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }

        return returnValue;
    }

    public int emailidfromtempapp(String parameter) {

        try {

            String textArea = webDriver.findElement(By.xpath(xPath)).getAttribute("value");
            println("text is" + textArea);
            conf.addRuntimeData("ILBCIMVALUE", textArea);
            println("inside the method1");
            parameter = textArea;
            println(parameter);
            return 0;
        } catch (Exception e) { // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    public int getenrollementnumberfromUIpasteinAWS(String parameter) {

        try {

            String textArea = webDriver.findElement(By.xpath(xPath)).getText();
            conf.addRuntimeData("ILBCIMVALUE", textArea);
            parameter = textArea;
            println(parameter);

            return 0;
        } catch (Exception e) { // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    public int getusernamefrommailpasteinUI(String parameter) {

        try {

            String textArea = webDriver.findElement(By.xpath(xPath)).getText();
            conf.addRuntimeData("ILBCIMVALUE1", textArea);
            parameter = textArea;
            println(parameter);

            return 0;
        } catch (Exception e) { // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    public int getpasswordfrommailpasteinUI(String parameter) {

        try {

            String textArea = webDriver.findElement(By.xpath(xPath)).getText();
            conf.addRuntimeData("ILBCIMVALUE2", textArea);
            parameter = textArea;
            println(parameter);

            return 0;
        } catch (Exception e) { // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    public int saleslink_Adminlogin(String Parameter) {
        println("");
        println(generalutils.getMethodName());
        println("");
        try {
            String delims = ",";
            String[] getValues = Parameter.split(delims);

            String admin_login_url = getValues[0];
            String admin_page_url = getValues[1];

            println("-----------------");
            println("admin login pag-->:" + admin_login_url);

            println("admin page_url-->" + admin_page_url);
            println("-----------------");

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            this.webDriver.get(admin_page_url);
            this.webDriver.manage().timeouts().pageLoadTimeout(20L, TimeUnit.SECONDS);

            return 0;
        } catch (Exception e) {
        }
        return 1;
    }

    public int saleslink_Adminlogin1(String Parameter) {
        println("");
        println(generalutils.getMethodName());
        println("");
        try {
            String delims = ",";
            String[] getValues = Parameter.split(delims);

            String admin_login_url = getValues[0];
            String admin_page_url = getValues[1];

            println("-----------------");
            println("admin login pag-->:" + admin_login_url);

            println("admin page_url-->" + admin_page_url);
            println("-----------------");

            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            println("tabs");
            this.webDriver.switchTo().window((String) tabs.get(0));
            this.webDriver.get(admin_login_url);
            this.webDriver.manage().timeouts().pageLoadTimeout(20L, TimeUnit.SECONDS);

            return 0;
        } catch (Exception e) {
        }
        return 1;
    }

    public int alerthandle1() throws Exception {
        int returnValue = 1;

        try {

            String parent = webDriver.getWindowHandle();

            Robot object = new Robot();
            object.keyPress(KeyEvent.VK_CONTROL);
            object.keyPress(KeyEvent.VK_T);
            object.keyRelease(KeyEvent.VK_T);
            object.keyRelease(KeyEvent.VK_CONTROL);

            println("Enter Key pressed");

//			webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.chord(Keys.CONTROL, "t"));
            println("New Tab Opened");
            Thread.sleep(2000);
            Set<String> handles = webDriver.getWindowHandles();
            println("-----------" + handles.size());
            for (String winHandle : webDriver.getWindowHandles()) {
                if (parent.equalsIgnoreCase(winHandle))
                    continue;
                webDriver.switchTo().window(winHandle);

                println("Driver switched to new tab");
            }
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    public int tpaDBCheckDependent(String parameter) {
        String[] value = parameter.split(",");
        String Driver_Path = "C:\\Assure_NXT\\Accessories\\lib\\Selenium\\utils\\chrome\\chromedriver_win.exe";
        System.setProperty("webdriver.chrome.driver", Driver_Path);
        WebDriver driver = new ChromeDriver();

        String QuoteId = conf.getParameterValue(value[0]);
        String relationUI = conf.getParameterValue(value[1]);
        String genderUI = conf.getParameterValue(value[2]);
        String smokeUI = "";
        if (conf.getParameterValue(value[3]).equalsIgnoreCase("NO")) {
            smokeUI = "false";
        } else {
            smokeUI = "true";
        }
        println("QuoteId ---> : " + QuoteId);
        println("relationUI---> : " + relationUI);
        println("genderUI---> : " + genderUI);
        println("smokeUI---> : " + smokeUI);
        int returnValue = 1;
        try {
            String DB_URL = "https://tpsql.myplanlink.com/";
            String userName = "viewpetest";
            String passWord = "q4i2nmkqdj63z4hp7248sa3khvz";
            String host = "hm1sw2e6nr8pr89.cspprptz1zip.us-east-1.rds.amazonaws.com";
            String dbName = "hpspetestdb";
            String Query = "Select quote_id,Quote_Data from Quote where quote_id='" + QuoteId + "';";

            driver.get(DB_URL);
            driver.manage().window().maximize();
            driver.findElement(By.id("pg_host")).sendKeys(host);
            driver.findElement(By.id("pg_user")).sendKeys(userName);
            driver.findElement(By.id("pg_password")).sendKeys(passWord);
            driver.findElement(By.id("pg_db")).sendKeys(dbName);
            driver.findElement(By.xpath("//button[@class='btn btn-block btn-primary open-connection']")).click();
            Thread.sleep(3000);
            WebElement webElement = driver.findElement(By.xpath("//div[@class='ace_scroller']"));
            Actions actions = new Actions(driver);
            actions.moveToElement(webElement).click().sendKeys(Query).build().perform();
            Thread.sleep(2000);

            driver.findElement(By.id("run")).click();
            Thread.sleep(2000);
            WebElement webElement1 = driver.findElement(By.id("results"));
            if (webElement1.isDisplayed()) {
                String QuoteIdDB = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[1]")).getText();
                if (QuoteId.equalsIgnoreCase(QuoteIdDB)) {
                    String QuoteData = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[2]"))
                            .getText();

                    System.out.println("Quote Data :" + QuoteData);
                    if (QuoteData.contains("<dependants>")) {
                        String[] Dependents = QuoteData.split("<dependants>");
                        String[] dep1 = Dependents[1].split("</dependants>");
                        println("Dependent Details : " + dep1[0]);
                        String[] DOB = QuoteData.split("<dob>");
                        String[] dob1 = Dependents[1].split("</dob>");
                        println("DOB Details : " + dob1[0]);

                        String[] gender1 = dep1[0].split("<gender>");
                        String[] gender = gender1[1].split("</gender>");
                        println("Gender :" + gender[0]);
                        String[] smoker1 = dep1[0].split("<smoker>");
                        String[] smoker = smoker1[1].split("</smoker>");
                        println("Smoker :" + smoker[0]);
                        String[] relation1 = dep1[0].split("<relationShipWithConsumer>");
                        String[] relation = relation1[1].split("</relationShipWithConsumer>");
                        println("Relationship with customer :" + relation[0]);

                        if ((relationUI.equalsIgnoreCase(relation[0])) && (smokeUI.equalsIgnoreCase(smoker[0]))
                                && (genderUI.equalsIgnoreCase(gender[0]))) {
                            println("Added dependent value matches with DB value");
                            returnValue = 0;
                        } else {
                            println("Value Mismatch");
                            returnValue = 1;
                        }
                    } else {
                        println("Dependent data is not available for this quote ID");
                        returnValue = 1;
                    }

                } else {
                    println("Quote ID mismatch");
                    returnValue = 1;

                }
            } else {
                println("Result not displayed");
                returnValue = 1;
            }
            driver.quit();
            println("Exiting the chrome browser");
            return returnValue;
        } catch (Exception e) {
            driver.quit();
            println("Exiting the chrome browser");

            e.printStackTrace();
            return returnValue;
        }

    }


    public boolean StaleElementHandleByID(String elementID) throws InterruptedException {
        int count = 0;
        boolean clicked = false;
        while ((count < 6) || (!clicked)) {
            try {

                WebElement yourSlipperyElement = this.webDriver.findElement(By.xpath(elementID));

                JavascriptExecutor jse = (JavascriptExecutor) webDriver;
                jse.executeScript("arguments[0].scrollIntoView()", yourSlipperyElement);
                Thread.sleep(5000L);
                yourSlipperyElement.click();
                clicked = true;
                println("Delete Clicked " + clicked);
                break;
            } catch (StaleElementReferenceException e) {
                println("Trying to recover from a stale element :" + e.getMessage());
                browser.strErrorInfo = "Trying to recover from a stale element :" + e.getMessage();
                count++;
                clicked = false;
            } catch (Exception e) {
                println("Trying to recover from a stale element :" + e.getMessage());
                browser.strErrorInfo = "Trying to recover from a stale element :" + e.getMessage();
                count++;
                clicked = false;
            }
        }
        return clicked;
    }


    public int isElementExist(String parameter, String elementname) {
        println("Keyword");
        try {
            if (this.webDriver.findElement(By.xpath(parameter)).isDisplayed()) {
                println("PASS " + elementname + " Element present");
            }

            return 0;
        } catch (Exception e) {
            println("FAIL " + elementname + "Element not present");
            browser.strErrorInfo = "FAIL : Element " + elementname + " is not present :";
            this.browser.close();
        }
        return 1;
    }

    public int Validate_LogoColor_Setup_A_Temp() {
        int passcount = 0;

        String update_new_logo = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_updatenewlogo_button");
        String quote_basic = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_quoteform_basic_rb");
        String quote_advanced = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_quoteform_advance_rb");
        String cobrowse_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_enablebrowse_yes_rb");
        String cobrowse_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_enablebrowse_no_rb");
        String enablechat_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_enablechat_yes_rb");
        String enablechat_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_enablechat_no_rb");
        String displaycontactus_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_displaycontact_yes_rb");
        String displaycontactus_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_displaycontact_no_rb");
        String displayphone_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_displayphone_yes_rb");
        String displayphone_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_displayphone_no_rb");
        String message_cb = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_message_checkbox");
        String multilang_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_langenabled_rb");
        String multilang_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_langdisabled_rb");
        String defaultlang_dd = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_quotelang_dd");
        String update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_Logo_Update_btn");
        String popup_msg = this.conf.getObjectProperties("Test1A", "popup_msg");
        String popup_close = this.conf.getObjectProperties("Test1A", "popup_msg_close_button");
        String fname = this.conf.getObjectProperties("Test1A", "firstname_label");
        String lname = this.conf.getObjectProperties("Test1A", "lastname_label");
        String zip = this.conf.getObjectProperties("Test1A", "zip_label");
        String what_ins_need = this.conf.getObjectProperties("Test1A", "what_ins_need_label");
        String gender = this.conf.getObjectProperties("Test1A", "gender_label");
        String dob = this.conf.getObjectProperties("Test1A", "DOB_label");
        String tobacco = this.conf.getObjectProperties("Test1A", "tobacco_label");
        String phone = this.conf.getObjectProperties("Test1A", "phone_label");
        String agent = this.conf.getObjectProperties("Test1A", "agent_member_label");
        String email = this.conf.getObjectProperties("Test1A", "email_label");
        String subsidy = this.conf.getObjectProperties("Test1A", "subsidy_label");
        String enable_cobrowse = this.conf.getObjectProperties("Test1A", "enable_cobrowse_part");
        String enable_chat = this.conf.getObjectProperties("Test1A", "enable_chat_label");
        String contact_us = this.conf.getObjectProperties("Test1A", "contact_us_link");
        String call_us = this.conf.getObjectProperties("Test1A", "callus_mobile_number");
        try {

            webDriver.findElement(By.xpath(update)).click();
            ;  // Update default setup

            boolean quote_basic_status = webDriver.findElement(By.xpath(quote_basic)).isEnabled();
            println("quote_basic_status : " + quote_basic_status);
            boolean cobrowse_no_status = webDriver.findElement(By.xpath(cobrowse_no)).isEnabled();
            println("cobrowse_no_status : " + cobrowse_no_status);
            boolean enablechat_yes_status = webDriver.findElement(By.xpath(enablechat_yes)).isEnabled();
            println("enablechat_yes_status : " + enablechat_yes_status);
            boolean displaycontactus_yes_status = webDriver.findElement(By.xpath(displaycontactus_yes)).isEnabled();
            println("displaycontactus_yes_status : " + displaycontactus_yes_status);
            boolean displayphone_yes_status = webDriver.findElement(By.xpath(displayphone_yes)).isEnabled();
            println("displayphone_yes_status : " + displayphone_yes_status);
            boolean message_cb_status = webDriver.findElement(By.xpath(message_cb)).isEnabled();
            println("message_cb_status : " + message_cb_status);
            boolean multilang_yes_status = webDriver.findElement(By.xpath(multilang_yes)).isEnabled();
            println("multilang_yes_status : " + multilang_yes_status);

            if (quote_basic_status == true && cobrowse_no_status == true && enablechat_yes_status == true && displaycontactus_yes_status == true
                    && displayphone_yes_status == true && message_cb_status == true && multilang_yes_status == true) {
                ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
                ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
                this.webDriver.switchTo().window((String) tabs.get(2));
                this.webDriver.get("https://test.myplanlink.com/web/testonea");
                this.webDriver.manage().timeouts().pageLoadTimeout(40L, TimeUnit.SECONDS);

                if (webDriver.findElement(By.xpath(zip)).isDisplayed() && webDriver.findElement(By.xpath(what_ins_need)).isDisplayed() && webDriver.findElement(By.xpath(gender)).isDisplayed()
                        && webDriver.findElement(By.xpath(dob)).isDisplayed() && webDriver.findElement(By.xpath(tobacco)).isDisplayed() &&
                        webDriver.findElement(By.xpath(agent)).isDisplayed() &&
                        webDriver.findElement(By.xpath(email)).isDisplayed() && webDriver.findElement(By.xpath(subsidy)).isDisplayed()) {
                    println("Pass Message : All required fields has been displayed ");
                } else {
                    println("Fail Message : required fields are missing in UI ");
                    browser.strErrorInfo = "FAIL : Required fields missing under Logo and color setup";
                    return 1;
                }

                boolean chat_enable = webDriver.findElement(By.xpath(enable_chat)).isDisplayed();
                println("chat_enable available : " + chat_enable);
                boolean contact_us_enable = webDriver.findElement(By.xpath(contact_us)).isDisplayed();
                println("contact_us_enable available : " + contact_us_enable);
                boolean call_us_enable = webDriver.findElement(By.xpath(call_us)).isDisplayed();
                println("call_us_enable available : " + call_us_enable);

                if (chat_enable == false && contact_us_enable == true && call_us_enable == true) {
                    println("Pass Message : Enable Chat , Contact_us and call us fields available in UI ");
                } else {
                    println("Fail Message : Enable Chat , Contact_us and call us fields are not available in UI ");
                    browser.strErrorInfo = "FAIL : Enable Chat, Contact_us and call us fields are not available in UI";
                    return 1;
                }
            } else {
                println("Fail message : Admin setup is not default, please verify default settings");
                browser.strErrorInfo = "Fail message : Admin setup is not default, please verify default settings";
                return 1;
            }

            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));

            println("Part 2 - Change Default settings from PE Admin Test A");
            webDriver.findElement(By.xpath(quote_advanced)).click();
            webDriver.findElement(By.xpath(cobrowse_yes)).click();
            webDriver.findElement(By.xpath(enablechat_no)).click();
            webDriver.findElement(By.xpath(displaycontactus_no)).click();
            webDriver.findElement(By.xpath(displayphone_no)).click();
            webDriver.findElement(By.xpath(message_cb)).click();
            webDriver.findElement(By.xpath(multilang_no)).click();
            webDriver.findElement(By.xpath(update)).click();
            ;  // Update default setup

            boolean quote_advanced_status = webDriver.findElement(By.xpath(quote_advanced)).isEnabled();
            println("quote_advanced_status : " + quote_advanced_status);
            boolean cobrowse_yes_status = webDriver.findElement(By.xpath(cobrowse_yes)).isEnabled();
            println("cobrowse_yes_status : " + cobrowse_yes_status);
            boolean enablechat_no_status = webDriver.findElement(By.xpath(enablechat_no)).isEnabled();
            println("enablechat_no_status : " + enablechat_no_status);
            boolean displaycontactus_no_status = webDriver.findElement(By.xpath(displaycontactus_no)).isEnabled();
            println("displaycontactus_no_status : " + displaycontactus_no_status);
            boolean displayphone_no_status = webDriver.findElement(By.xpath(displayphone_no)).isEnabled();
            println("displayphone_no_status : " + displayphone_no_status);
            boolean message_cb_status1 = webDriver.findElement(By.xpath(message_cb)).isSelected();
            println("message_cb_status1 : " + message_cb_status1);


            if (quote_advanced_status == true && cobrowse_yes_status == true && enablechat_no_status == true && displaycontactus_no_status == true && displayphone_no_status == true
                    && message_cb_status1 == false) {
                ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
                //ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
                this.webDriver.switchTo().window((String) tabs.get(2));
                this.webDriver.get("https://test.myplanlink.com/web/testonea");
                this.webDriver.manage().timeouts().pageLoadTimeout(40L, TimeUnit.SECONDS);

                boolean zip_status = webDriver.findElement(By.xpath(zip)).isDisplayed();
                println("zip_available : " + zip_status);
                boolean what_ins_need_status = webDriver.findElement(By.xpath(what_ins_need)).isDisplayed();
                println("what_ins_need_status : " + what_ins_need_status);
                boolean gender_status = webDriver.findElement(By.xpath(gender)).isDisplayed();
                println("gender_status : " + gender_status);
                boolean dob_status = webDriver.findElement(By.xpath(dob)).isDisplayed();
                println("dob_status : " + dob_status);
                boolean tobacco_status = webDriver.findElement(By.xpath(tobacco)).isDisplayed();
                println("tobacco_status : " + tobacco_status);
                boolean agent_status = webDriver.findElement(By.xpath(agent)).isDisplayed();
                println("agent_status : " + agent_status);
                boolean email_status = webDriver.findElement(By.xpath(email)).isDisplayed();
                println("email_status : " + email_status);
                boolean subsidy_status = webDriver.findElement(By.xpath(subsidy)).isDisplayed();
                println("subsidy_status : " + subsidy_status);
                boolean fname_status = webDriver.findElement(By.xpath(fname)).isDisplayed();
                println("fname_status : " + fname_status);
                boolean lname_status = webDriver.findElement(By.xpath(lname)).isDisplayed();
                println("zip_available : " + lname_status);

                if (zip_status == true && what_ins_need_status == true && gender_status == true && dob_status == true && tobacco_status == true
                        && agent_status == true && email_status == true && subsidy_status == true && fname_status == true && lname_status == true) {
                    println("Pass Message : All required fields has been displayed ");
                } else {
                    println("Fail Message : required fields are missing in UI ");
                    browser.strErrorInfo = "FAIL : Required fields missing under Logo and color setup";
                    return 1;
                }

                boolean enable_cobrowse_status = webDriver.findElement(By.xpath(enable_cobrowse)).isDisplayed();
                println("enable_cobrowse_status is available : " + enable_cobrowse_status);

                if (enable_cobrowse_status == true) {
                    println("Pass Message : Co Browse field is dispaled in UI");
                } else {
                    println("Fail Message : Co Browse field is not available in UI ");
                    browser.strErrorInfo = "FAIL : Required field (Co Browse) missing under Logo and color setup";
                    return 1;
                }
            } else {
                println("Fail message : Admin setup is not changed properly, default settings has been changed as default");

                this.webDriver.switchTo().window((String) tabs.get(1));
                println("Change Default settings from PE Admin Test A");
                webDriver.findElement(By.xpath(quote_basic)).click();
                webDriver.findElement(By.xpath(cobrowse_no)).click();
                webDriver.findElement(By.xpath(enablechat_yes)).click();
                webDriver.findElement(By.xpath(displaycontactus_yes)).click();
                webDriver.findElement(By.xpath(displayphone_yes)).click();
                webDriver.findElement(By.xpath(message_cb)).click();
                webDriver.findElement(By.xpath(multilang_yes)).click();
                webDriver.findElement(By.xpath(update)).click();
                ;  // Update default setup
                println("Change to Default settings in PE Admin Test A");
                browser.strErrorInfo = "Fail message : Admin setup is not changed properly, default settings has been changed as default";
                return 1;
            }

        } catch (NoSuchElementException e) {
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        } finally {
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            println("Change Default settings from PE Admin Test A");
            webDriver.findElement(By.xpath(quote_basic)).click();
            webDriver.findElement(By.xpath(cobrowse_no)).click();
            webDriver.findElement(By.xpath(enablechat_yes)).click();
            webDriver.findElement(By.xpath(displaycontactus_yes)).click();
            webDriver.findElement(By.xpath(displayphone_yes)).click();
            webDriver.findElement(By.xpath(message_cb)).click();
            webDriver.findElement(By.xpath(multilang_yes)).click();
            webDriver.findElement(By.xpath(update)).click();
            ;  // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }


    public int Validate_crossell_A() throws InterruptedException, AWTException {
        int passcount = 0;

        String remove_new = this.conf.getObjectProperties("HealthPlanAdmin", "Crossell_remove_new");
        ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
        ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
        this.webDriver.switchTo().window((String) tabs.get(2));
        this.webDriver.get("https://test.myplanlink.com/web/testonea");
        this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);

        this.temp_A_values();

        return passcount;
    }

    public int Validate_crossell_B() throws InterruptedException, AWTException {
        int passcount = 0;

        String remove_new = this.conf.getObjectProperties("HealthPlanAdmin", "Crossell_remove_new");
        ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
        ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
        this.webDriver.switchTo().window((String) tabs.get(2));
        this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1");
        this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);

        this.temp_B_values();

        return passcount;
    }


    public void temp_B_values() {
        try {

            String aeAlert = this.conf.getObjectProperties("TB_QuoteHome", "AE_End_Alert_msg");
            String aeAlertClose = this.conf.getObjectProperties("TB_QuoteHome", "AE_End_Alert_close");
            String zip = this.conf.getObjectProperties("TB_QuoteHome", "Zipcode");
            String quote = this.conf.getObjectProperties("TB_QuoteHome", "GetQuote_btn");
            String primaryAppl = this.conf.getObjectProperties("TB_QuoteHome", "PrimaryAppl_btn");
            String dob = this.conf.getObjectProperties("TB_QuoteHome", "DOB");
            String gender = this.conf.getObjectProperties("TB_QuoteHome", "Gender");
            String smoker = this.conf.getObjectProperties("TB_QuoteHome", "Tobacco");
            String add = this.conf.getObjectProperties("TB_QuoteHome", "PrimaryAdd_btn");
            String next_btn = this.conf.getObjectProperties("TB_QuoteHome", "Home_Next_btn");
            String fname = this.conf.getObjectProperties("TB_QuoteHome", "Fname");
            String lname = this.conf.getObjectProperties("TB_QuoteHome", "lname");
            String phone = this.conf.getObjectProperties("TB_QuoteHome", "phone");
            String email = this.conf.getObjectProperties("TB_QuoteHome", "email");
            String agm = this.conf.getObjectProperties("TB_QuoteHome", "affiliation_dd");
            String qlevent = this.conf.getObjectProperties("TB_QuoteHome", "QlEvent");
            String getstarted = this.conf.getObjectProperties("TB_QuoteHome", "Home_Getstarted_btn");
            String alert = this.conf.getObjectProperties("TB_Checkout", "Model_alert");
            String alertDiscard = this.conf.getObjectProperties("TB_Checkout", "Alert_discard_btn");
            String select_plan1 = this.conf.getObjectProperties("TB_Checkout", "Select_plan1");
            String itemcart = this.conf.getObjectProperties("TB_QuoteResult", "Items_cart_btn");
            String checkout = this.conf.getObjectProperties("TB_QuoteResult", "Checkout_btn");
            String dental_inplan = this.conf.getObjectProperties("TB_Checkout", "Dental_in_opt_plan");

            if (webDriver.findElement(By.xpath(aeAlert)).isDisplayed()) {
                webDriver.findElement(By.xpath(aeAlertClose)).click();
            }
            webDriver.findElement(By.xpath(zip)).sendKeys("90001");
            webDriver.findElement(By.xpath(zip)).sendKeys(Keys.TAB);
            Thread.sleep(5000);
            webDriver.findElement(By.xpath(quote)).click();
            Thread.sleep(1000);
            webDriver.findElement(By.xpath(primaryAppl)).click();
            Thread.sleep(3000);
            webDriver.findElement(By.xpath(dob)).sendKeys("01/01/1989");
            webDriver.findElement(By.xpath(gender)).sendKeys("Male");
            webDriver.findElement(By.xpath(smoker)).sendKeys("YES");
            webDriver.findElement(By.xpath(add)).click();
            Thread.sleep(2000);
            Robot r;
            r = new Robot();
            r.keyPress(KeyEvent.VK_PAGE_DOWN);
            r.keyRelease(KeyEvent.VK_PAGE_DOWN);
			/*JavascriptExecutor je = (JavascriptExecutor) webDriver;
			WebElement element = webDriver.findElement(By.xpath("next_btn"));
			je.executeScript("arguments[0].scrollIntoView(true);",element);*/
            webDriver.findElement(By.xpath(next_btn)).click();
            Thread.sleep(2000);
            webDriver.findElement(By.xpath(fname)).sendKeys("TEST");
            webDriver.findElement(By.xpath(lname)).sendKeys("HPS");
            webDriver.findElement(By.xpath(phone)).sendKeys("5555555555");
            webDriver.findElement(By.xpath(email)).sendKeys("test@hps.com");
            webDriver.findElement(By.xpath(agm)).sendKeys("Avizva");
            webDriver.findElement(By.xpath(next_btn)).click();
            Thread.sleep(1000);
            webDriver.findElement(By.xpath(qlevent)).sendKeys("Marriage or Divorce");
			/*Robot r2;
			r2 = new Robot();
			r2.keyPress(KeyEvent.VK_PAGE_DOWN);
			r2.keyRelease(KeyEvent.VK_PAGE_DOWN);
			Thread.sleep(2000);*/
            webDriver.findElement(By.xpath("//button[normalize-space(text())='get started']")).click();
            Thread.sleep(10000);
            Robot r1;
            r1 = new Robot();
            r1.keyPress(KeyEvent.VK_PAGE_DOWN);
            r1.keyRelease(KeyEvent.VK_PAGE_DOWN);
            Thread.sleep(2000);
            webDriver.findElement(By.xpath("//a[normalize-space(text())='Carrier Contact List']/following::button[normalize-space(text())='ADD TO CART'][1]")).click();
            Thread.sleep(5000);
            webDriver.findElement(By.xpath(itemcart)).click();
            webDriver.findElement(By.xpath(checkout)).click();

//			webDriver.findElement(By.xpath(cartchekout)).click();
            Thread.sleep(10000);
            JavascriptExecutor jes = (JavascriptExecutor) webDriver;
            WebElement element2 = webDriver.findElement(By.xpath("dental_inplan"));
            jes.executeScript("arguments[0].scrollIntoView(true);", element2);

            boolean vision_status = webDriver.findElement(By.xpath(dental_inplan)).isDisplayed();
            println("vision_status : " + vision_status);
            if (vision_status == true) {
                println("Pass Message : Dental plan comes in add on section on checkout page");
            } else {
                println("Fail Message : Dental plan not available under add on section on checkout page");
                return;
            }
        } catch (NoSuchElementException e) {
            println("Error found while filling quote : " + e);
            browser.strErrorInfo = "Error found while filling quote : " + e;
            return;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void Robot() {
        // TODO Auto-generated method stub

    }


    public int PE_Subsidy_Yes_A() {
        int passcount = 0;

        String subsidy_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_subsidy_yes");
        String subsidy_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_subsidy_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String pe_subsidy_box = this.conf.getObjectProperties("Test1A", "configpe_subsidy_box");

        try {

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/testonea");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);

            boolean subsidy_enable = webDriver.findElement(By.xpath(pe_subsidy_box)).isDisplayed();
            println("subsidy_enable status : " + subsidy_enable);

            if (subsidy_enable == true) {
                println("Pass Message : Subsidy toogle is available at quote home page");
            } else {
                println("Fail message : Subsidy toogle is not available at quote home page");
                browser.strErrorInfo = "Fail message : Subsidy toogle is not available at quote home page";
                return 1;
            }

            //ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
        } catch (NoSuchElementException e) {
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        }
        return passcount;
    }
    //Validate Subsidy with Subsidy option is No in template A


    //Validate Subsidy with Subsidy option is No in template A

    public int PE_Subsidy_No_A() {
        int passcount = 0;

        String subsidy_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_subsidy_yes");
        String subsidy_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_subsidy_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String pe_subsidy_box = this.conf.getObjectProperties("Test1A", "configpe_subsidy_box");

        try {

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());

            println("Part 2 - PE Subsidy Test with SHOW SUBSIDY PLANS = No ");
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/testonea");
            this.webDriver.manage().timeouts().pageLoadTimeout(100L, TimeUnit.SECONDS);

            boolean subsidy_disable = webDriver.findElement(By.xpath(pe_subsidy_box)).isDisplayed();
            println("subsidy_disable status : " + subsidy_disable);

            if (subsidy_disable == false) {
                println("Pass Message : Subsidy toogle is not available at quote home page as expected");
            } else {
                println("Fail message : Subsidy toogle is available at quote home page");
                browser.strErrorInfo = "Fail message : Subsidy toogle is available at quote home page";
                return 1;
            }
            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        }

        return passcount;
    }

    public int PE_Subsidy_Yes_B() throws InterruptedException, AWTException {
        int passcount = 0;

        String subsidy_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_subsidy_yes");
        String subsidy_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_subsidy_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String pe_subsidy_box = this.conf.getObjectProperties("Test1B", "subsidy_div");

        try {

            println("Part 1 - PE Subsidy Test with SHOW SUBSIDY PLANS = Yes ");
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);
            this.temp_B_insurance();
            boolean subsidy_enable = webDriver.findElement(By.xpath(pe_subsidy_box)).isDisplayed();
            println("subsidy_enable status : " + subsidy_enable);

            if (subsidy_enable == true) {
                println("Pass Message : Subsidy toogle is available at quote home page");
            } else {
                println("Fail message : Subsidy toogle is not available at quote home page");
            }

            //ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
        } catch (NoSuchElementException e) {
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        }
        return passcount;
    }


    public int PE_Subsidy_No_B() throws InterruptedException, AWTException {
        int passcount = 0;

        String subsidy_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_subsidy_yes");
        String subsidy_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_subsidy_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String pe_subsidy_box = this.conf.getObjectProperties("Test1B", "subsidy_div");

        try {

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);
            this.temp_B_insurance();
            boolean subsidy_disable = webDriver.findElement(By.xpath(pe_subsidy_box)).isDisplayed();
            println("subsidy_disable status : " + subsidy_disable);

            if (subsidy_disable == false) {
                println("Pass Message : Subsidy toogle is not available at quote home page as expected");
            } else {
                println("Fail message : Subsidy toogle is available at quote home page");
                browser.strErrorInfo = "Fail message : Subsidy toogle is available at quote home page";
                return 1;
            }
            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        } finally {
            webDriver.findElement(By.xpath(subsidy_yes)).click();
            println("Subsidy plan option is Yes ");
            webDriver.findElement(By.xpath(pe_update)).click(); // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }


    public void temp_A_values() throws InterruptedException, AWTException {
        try {

            String gender = this.conf.getObjectProperties("TA_QuoteHome", "Gender");
            String dob = this.conf.getObjectProperties("TA_QuoteHome", "DOB");
            String smoker = this.conf.getObjectProperties("TA_QuoteHome", "Tobacco");
            String zip = this.conf.getObjectProperties("TA_QuoteHome", "Zipcode");
            String agm = this.conf.getObjectProperties("TA_QuoteHome", "AGM_dropdown");
            String email = this.conf.getObjectProperties("TA_QuoteHome", "Email");
            String next = this.conf.getObjectProperties("TA_QuoteHome", "Home_Next_btn");
            String qlevent = this.conf.getObjectProperties("TA_QuoteHome", "QlEvent");
            String getstarted = this.conf.getObjectProperties("TA_QuoteHome", "Home_Getstarted_btn");
            String choosehealth = this.conf.getObjectProperties("TA_QuoteResult", "Health_plan1_select");
            String itemcart = this.conf.getObjectProperties("TA_QuoteResult", "Items_cart_btn");
            String cartchekout = this.conf.getObjectProperties("TA_QuoteResult", "Checkout_btn");
            String alert1 = this.conf.getObjectProperties("TA_Checkout", "Model_alert");
            String alert_discard = this.conf.getObjectProperties("TA_Checkout", "Alert_discard_btn");
            String vision_opt = this.conf.getObjectProperties("TA_Checkout", "Vision_in_opt_plan");

            webDriver.findElement(By.xpath(zip)).sendKeys("90001");
            webDriver.findElement(By.xpath(zip)).sendKeys(Keys.TAB);
            Thread.sleep(5000);
            webDriver.findElement(By.xpath(gender)).sendKeys("Male");
            webDriver.findElement(By.xpath(dob)).sendKeys("01/01/1989");
            webDriver.findElement(By.xpath(smoker)).sendKeys("YES");
            webDriver.findElement(By.xpath(agm)).sendKeys("HPS");
            webDriver.findElement(By.xpath(email)).sendKeys("test@hps.com");
            webDriver.findElement(By.xpath(next)).click();
            Thread.sleep(1000);
            webDriver.findElement(By.xpath(qlevent)).sendKeys("Marriage or Divorce");
            webDriver.findElement(By.xpath(getstarted)).click();
            Thread.sleep(5000);
            if (webDriver.findElement(By.xpath(alert1)).isDisplayed()) {
                webDriver.findElement(By.xpath(alert_discard)).click();
                Thread.sleep(2000);
            }
            webDriver.findElement(By.xpath(choosehealth)).click();
            Thread.sleep(5000);
            WebElement element = webDriver.findElement(By.xpath("//span[@class='icon-cart hps-page-title__shipping-container__cart-icon']"));
            Actions action = new Actions(webDriver);
            action.moveToElement(element).moveToElement(webDriver.findElement(By.xpath("//span[@class='icon-cart hps-page-title__shipping-container__cart-icon']"))).click().build().perform();
            try {
                webDriver.findElement(By.xpath(cartchekout)).click();
            } catch (org.openqa.selenium.StaleElementReferenceException ex) {
                webDriver.findElement(By.xpath(cartchekout)).click();
            }
//			webDriver.findElement(By.xpath(cartchekout)).click();

            Thread.sleep(10000);

            Robot r;
            r = new Robot();
            r.keyPress(KeyEvent.VK_PAGE_DOWN);
            r.keyRelease(KeyEvent.VK_PAGE_DOWN);
	        /*JavascriptExecutor je = (JavascriptExecutor) webDriver;
			WebElement element1 = webDriver.findElement(By.xpath("vision_opt"));
			je.executeScript("arguments[0].scrollIntoView(true);",element1);
			*/

            boolean vision_status = webDriver.findElement(By.xpath(vision_opt)).isDisplayed();
            println("vision_status : " + vision_status);
            if (vision_status == true) {
                println("Pass Message : Vision plan comes in add on section on checkout page");
            } else {
                println("Fail Message : Vision plan not available under add on section on checkout page");
                browser.strErrorInfo = "Fail Message : Vision plan not available under add on section on checkout page";
                return;
            }
        } catch (NoSuchElementException e) {
            println("Error found while filling quote : " + e);
            browser.strErrorInfo = "Error found while filling quote : " + e;
            return;
        }
    }


    public void temp_B_insurance() throws InterruptedException, AWTException {
        //int passcount=0;
        try {

            String oealert = this.conf.getObjectProperties("Test1B", "oe_popup");
            String pop_close = this.conf.getObjectProperties("Test1B", "popup_close");
            String zip = this.conf.getObjectProperties("Test1B", "zip_text");
            String getquote = this.conf.getObjectProperties("Test1B", "getquote_link");
            String primary = this.conf.getObjectProperties("Test1B", "primappl_link");
            String dob = this.conf.getObjectProperties("Test1B", "dob_text");
            String gender = this.conf.getObjectProperties("Test1B", "gender_select");
            String toboco = this.conf.getObjectProperties("Test1B", "tobacco_select");
            String home_next = this.conf.getObjectProperties("TB_QuoteHome	Home_Next_btn", "next_btn");
            String subsidy_div = this.conf.getObjectProperties("Test1B", "subsidy_div");
            String add_primary = this.conf.getObjectProperties("Test1B", "add_primary");

            if (webDriver.findElement(By.xpath(oealert)).isDisplayed()) {
                webDriver.findElement(By.xpath(pop_close)).click();
            }
            webDriver.findElement(By.xpath(zip)).sendKeys("90001");
            webDriver.findElement(By.xpath(zip)).sendKeys(Keys.TAB);
            Thread.sleep(8000);
            webDriver.findElement(By.xpath(getquote)).click();
            Thread.sleep(3000);
            webDriver.findElement(By.xpath(primary)).click();
            webDriver.findElement(By.xpath(dob)).sendKeys("01/01/1989");
            webDriver.findElement(By.xpath(gender)).sendKeys("Male");
            webDriver.findElement(By.xpath(toboco)).sendKeys("YES");
            webDriver.findElement(By.xpath(add_primary)).click();
            Thread.sleep(1000);
            Robot r;
            r = new Robot();
            r.keyPress(KeyEvent.VK_PAGE_DOWN);
            r.keyRelease(KeyEvent.VK_PAGE_DOWN);
            webDriver.findElement(By.xpath("//button[normalize-space(text())='next']")).click();
            Thread.sleep(2000);

        } catch (NoSuchElementException e) {
            println("Error found : " + e);
            browser.strErrorInfo = "Error found while filling quote : " + e;
            return;
        }
        //return passcount;
    }


    public void temp_B_quoteresult() throws InterruptedException, AWTException {
        //int passcount=0;
        try {

            String aeAlert = this.conf.getObjectProperties("TB_QuoteHome", "AE_End_Alert_msg");
            String aeAlertClose = this.conf.getObjectProperties("TB_QuoteHome", "AE_End_Alert_close");
            String zip = this.conf.getObjectProperties("TB_QuoteHome", "Zipcode");
            String quote = this.conf.getObjectProperties("TB_QuoteHome", "GetQuote_btn");
            String primaryAppl = this.conf.getObjectProperties("TB_QuoteHome", "PrimaryAppl_btn");
            String dob = this.conf.getObjectProperties("TB_QuoteHome", "DOB");
            String gender = this.conf.getObjectProperties("TB_QuoteHome", "Gender");
            String smoker = this.conf.getObjectProperties("TB_QuoteHome", "Tobacco");
            String add = this.conf.getObjectProperties("TB_QuoteHome", "PrimaryAdd_btn");
            String next_btn = this.conf.getObjectProperties("TB_QuoteHome", "PrimaryApplicant_Next_Button");
            String fname = this.conf.getObjectProperties("TB_QuoteHome", "Fname");
            String lname = this.conf.getObjectProperties("TB_QuoteHome", "lname");
            String phone = this.conf.getObjectProperties("TB_QuoteHome", "phone");
            String email = this.conf.getObjectProperties("TB_QuoteHome", "email");
            String agm = this.conf.getObjectProperties("TB_QuoteHome", "affiliation_dd");
            String qlevent = this.conf.getObjectProperties("TB_QuoteHome", "QlEvent");
            String alert = this.conf.getObjectProperties("TB_Checkout", "Model_alert_result");
            String alertDiscard = this.conf.getObjectProperties("TB_Checkout", "Alert_discard_btn");

            if (webDriver.findElement(By.xpath(aeAlert)).isDisplayed()) {
                webDriver.findElement(By.xpath(aeAlertClose)).click();
            }
            webDriver.findElement(By.xpath(zip)).sendKeys("90001");
            webDriver.findElement(By.xpath(zip)).sendKeys(Keys.TAB);
            Thread.sleep(5000);
            webDriver.findElement(By.xpath(quote)).click();
            Thread.sleep(1000);
            webDriver.findElement(By.xpath(primaryAppl)).click();
            Thread.sleep(3000);
            webDriver.findElement(By.xpath(dob)).sendKeys("01/01/1989");
            webDriver.findElement(By.xpath(gender)).sendKeys("Male");
            webDriver.findElement(By.xpath(smoker)).sendKeys("YES");
            webDriver.findElement(By.xpath(add)).click();
            Thread.sleep(2000);
            Robot r;
            r = new Robot();
            r.keyPress(KeyEvent.VK_PAGE_DOWN);
            r.keyRelease(KeyEvent.VK_PAGE_DOWN);
			/*JavascriptExecutor je = (JavascriptExecutor) webDriver;
			WebElement element = webDriver.findElement(By.xpath("next_btn"));
			je.executeScript("arguments[0].scrollIntoView(true);",element);*/
            webDriver.findElement(By.xpath(next_btn)).click();
            Thread.sleep(2000);
            webDriver.findElement(By.xpath(fname)).sendKeys("TEST");
            webDriver.findElement(By.xpath(lname)).sendKeys("HPS");
            webDriver.findElement(By.xpath(phone)).sendKeys("5555555555");
            webDriver.findElement(By.xpath(email)).sendKeys("test@hps.com");
            webDriver.findElement(By.xpath(agm)).sendKeys("Avizva");
            webDriver.findElement(By.xpath(next_btn)).click();
            Thread.sleep(1000);
            webDriver.findElement(By.xpath(qlevent)).sendKeys("Marriage or Divorce");
			/*Robot r2;
			r2 = new Robot();
			r2.keyPress(KeyEvent.VK_PAGE_DOWN);
			r2.keyRelease(KeyEvent.VK_PAGE_DOWN);
			Thread.sleep(2000);*/
            webDriver.findElement(By.xpath("//button[normalize-space(text())='get started']")).click();
            Thread.sleep(10000);
            if (webDriver.findElement(By.xpath(alert)).isDisplayed()) {
                webDriver.findElement(By.xpath(alertDiscard)).click();
            }
            Thread.sleep(10000);
        } catch (NoSuchElementException e) {
            println("Error found : " + e);
            browser.strErrorInfo = "Error found : " + e;
            return;
        }
        //return passcount;
    }

    public void temp_a_quote() throws InterruptedException {
        //int passcount=0;
        try {

            String gender = this.conf.getObjectProperties("Test1A", "gender_select");
            String dob = this.conf.getObjectProperties("Test1A", "dob_input");
            String smoker = this.conf.getObjectProperties("Test1A", "smoker_select");
            String zipcode = this.conf.getObjectProperties("Test1A", "zipcode_input");
            String affinity = this.conf.getObjectProperties("Test1A", "Affinity_select");
            String email = this.conf.getObjectProperties("Test1A", "email_input");
            String subsidy = this.conf.getObjectProperties("Test1A", "subsidy_button");
            String householdincome = this.conf.getObjectProperties("Test1A", "householdincome_input");
            String householdsize = this.conf.getObjectProperties("Test1A", "householdsize_select");
            String home_next = this.conf.getObjectProperties("Test1A", "home_next");
            String qlevent = this.conf.getObjectProperties("Test1A", "qlevent_select");
            String getstarted = this.conf.getObjectProperties("Test1A", "get_started_btn");
            String popup_msg = this.conf.getObjectProperties("Test1A", "popup_msg");
            String popup_cont = this.conf.getObjectProperties("Test1A", "popup_continue");

            webDriver.findElement(By.xpath(gender)).sendKeys("Male");
            webDriver.findElement(By.xpath(dob)).sendKeys("01/01/1989");
            webDriver.findElement(By.xpath(smoker)).sendKeys("YES");
            webDriver.findElement(By.xpath(zipcode)).sendKeys("90001");
            webDriver.findElement(By.xpath(zipcode)).sendKeys(Keys.TAB);
            Thread.sleep(5000);
            webDriver.findElement(By.xpath(affinity)).sendKeys("HPS");
            webDriver.findElement(By.xpath(email)).sendKeys("abc@gmail.com");
            Thread.sleep(2000);
            WebElement element = webDriver.findElement(By.xpath(subsidy));
            Actions actions = new Actions(webDriver);
            actions.moveToElement(element);
            actions.click().perform();
            //webDriver.findElement(By.xpath(subsidy)).click();
            webDriver.findElement(By.xpath(householdincome)).sendKeys("25000");
            webDriver.findElement(By.xpath(householdsize)).sendKeys("2");
            webDriver.findElement(By.xpath(home_next)).click();
            Thread.sleep(2000);
            webDriver.findElement(By.xpath(qlevent)).sendKeys("Adoption or Birth of a child");
            webDriver.findElement(By.xpath(getstarted)).click();
            Thread.sleep(2000);
            if (webDriver.findElement(By.xpath(popup_msg)).isDisplayed()) {
                webDriver.findElement(By.xpath(popup_cont)).click();
            }


        } catch (NoSuchElementException e) {
            println("Error found : " + e);
            browser.strErrorInfo = "Error found : " + e;
            return;
        }
        //return passcount;
    }

    // Validate config PE WBE text availability option under manage Quote result page

    public int PE_WBE_YES_A() throws InterruptedException {


        int passcount = 0;

        String wbe_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_yes");
        String wbe_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String ta_wbe_note = this.conf.getObjectProperties("Test1A", "WBE_Disclaimer_Note");

        try {

            println("Part 1 - PE Is PE WBE Option Test >> Is PE WBE = Yes ");
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/testonea");
            this.webDriver.manage().timeouts().pageLoadTimeout(100L, TimeUnit.SECONDS);

            boolean wbe_enable = webDriver.findElement(By.xpath(ta_wbe_note)).isDisplayed();
            println("wbe_enable status : " + wbe_enable);

            if (wbe_enable == true) {
                println("Pass Message : WBE Desclaimer note displayed at quote home page");
            } else {
                println("Fail message : WBE Desclaimer note is not displayed at quote home page");
                browser.strErrorInfo = "Fail message : WBE Desclaimer note is not displayed at quote home page";
                return 1;
            }
            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            e.printStackTrace();
            browser.strErrorInfo = "Error found : " + e;
            return 1;
        }
        return passcount;
    }


    public int PE_WBE_No_A() throws InterruptedException {
        int passcount = 0;

        String wbe_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_yes");
        String wbe_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String ta_wbe_note = this.conf.getObjectProperties("Test1A", "WBE_Disclaimer_Note");

        try {

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/testonea");
            this.webDriver.manage().timeouts().pageLoadTimeout(100L, TimeUnit.SECONDS);
            boolean wbe_disable = webDriver.findElement(By.xpath(ta_wbe_note)).isDisplayed();
            println("wbe_disable status : " + wbe_disable);

            if (wbe_disable == false) {
                println("Pass Message : WBE Desclaimer note is not displayed at quote home page as expected");
            } else {
                println("Fail message : WBE Desclaimer note displayed at quote home page");
                browser.strErrorInfo = "Fail message : WBE Desclaimer note displayed at quote home page";
                return 1;
            }
            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            e.printStackTrace();
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        } finally {

            webDriver.findElement(By.xpath(wbe_yes)).click();
            println("Is PE WBE option changed to Default value i.e Yes ");
            webDriver.findElement(By.xpath(pe_update)).click(); // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }

    public int PE_WBE_YES_B() throws InterruptedException {
        int passcount = 0;
        String ta_wbe_note = this.conf.getObjectProperties("Test1A", "WBE_Disclaimer_Note");
        String oealert = this.conf.getObjectProperties("Test1B", "oe_popup");
        String pop_close = this.conf.getObjectProperties("Test1B", "popup_close");
        try {

            println("Part 1 - PE Is PE WBE Option Test >> Is PE WBE = Yes ");
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1");
            this.webDriver.manage().timeouts().pageLoadTimeout(100L, TimeUnit.SECONDS);
            if (webDriver.findElement(By.xpath(oealert)).isDisplayed()) {
                webDriver.findElement(By.xpath(pop_close)).click();
            }
            boolean wbe_enable = webDriver.findElement(By.xpath(ta_wbe_note)).isDisplayed();
            println("wbe_enable status : " + wbe_enable);

            if (wbe_enable == true) {
                println("Pass Message : WBE Desclaimer note displayed at quote home page");
            } else {
                println("Fail message : WBE Desclaimer note is not displayed at quote home page");
                browser.strErrorInfo = "Fail message : WBE Desclaimer note is not displayed at quote home page";
                return 1;
            }
            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            browser.strErrorInfo = "FAIL Message :" + e;
            e.printStackTrace();
            return 1;
        }
        return passcount;
    }

    public int PE_WBE_No_B() throws InterruptedException {
        int passcount = 0;

        String wbe_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_yes");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String ta_wbe_note = this.conf.getObjectProperties("Test1A", "WBE_Disclaimer_Note");
        String oealert = this.conf.getObjectProperties("Test1B", "oe_popup");
        String pop_close = this.conf.getObjectProperties("Test1B", "popup_close");

        try {

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1");
            this.webDriver.manage().timeouts().pageLoadTimeout(100L, TimeUnit.SECONDS);
            if (webDriver.findElement(By.xpath(oealert)).isDisplayed()) {
                webDriver.findElement(By.xpath(pop_close)).click();
            }
            boolean wbe_disable = webDriver.findElement(By.xpath(ta_wbe_note)).isDisplayed();
            println("wbe_disable status : " + wbe_disable);

            if (wbe_disable == false) {
                println("Pass Message : WBE Desclaimer note is not displayed at quote home page as expected");
            } else {
                println("Fail message : WBE Desclaimer note displayed at quote home page");
                browser.strErrorInfo = "Fail message : WBE Desclaimer note displayed at quote home page";
                return 1;
            }
            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            browser.strErrorInfo = "FAIL Message :" + e;
            e.printStackTrace();
            return 1;
        } finally {

            webDriver.findElement(By.xpath(wbe_yes)).click();
            println("Is PE WBE option changed to Default value i.e Yes ");
            webDriver.findElement(By.xpath(pe_update)).click(); // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }

    public int CheckElig_A_PE_Yes() throws InterruptedException {
        int passcount = 0;

        String qr_check = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_elig_quoteresult_cb");
        String co_check = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_elig_checkout_cb");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String qr_check_btn = this.conf.getObjectProperties("Test1A", "check_elig_btn");
        try {

            println("Part 1 - Eligibility of Checkout and Result is ON ");
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/testonea");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);
            this.temp_a_quote();

            boolean wbe_Elig_Enable = webDriver.findElement(By.xpath(qr_check_btn)).isDisplayed();
            println("resultpage_eligible status : " + wbe_Elig_Enable);

            if (wbe_Elig_Enable == true) {
                println("Pass Message : Check Eligibility button displayed at quote result page");
            } else {
                println("Fail Message : Check Eligibility button is not avilable at quote result page");
                browser.strErrorInfo = "Fail Message : Check Eligibility button is not avilable at quote result page";
                return 1;

            }

            this.webDriver.switchTo().window((String) tabs.get(1));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        }
        return passcount;
    }

    public int CheckElig_A_PE_No() throws InterruptedException {

        int passcount = 0;

        String qr_check = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_elig_quoteresult_cb");
        String co_check = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_elig_checkout_cb");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String qr_check_btn = this.conf.getObjectProperties("Test1A", "check_elig_btn");
        String co_elig_btn = this.conf.getObjectProperties("Test1A", "co_elig_btn");
        try {

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());

            println("Part 2 - Eligibility of Checkout and Result is OFF ");
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/testonea");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);

            this.temp_a_quote();

            boolean qr_check_disable = webDriver.findElement(By.xpath(qr_check_btn)).isDisplayed();
            println("resultpage_eligible status : " + qr_check_disable);

            if (qr_check_disable == false) {
                println("Pass Message : Check Eligibility button is not displayed at quote result page as expected");
            } else {
                println("Fail Message : Check Eligibility button is avilable at quote result page");
                browser.strErrorInfo = "Fail Message : Check Eligibility button is avilable at quote result page";
            }

            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            e.printStackTrace();
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        } finally {

            println("Eligibility of Checkout and Result is ON ");
            webDriver.findElement(By.xpath(qr_check)).click();
            webDriver.findElement(By.xpath(co_check)).click();
            webDriver.findElement(By.xpath(pe_update)).click(); // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }


    public int CheckElig_B_PE_Yes() throws InterruptedException, AWTException {
        int passcount = 0;

        String qr_check = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_elig_quoteresult_cb");
        String co_check = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_elig_checkout_cb");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String qr_check_btn = this.conf.getObjectProperties("Test1A", "check_elig_btn");
        try {

            println("Part 1 - Eligibility of Checkout and Result is ON ");
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);
            this.temp_B_quoteresult();

            boolean wbe_Elig_Enable = webDriver.findElement(By.xpath(qr_check_btn)).isDisplayed();
            println("resultpage_eligible status : " + wbe_Elig_Enable);

            if (wbe_Elig_Enable == true) {
                println("Pass Message : Check Eligibility button displayed at quote result page");
            } else {
                println("Fail Message : Check Eligibility button is not avilable at quote result page");

            }

            this.webDriver.switchTo().window((String) tabs.get(1));
        } catch (NoSuchElementException e) {
            browser.strErrorInfo = "Failed to find the element:" + qr_check_btn;
            browser.strErrorInfo = "Fail Message : Check Eligibility button is not avilable at quote result page";
            return 1;
        }
        return passcount;
    }

    public int CheckElig_B_PE_No() throws InterruptedException, AWTException {
        int passcount = 0;

        String qr_check = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_elig_quoteresult_cb");
        String co_check = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_elig_checkout_cb");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String qr_check_btn = this.conf.getObjectProperties("Test1A", "check_elig_btn");
        String co_elig_btn = this.conf.getObjectProperties("Test1A", "co_elig_btn");
        try {

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());

            println("Part 2 - Eligibility of Checkout and Result is OFF ");
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1/quotehome");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);

            this.temp_B_quoteresult();

            boolean qr_check_disable = webDriver.findElement(By.xpath(qr_check_btn)).isDisplayed();
            println("resultpage_eligible status : " + qr_check_disable);

            if (qr_check_disable == false) {
                println("Pass Message : Check Eligibility button is not displayed at quote result page as expected");
            } else {
                println("Fail Message : Check Eligibility button is avilable at quote result page");
                browser.strErrorInfo = "Fail Message : Check Eligibility button is avilable at quote result page";
                return 1;
            }

            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            browser.strErrorInfo = "Failed to find the element:" + qr_check_btn;
            return 1;
        } finally {

            println("Eligibility of Checkout and Result is ON ");
            webDriver.findElement(By.xpath(qr_check)).click();
            webDriver.findElement(By.xpath(co_check)).click();
            webDriver.findElement(By.xpath(pe_update)).click(); // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }

    public int Validate_PrefCarrier_A() throws InterruptedException {
        int passcount = 0;

        String kaiser_check = this.conf.getObjectProperties("HealthPlanAdmin", "Kaiser_checkbox");
        String americas_check = this.conf.getObjectProperties("HealthPlanAdmin", "americas_checkbox");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String kaiser_premium = this.conf.getObjectProperties("Test1A", "Kaiser_premium");
        try {

            println("Part 1 - Pref carrier check is Kaiser for Zip 90001 ");
            webDriver.findElement(By.xpath(kaiser_check)).click();
            webDriver.findElement(By.xpath(americas_check)).click();
            webDriver.findElement(By.xpath(pe_update)).click();

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/testonea");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);
            this.temp_a_quote();

            boolean pref_carr_kaiser = webDriver.findElement(By.xpath(kaiser_premium)).isDisplayed();
            println("pref_carr_kaiser status : " + pref_carr_kaiser);

            if (pref_carr_kaiser == true) {
                println("Pass Message : Preefered carrier is kaiser and functionality is working fine");
            } else {
                println("Fail Message : Prefered carrier functionality is not working fine");
                browser.strErrorInfo = "Fail Message : Prefered carrier functionality is not working fine";
                return 1;
            }
            Thread.sleep(1000);
            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            println("Error found : " + e);
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        } finally {
            println("Prefered carrier changed as default as Americas ");
            webDriver.findElement(By.xpath(kaiser_check)).click();
            webDriver.findElement(By.xpath(americas_check)).click();
            webDriver.findElement(By.xpath(pe_update)).click(); // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }


    public int PE_WBEElig_Quote_Yes_A() throws InterruptedException {
        int passcount = 0;

        String wbe_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_yes");
        String wbe_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String checkelig = this.conf.getObjectProperties("Test1A", "check_elig_btn");

        try {
            println("Part 1 - PE Is PE WBE Option Test >> Is PE WBE = Yes ");
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/testonea");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);
            this.temp_a_quote();
            boolean wbe_Elig_Enable = webDriver.findElement(By.xpath(checkelig)).isDisplayed();
            println("wbe_eligible status : " + wbe_Elig_Enable);

            if (wbe_Elig_Enable == true) {
                println("Pass Message : WBE Check Eligibility displayed at quote result page");
            } else {
                println("Fail Message : WBE Check Eligibility is not avilable at quote result page");
                browser.strErrorInfo = "Fail Message : WBE Check Eligibility is not avilable at quote result page";
            }

            //ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        }
        return passcount;
    }


    public int saleslink_Adminlogin2(String Parameter) {
        println("");
        println(generalutils.getMethodName());
        println("");
        try {
            String delims = ",";
            String[] getValues = Parameter.split(delims);


            String admin_login_url = this.conf.getParameterValue(getValues[0]);
            String admin_page_url = this.conf.getParameterValue(getValues[1]);
            String username = this.conf.getParameterValue(getValues[2]);
            String password = this.conf.getParameterValue(getValues[3]);

            println("-----------------");
            println("admin login pag--> " + admin_login_url);
            println("admin username-->" + username);
            println("admin pasword-->" + password);
            println("admin page_url-->" + admin_page_url);
            println("-----------------");
            String xpath_email = this.conf.getObjectProperties("HealthPlan", "emailAddress");
            String xpath_password = this.conf.getObjectProperties("HealthPlan", "password");
            String xpath_sigon = this.conf.getObjectProperties("HealthPlan", "signIn_button");

            this.webDriver.get(admin_login_url);
            this.webDriver.manage().timeouts().pageLoadTimeout(100L, TimeUnit.SECONDS);
            WebElement id = this.webDriver.findElement(By.xpath(xpath_email));

            WebElement pass = this.webDriver.findElement(By.xpath(xpath_password));

            WebElement signin = this.webDriver.findElement(By.xpath(xpath_sigon));

            id.sendKeys(new CharSequence[]{username});
            Thread.sleep(1000L);
            pass.sendKeys(new CharSequence[]{password});
            Thread.sleep(1000L);
            signin.click();
            Thread.sleep(1000L);


            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            this.webDriver.get(admin_page_url);
            this.webDriver.manage().timeouts().pageLoadTimeout(150L, TimeUnit.SECONDS);

            return 0;
        } catch (Exception e) {
            browser.strErrorInfo = "Time Out Exception - Taking too much time to load this site";
            this.browser.close();
            return 1;
        }

    }


    public int PE_WBEElig_Quote_No_A() throws InterruptedException {
        int passcount = 0;

        String wbe_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_yes");
        String wbe_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String checkelig = this.conf.getObjectProperties("Test1A", "check_elig_btn");

        try {

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            println("Part 2 - PE Is PE WBE Option Test >> Is PE WBE = No");
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/testonea");
            this.webDriver.manage().timeouts().pageLoadTimeout(100L, TimeUnit.SECONDS);

            this.temp_a_quote();
            boolean wbe_Elig_Disble = webDriver.findElement(By.xpath(checkelig)).isDisplayed();
            println("wbe_eligible status : " + wbe_Elig_Disble);

            if (wbe_Elig_Disble == false) {
                println("Pass Message : WBE Check Eligibility is not avilable at quote result page as expected");
            } else {
                println("Fail message : WBE Check Eligibility displayed at quote result page");
                browser.strErrorInfo = "Fail message : WBE Check Eligibility displayed at quote result page";
                return 1;
            }

            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            e.printStackTrace();
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        } finally {
            webDriver.findElement(By.xpath(wbe_yes)).click();
            println("Is PE WBE option changed to Default value i.e Yes ");
            webDriver.findElement(By.xpath(pe_update)).click(); // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }

    public int PE_WBEElig_Quote_Yes_B() throws InterruptedException {
        int passcount = 0;

        String wbe_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_yes");
        String wbe_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String checkelig = this.conf.getObjectProperties("Test1A", "WBE_Disclaimer_Note");

        try {

            println("Part 1 - PE Is PE WBE Option Test >> Is PE WBE = Yes ");
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1/quotehome");
            this.webDriver.manage().timeouts().pageLoadTimeout(60L, TimeUnit.SECONDS);
            this.temp_B_quote();
            boolean wbe_Elig_Enable = webDriver.findElement(By.xpath(checkelig)).isDisplayed();
            println("wbe_eligible status : " + wbe_Elig_Enable);

            if (wbe_Elig_Enable == true) {
                println("Pass Message : WBE Check Eligibility displayed at quote result page");
            } else {
                println("Fail Message : WBE Check Eligibility is not avilable at quote result page");
                return 1;

            }

            //ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
        } catch (NoSuchElementException e) {
            browser.strErrorInfo = "Failed to find the element:" + checkelig;
            return 1;
        }
        return passcount;
    }

    public int PE_WBEElig_Quote_No_B() throws InterruptedException {
        int passcount = 0;

        String wbe_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_yes");
        String wbe_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_mq_ispeweb_no");
        String pe_update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_update_button");
        String checkelig = this.conf.getObjectProperties("Test1A", "WBE_Disclaimer_Note");

        try {

            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            println("Part 2 - PE Is PE WBE Option Test >> Is PE WBE = No");
            this.webDriver.switchTo().window((String) tabs.get(2));
            this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1/quotehome");
            this.webDriver.manage().timeouts().pageLoadTimeout(100L, TimeUnit.SECONDS);

            this.temp_B_quote();

            boolean wbe_Elig_Disble = webDriver.findElement(By.xpath(checkelig)).isDisplayed();
            println("wbe_eligible status : " + wbe_Elig_Disble);

            if (wbe_Elig_Disble == false) {
                println("Pass Message : WBE Check Eligibility is not avilable at quote result page as expected");
            } else {
                println("Fail message : WBE Check Eligibility displayed at quote result page");
                browser.strErrorInfo = "Fail message : WBE Check Eligibility displayed at quote result page";
                return 1;
            }

            this.webDriver.switchTo().window((String) tabs.get(1));

        } catch (NoSuchElementException e) {
            ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            browser.strErrorInfo = "Failed to find the element:" + checkelig;
            return 1;
        } finally {
            webDriver.findElement(By.xpath(wbe_yes)).click();
            println("Is PE WBE option changed to Default value i.e Yes ");
            webDriver.findElement(By.xpath(pe_update)).click(); // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }

    public void temp_B_quote() {
        try {

            String aeAlert = this.conf.getObjectProperties("TB_QuoteHome", "AE_End_Alert_msg");
            String aeAlertClose = this.conf.getObjectProperties("TB_QuoteHome", "AE_End_Alert_close");
            String zip = this.conf.getObjectProperties("TB_QuoteHome", "Zipcode");
            String quote = this.conf.getObjectProperties("TB_QuoteHome", "GetQuote_btn");
            String primaryAppl = this.conf.getObjectProperties("TB_QuoteHome", "PrimaryAppl_btn");
            String dob = this.conf.getObjectProperties("TB_QuoteHome", "DOB");
            String gender = this.conf.getObjectProperties("TB_QuoteHome", "Gender");
            String smoker = this.conf.getObjectProperties("TB_QuoteHome", "Tobacco");
            String add = this.conf.getObjectProperties("TB_QuoteHome", "PrimaryAdd_btn");
            String next_btn = this.conf.getObjectProperties("TB_QuoteHome", "Home_Next_btn");
            String fname = this.conf.getObjectProperties("TB_QuoteHome", "Fname");
            String lname = this.conf.getObjectProperties("TB_QuoteHome", "lname");
            String phone = this.conf.getObjectProperties("TB_QuoteHome", "phone");
            String email = this.conf.getObjectProperties("TB_QuoteHome", "email");
            String agm = this.conf.getObjectProperties("TB_QuoteHome", "affiliation_dd");
            String qlevent = this.conf.getObjectProperties("TB_QuoteHome", "QlEvent");
            String getstarted = this.conf.getObjectProperties("TB_QuoteHome", "Home_Getstarted_btn");
            String alert = this.conf.getObjectProperties("TB_Checkout", "Model_alert");
            String alertDiscard = this.conf.getObjectProperties("TB_Checkout", "Alert_discard_btn");
            String select_plan1 = this.conf.getObjectProperties("TB_Checkout", "Select_plan1");
            String itemcart = this.conf.getObjectProperties("TB_QuoteResult", "Items_cart_btn");
            String checkout = this.conf.getObjectProperties("TB_QuoteResult", "Checkout_btn");
            String dental_inplan = this.conf.getObjectProperties("TB_Checkout", "Dental_in_opt_plan");

            if (webDriver.findElement(By.xpath(aeAlert)).isDisplayed()) {
                webDriver.findElement(By.xpath(aeAlertClose)).click();
            }
            webDriver.findElement(By.xpath(zip)).sendKeys("90001");
            webDriver.findElement(By.xpath(zip)).sendKeys(Keys.TAB);
            Thread.sleep(5000);
            webDriver.findElement(By.xpath(quote)).click();
            Thread.sleep(1000);
            webDriver.findElement(By.xpath(primaryAppl)).click();
            Thread.sleep(3000);
            webDriver.findElement(By.xpath(dob)).sendKeys("01/01/1989");
            webDriver.findElement(By.xpath(gender)).sendKeys("Male");
            webDriver.findElement(By.xpath(smoker)).sendKeys("YES");
            webDriver.findElement(By.xpath(add)).click();
            Thread.sleep(2000);
            Robot r;
            r = new Robot();
            r.keyPress(KeyEvent.VK_PAGE_DOWN);
            r.keyRelease(KeyEvent.VK_PAGE_DOWN);
  				/*JavascriptExecutor je = (JavascriptExecutor) webDriver;
  				WebElement element = webDriver.findElement(By.xpath("next_btn"));
  				je.executeScript("arguments[0].scrollIntoView(true);",element);*/
            webDriver.findElement(By.xpath(next_btn)).click();
            Thread.sleep(2000);
            webDriver.findElement(By.xpath(fname)).sendKeys("TEST");
            webDriver.findElement(By.xpath(lname)).sendKeys("HPS");
            webDriver.findElement(By.xpath(phone)).sendKeys("5555555555");
            webDriver.findElement(By.xpath(email)).sendKeys("test@hps.com");
            webDriver.findElement(By.xpath(agm)).sendKeys("Avizva");
            webDriver.findElement(By.xpath(next_btn)).click();
            Thread.sleep(1000);
            webDriver.findElement(By.xpath(qlevent)).sendKeys("Marriage or Divorce");
  				/*Robot r2;
  				r2 = new Robot();
  				r2.keyPress(KeyEvent.VK_PAGE_DOWN);
  				r2.keyRelease(KeyEvent.VK_PAGE_DOWN);
  				Thread.sleep(2000);*/
            webDriver.findElement(By.xpath("//button[normalize-space(text())='get started']")).click();
            Thread.sleep(10000);
            Robot r1;
            r1 = new Robot();
            r1.keyPress(KeyEvent.VK_PAGE_DOWN);
            r1.keyRelease(KeyEvent.VK_PAGE_DOWN);
            Thread.sleep(2000);
            webDriver.findElement(By.xpath("//a[normalize-space(text())='Carrier Contact List']/following::button[normalize-space(text())='ADD TO CART'][1]")).click();
            Thread.sleep(5000);
        } catch (Exception e) {
            println("Error found while filling quote : " + e);
            browser.strErrorInfo = "Error found while filling quote : " + e;
            return;
        }
    }

    public int tpaDBCheckConfig(String parameter) {
        String[] value = parameter.split(",");
        String Driver_Path = "C:\\Assure_NXT\\Accessories\\lib\\Selenium\\utils\\chrome\\chromedriver_win.exe";
        System.setProperty("webdriver.chrome.driver", Driver_Path);
        WebDriver driver = new ChromeDriver();
        int returnValue = 1;
        try {
            System.out.println("*******************");
            System.out.println("launching chrome browser");
//		System.setProperty("webdriver.chrome.driver", driverPath+"chromedriver.exe");
            String DB_URL = "https://tpsql.myplanlink.com/";

            String userName = "viewpetest";
            String passWord = "q4i2nmkqdj63z4hp7248sa3khvz";
            String host = "hm1sw2e6nr8pr89.cspprptz1zip.us-east-1.rds.amazonaws.com";
            String dbName = "hpspetestdb";
            //	String Query="Select quote_id,Quote_Data from Quote where quote_id='"+QuoteId+"';";
            String Query1 = "select pe_config_name,pe_config_value from pe_config where pe_id='28701' and pe_config_name not like '%color%'";
            String Query2 = "select pe_config_name,pe_config_value from pe_config where pe_id='28606' and pe_config_name not like '%color%'";
            driver.get(DB_URL);
            driver.manage().window().maximize();
            driver.findElement(By.id("pg_host")).sendKeys(host);
            driver.findElement(By.id("pg_user")).sendKeys(userName);
            driver.findElement(By.id("pg_password")).sendKeys(passWord);
            driver.findElement(By.id("pg_db")).sendKeys(dbName);
            driver.findElement(By.xpath("//button[@class='btn btn-block btn-primary open-connection']")).click();
            Thread.sleep(5000);
            WebElement webElement = driver.findElement(By.xpath("//div[@class='ace_scroller']"));
            Actions actions = new Actions(driver);
            actions.moveToElement(webElement);
            actions.click();
            if (conf.getParameterValue(value[0]).equalsIgnoreCase("A")) {
                actions.sendKeys(Query1);
            } else if (conf.getParameterValue(value[0]).equalsIgnoreCase("B")) {
                actions.sendKeys(Query2);
            }
            actions.build().perform();
            Thread.sleep(5000);
            driver.findElement(By.id("run")).click();
            Thread.sleep(5000);
            WebElement webElement1 = driver.findElement(By.id("results"));
            int we = driver.findElements(By.tagName("tr")).size();
            HashMap map = new HashMap<String, String>();
            System.out.println("no of rows fetched : " + we);
            if (webElement1.isDisplayed()) {
                for (int i = 1; i < we; i++) {
                    String Config_Name = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[" + i + "]/td[1]")).getText();
                    String Config_Value = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[" + i + "]/td[2]")).getText();
                    //println("Config_Name :"+Config_Name);
                    map.put(Config_Name, Config_Value);
                    //println("Config_Value :"+map.get(Config_Name));
                }

            } else {
                println("Result not displayed");
                browser.strErrorInfo = "Result not displayed";
            }
            int paramsize = value.length;
            for (int j = 1; j < paramsize; j++) {
                String DB_Value = map.get(value[j]).toString();
                println(value[j]);
                println("UI:" + conf.getParameterValue(value[j]));
                if (conf.getParameterValue(value[j]).equalsIgnoreCase("ADVANCED")) {
                    DB_Value = "ADVANCED";
                }
                println("DB:" + DB_Value);
                if (value[j].equalsIgnoreCase("planRecommend")) {
                    String UIValue = conf.getParameterValue(value[j]);
                    UIValue = UIValue.replaceAll("\\s", "").toUpperCase();
                    DB_Value = DB_Value.substring(3).toUpperCase();
                    println("UI plan :" + UIValue);
                    println("DB plan :" + DB_Value);
                    if (UIValue.contains(DB_Value)) {

                        returnValue = 0;
                    } else {
                        returnValue = 1;
                        break;
                    }
                } else if (conf.getParameterValue(value[j]).equalsIgnoreCase(DB_Value)) {
                    //println(value[j]);
                    returnValue = 0;
                } else {
                    returnValue = 1;
                    break;
                }
            }
            driver.quit();
            System.out.println("Exiting the chrome browser");
            return returnValue;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            driver.quit();
            println("Exiting the chrome browser");

            e.printStackTrace();
            return returnValue;
        }
    }


    public int tpaDBCheck(String parameter) {
        String Driver_Path = "C:\\Assure_NXT\\Accessories\\lib\\Selenium\\utils\\chrome\\chromedriver_win.exe";
        System.setProperty("webdriver.chrome.driver", Driver_Path);
        WebDriver driver = new ChromeDriver();

        String QuoteId = conf.getParameterValue(parameter);

        println("QuoteId ---> : " + QuoteId);
        String user_Id = "";
        String pe_id;
        String total_Amount;
        String Id = "";
        try {
            String DB_URL = "https://tpsql.myplanlink.com/";
            String userName = "viewpetest";
            String passWord = "q4i2nmkqdj63z4hp7248sa3khvz";
            String host = "hm1sw2e6nr8pr89.cspprptz1zip.us-east-1.rds.amazonaws.com";
            String dbName = "hpspetestdb";
            String Query = "Select quote_id,id,user_id,pe_id,total_amount from Cart where quote_id='" + QuoteId + "';";

            driver.get(DB_URL);
            driver.manage().window().maximize();
            driver.findElement(By.id("pg_host")).sendKeys(host);
            driver.findElement(By.id("pg_user")).sendKeys(userName);
            driver.findElement(By.id("pg_password")).sendKeys(passWord);
            driver.findElement(By.id("pg_db")).sendKeys(dbName);
            driver.findElement(By.xpath("//button[@class='btn btn-block btn-primary open-connection']")).click();
            Thread.sleep(3000);
            WebElement webElement = driver.findElement(By.xpath("//div[@class='ace_scroller']"));
            Actions actions = new Actions(driver);
            actions.moveToElement(webElement).click().sendKeys(Query).build().perform();
            Thread.sleep(2000);

            driver.findElement(By.id("run")).click();
            Thread.sleep(2000);
            WebElement webElement1 = driver.findElement(By.id("results"));
            if (webElement1.isDisplayed()) {
                String QuoteIdDB = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[1]")).getText();
                if (QuoteId.equalsIgnoreCase(QuoteIdDB)) {
                    Id = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[2]")).getText();
                    user_Id = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[3]")).getText();
                    pe_id = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[4]")).getText();
                    total_Amount = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[5]")).getText();
                    println("ID :" + Id);
                    println("UserID :" + user_Id);
                    println("pe_id :" + pe_id);
                    println("total_Amount :" + total_Amount);
                    WebElement ele = driver.findElement(By.xpath("//div[@class='ace_scroller']"));
                    Actions builder = new Actions(driver);
                    builder.moveToElement(ele).click().build().perform();
                    Thread.sleep(2000);
                    builder.moveToElement(ele).click().sendKeys(Keys.HOME + "--").build().perform();
                    Thread.sleep(2000);

                    String Query2 = "Select cart_id, plan_premium, persons_covered, plan_id from Cart_item where cart_id='"
                            + Id + "';";
                    println("Query2:" + Query2);
                    actions.moveToElement(webElement);
                    actions.click();
                    actions.sendKeys(Keys.ENTER);
                    actions.sendKeys(Query2);
                    actions.build().perform();
                    Thread.sleep(2000);
                    actions.sendKeys(Query2);
                    Thread.sleep(2000);
                    actions.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0061'));
                    driver.findElement(By.id("run")).click();
                    println("Clicked run");
                    Thread.sleep(2000);
                    webElement = driver.findElement(By.id("results"));
                    if (webElement1.isDisplayed()) {
                        String IdDB = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[1]"))
                                .getText();
                        if (Id.equalsIgnoreCase(IdDB)) {
                            String Premium = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[2]"))
                                    .getText();
                            String persons_covered = driver
                                    .findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[3]")).getText();
                            String plan_id = driver.findElement(By.xpath("//table[@id='results']/tbody/tr[1]/td[4]"))
                                    .getText();
                            println("Premium :" + Premium);
                            println("persons_covered :" + persons_covered);
                            println("plan_id :" + plan_id);
                        } else {
                            println("Id is different");
                        }
                    } else {
                        println("Result table is not displayed");
                    }
                } else {
                    println("QuoteId is different");
                }
            } else {
                println("Result table is not displayed");
            }

            driver.quit();
            println("Exiting the chrome browser");
        } catch (Exception e) {
            driver.quit();
            println("Exiting the chrome browser");

            e.printStackTrace();
            return 1;
        }

        return 0;
    }


    public int compareDefaultSelect(String parameter) {

        try {

            String expected = conf.getParameterValue(parameter);

            Select select = new Select(webDriver.findElement(By.xpath(xPath)));
            WebElement option = select.getFirstSelectedOption();
            String defaultItem = option.getText();

            println("Expected Value: " + defaultItem);
            println("Passing Value: " + parameter);

            if (parameter.equals(defaultItem)) {
                println("Text : " + parameter + " is default item of dropdown");
                return 0;
            } else {
                println("Text : " + parameter + " is not an default item of given dropdown");
                return 1;
            }

        } catch (Exception e) {
            println("Given item is not available in UI");
            return 1;
        }
    }

    public int Validate_LogoColor_Setup_B_Temp() {
        int passcount = 0;

        String update_new_logo = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_updatenewlogo_button");
        String quote_basic = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_quoteform_basic_rb");
        String quote_advanced = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_quoteform_advance_rb");
        String cobrowse_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_enablebrowse_yes_rb");
        String cobrowse_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_enablebrowse_no_rb");
        String enablechat_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_enablechat_yes_rb");
        String enablechat_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_enablechat_no_rb");
        String displaycontactus_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_displaycontact_yes_rb");
        String displaycontactus_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_displaycontact_no_rb");
        String displayphone_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_displayphone_yes_rb");
        String displayphone_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_displayphone_no_rb");
        String message_cb = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_message_checkbox");
        String multilang_yes = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_langenabled_rb");
        String multilang_no = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_langdisabled_rb");
        String defaultlang_dd = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_quotelang_dd");
        String update = this.conf.getObjectProperties("HealthPlanAdmin", "configpe_Logo_Update_btn");
        String popup_msg = this.conf.getObjectProperties("Test1B", "popup_msg");
        String popup_close = this.conf.getObjectProperties("Test1B", "popup_msg_close_button");
        String enable_cobrowse = this.conf.getObjectProperties("Test1A", "enable_cobrowse_part");
        String enable_chat = this.conf.getObjectProperties("Test1A", "enable_chat_label");
        String contact_us = this.conf.getObjectProperties("Test1A", "contact_us_link");
        String call_us = this.conf.getObjectProperties("Test1A", "callus_mobile_number");
        try {

            webDriver.findElement(By.xpath(update)).click();
            ;  // Update default setup

            boolean quote_advanced_status = webDriver.findElement(By.xpath(quote_advanced)).isEnabled();
            println("quote_advanced_status : " + quote_advanced_status);
            boolean cobrowse_yes_status = webDriver.findElement(By.xpath(cobrowse_yes)).isEnabled();
            println("cobrowse_yes_status : " + cobrowse_yes_status);
            boolean enablechat_yes_status = webDriver.findElement(By.xpath(enablechat_yes)).isEnabled();
            println("enablechat_yes_status : " + enablechat_yes_status);
            boolean displaycontactus_no_status = webDriver.findElement(By.xpath(displaycontactus_no)).isEnabled();
            println("displaycontactus_no_status : " + displaycontactus_no_status);
            boolean displayphone_yes_status = webDriver.findElement(By.xpath(displayphone_yes)).isEnabled();
            println("displayphone_yes_status : " + displayphone_yes_status);
            boolean message_cb_status = webDriver.findElement(By.xpath(message_cb)).isEnabled();
            println("message_cb_status : " + message_cb_status);
            boolean multilang_yes_status = webDriver.findElement(By.xpath(multilang_yes)).isEnabled();
            println("multilang_yes_status : " + multilang_yes_status);

            if (quote_advanced_status == true && cobrowse_yes_status == true && enablechat_yes_status == true && displaycontactus_no_status == true
                    && displayphone_yes_status == true && message_cb_status == true && multilang_yes_status == true) {
                ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
                ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
                this.webDriver.switchTo().window((String) tabs.get(2));
                this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1");
                this.webDriver.manage().timeouts().pageLoadTimeout(40L, TimeUnit.SECONDS);

                boolean popup_status = webDriver.findElement(By.xpath(popup_msg)).isEnabled();
                println("popup_status : " + popup_status);

                if (popup_status == true) {
                    println("Pass Message : Pop up message is displayed in UI");
                    webDriver.findElement(By.xpath(popup_close)).click();
                } else {
                    println("Fail message : Pop up not found");
                    browser.strErrorInfo = "Fail message : Pop up not found";
                    return 1;
                }

                boolean chat_enable = webDriver.findElement(By.xpath(enable_chat)).isDisplayed();
                println("chat_enable available : " + chat_enable);

                if (chat_enable == false) {
                    println("Pass Message : Enable Chat , Contact_us and call us fields availability matched with Setup ");
                } else {
                    println("Fail Message : Enable Chat , Contact_us and call us fields availability not matched with Setup ");
                    browser.strErrorInfo = "Fail Message : Enable Chat , Contact_us and call us fields availability not matched with Setup ";
                    return 1;
                }
            } else {
                println("Fail message : Admin setup is not default, please verify default settings");
                browser.strErrorInfo = "Fail message : Admin setup is not default, please verify default settings";
                return 1;
            }

            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));

            println("Part 2 - Change Default settings from PE Admin Test A");
            webDriver.findElement(By.xpath(quote_basic)).click();
            webDriver.findElement(By.xpath(cobrowse_no)).click();
            webDriver.findElement(By.xpath(enablechat_yes)).click();
            webDriver.findElement(By.xpath(displaycontactus_yes)).click();
            webDriver.findElement(By.xpath(displayphone_no)).click();
            webDriver.findElement(By.xpath(message_cb)).click();
            webDriver.findElement(By.xpath(multilang_no)).click();
            webDriver.findElement(By.xpath(update)).click();
            ;  // Update default setup

            boolean quote_basic_status = webDriver.findElement(By.xpath(quote_basic)).isEnabled();
            println("quote_basic_status : " + quote_basic_status);
            boolean cobrowse_no_status = webDriver.findElement(By.xpath(cobrowse_no)).isEnabled();
            println("cobrowse_no_status : " + cobrowse_no_status);
            boolean enablechat_yess_status = webDriver.findElement(By.xpath(enablechat_yes)).isEnabled();
            println("enablechat_yess_status : " + enablechat_yess_status);
            boolean displaycontact_no_status = webDriver.findElement(By.xpath(displaycontactus_no)).isEnabled();
            println("displaycontactus_no_status : " + displaycontactus_no_status);
            boolean displayphone_no_status = webDriver.findElement(By.xpath(displayphone_no)).isEnabled();
            println("displayphone_no_status : " + displayphone_no_status);
            boolean message_cb_status1 = webDriver.findElement(By.xpath(message_cb)).isSelected();
            println("message_cb_status1 : " + message_cb_status1);


            if (quote_basic_status == true && cobrowse_no_status == true && enablechat_yess_status == true && displaycontact_no_status == true && displayphone_no_status == true
                    && message_cb_status1 == false) {
                ((JavascriptExecutor) this.webDriver).executeScript("window.open()", new Object[0]);
                //ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
                this.webDriver.switchTo().window((String) tabs.get(2));
                this.webDriver.get("https://test.myplanlink.com/web/blackrockbusiness.1");
                this.webDriver.manage().timeouts().pageLoadTimeout(40L, TimeUnit.SECONDS);

                boolean chat_status = webDriver.findElement(By.xpath(enable_chat)).isDisplayed();
                println("chat_status is available : " + chat_status);

                if (chat_status == false) {
                    println("Pass Message : Enable Chat , Contact_us and call us fields availability matched with Setup ");
                } else {
                    println("Fail Message : Enable Chat , Contact_us and call us fields availability not matched with Setup ");
                    browser.strErrorInfo = "Fail Message : Enable Chat , Contact_us and call us fields availability not matched with Setup ";
                    return 1;
                }
            } else {
                println("Fail message : Admin setup is not changed properly, default settings has been reverted to default");

                this.webDriver.switchTo().window((String) tabs.get(1));
                println("Change Default settings from PE Admin Test A");
                webDriver.findElement(By.xpath(quote_advanced)).click();
                webDriver.findElement(By.xpath(cobrowse_yes)).click();
                webDriver.findElement(By.xpath(enablechat_yes)).click();
                webDriver.findElement(By.xpath(displaycontactus_no)).click();
                webDriver.findElement(By.xpath(displayphone_yes)).click();
                webDriver.findElement(By.xpath(message_cb)).click();
                webDriver.findElement(By.xpath(multilang_yes)).click();
                webDriver.findElement(By.xpath(update)).click();
                ;  // Update default setup
                println("Change to Default settings in PE Admin Test A");
                return 1;
            }

        } catch (NoSuchElementException e) {
            e.printStackTrace();
            browser.strErrorInfo = "FAIL Message :" + e;
            return 1;
        } finally {
            ArrayList tabs = new ArrayList(this.webDriver.getWindowHandles());
            this.webDriver.switchTo().window((String) tabs.get(1));
            println("Change Default settings from PE Admin Test A");
            webDriver.findElement(By.xpath(quote_advanced)).click();
            webDriver.findElement(By.xpath(cobrowse_yes)).click();
            webDriver.findElement(By.xpath(enablechat_yes)).click();
            webDriver.findElement(By.xpath(displaycontactus_no)).click();
            webDriver.findElement(By.xpath(displayphone_yes)).click();
            webDriver.findElement(By.xpath(message_cb)).click();
            webDriver.findElement(By.xpath(multilang_yes)).click();
            webDriver.findElement(By.xpath(update)).click();
            ;  // Update default setup
            println("Change to Default settings in PE Admin Test A");
        }
        return passcount;
    }

    public int beforePEDemoDB() {

        try {
            String count = "";
            String query = "";
            String comp_data_rowname = conf.getParameterValue("CREATE PE DEMO");
            conf.getComponentData("CREATE PE DEMO", comp_data_rowname);
            String Email = conf.getParameterValue("Email");

            //xpath for object
            String Xpath_textarea = conf.getObjectProperties("DB_inputbox", "Text_Area");
            println("Xpath for Text box is : " + Xpath_textarea);
            String run_query_button = conf.getObjectProperties("DB_inputbox", "button_Run_query");
            println("Xpath for Run_query_button is : " + run_query_button);
            String query_loading = conf.getObjectProperties("DB_inputbox", "query_loading");
            println("Xpath for query_loading is : " + query_loading);
            String Error_data = conf.getObjectProperties("DB_outputbox", "Error_data");
            println("Xpath for Error_data is : " + Error_data);
            String first_row_data = conf.getObjectProperties("DB_outputbox", "first_row_data");
            println("Xpath for first_row_data is : " + first_row_data);

            query = "select count(1) from pe_user where email_addr='" + Email + "';";
            println("-------------------------------------------------");
            println("query to insert: " + query);


            webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(Keys.CONTROL, "a");
            webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(Keys.DELETE);
            webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(query);
            webDriver.findElement(By.xpath(run_query_button)).click();
            WebDriverWait wait = new WebDriverWait(webDriver, 30);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(query_loading)));

            boolean noerrordata = webDriver.findElements(By.xpath(Error_data)).isEmpty();
            if (noerrordata) {

                count = webDriver.findElement(By.xpath(first_row_data)).getText();
                println("Count value for Email: " + Email + " is " + count);
                conf.addRuntimeData("user_count", count);
                return 0;
            } else {
                browser.strErrorInfo = "Error Message pops up after searching query: \n " + query;
                return 1;
            }
        } catch (Exception e) {
            println("Exception:  " + e);
            browser.strErrorInfo = "Exception: " + e;
            return 1;
        }
    }


    public int afterPEDemoDB() {

        int beforecountvalue = 0;
        int aftercountvalue = 0;
        try {
            String beforecount = conf.getParameterValue("user_count");
            beforecountvalue = Integer.parseInt(beforecount);
            aftercountvalue = 1;
            String pe_user_id = "";
            String count = "";
            String query = "";
            String comp_data_rowname = conf.getParameterValue("CREATE PE DEMO");
            conf.getComponentData("CREATE PE DEMO", comp_data_rowname);
            String Email = conf.getParameterValue("Email");

            //xpath for object
            String Xpath_textarea = conf.getObjectProperties("DB_inputbox", "Text_Area");
            println("Xpath for Text box is : " + Xpath_textarea);
            String run_query_button = conf.getObjectProperties("DB_inputbox", "button_Run_query");
            println("Xpath for Run_query_button is : " + run_query_button);
            String query_loading = conf.getObjectProperties("DB_inputbox", "query_loading");
            println("Xpath for query_loading is : " + query_loading);
            String Error_data = conf.getObjectProperties("DB_outputbox", "Error_data");
            println("Xpath for Error_data is : " + Error_data);
            String first_row_data = conf.getObjectProperties("DB_outputbox", "first_row_data");
            println("Xpath for first_row_data is : " + first_row_data);
            String Head_data = conf.getObjectProperties("DB_outputbox", "Head_data");
            println("Xpath for first_row_data is : " + Head_data);
            String body_data = conf.getObjectProperties("DB_outputbox", "body_data");
            println("Xpath for first_row_data is : " + body_data);

            query = "select count(1) from pe_user where email_addr='" + Email + "';";
            println("-------------------------------------------------");
            println("query to insert: " + query);

            webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(Keys.CONTROL, "a");
            webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(Keys.DELETE);
            webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(query);
            webDriver.findElement(By.xpath(run_query_button)).click();
            WebDriverWait wait = new WebDriverWait(webDriver, 30);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(query_loading)));

            boolean noerrordata = webDriver.findElements(By.xpath(Error_data)).isEmpty();
            if (noerrordata) {

                count = webDriver.findElement(By.xpath(first_row_data)).getText();
                println("Count value for Email: " + Email + " is " + count);
                conf.addRuntimeData("user_count", count);
                aftercountvalue = Integer.parseInt(count);
                println("Before creating PEDemo count = " + beforecountvalue);
                println("after  creating PEDemo count = " + aftercountvalue);

            } else {
                browser.strErrorInfo = "Error Message pops up after searching query: \n " + query;
                return 1;
            }

            if (aftercountvalue > beforecountvalue) {
                query = "select  user_id "
                        + "from pe_user "
                        + "where email_addr='" + Email + "' and created_date_time="
                        + "(select max(created_date_time) from pe_user where email_addr='" + Email + "');";

                webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(Keys.CONTROL, "a");
                webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(Keys.DELETE);
                webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(query);
                webDriver.findElement(By.xpath(run_query_button)).click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(query_loading)));

                noerrordata = webDriver.findElements(By.xpath(Error_data)).isEmpty();
                if (noerrordata) {

                    pe_user_id = webDriver.findElement(By.xpath(first_row_data)).getText();
                    println("pe_user.user_id: " + pe_user_id);
                    query = "select * from pe_details where pe_owner_id='" + pe_user_id + "'";

                    webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(Keys.CONTROL, "a");
                    webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(Keys.DELETE);
                    webDriver.findElement(By.xpath(Xpath_textarea)).sendKeys(query);
                    webDriver.findElement(By.xpath(run_query_button)).click();
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(query_loading)));

                    noerrordata = webDriver.findElements(By.xpath(Error_data)).isEmpty();
                    if (noerrordata) {
                        int i = 0;
                        List<WebElement> headli = webDriver.findElements(By.xpath(Head_data));
                        List<WebElement> bodyli = webDriver.findElements(By.xpath(body_data));
                        String[] headvalue = new String[headli.size()];
                        String[] bodyvalue = new String[bodyli.size()];
                        for (WebElement wb : headli) {
                            headvalue[i] = wb.getText();
                            i = i + 1;
                        }
                        i = 0;
                        for (WebElement bb : bodyli) {
                            bodyvalue[i] = bb.getText();
                            i = i + 1;
                        }
                        println("Headername : data");
                        String message = "";
                        for (i = 0; i < headli.size(); i++) {
                            println(headvalue[i] + " : " + bodyvalue[i]);
                            message = message + "\n" + headvalue[i] + " : " + bodyvalue[i];
                        }
                        conf.addRuntimeData("datavalues", message);

                    } else {
                        browser.strErrorInfo = "Error Message pops up after searching query: \n " + query;
                        return 1;
                    }

                } else {
                    browser.strErrorInfo = "Error Message pops up after searching query: \n " + query;
                    return 1;
                }

                return 0;
            } else {
                browser.strErrorInfo = "new row not created in PE_User table for Email: " + Email;
                return 1;
            }


        } catch (Exception e) {
            println("Exception:  " + e);
            browser.strErrorInfo = "Exception: " + e;
            return 1;
        }


    }


    public int getfrommail(String parameter) throws MessagingException, IOException, InterruptedException {

        try {
            String[] parray = parameter.split(",");
            int pcount = parray.length;
            String[] data = new String[pcount];
            String[] newdata = new String[pcount];
            String subject = conf.getParameterValue(parray[0]);
            String Email_username = conf.getParameterValue("Email_username");
            String Email_password = conf.getParameterValue("Email_password");

            println("Email UserName: " + Email_username);
            println("Email Password: " + Email_password);
            println("SUBJECT: " + subject);
            if (!Email_username.equalsIgnoreCase("Email_username") && !Email_username.equalsIgnoreCase("Email_password")) {
                for (int i = 1; i < pcount; i++) {
                    data[i] = conf.getParameterValue(parray[i]);
                    if (data[i].isEmpty()) {
                        data[i] = parray[i];
                    }
                    println("Data " + i + " : " + data[i]);
                }
                //				Mail mailobj = new Mail();
                newdata = hpsmailbox(subject, data, Email_username, Email_password);
                for (int i = 1; i < pcount; i++) {

                    println("NewData " + i + " : " + data[i]);
                    conf.addRuntimeData(parray[i], newdata[i]);
                }
                return 0;
            } else {
                println("Please enter valid Credentials for email");
                browser.strErrorInfo = "Please enter valid Credentials for Email Login";
                return 1;
            }
        } catch (Exception e) {
            println(e.toString());
            browser.strErrorInfo = "" + e;
            return 1;
        }

    }

    public String[] hpsmailbox(String subject, String[] data, String Email_username, String Email_password) throws MessagingException, IOException, InterruptedException {

        //test users
        String username = Email_username;
        String password = Email_password;
        //		String username ="HPS_AUTO_USER";
        //		String password="Welcome1";
        //		String username ="nkbabu";
        //		String password="byebye@18";
        String host = "TPAMail.healthplan.com";
        //		String subject="Fw: Sandbox: Verify your identity in Salesforce";
        //		String datapar ="Verification Code:";
        //"Sandbox: We can't reset your Salesforce password";
        //"Your new Demo Private Exchange";
        //"Sandbox: Verify your identity in Salesforce";
        //"Sandbox: Welcome to Salesforce: Verify your account";
        String msgtext = "";
        //		String loginname="";
        //		String loginpassword="";
        boolean search = true;
        boolean msgfetch = false;
        Properties properties = new Properties();

        properties.setProperty("mail.imaps.starttls.enable", "true");
        properties.setProperty("mail.imaps.host", "TPAMail.healthplan.com");
        properties.setProperty("mail.imaps.port", "143");
        properties.put("mail.imap.partialfetch", "false");
        properties.put("mail.store.protocol", "imaps");

        Session session = Session.getInstance(properties);
        Store store = session.getStore("imap");
        store.connect(host, username, password);
        println("Connected to Mail");

        for (int t = 0; t < 12; t++) {
            println("------------------------------------");
            if (search) {

                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);
                inbox.getMessage(1).toString();
                Flags seen = new Flags(Flags.Flag.SEEN);
                FlagTerm unread = new FlagTerm(seen, false);
                FlagTerm read = new FlagTerm(seen, true);
                Message[] messages = inbox.search(unread);
                //				messages = inbox.search(unread);

                println("Total Unread Message is :" + messages.length);

                if (messages.length > 0) {
                    Arrays.sort(messages, (m1, m2) -> {
                        try {
                            return m2.getSentDate().compareTo(m1.getSentDate());
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    for (int i = 0; i < messages.length; i++) {
                        println("subject " + i + " :" + messages[i].getSubject());
                        if (messages[i].getSubject().equals(subject)) {
                            println("Message fetched with subject : " + subject);
                            messages[i].setFlag(Flag.SEEN, false);
                            search = false;
                            msgfetch = true;
                            if (messages[i].isMimeType("text/html")) {
                                String html = messages[i].getContent().toString();
                                Document doc = Jsoup.parse(html);
                                msgtext = doc.body().text();
                                println(msgtext);
                                break;
                            } else if (messages[i].isMimeType("text/plain")) {
                                msgtext = messages[i].getContent().toString();
                                break;
                            } else {
                                if (messages[i].isMimeType("multipart/*")) {
                                    MimeMultipart mimeMultipart = (MimeMultipart) messages[i].getContent();
                                    int count = mimeMultipart.getCount();
                                    for (int c = 0; c < count; c++) {
                                        BodyPart bodyPart = mimeMultipart.getBodyPart(c);
                                        if (bodyPart.isMimeType("text/plain")) {
                                            msgtext = msgtext + "\n" + bodyPart.getContent();
                                            break; // without break same text appears twice in msg tests
                                        } else if (bodyPart.isMimeType("text/html")) {
                                            String html = (String) bodyPart.getContent();
                                            msgtext = msgtext + "\n" + org.jsoup.Jsoup.parse(html).text();
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                if (!msgfetch)
                    println("In " + t * 10 + " second, no New message with subject :" + subject);
            }
            //			else{
            //				println("In " +t*10+" second No message found");
            //			}
            try {
                if (msgfetch)
                    break;
                println("Waiting.....");
                Thread.sleep(10000);


            } catch (Exception e) {

            }
        }

        if (msgfetch) {
            println("******************************************");

            println(msgtext);
            for (int i = 1; i < data.length; i++) {
                String[] body = msgtext.split(data[i]);
                String[] text = body[1].trim().split("\\s+");
                data[i] = text[0].trim();
                println(text[0]);


            }

            println("close");
        }
        return data;

    }

    public int dynamiccellclick(String parameter) {
        try {
            String parametervalue = conf.getParameterValue(parameter);
            String dynamicrow = xPath;
            String dynamiccell = dynamicrow + "//*[contains(text(),'" + parametervalue + "')]";
            String dynamcicelldata = webDriver.findElement(By.xpath(dynamiccell)).getText();
            println("dynamic cell xpath : " + dynamiccell);
            webDriver.findElement(By.xpath(dynamiccell)).click();
            println("dynamic cell " + dynamcicelldata + " Clicked");
            return 0;
        } catch (Exception e) {
            println(e.toString());
            browser.strErrorInfo = "" + e;
            return 1;
        }

    }


    public int randomNumberGenrator(int numberOfOptionsAvailable) {
        Random r = new Random();
        int randomNumber = r.nextInt((numberOfOptionsAvailable - 1)) + 1;
        return randomNumber;
    }

    void println(String strLog) {
        log.deb(strLog);
    }

    public int elementPresent(String Xpath) {
        String strElement = Xpath;
        int returnValue = 0;
        try {
            String textValue = webDriver.findElement(By.xpath(strElement)).getText();

            println(textValue);
            if (textValue.equalsIgnoreCase(null)) {
                println("Element not present");
                returnValue = 1;

            } else {
                println("Element present");
                returnValue = 0;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            println("Element not present");
            returnValue = 1;
            e.printStackTrace();
        }
        return returnValue;

    }

    public void closeConnection() throws SQLException {
        connection.close();
    }


}
