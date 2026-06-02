import java.util.EnumSet;

/**
 * Triangle. The main function takes 3 positive whole-number lengths
 * to be typed in as command line arguments. The program responds
 * with a description of the triangle, as follows:
 *
 * <ul>
 *  <li><b>equilateral</b> - if all three sides have equal length
 *  <li><b>isosceles</b> - if two sides have equal length
 *  <li><b>right-angled</b> - if one angle is a right angle
 *  <li><b>scalene</b> - all sides different lengths, no right angles
 *  <li><b>impossible</b> - if the given side lengths do not form a triangle
 * </ul>
 * Area and perimeter of the triangle are calculated, too.
 *
 * @author Mikko Rusama, SoberIT
 * @version 26.8.2004
 */
public class Triangle
{
	private int side1;
	private int side2;
	private int side3;
	private final RightAngleChecker rightAngleChecker;
	private static final String P_EQUILATERAL = "equilateral";
	private static final String P_ISOSCELES   = "isosceles";
	private static final String P_RIGHTANGLED = "right-angled";
	private static final String P_SCALENE     = "scalene";
	private static final String P_IMPOSSIBLE  = "impossible";

	public Triangle(int s1, int s2, int s3)
	{
		this(s1, s2, s3, new RealRightAngleChecker());
	}

	Triangle(int s1, int s2, int s3, RightAngleChecker checker)
	{
		side1 = s1;
		side2 = s2;
		side3 = s3;
		rightAngleChecker = checker;
	}

	public Triangle setSideLengths(int s1, int s2, int s3)
	{
		side1 = s1;
		side2 = s2;
		side3 = s3;
		return this;
	}

	public String getSideLengths()
	{
		return side1 + "," + side2 + "," + side3;
	}

	public int getPerimeter()
	{
		return side1 + side2 + side3;
	}

	public double getArea()
	{
		if (!isImpossible())
		{
			double semiPerimeter = getPerimeter() / 2.0;
			return Math.sqrt(semiPerimeter
			* (semiPerimeter - side1)
			* (semiPerimeter - side2)
			* (semiPerimeter - side3));
		}
		return -1;
	}

	public String classify()
	{
		if (isImpossible())
		{
			return P_IMPOSSIBLE;
		}

		if (side1 == side2 && side2 == side3)
		{
			return P_EQUILATERAL;
		}

		if (side1 == side2 || side1 == side3 || side2 == side3)
		{
			return P_ISOSCELES;
		}

		if (isRightAngled())
		{
			return P_RIGHTANGLED;
		}

		return P_SCALENE;
	}

	public EnumSet<TriangleType> getTypeFlags()
	{
		if (isImpossible())
		{
			return EnumSet.of(TriangleType.IMPOSSIBLE);
		}

		EnumSet<TriangleType> flags = EnumSet.noneOf(TriangleType.class);

		if (side1 == side2 && side2 == side3)
		{
			flags.add(TriangleType.EQUILATERAL);
		}
		else if (side1 == side2 || side1 == side3 || side2 == side3)
		{
			flags.add(TriangleType.ISOSCELES);
		}
		else
		{
			flags.add(TriangleType.SCALENE);
		}

		if (isRightAngled())
		{
			flags.add(TriangleType.RIGHT_ANGLED);
		}

		return flags;
	}

	public boolean isRightAngled()
	{
		if (isImpossible())
		{
			return false;
		}
		return rightAngleChecker.isRightAngled(side1, side2, side3);
	}

	public boolean isImpossible()
	{
		if (side1 <= 0 || side2 <= 0 || side3 <= 0)
		{
			return true;
		}
		long s1 = side1;
		long s2 = side2;
		long s3 = side3;

		return s1 + s2 <= s3 || s1 + s3 <= s2 || s2 + s3 <= s1;
	}

	public static void main(String[] args)
	{
		Triangle triangle;
		try
		{
			triangle = new Triangle(
					Integer.parseInt(args[0]),
					Integer.parseInt(args[1]),
					Integer.parseInt(args[2]));
		}
		catch (NumberFormatException | ArrayIndexOutOfBoundsException e)
		{
			System.out.println(
				"Usage: java Triangle <side1:int> <side2:int> <side3:int>");
			return;
		}
		System.out.println("Type: " + triangle.classify());
		System.out.println("Type flags: " + triangle.getTypeFlags());
		System.out.println("Triangle sides: " + triangle.getSideLengths());
		System.out.println("Area: " + triangle.getArea());
		System.out.println("Perimeter: " + triangle.getPerimeter());
	}

} // End of class.
