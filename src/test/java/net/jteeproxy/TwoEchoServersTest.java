package net.jteeproxy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.jteeproxy.testtools.*;

class TwoEchoServersTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		for (int i = 0; i < 5; i++) {
			System.out.println("Waiting " + (i + 1));
			TimeUnit.SECONDS.sleep(1);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {

		try {

//
//			DRAFT FOR INTEGRATION TEST
//
			final int SOURCE_PORT = 1234;
			final String LOCALHOST = "localhost";
			final String PRIMARY_DESTINATION_HOST = LOCALHOST;
			final int PRIMARY_DESTINATION_PORT = 6789;
			final String SECONDARY_DESTINATION_HOST = LOCALHOST;
			final int SECONDARY_DESTINATION_PORT = 6790;

			Runnable r1 = new Runnable() {
				public void run() {
					EchoServer server1 = new EchoServer(PRIMARY_DESTINATION_PORT, 50);
					server1.startServer();
				}
			};
			Thread t1 = new Thread(r1);
			t1.start();

			Runnable r2 = new Runnable() {
				public void run() {
					EchoServer server2 = new EchoServer(SECONDARY_DESTINATION_PORT, 50);
					server2.startServer();
				}
			};
			Thread t2 = new Thread(r2);
			t2.start();

			Runnable r3 = new Runnable() {
				JTeeProxy proxy = new JTeeProxy();

				public void run() {
					JTeeProxy.sourcePort = SOURCE_PORT;
					JTeeProxy.primaryDestinationHost = PRIMARY_DESTINATION_HOST;
					JTeeProxy.primaryDestinationPort = PRIMARY_DESTINATION_PORT;
					JTeeProxy.secondaryDestinationHost = SECONDARY_DESTINATION_HOST;
					JTeeProxy.secondaryDestinationPort = SECONDARY_DESTINATION_PORT;
					try {
						proxy.startServer();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			Thread t3 = new Thread(r3);
			t3.start();

			TimeUnit.SECONDS.sleep(3);

			Runnable r4 = new Runnable() {
				public void run() {
					String[] args = { LOCALHOST, String.valueOf(SOURCE_PORT) };
					EchoClient.main(args);
				}
			};
			Thread t4 = new Thread(r4);
			t4.start();

			for (int i = 0; i < 5; i++) {
				System.out.println("Waiting " + (i + 1));
				TimeUnit.SECONDS.sleep(1);
			}

			assertEquals(true, t4.isAlive());
			assertEquals(true, t1.isAlive());
			assertEquals(true, t2.isAlive());
			assertEquals(true, t3.isAlive());

			System.out.println("start interrupt...");
			t1.interrupt();
			t2.interrupt();
			t3.interrupt();
			t4.interrupt();

		} catch (Exception e) {
			fail(e.toString());
		}

	}

}
