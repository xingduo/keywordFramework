package com.demo.test.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;


/*
 * @author 留仙洞_sageliu
 * @Description 包装所有selenium的操作以及通用方法，简化用例中代码量
 * */
public class SeleniumUtil {
	/* 使用Log4j，第一步就是获取日志记录器，这个记录器将负责控制日志信息 */
	public static Logger logger = Logger.getLogger(SeleniumUtil.class.getName());
	public ITestResult it = null;
	public WebDriver driver = null;
	public WebDriver window = null;
	public String current_handles="";



	/*
	 * 启动浏览器并打开页面
	 * */
	public void launchBrowser(String browserName, ITestContext context,String webUrl,int timeOut) {
		SelectBrowser select = new SelectBrowser();
		driver = select.selectExplorerByName(browserName, context);
		try {
			maxWindow(browserName);
			waitForPageLoading(timeOut);
			get(webUrl);
		} catch (TimeoutException e) {
			logger.warn("注意：页面没有完全加载出来，刷新重试！！");
			refresh();
			 JavascriptExecutor js = (JavascriptExecutor)driver;
			String status= (String)js.executeScript("return document.readyState");
		
			
			logger.info("打印状态："+status);
		}

	}

	
	/*
	 * 最大化浏览器操作
	 * */
	public void maxWindow(String browserName) {
		logger.info("最大化浏览器:" + browserName);
		driver.manage().window().maximize();
	}

	/*
	 * 设定浏览器窗口大小： 设置浏览器窗口的大小有下面两个比较常见的用途：<br>
	 * */
	public void setBrowserSize(int width, int height) {
		driver.manage().window().setSize(new Dimension(width, height));
	}

	/*
	 * 包装查找元素的方法 element
	 * */
	public WebElement findElementBy(By by) {
		return driver.findElement(by);
	}

	/*
	 * 包装查找元素的方法 elements
	 * */
	public List<WebElement> findElementsBy(By by) {
		return driver.findElements(by);
	}
	
	/*导航链接到url*/
	public void navigateTargetUrl(String url){
		driver.navigate().to(url);
		logger.info("导航到："+url);
	}

	/*
	 * 包装点击操作- By
	 * */
	public void click(By byElement) {

		try {
			clickTheClickable(byElement, System.currentTimeMillis(), 2500);
		} catch (StaleElementReferenceException e) {
			logger.error("The element you clicked:[" + byElement + "] is no longer exist!");
			Assert.fail("The element you clicked:[" + byElement + "] is no longer exist!");
		} catch (Exception e) {
			logger.error("Failed to click element [" + byElement + "]");
			Assert.fail("Failed to click element [" + byElement + "]",e);
		}
		logger.info("点击元素 [" + byElement + "]");
	}
	
	public boolean isEnabled(By by){
		return driver.findElement(by).isEnabled();
	}
	
	/*提交*/
	public void submit(WebElement w){
		try{
		w.submit();
		
		}catch(Exception e){
			logger.error("在元素："+w+"做的提交操作失败");
			Assert.fail("在元素："+w+"做的提交操作失败");
		}
		logger.info("在元素："+w+"做了提交操作");
	}

	/*
	 * 包装点击操作 -webelment
	 * */
	public void click(WebElement element) {

		try {
			element.click();
		} catch (StaleElementReferenceException e) {
			logger.error("The element you clicked:[" + element.toString() + "] is no longer exist!");
			Assert.fail("The element you clicked:[" + element.toString() + "] is no longer exist!");
		} catch (Exception e) {
			logger.error("Failed to click element [" + element.toString() + "]");
			Assert.fail("Failed to click element [" + element.toString() + "]",e);
		}
		logger.info("点击元素 [" + element.toString() + "]");
	}

	/* 不能点击时候重试点击操作 */
	public void clickTheClickable(By byElement, long startTime, int timeOut) throws Exception {
		try {
			findElementBy(byElement).click();
		} catch (Exception e) {
			if (System.currentTimeMillis() - startTime > timeOut) {
				logger.warn(byElement+ " is unclickable");
				throw new Exception(e);
			} else {
				Thread.sleep(500);
				logger.warn(byElement + " is unclickable, try again");
				clickTheClickable(byElement, startTime, timeOut);
			}
		}
	}

	/*
	 * 获得页面的标题
	 * */
	public String getTitle() {
		return driver.getTitle();
	}

	/*
	 * 获得元素的文本
	 * */
	public String getText(By elementLocator) {
		return driver.findElement(elementLocator).getText().trim();
	}

	/*
	 * 获得元素 属性的文本
	 * */
	public String getAttributeText(By elementLocator, String attribute) {
		return driver.findElement(elementLocator).getAttribute(attribute).trim();
	}

	/*
	 * 包装清除操作
	 * */
	public void clear(By byElement) {
		try {
			findElementBy(byElement).clear();
		} catch (Exception e) {
			logger.error("清除元素 [" + byElement + "] 上的内容失败!");
		}
		logger.info("清除元素 [" + byElement  + "]上的内容");
	}

	/*
	 * 向输入框输入内容
	 * */
	public void type(By byElement, String key) {
		try {
			findElementBy(byElement).sendKeys(key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("输入 [" + key + "] 到 元素[" + byElement + "]失败");
			Assert.fail("输入 [" + key + "] 到 元素[" + byElement + "]失败",e);
		}
		logger.info("输入：[" + key + "] 到 [" + byElement + "]");
	}

	/*
	 * 模拟键盘操作的,比如Ctrl+A,Ctrl+C等 参数详解：<br>
	 * 1、WebElement element - 要被操作的元素 <br>
	 * 2、Keys key- 键盘上的功能键 比如ctrl ,alt等 <br>
	 * 3、String keyword - 键盘上的字母
	 * */
	public void pressKeysOnKeyboard(WebElement element, Keys key, String keyword) {

		element.sendKeys(Keys.chord(key, keyword));
	}

	/*
	 * 在给定的时间内去查找元素，如果没找到则超时，抛出异常
	 * */
	public void waitForElementToLoad(int timeOut, final By By) {
		logger.info("开始查找元素[" + By + "]");
		try {
			(new WebDriverWait(driver, timeOut)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					WebElement element = driver.findElement(By);
					return element.isDisplayed();
				}
			});
		} catch (TimeoutException e) {
			logger.error("超时!! " + timeOut + " 秒之后还没找到元素 [" + By + "]");
			Assert.fail("超时!! " + timeOut + " 秒之后还没找到元素 [" + By + "]");

		}
		logger.info("找到了元素 [" + By + "]");
	}

	/*
	 * 在给定的时间内查询元素，共尝试三次，如果第三次还是找不到就报错，这就可能是网页性能问题了，响应速度不够
	 * */
	public void FindElementUtil3TimesTry(int timeOut, final By By ) {
	 int find=0;
		int notFindTimes = 0;
		boolean flag = true;
		while(flag){
			if(notFindTimes==3){
				logger.error("尝试了3次查找都未查找到元素："+By+"请检查是不是网络或者网站性能问题（响应速度不够）");
				Assert.fail("尝试了3次查找都未查找到元素："+By+"请检查是不是网络或者网站性能问题（响应速度不够）");
			}
		logger.info("开始第"+(notFindTimes+1)+"次查找元素[" + By + "]");
		try {
			(new WebDriverWait(driver, timeOut)).until(new ExpectedCondition<Boolean>() {

				public Boolean apply(WebDriver driver) {
					WebElement element = driver.findElement(By);
					return element.isDisplayed();
				}
			});
			find++;
		} catch (TimeoutException e) {
			logger.warn("超时!! " + timeOut + " 秒之后还没找到元素 [" + By + "],这是第"+(notFindTimes+1)+"次查找！");
			notFindTimes++;
			if(notFindTimes<3){
			refresh();
			}
		}
		
		
		if(notFindTimes>0&find!=1){
			flag = true;
		}else{
			flag = false;
		}
		

		}
	
		logger.info("找到了元素 [" + By + "]");
	}

	/*
	 * 判断文本是不是和需求要求的文本一致
	 * **/
	public void isTextCorrect(String actual, String expected) {
		try {
			Assert.assertEquals(actual, expected);
		} catch (AssertionError e) {
			logger.error("期望的文字是 [" + expected + "] 但是找到了 [" + actual + "]");
			Assert.fail("期望的文字是 [" + expected + "] 但是找到了 [" + actual + "]");

		}
		logger.info("找到了期望的文字: [" + expected + "]");

	}

	/*
	 * 判断编辑框是不是可编辑
	 * */
	public void isInputEdit(WebElement element) {

	}

	/*
	 * 等待alert出现
	 * */
	public Alert switchToPromptedAlertAfterWait(long waitMillisecondsForAlert) throws NoAlertPresentException {
		final int ONE_ROUND_WAIT = 200;
		NoAlertPresentException lastException = null;

		long endTime = System.currentTimeMillis() + waitMillisecondsForAlert;

		for (long i = 0; i < waitMillisecondsForAlert; i += ONE_ROUND_WAIT) {

			try {
				Alert alert = driver.switchTo().alert();
				return alert;
			} catch (NoAlertPresentException e) {
				lastException = e;
			}
				try {
					Thread.sleep(ONE_ROUND_WAIT);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			if (System.currentTimeMillis() > endTime) {
				break;
			}
		}
		throw lastException;
	}

	/*
	 * 暂停当前用例的执行，暂停的时间为：sleepTime
	 * */
	public void pause(int sleepTime) {
		if (sleepTime <= 0) {
			return;
		}
		try {
			TimeUnit.SECONDS.sleep(sleepTime); 
			logger.info("暂停:"+sleepTime+"秒");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * 退出
	 * */
	public void quit() {
		driver.quit();
	}

	/*
	 * 切换frame - 根据String类型（frame名字）
	 * */
	public void inFrame(String frameId) {
		driver.switchTo().frame(frameId);
	}

	/*
	 * 切换frame - 根据frame在当前页面中的顺序来定位
	 * */
	public void inFrame(int frameNum) {
		driver.switchTo().frame(frameNum);
	}

	/*
	 * 切换frame - 根据页面元素定位
	 * */
	public void switchFrame(By byElement) {
		try {
			logger.info("Start switching to frame [" + byElement + "]");
			driver.switchTo().frame(findElementBy(byElement));
		} catch (Exception e) {
			logger.info("Switch to frame [" + byElement + "] failed");
			Assert.fail("Switch to frame [" + byElement + "] failed");
		}
		logger.info("Switch to frame [" + byElement + "] successed");
	}

	/*
	 * 选择下拉选项 -根据value
	 * */
	public void selectByValue(By by, String value) {
		Select s = new Select(driver.findElement(by));
		s.selectByValue(value);
	}

	/*
	 * 选择下拉选项 -根据index角标
	 * */
	public void selectByIndex(By by, int index) {
		Select s = new Select(driver.findElement(by));
		s.selectByIndex(index);
	}

	/* 检查checkbox是不是勾选 */
	public boolean doesCheckboxSelected(By elementLocator) {
		if (findElementBy(elementLocator).isSelected() == true) {
			logger.info("CheckBox: " + getLocatorByElement(findElementBy(elementLocator), ">") + " 被勾选");
			return true;
		} else
			logger.info("CheckBox: " + getLocatorByElement(findElementBy(elementLocator), ">") + " 没有被勾选");
		return false;

	}

	/*
	 * 选择下拉选项 -根据文本内容
	 * */
	public void selectByText(By by, String text) {
		Select s = new Select(driver.findElement(by));
		s.selectByVisibleText(text);
		logger.info("你选择了："+text);
	}
	
	/*
	 * 获得当前select选择的值
	 * */
	public String getCurrentSelectValue(By by){
		
		Select s = new Select(driver.findElement(by));
		WebElement e =  s.getFirstSelectedOption();
			return e.getText().trim();
	}
	
	/*
	 * 获取下拉列表的所有选项
	 * @param By：By元素对象
	 * @return 返回所有下拉列表中的选项，如option1,option2,……
	 * */
	public String getSelectOption(By by) {
		String value = null;
		Select s = new Select(driver.findElement(by));
		List<WebElement> options = s.getOptions();
	    for(int i = 0 ; i< options.size() ; i++){
			value = value + "," + options.get(i).getText();		
		}	    
		return value.replace("null,","");
	}

	/*
	 * 执行JavaScript 方法
	 * */
	public void executeJS(String js) {
		((JavascriptExecutor) driver).executeScript(js);
		logger.info("执行JavaScript语句：[" + js + "]");
	}
	
	/*
	 * 获得输入框的值 这个方法 是针对某些input输入框 没有value属性，但是又想取得input的 值得方法
	 * */
	public String getInputValue(String chose,String choseValue) {
		String value = null;
		switch(chose.toLowerCase()){
		case "name":
			 String jsName = "return document.getElementsByName('"+choseValue+"')[0].value;"; //把JS执行的值 返回出去
			 value = (String)((JavascriptExecutor) driver).executeScript(jsName);
			 break;
			
		case "id":
			 String jsId = "return document.getElementById('"+choseValue+"').value;"; //把JS执行的值 返回出去
			 value = (String)((JavascriptExecutor) driver).executeScript(jsId);
			 break;
		
			default:
				logger.error("未定义的chose:"+chose);
				Assert.fail("未定义的chose:"+chose);
		
		}
		return value;

	}

	/*
	 * 执行JavaScript 方法和对象
	 * 用法：seleniumUtil.executeJS("arguments[0].click();", 
	 * seleniumUtil.findElementBy(MyOrdersPage.MOP_TAB_ORDERCLOSE));
	 * */
	public void executeJS(String js, Object... args) {
		((JavascriptExecutor) driver).executeScript(js, args);
		logger.info("执行JavaScript语句：[" + js + "]");
	}

	/*
	 * get方法包装
	 * */
	public void get(String url) {
		driver.get(url);
		logger.info("打开测试页面:[" + url + "]");
	}

	/*
	 * close方法包装
	 * */
	public void close() {
		driver.close();
	}

	/*
	 * 刷新方法包装
	 * */
	public void refresh() {
		driver.navigate().refresh();
		logger.info("页面刷新成功！");
	}

	/*
	 * 后退方法包装
	 * */
	public void back() {
		driver.navigate().back();
	}

	/*
	 * 前进方法包装
	 * */
	public void forward() {
		driver.navigate().forward();
	}

	/*
	 * 包装selenium模拟鼠标操作 - 鼠标移动到指定元素
	 * */
	public void mouseMoveToElement(By by) {
		Actions builder = new Actions(driver);
		Actions mouse = builder.moveToElement(driver.findElement(by));
		mouse.perform();
	}

	/*
	 * 包装selenium模拟鼠标操作 - 鼠标移动到指定元素
	 * */
	public void mouseMoveToElement(WebElement element) {
		Actions builder = new Actions(driver);
		Actions mouse = builder.moveToElement(element);
		mouse.perform();
	}
	
	/*
	 * 包装selenium模拟鼠标操作 - 鼠标右击
	 * */
	public void mouseRightClick(By element) {
		Actions builder = new Actions(driver);
		Actions mouse = builder.contextClick(findElementBy(element));
		mouse.perform();
	}

	/*
	 * 添加cookies,做自动登陆的必要方法
	 * */
	public void addCookies(int sleepTime) {
		pause(sleepTime);
		Set<Cookie> cookies = driver.manage().getCookies();
		for (Cookie c : cookies) {
			System.out.println(c.getName() + "->" + c.getValue());
			if (c.getName().equals("logisticSessionid")) {
				Cookie cook = new Cookie(c.getName(), c.getValue());
				driver.manage().addCookie(cook);
				System.out.println(c.getName() + "->" + c.getValue());
				System.out.println("添加成功");
			} else {
				System.out.println("没有找到logisticSessionid");
			}

		}

	}

	/* 获得CSS value */
	public String getCSSValue(WebElement e, String key) {

		return e.getCssValue(key);
	}

	/* 使用testng的assetTrue方法 */
	public void assertTrue(WebElement e, String content) {
		String str = e.getText();
		Assert.assertTrue(str.contains(content), "字符串数组中不含有：" + content);

	}

	/* 跳出frame */
	public void outFrame() {

		driver.switchTo().defaultContent();
	}

	// webdriver中可以设置很多的超时时间
	/* implicitlyWait。识别对象时的超时时间。过了这个时间如果对象还没找到的话就会抛出NoSuchElement异常 */
	public void implicitlyWait(int timeOut) {
		driver.manage().timeouts().implicitlyWait(timeOut, TimeUnit.SECONDS);
	}

	/* setScriptTimeout。异步脚本的超时时间。webdriver可以异步执行脚本，这个是设置异步执行脚本脚本返回结果的超时时间 */
	public void setScriptTimeout(int timeOut) {
		driver.manage().timeouts().setScriptTimeout(timeOut, TimeUnit.SECONDS);
	}

	/*
	 * pageLoadTimeout。页面加载时的超时时间。因为webdriver会等页面加载完毕在进行后面的操作，
	 * 所以如果页面在这个超时时间内没有加载完成，那么webdriver就会抛出异常
	 */

	public void waitForPageLoading(int pageLoadTime) {
		driver.manage().timeouts().pageLoadTimeout(pageLoadTime, TimeUnit.SECONDS);

	}

	/* 根据元素来获取此元素的定位值 */
	public String getLocatorByElement(WebElement element, String expectText) {
		String text = element.toString();
		String expect = null;
		try {
			expect = text.substring(text.indexOf(expectText) + 1, text.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("failed to find the string [" + expectText + "]");

		}

		return expect;

	}
	/*
	 * 获取当前页面的URL
	 * */
	public String getPageURL(){
		return driver.getCurrentUrl();
		
	}
	/*
	 * 这是一堆相同的elements中 选择 其中方的 一个 然后在这个选定的中 继续定位
	 * */
	public WebElement getOneElement(By bys, By by, int index) {
		return findElementsBy(bys).get(index).findElement(by);
	}

	/*
	 * 上传文件，需要点击弹出上传照片的窗口才行
	 * 
	 * @param brower
	 *            使用的浏览器名称
	 * @param file
	 *            需要上传的文件及文件名
	 */
	public void handleUpload(String browser, File file) {
		String filePath = file.getAbsolutePath();
		String executeFile = "res/script/autoit/Upload.exe";
		String cmd = "\"" + executeFile + "\"" + " " + "\"" + browser + "\"" + " " + "\"" + filePath + "\"";
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	 * @Description 对于windows GUI弹出框，要求输入用户名和密码时，
	 * 
	 * */
	public void loginOnWinGUI(String username, String password, String url) {
		driver.get(username + ":" + password + "@" + url);
	}

	/* 检查元素是否显示 */
	public boolean isDisplayed(WebElement element) {
		boolean isDisplay = false;
		if (element.isDisplayed()) {
			logger.info("The element: [" + getLocatorByElement(element, ">") + "] is displayed");
			isDisplay = true;
		} else if (element.isDisplayed() == false) {
			logger.warn("The element: [" + getLocatorByElement(element, ">") + "] is not displayed");

			isDisplay = false;
		}
		return isDisplay;
	}
	
	/*检查元素是不是存在*/
	public  boolean doesElementsExist(By byElement){
		try{
		findElementBy(byElement);
		return true;
		}catch(NoSuchElementException nee){
			
			return false;
		}
		
		
	}
	
	/* 检查元素是否被勾选 */
	public boolean isSelected(WebElement element) {
		boolean flag = false;
		if (element.isSelected() == true) {
			logger.info("The element: [" + getLocatorByElement(element, ">") + "] is selected");
			flag = true;
		} else if (element.isSelected() == false) {
			logger.info("The element: [" + getLocatorByElement(element, ">") + "] is not selected");
			flag = false;
		}
		return flag;
	}

	/*
	 * 判断实际文本时候包含期望文本
	 * 
	 * @param actual
	 *            实际文本
	 * @param expect
	 *            期望文本
	 */
	public void isContains(String actual, String expect) {
		try {
			Assert.assertTrue(actual.contains(expect));
		} catch (AssertionError e) {
			logger.error("The [" + actual + "] is not contains [" + expect + "]");
			Assert.fail("The [" + actual + "] is not contains [" + expect + "]");
		}
		logger.info("The [" + actual + "] is contains [" + expect + "]");
	}
	
	/*
	 * 判断实际文本,不包含期望文本
	 * 
	 * @param actual
	 *            实际文本
	 * @param expect
	 *            期望文本
	 */
	public void isNotContains(String actual, String expect) {
		try {
			Assert.assertFalse(actual.contains(expect));
		} catch (AssertionError e) {
			logger.error("The [" + actual + "] is  contains [" + expect + "]");
			Assert.fail("The [" + actual + "] is  contains [" + expect + "]");
		}
		logger.info("The [" + actual + "] is not contains [" + expect + "]");
	}

	/* 获得屏幕的分辨率 - 宽 */
	public  double getScreenWidth() {
		return java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	}
	
	/*进入新窗口*/
	public void switchNewWindow(By byElement){
		//获取当前页面句柄
		 current_handles = driver.getWindowHandle();
		//点击某个链接会弹出一个新窗口
		click(byElement);
		//接下来会有新的窗口打开，获取所有窗口句柄
		Set<String> all_handles = driver.getWindowHandles();
		//循环判断，把当前句柄从所有句柄中移除，剩下的就是你想要的新窗口
		Iterator<String> it = all_handles.iterator();
		while(it.hasNext()){
		if(current_handles == it.next()) continue;
		//跳入新窗口,并获得新窗口的driver - newWindow
		 window = driver.switchTo().window(it.next());
		}
		
	}
	
	/*回到原始窗口*/
	public void backToOriginalWindow(){
		window.close();
		driver.switchTo().window(current_handles);
		
	}
	
	/*停止页面加载*/
	public void stopLoad(){	
		pause(1);
		Robot r;
		try {
			r = new Robot();
			r.keyPress(KeyEvent.VK_ESCAPE);
			logger.info("按下了Esc键");
			r.keyRelease(KeyEvent.VK_ESCAPE);
			logger.info("松开了Esc键");
		} catch (AWTException e) {
			e.printStackTrace();
		}

		logger.info("正在停止页面加载...");
	}

	/*获取系统时间*/
	public int getDate(String getOption){
		 Calendar a=Calendar.getInstance();
		 int result=0;
		switch(getOption){
		
		case "年":
			result = a.get(Calendar.YEAR);
			break;
		case "月":
			result = a.get(Calendar.MONTH)+1;
			break;
		case "日":
			result = a.get(Calendar.DATE);
			break;
			default:
				Assert.fail("只支持输入年、月、日。你输入了："+getOption);
		
		}
		
		return result;
	}
	/*判断alert是否出现*/
	public boolean isAlertPresent(){
        try
        {
            driver.switchTo().alert();
            logger.info("alert出现");
            return true;
        }   
        catch (NoAlertPresentException Ex)
        {
        	logger.warn("alert没有出现");
            return false;
        }   
}

	
	/*
	 * 在多个相同元素中，定位到指定的元素
	 * @param by
	 * @param index
	 * @return
	 */
	public WebElement getOneElement(By by, int index) {
		 List<WebElement> element = driver.findElements(by);
		 return element.get(index);
	}
	
	/*
	 * 获取指定table某一整列的值
	 */
	public String getColumnText(By by){
		String values = null;
		List<WebElement> elements = findElementsBy(by);
		for(WebElement e: elements){
			String value = e.getText();
			if(value.length() > 0){
				values = values + "," + value;
			}			
		}
		return values.replace("null,", "");
	}
	
	/*
	 * 获取指定table某一行的值
	 * @param index：行号，行号从1开始（0代表table的表头）
	 */
	public String getRowText(By by, int index){
		String values = null;
		List<WebElement> rows = findElementsBy(by);  //tr对象
		WebElement row = rows.get(index); 
		if(row.findElements(By.tagName("td")).size()>0){
			List<WebElement> cells = row.findElements(By.tagName("td"));  //td对象
			for(WebElement cell:cells){
				String value = cell.getText();
				if(value.length() > 0){
					values = values + "," + value;
				}
				
			}
		}
		return values.replace("null,", "");
	}
	
	/*
	 * 获取指定table个单元格的值
	 * @param index：行号，行号从1开始（0代表table的表头）
	 */
	public String getCellText(By by, int RowID, int ColID){
		String value = null;
		//得到table元素对象
		WebElement table = driver.findElement(by);
		//得到table表中所有行对象，并得到所要查询的行对象。
		List<WebElement> rows = table.findElements(By.tagName("tr"));
		WebElement theRow = rows.get(RowID);
		//调用getCell方法得到对应的列对象，然后得到要查询的文本。
		value = getCell(theRow, ColID).getText();	
		return value.replace("null,", "");
	}
	

	/*
	 * 
	 * @param Row: 一行的对象
	 * @param ColID：对应列
	 * @return
	 */
	private WebElement getCell(WebElement Row,int ColID){
		List<WebElement> cells;
		WebElement target = null;
		//列里面有"<th>"、"<td>"两种标签，所以分开处理。
		if(Row.findElements(By.tagName("th")).size()>0){
		cells = Row.findElements(By.tagName("th"));		
		target = cells.get(ColID);
		}
		if(Row.findElements(By.tagName("td")).size()>0){
		cells = Row.findElements(By.tagName("td"));
		target = cells.get(ColID);
		}
		return target;
	}

	/*
	 * 在给定的时间内去查找元素，如果没找到则超时，抛出异常
	 * */
	public boolean isShown(int timeOut, final By By) {
		boolean flag = true;
		logger.info("开始查找元素[" + By + "]");
		try {
			(new WebDriverWait(driver, timeOut)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					WebElement element = driver.findElement(By);
					return element.isDisplayed();
				}
			});
		} catch (TimeoutException e) {
			flag = false;

		}	
		return flag;
	}
	
	
	/*页面过长时候滑动页面 window.scrollTo(左边距,上边距); */
	public void scrollPage(int x,int y){
		String js ="window.scrollTo("+x+","+y+");";
	    ((JavascriptExecutor)driver).executeScript(js);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}