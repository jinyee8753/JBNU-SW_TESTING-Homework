public class RealRightAngleChecker implements RightAngleChecker
{
	@Override
	public boolean isRightAngled(int s1, int s2, int s3)
	{
		long side1Squared = square(s1);
		long side2Squared = square(s2);
		long side3Squared = square(s3);

		return side1Squared + side2Squared == side3Squared
			|| side1Squared + side3Squared == side2Squared
			|| side2Squared + side3Squared == side1Squared;
	}

	private long square(int value)
	{
		return (long) value * value;
	}
}
