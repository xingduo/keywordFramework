package com.demo.test.testcase.login; 
import org.testng.annotations.Test; 
import com.demo.test.base.BaseParpare; 
 import com.demo.test.utils.SuperAction; 
public class LoginPage_001_LoginSuccessFunction_Test extends BaseParpare{ 
@Test 
 public void loginSuccessFunction() { 
SuperAction.parseExcel("Login","001_LoginSuccessFunction",seleniumUtil);
 }
}