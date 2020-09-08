/**
 * 
 */
package tests.echoserver;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.stenschmidt.jteeproxy.*;
import net.stenschmidt.jteeproxy.testtools.*;


/**
 * @author ST
 *
 */
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
			
//		    Runnable r1 = new Runnable() {
//		        public void run() {
//		        	EchoServer server1 = new EchoServer(6789, 50);
//		        	server1.startServer();
//		        }
//		    };
//		    new Thread(r1).start();
//			
//		    Runnable r2 = new Runnable() {
//		        public void run() {
//		        	EchoServer server2 = new EchoServer(6790, 50);
//		        	server2.startServer();
//		        }
//		    };
//		    new Thread(r2).start();
//		    
//		    Runnable r3 = new Runnable() {
//		    	public void run() {
//		    		JTeeProxy.SOURCE_PORT = 1234;
//					JTeeProxy.PRIMARY_DESTINATION_HOST = "localhost";
//					JTeeProxy.PRIMARY_DESTINATION_PORT = 6789;
//					JTeeProxy.SECONDARY_DESTINATION_HOST = "localhost";
//					JTeeProxy.SECONDARY_DESTINATION_PORT = 6790;
//					try {
//						JTeeProxy.startServer();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//		    	}
//		    };
//		    new Thread(r3).start();
//			
//			Runnable r4 = new Runnable() {
//				public void run() {
//					String[] args = {"localhost", "1234"};
//					EchoClient.main(args);
//				}
//			};
//			new Thread(r4).start();
//			
//			
//			TimeUnit.SECONDS.sleep(5);
		    			
			assertEquals(1, 1);
			
		} catch (Exception e) {
			fail(e.toString());
		}

		
	}

}
