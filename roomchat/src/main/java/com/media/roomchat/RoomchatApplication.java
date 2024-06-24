package com.media.roomchat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

@SpringBootApplication
public class RoomchatApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoomchatApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(SocketIOServer server){
		return args -> {
			server.start();
			Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
		};
	}

	@Bean
	public SocketIOServer socketIOServer(){

		//----------------- socket.io server configurations
		Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);
		config.setOrigin("*");

		//------------------ socket.io server object
		SocketIOServer server = new SocketIOServer(config);


		//------------------- managing request for room joining
		server.addEventListener("joinRoom", clientMessage.class, new DataListener<clientMessage>() {
			@Override
			public void onData(SocketIOClient client, clientMessage msg, AckRequest ackRequest ){
				// client joined room
				client.joinRoom(msg.getMsg());
				
				// sending joined message to room
				BroadcastOperations ros = server.getRoomOperations(client.getAllRooms().toArray()[1].toString());
				ros.sendEvent("joinMsg", msg.getName() + " joined");

				/*-------------------------- __NOTE__ ------------------------------
				 *
				 * client.getAllRooms() -> set of all room -> 0th element is empty
				 * Set.toArray()[1] -> converting set to array and getting 1th element (room name) -> class Object
				 * (Object).toString() -> converting object to String
				 * server.getRoomOperations() -> accepts String value -> class BroadcastOperations
				 * BroadcastOperations -> contains all room operations
				 */

				// System.out.println(msg.getName() + " joined room : " + msg.getMsg())
			}
		});		

		//------------------ managing request for room leaving
		server.addEventListener("leaveRoom", clientMessage.class, new DataListener<clientMessage>() {
			@Override
			public void onData(SocketIOClient client, clientMessage msg, AckRequest ackRequest ){
				
				// client left room
				
				// sending left message to room
				BroadcastOperations ros = server.getRoomOperations(client.getAllRooms().toArray()[0].toString());
				ros.sendEvent("leftMsg", msg.getName() + " left");
				client.leaveRoom(msg.getMsg());

				System.out.println(msg.getName() + " left room : " + msg.getMsg());
			}
		});		

		
		//----------------- sending message to room
		server.addEventListener("msg", clientMessage.class, new DataListener<clientMessage>() {
			@Override
			public void onData(SocketIOClient client, clientMessage msg, AckRequest ackRequest ){
				
				// forwarding the client message to the room
				BroadcastOperations ros = server.getRoomOperations(client.getAllRooms().toArray()[1].toString());
				ros.sendEvent("message", msg);

				// System.out.println(msg.getName() + " sent : " + msg.getMsg());
			}
		});

		return server;
	}
}