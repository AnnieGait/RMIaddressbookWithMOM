import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fib(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        
        AddressBook addrObj = new AddressBookImpl();
        
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();
                // to response einai keno gt den to exw balei kati akoma.
                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    response = executeOptionFromDB(message, addrObj);

                    System.out.println(" [.] test action " + message.charAt(0) );
                                  
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            
            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        }
    }
    
    private static String executeOptionFromDB(String message, AddressBook addrObj) throws RemoteException {
    	
    	//1#ac!idAC3N3w%AlliceWonderLand-Miracles@gmail.com
    	if(message.charAt(0) == '1' || message.charAt(0) == '3') {
    		String newMessage = "";
    		
    		//ac!idAC3N3w%AlliceWonderLand-Miracles@gmail.com
    		if(message.charAt(0) == '1')
    			newMessage = message.replace("1#", ""); 
    		else if(message.charAt(0) == '3')
    			newMessage = message.replace("3#", "");
    		
    		//ac!idAC3N3w%AlliceWonderLand-Miracles@gmail.com
    		String[] parts = newMessage.split("!");
    		String part1 = parts[0]; // ac
    		String part2 = parts[1]; // idAC3N3w%AlliceWonderLand-Miracles@gmail.com
    		
    		// idAC3N3w%AlliceWonderLand-Miracles@gmail.com
    		String[] partNoPhone = part2.split("%");
    		String noPho1 = partNoPhone[0]; // idAC3N3w
    		String noPho2 = partNoPhone[1]; // AlliceWonderLand-Miracles@gmail.com
    		
    		// AlliceWonderLand-Miracles@gmail.com
    		String[] partNoName = noPho2.split("-");
    		String noNam1 = partNoName[0]; // AlliceWonderLand
    		String noNam2 = partNoName[1]; // Miracles@gmail.com
    		
    		if(message.charAt(0) == '1') {
    			addrObj.insertInDB(part1, noPho1, noNam1, noNam2);
    			return "ContactInserted";
    		}
    		else {
    			addrObj.updateDB(part1, noPho1, noNam1, noNam2);  
    			System.out.println(part1 + noPho1 + noNam1 + noNam2);
    			return "ContactUpdated";
    		}
    		
    		
    	}
    	else if(message.charAt(0) == '2') {
    		Map<String, ArrayList<String>> aHash = new Hashtable<String, ArrayList<String>>();
    		StringBuilder appendingAllContacts = new StringBuilder();
    		System.out.println();
    		aHash = addrObj.selectFromDB();
    		for(Entry<String, ArrayList<String>> e:aHash.entrySet() ) {
    		    
    		    appendingAllContacts.append(e.getKey());
    		    appendingAllContacts.append(e.getValue());
    		    appendingAllContacts.append(", ");
    		    
    		}
    		return appendingAllContacts.toString();
    	}
    	else if(message.charAt(0) == '4') {
    		System.out.println("4 - Deleting from DB");
    		String delStr = message.replace("4#", "");
    		addrObj.deleteDB(delStr);
    		return "ContactDeleted";
    	}
    	else if(message.charAt(0) == '5') {
    		ArrayList<String> anArray = new ArrayList<String>();
    		StringBuilder builder = new StringBuilder();
    		System.out.println("5 - Selecting one element from DB");
    		String selStr = message.replace("5#", "");
    		anArray = addrObj.selectContactFromDB(selStr);
    		for(String a: anArray) {
    			builder.append(a);
    			builder.append(", ");
    		}
    		return builder.toString();
    	}
    	
    	return null;
    }
    
    
}