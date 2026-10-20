/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.project1;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
/**
 *
 * @author bigbe
 */
public class Project1 {

    public static void main(String[] args) {
       
       String[][] users = new String[2][3];
       int userCount = 0;
       //Registration
       for(int i = 0; i < users.length; i++){
            Scanner usrName = new Scanner(System.in);
            System.out.println("Please write your username");
            String userName = usrName.nextLine();
            if(userName.length() <= 5 && userName.contains("_")){
            System.out.println("User name successfully captured");
            }else{
            System.out.println("User name is not correctly formatted; please ensure that your username contains an underscore and is no longer than 5 characters long.");
            return;
            }
        
            Scanner psword = new Scanner(System.in);
            System.out.println("Please write your password");
            String password = psword.nextLine();
            if(password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*\\d.*") && password.matches(".*[^a-zA-z0-9].*")){
            System.out.println("Password successfully captured");
            }else{
            System.out.println("Password is not correctly formatted; please make sure that the password contains at least 8 characters, a capital letter, and a spacial character.");
            return;
            }
        
        
            Scanner pnmbr = new Scanner(System.in);
            System.out.println("Please write your phone number");
            String phoneNumber = pnmbr.nextLine();
            if(phoneNumber.contains("+27") && phoneNumber.length() >= 10){
            System.out.println("Cell phone number successfully added");
            } else{
            System.out.println("Cell phone number incorrectly formatted or does not contain international code");
            return;
            }  

            users[i][0] = userName;
            users[i][1] = password;
            users[i][2] = phoneNumber;
            userCount++;
            break;
       }
        
       //Login
          Scanner loginScanner = new Scanner(System.in);

System.out.println("Please write your username to sign in");
String enteredUserName = loginScanner.nextLine();

System.out.println("Please write your password to sign in");
String enteredPassword = loginScanner.nextLine();


//login validation
boolean loginSuccessful = false;

for (int i = 0; i < userCount; i++) {
    boolean usernameMatches = users[i][0] != null && users[i][0].equals(enteredUserName);
    boolean passwordMatches = users[i][1] != null && users[i][1].equals(enteredPassword);

    if (usernameMatches && passwordMatches) {
        loginSuccessful = true;
        System.out.println("Welcome " + users[i][0] + " it is great to see you again.");
        break;
    } else if (usernameMatches && !passwordMatches) {
        System.out.println("Username is correct, but the password is wrong.");
    } else if (!usernameMatches && passwordMatches) {
        System.out.println("Password is correct, but the username is wrong.");
    }
}

if (!loginSuccessful) {
    System.out.println("Invalid username or password.");
}else{
    boolean running = true;
    ArrayList<String> sentMessages = new ArrayList<>();
    Random randomId = new Random();
    int totalMessages = 0;
    
    do{
    System.out.println("Welcome to QuickChat");
       Scanner input = new Scanner(System.in);
           String option1 = "1) Chat";
           String option2 = "2) Recently sent - Coming soon";
           String option3 = "3) Exit";
    System.out.println(option1);
    System.out.println(option2);
    System.out.println(option3);        
    
    System.out.println("Please choose an action");
    String answer = input.nextLine();

    
    switch (answer){
        case "1" -> {
            System.out.println("Opening Chat...");
            
            
            System.out.println("How many messages would you like to send?");
            int messageCount = Integer.parseInt(input.nextLine());
            
           for(int i = 1; i <= messageCount; i++){
            Scanner reciever = new Scanner(System.in);
            System.out.println("Who would you like to send your message to?");
            String recipient = reciever.nextLine();
              
            Scanner text = new Scanner(System.in);
            System.out.println("Write the text you want to send to:" + recipient + i + "or type '0' to return to main menu." );
            String message = text.nextLine().trim();
            
            if (message.equalsIgnoreCase("0")){
                System.out.println("Message discarded. Returning to main menu");
                break;
            }else{
            if (message.length()> 250){
                System.out.println("Please enter a message of less than 250 character");
            }else{
            
            if(message.isBlank()){
                System.out.println("Please write your message before sending");
            }else{
                //creating the tracking id
                long trackingNumber = 100000000000L + randomId.nextLong(9000000000L);
                
                //extracting the first two digits of the tracking id
                String idString = String.valueOf(trackingNumber);
                String firstTwoDigits = idString.substring(0, 2);
                
                //extracting the first and last words of the message
                String[] words = message.split("\\s+"); //separates the words by spaces
                String firstWord = words[0]; //gets the first word
                String lastWord = words[words.length-1]; //gets the last word
                
                //hashing the message
                String messageHash = (firstTwoDigits + ":" + i + ":" + firstWord + lastWord).toUpperCase();
                
                System.out.println("Message ID: " + trackingNumber);
                System.out.println("MessageHash: "+ messageHash);
                System.out.println("Recipient: " + recipient);
                System.out.println("Message" + message);
                
                
                
                
                sentMessages.add("ID: "+ trackingNumber + " | Message: " + message +"MessageHash: " + messageHash + "recipient:" + recipient);
                System.out.println("Message sent successfully");
                System.out.println("Message successfully saved!");
                totalMessages++;
            }
            }
            
           }
           }
        }
        
        case "2" -> {
            System.out.println("This feature is currently under development...");
        }
        
        case "3" -> {
            System.out.println("Good bye!");
            running = false;
        }
            
                    
    }



    } while (running);  

}
        
}
}
