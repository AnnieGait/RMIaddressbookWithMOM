
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClient implements AutoCloseable {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public static void main(String[] argv) {
    	
    	 try (RPCClient fibonacciRpc = new RPCClient()) {
    		char ans;
 			do {
 				int choice;
 				System.out.println("DATABASE");
 				System.out.println("Press a number between [1,5]");
				System.out.println("1 - For insertion in DB");
				System.out.println("2 - For selecting all elements of DB");
				System.out.println("3 - For updating the DB");
				System.out.println("4 - Deleting from DB");
				System.out.println("5 - Selecting a contact from DB");
 				
 				Scanner scan = new Scanner(System.in);
 				choice = scan.nextInt();
 				String strChoice = fibonacciRpc.optionFromDB(choice);  
 				//System.out.println(strChoice); //[1,5]#phone!id%name-mail
 				
 				System.out.println(" [x] Requesting DB's action " + strChoice.charAt(0) );
                String response = fibonacciRpc.call(strChoice);
                System.out.println(" [.] Got '" + response + "'");
 				
 				System.out.println("Would you like to continue? (Y or y:yes) ");
				ans = scan.next().charAt(0);
			} while(ans == 'Y' || ans == 'y');	
 			
 			
          } catch (IOException | TimeoutException | InterruptedException e) {
              e.printStackTrace();
          }
    }

    public String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
    
    
    public String optionFromDB(int choice) {
    	String stringChoice = Integer.toString(choice);
    	
    	StringBuilder stringBuilder = new StringBuilder();
    	stringBuilder.append(stringChoice);
    	stringBuilder.append('#');
    	Scanner scan = new Scanner(System.in);
    	
    	if (choice == 1 || choice == 3) {
    		//System.out.println("You selected either inserting or updating in the DB.");
			
			System.out.println("Enter phone");
			String phone = scan.next();
			stringBuilder.append(phone);
	    	stringBuilder.append('!');
			
			System.out.println("Enter id");
			String id = scan.next();
			stringBuilder.append(id);
	    	stringBuilder.append('%');
			
			System.out.println("Enter name");
			String name = scan.next();
			stringBuilder.append(name);
	    	stringBuilder.append('-');
			
			System.out.println("Enter email");
			String mail = scan.next();
			stringBuilder.append(mail);
	    	return stringBuilder.toString();
    	}
    	else if(choice == 2) {
    		System.out.println("You selected selection in DB.");
			return stringChoice;
    		
    	}
    	else if(choice == 4) {
    		//System.out.println("You selected deletion in DB.");
			System.out.println("Enter existing phone you want to delete");
			String phone = scan.next();
			stringBuilder.append(phone);
			return stringBuilder.toString();
    	}
    	else if(choice == 5) {
    		System.out.println("Enter existing phone you want to select");
    		String phone = scan.next();
			stringBuilder.append(phone);
			return stringBuilder.toString();
    	}
    	return null;
    } 
}