package com.demo.test.testcase.login; 
import org.testng.annotations.Test; 
import com.demo.test.base.BaseParpare; 
 import com.demo.test.utils.SuperAction; 
public class LoginPage_003_LoginWithoutPassword_Test extends BaseParpare{ 
@Test 
 public void loginWithoutPassword() { 
SuperAction.parseExcel("Login","003_LoginWithoutPassword",seleniumUtil);
 }
}