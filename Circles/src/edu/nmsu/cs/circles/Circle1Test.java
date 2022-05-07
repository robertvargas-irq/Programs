package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle1 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore Circle1Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in a JUnit testing class; they are
 * here just so you see what is happening. You should not have them in your test cases.
 ***/

import org.junit.*;

public class Circle1Test
{
	// Data you need for each test case
	private Circle1 circle1;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle1 = new Circle1(1, 2, 3);
	}

	//
	// Stuff you want to do after each test case
	//
	@After
	public void teardown()
	{
		System.out.println("\nTest finished.");
	}

	//
	// Test a simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
		System.out.println("Running test simpleMove.");
		p = circle1.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);
	}

	//
	// Test a simple negative move
	//
	@Test
	public void simpleMoveNeg()
	{
		Point p;
		System.out.println("Running test simpleMoveNeg.");
		p = circle1.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}

	//
	// Test an intersection of two circles
	//
	@Test
	public void intersectTwo()
	{
		Circle1 secondCircle = new Circle1(-1, -1, 3);
		boolean intersects;

		System.out.println("Running test intersectTwo.");
		intersects = circle1.intersects(secondCircle);

		Assert.assertTrue(intersects == true);
	}

	//
	// Equal distance two circles; the two circles meet but not intersect
	//
	@Test
	public void meetTwo()
	{
		Circle1 secondCircle = new Circle1(7, 2, 3);
		boolean intersects;

		System.out.println("Running test meetTwo.");
		intersects = circle1.intersects(secondCircle);

		Assert.assertFalse(intersects);
	}

	//
	// Two circles do not meet; no intersection
	//
	@Test
	public void apartTwo()
	{
		Circle1 secondCircle = new Circle1(-5, -5, 2);
		boolean intersects;

		System.out.println("Running apartTwo.");
		intersects = circle1.intersects(secondCircle);

		Assert.assertFalse(intersects);
	}

	//
	// Test scaling by a factor less than 1
	//
	@Test
	public void smallerScale()
	{
		double newScale;
		double oldScale = circle1.radius;
		System.out.println("Running test negativeScale.");
		newScale = circle1.scale(0.5);
		Assert.assertTrue(oldScale * 0.5 == newScale);
	}

	//
	// Test scaling by a factor of 1
	//
	@Test
	public void equalScale()
	{
		double newScale;
		double oldScale = circle1.radius;
		System.out.println("Running test equalScale.");
		newScale = circle1.scale(1.0);
		Assert.assertTrue(oldScale * 1.0 == newScale);
	}

	//
	// Test scaling by a factor greater than 1
	//
	@Test
	public void greaterScale()
	{
		double newScale;
		double oldScale = circle1.radius;
		System.out.println("Running test greaterScale.");
		newScale = circle1.scale(3.5);
		Assert.assertTrue(oldScale * 3.5 == newScale);
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
