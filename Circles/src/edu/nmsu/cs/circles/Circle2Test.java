package edu.nmsu.cs.circles;
import org.junit.*;

public class Circle2Test {
	// Data you need for each test case
	private Circle2 circle2;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle2 = new Circle2(1, 2, 3);
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
		p = circle2.moveBy(1, 1);
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
		p = circle2.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}

	//
	// Test an intersection of two circles
	//
	@Test
	public void intersectTwo()
	{
		Circle2 secondCircle = new Circle2(-1, -1, 3);
		boolean intersects;

		System.out.println("Running test intersectTwo.");
		intersects = circle2.intersects(secondCircle);

		Assert.assertTrue(intersects == true);
	}

	//
	// Equal distance two circles; the two circles meet but not intersect
	//
	@Test
	public void meetTwo()
	{
		Circle2 secondCircle = new Circle2(7, 2, 3);
		boolean intersects;

		System.out.println("Running test meetTwo.");
		intersects = circle2.intersects(secondCircle);

		Assert.assertFalse(intersects);
	}

	//
	// Two circles do not meet; no intersection
	//
	@Test
	public void apartTwo()
	{
		Circle2 secondCircle = new Circle2(-5, -5, 2);
		boolean intersects;

		System.out.println("Running apartTwo.");
		intersects = circle2.intersects(secondCircle);

		Assert.assertFalse(intersects);
	}
	
	//
	// Test scaling a circle down by a factor less than 1
	//
	@Test
	public void smallerScale()
	{
		double newScale;
		double oldScale = circle2.radius;
		System.out.println("Running test negativeScale.");
		newScale = circle2.scale(0.5);
		Assert.assertTrue(oldScale * 0.5 == newScale);
	}

	//
	// Test scaling by a factor of 1 (no scale)
	//
	@Test
	public void equalScale()
	{
		double newScale;
		double oldScale = circle2.radius;
		System.out.println("Running test equalScale.");
		newScale = circle2.scale(1.0);
		Assert.assertTrue(oldScale * 1.0 == newScale);
	}

	//
	// Test scaling a circle up by a factor greater than 1
	//
	@Test
	public void greaterScale()
	{
		double newScale;
		double oldScale = circle2.radius;
		System.out.println("Running test greaterScale.");
		newScale = circle2.scale(3.5);
		Assert.assertTrue(oldScale * 3.5 == newScale);
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle2Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
