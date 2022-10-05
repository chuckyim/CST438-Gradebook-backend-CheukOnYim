package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

@SpringBootTest
public class E2ECreateAssignment {
	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/Users/cheuk.yim/chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_ASSIGNMENT_DUE_DATE = "10-11-2022";
	public static final int TEST_COURSE_ID = 999999;
	public static final int TEST_ASSIGNMENT_ID = 99;
	

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void creatAssignment() throws Exception {

//		Database setup:  create course		
		Course c = new Course();
		c.setCourse_id(TEST_COURSE_ID);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2022);
		c.setTitle("Test Course Title");
		courseRepository.save(c);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
			
			driver.findElement(By.xpath("//*[@id='btnCreate']")).click();
			
			driver.findElement(By.xpath("//input[@name='courseID']")).sendKeys(String.valueOf(TEST_COURSE_ID));
			driver.findElement(By.xpath("//input[@name='dueDate']")).sendKeys(TEST_ASSIGNMENT_DUE_DATE);
			driver.findElement(By.xpath("//input[@name='assignmentName']")).sendKeys(TEST_ASSIGNMENT_NAME);

			
			driver.findElement(By.xpath("//input[@name='submit']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			//Verify
			Assignment a = assignmentRepository.findByCourseAndName(TEST_COURSE_ID, TEST_ASSIGNMENT_NAME);
			assertNotNull(a); 
			
		} catch (Exception ex) {
			throw ex;
		} finally {

			/*
			 *  clean up database so the test is repeatable.
			 */
			Assignment a = assignmentRepository.findByCourseAndName(TEST_COURSE_ID, TEST_ASSIGNMENT_NAME);
			if(a !=null) {assignmentRepository.delete(a);}
			
			Course crs = courseRepository.findByid(TEST_COURSE_ID);
			if(crs !=null) {courseRepository.delete(crs);}
	         
			driver.quit();
		}

	}
}
