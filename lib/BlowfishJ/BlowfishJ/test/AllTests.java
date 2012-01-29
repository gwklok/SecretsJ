
package BlowfishJ.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for BlowfishJ.test");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(TestVectors.class));
		suite.addTest(new TestSuite(BinConverterTest.class));
		suite.addTest(new TestSuite(BlowfishTests.class));
		suite.addTest(new TestSuite(InOutputStreamTest.class));
		//$JUnit-END$
		return suite;
	}
}
