package com.demo.test.testcase.oneModule; 
import org.testng.annotations.Test; 
import com.demo.test.base.BaseParpare; 
 import com.demo.test.utils.SuperAction; 
public class OneModulePage_006_OneModule_Test extends BaseParpare{ 
@Test 
 public void oneModule() { 
SuperAction.parseExcel("OneModule","006_OneModule",seleniumUtil);
 }
}