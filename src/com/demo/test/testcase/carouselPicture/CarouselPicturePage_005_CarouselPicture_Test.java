package com.demo.test.testcase.carouselPicture; 
import org.testng.annotations.Test; 
import com.demo.test.base.BaseParpare; 
 import com.demo.test.utils.SuperAction; 
public class CarouselPicturePage_005_CarouselPicture_Test extends BaseParpare{ 
@Test 
 public void carouselPicture() { 
SuperAction.parseExcel("CarouselPicture","005_CarouselPicture",seleniumUtil);
 }
}