package com.demo.test.testcase.login; 
import org.testng.annotations.Test; 
import com.demo.test.base.BaseParpare; 
 import com.demo.test.utils.SuperAction; 
public class LoginPage_002_LoginWithoutUserName_Test extends BaseParpare{ 
@Test 
 public void loginWithoutUserName() { 
SuperAction.parseExcel("Login","002_LoginWithoutUserName",seleniumUtil);
 }
}