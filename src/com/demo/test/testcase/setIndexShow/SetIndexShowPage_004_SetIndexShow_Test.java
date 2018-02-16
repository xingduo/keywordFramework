package com.demo.test.testcase.setIndexShow; 
import org.testng.annotations.Test; 
import com.demo.test.base.BaseParpare; 
 import com.demo.test.utils.SuperAction; 
public class SetIndexShowPage_004_SetIndexShow_Test extends BaseParpare{ 
@Test 
 public void setIndexShow() { 
SuperAction.parseExcel("SetIndexShow","004_SetIndexShow",seleniumUtil);
 }
}