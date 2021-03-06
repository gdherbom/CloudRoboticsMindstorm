import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.*;

public class BluetoothCom {

	DataInputStream dis;
	DataOutputStream dos;
	NXTConnection connection;
	Thread connectionThread;
	boolean endingProgramm = false;

	public BluetoothCom() throws Exception {
		connectionThread = new Thread(){
			@Override
			public void run() {
				while (!endingProgramm) {
					try {
						LCD.drawString("Hi, I'm Robogator.", 0, 0);
						LCD.drawString(" Roaaaaarrrr !!", 0, 1);
						
						waitBluetoothConnexion();
	
						// The received message reader
						dis = connection.openDataInputStream();
						dos = connection.openDataOutputStream();
						computeCommand();
						
						LCD.clear();
					} catch (Exception e) {
					}
				}
			}
		};
		
		connectionThread.start();
		
		while(!endingProgramm){
			if(Button.waitForAnyPress() == Button.ID_ESCAPE){
				endingProgramm = true;
			}
		}
		return;
	}

	private void waitBluetoothConnexion() {
		LCD.drawString("I'm waiting for", 0, 2);
		LCD.drawString("a Bluetooth", 0, 3);
		LCD.drawString("connection", 0, 4);
		connection = Bluetooth.waitForConnection();
		//connection = Bluetooth.waitForConnection(2000, NXTConnection.RAW);
	}

	private void serverAck(int value) throws IOException {
		dos.writeInt(value);
		dos.flush();
	}

	private void computeCommand() throws IOException {
		int command = 0;
		int commandParameter = 1;

		while (!endingProgramm) {
			LCD.clear();
			LCD.drawString("Command wait...", 0, 1);
			command = 0;
			commandParameter = 1000;
			try {
				command = dis.readInt();
				commandParameter = dis.readInt();
			} catch (Exception e) {
				return;
			}
			int returnValue = 1;
			if (command != 0) {
				switch (command) {case 1:
					Movements.moveForward(commandParameter);
					break;
				case 2:
					Movements.stop();
					return;
				case 3:
					Movements.turnLeft(commandParameter);
					break;
				case 4:
					Movements.turnRight(commandParameter);
					break;
				case 5:
					Movements.moveBackward(commandParameter);
					break;
				case 6:
					Movements.stop();
					break;
				case 7:
					if (commandParameter == 1000) {
						commandParameter = 45;
					}
					Movements.openMouth(commandParameter);
					break;
				case 8:
					if (commandParameter == 1000) {
						commandParameter = 45;
					}
					Movements.closeMouth(commandParameter);
					break;
				case 9:
					returnValue = Movements.getUltraSonic();
					break;
				default:
					return;
				}
				serverAck(returnValue);
			}
		}
	}

}
